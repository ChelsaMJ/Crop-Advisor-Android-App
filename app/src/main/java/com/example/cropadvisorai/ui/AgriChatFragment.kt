package com.example.cropadvisorai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropadvisorai.ApiContent
import com.example.cropadvisorai.ApiPart
import com.example.cropadvisorai.GeminiRequest
import com.example.cropadvisorai.GeminiResponse
import com.example.cropadvisorai.R
import com.example.cropadvisorai.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgriChatFragment : Fragment() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var inputEdit: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private val adapter = MessagesAdapter()

    // Replace with your actual API key retrieval method (BuildConfig or secure store)
    // This code expects Retrofit service method signature:
    // RetrofitClient.apiService.generateContent(apiKey, geminiRequest)
    private val apiKey: String
        get() = "AIzaSyC9Nq-PRx4pmdau5B_gREf4L6l9-q6C0Q4" // TODO: Put your API key here temporarily or use BuildConfig.AI_API_KEY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agri_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvMessages = view.findViewById(R.id.rvMessages)
        inputEdit = view.findViewById(R.id.editTextPrompt)
        sendButton = view.findViewById(R.id.send_button)
        progressBar = view.findViewById(R.id.progressBar)
        statusText = view.findViewById(R.id.statusText)

        rvMessages.layoutManager = LinearLayoutManager(requireContext())
        rvMessages.adapter = adapter

        sendButton.setOnClickListener {
            val prompt = inputEdit.text.toString().trim()
            if (prompt.isEmpty()) return@setOnClickListener

            addMessage(prompt, isUser = true)
            inputEdit.setText("")
            callGeminiApi(prompt)
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        adapter.items.add(Message(text, isUser))
        adapter.notifyItemInserted(adapter.items.size - 1)
        rvMessages.scrollToPosition(adapter.items.size - 1)
    }

    private fun callGeminiApi(prompt: String) {
        progressBar.visibility = View.VISIBLE
        statusText.visibility = View.GONE
        // Construct request following your GeminiRequest/ApiContent/ApiPart model
        val part = ApiPart(text = prompt)
        val content = ApiContent(parts = listOf(part), role = "user")
        val request = GeminiRequest(contents = listOf(content))

        val call: Call<GeminiResponse> = RetrofitClient.apiService.generateContent(apiKey, request)
        call.enqueue(object : Callback<GeminiResponse> {
            override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                progressBar.visibility = View.GONE
                if (!response.isSuccessful || response.body() == null) {
                    val err = "API error: ${response.code()} ${response.message()}"
                    statusText.visibility = View.VISIBLE
                    statusText.text = err
                    addMessage("Error: $err", isUser = false)
                    return
                }

                val body = response.body()!!
                // Extract text from the first candidate -> content -> parts
                val replyText = body.candidates
                    .firstOrNull()
                    ?.content
                    ?.parts
                    ?.mapNotNull { it.text }
                    ?.joinToString(separator = "\n")
                    ?: "No reply"

                addMessage(replyText, isUser = false)
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                statusText.visibility = View.VISIBLE
                statusText.text = "Network error: ${t.localizedMessage}"
                addMessage("Network error: ${t.localizedMessage}", isUser = false)
            }
        })
    }

    // Simple local models for the chat adapter
    data class Message(val text: String, val isUser: Boolean)

    class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
        val items = mutableListOf<Message>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val layout = if (viewType == 0) R.layout.item_message_user else R.layout.item_message_bot
            val v = inflater.inflate(layout, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val m = items[position]
            holder.text.text = m.text
        }

        override fun getItemViewType(position: Int): Int {
            return if (items[position].isUser) 0 else 1
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val text: TextView = v.findViewById(R.id.itemText)
        }
    }
}
