package com.example.cropadvisorai

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class ResultActivity : AppCompatActivity() {

    private lateinit var txtSummary: TextView
    private lateinit var progressBar: ProgressBar
    private val TAG = "ResultActivity"

    // If not passed via intent, this default can be blank. Prefer passing the key from HomeFragment or using BuildConfig.
    private var apiKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        txtSummary = findViewById(R.id.txtSummary)
        progressBar = findViewById(R.id.progressBar)

        val inputSummary = intent.getStringExtra("inputSummary") ?: "No input data provided."
        apiKey = intent.getStringExtra("apiKey") ?: ""

        txtSummary.text = "Analyzing conditions...\n\n$inputSummary"

        if (inputSummary.isNotBlank()) {
            if (apiKey.isBlank()) {
                txtSummary.text = "API key missing. The request cannot be sent."
                Toast.makeText(this, "API key not provided. Check HomeFragment.", Toast.LENGTH_LONG).show()
            } else {
                // show progress and kick off the AI call
                progressBar.visibility = View.VISIBLE
                getAiRecommendation(inputSummary)
            }
        }
    }

    private fun getAiRecommendation(inputSummary: String) {
        val systemInstruction =
            "Act as a leading, experienced agricultural scientist and provide precise crop advice. You MUST recommend the SINGLE BEST CROP and provide a brief, professional justification for the recommendation based ONLY on the provided soil and climate data."

        val userPrompt =
            "Based on the following conditions, recommend the best single crop and justify your choice. Return JSON with keys: recommendedCrop, justification, alertLevel.\n\n$inputSummary"

        // Build payload with proper arrays for `contents` and `parts`
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()
        val partObj = JSONObject()
        partObj.put("text", "$systemInstruction\n\n$userPrompt")
        partsArray.put(partObj)
        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)

        val payload = JSONObject()
        payload.put("contents", contentsArray)

        // Optional: set generationConfig if required by your API
        val genConfig = JSONObject()
        genConfig.put("responseMimeType", "application/json")
        payload.put("generationConfig", genConfig)

        // Launch network call in coroutine
        lifecycleScope.launch {
            try {
                val responseJson = withContext(Dispatchers.IO) {
                    performApiCall(payload.toString())
                }
                // process response on main thread
                withContext(Dispatchers.Main) {
                    processAiResponse(responseJson)
                }
            } catch (e: Exception) {
                Log.e(TAG, "AI call failed", e)
                withContext(Dispatchers.Main) {
                    txtSummary.text =
                        "❌ AI Analysis Failed: ${e.message}. Ensure network connection and API key are valid."
                    Toast.makeText(this@ResultActivity, "API Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            } finally {
                // Always hide progress when done (success or failure)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun performApiCall(payload: String): JSONObject {
        // Use a reasonable timeout
        val apiUrl =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val url = URL(apiUrl)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connectTimeout = TimeUnit.SECONDS.toMillis(30).toInt()
            readTimeout = TimeUnit.SECONDS.toMillis(60).toInt()
            doOutput = true
        }

        // Write payload
        connection.outputStream.use { os ->
            val bytes = payload.toByteArray(StandardCharsets.UTF_8)
            os.write(bytes, 0, bytes.size)
            os.flush()
        }

        return try {
            val responseCode = connection.responseCode
            val respText = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val err =
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error body"
                throw Exception("API returned code $responseCode: $err")
            }
            // Convert to JSONObject safely
            JSONObject(respText)
        } finally {
            connection.disconnect()
        }
    }

    private fun processAiResponse(response: JSONObject) {
        try {
            // The Generative API returns `candidates` array. Each candidate has `content.parts` (array) with text.
            val candidates = response.optJSONArray("candidates")
            val rawText = if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    parts.getJSONObject(0).optString("text", "")
                } else {
                    candidate.optString("text", "")
                }
            } else {
                // fallback: try `output` or raw string
                response.optString("output", response.toString())
            }

            // Try to parse rawText as JSON (in case model returned JSON string)
            var recommendedCrop: String
            var justification: String
            var alert: String
            try {
                val parsed = JSONObject(rawText)
                recommendedCrop =
                    parsed.optString("recommendedCrop", parsed.optString("crop", "Unknown"))
                justification =
                    parsed.optString("justification", parsed.optString("reason", rawText))
                alert = parsed.optString("alertLevel", parsed.optString("risk", "Unknown"))
            } catch (je: Exception) {
                // If not JSON, present rawText as justification and unknown crop
                recommendedCrop = "Unknown"
                justification = rawText
                alert = "Unknown"
            }

            // Update UI (already on Main because caller used withContext)
            txtSummary.text = """
                    ⇨ AI Recommendation Complete!

                    Best Crop: $recommendedCrop
                    Risk Level: $alert

                    Justification:
                    $justification
                """.trimIndent()
        } catch (e: Exception) {
            txtSummary.text = "Error processing AI response: ${e.message}"
        }
    }
}
