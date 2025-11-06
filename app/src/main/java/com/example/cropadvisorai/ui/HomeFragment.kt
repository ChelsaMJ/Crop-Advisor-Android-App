package com.example.cropadvisorai.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.cropadvisorai.R
import com.example.cropadvisorai.ResultActivity

class HomeFragment : Fragment() {

    // ======== UI elements ========
    private lateinit var spinnerSoilType: Spinner
    private lateinit var sliderNitrogen: SeekBar
    private lateinit var sliderPh: SeekBar
    private lateinit var sliderTemp: SeekBar
    private lateinit var sliderRainfall: SeekBar
    private lateinit var sliderHumidity: SeekBar
    private lateinit var btnRecommend: Button

    private lateinit var tvNitrogenValue: TextView
    private lateinit var tvPhValue: TextView
    private lateinit var tvTempValue: TextView
    private lateinit var tvRainfallValue: TextView
    private lateinit var tvHumidityValue: TextView

    // ======== Data defaults ========
    private var nitrogenValue: Int = 50
    private var phValue: Double = 7.0
    private var tempValue: Int = 25
    private var rainfallValue: Int = 1500
    private var humidityValue: Int = 50
    private var soilTypeValue: String = ""

    private val soilTypes = arrayOf("Loamy", "Sandy", "Clay", "Silt", "Peat")

    // ======== API KEY (set here for now) ========
    // Replace with BuildConfig.AI_API_KEY or other safe retrieval for production.
    // You said "i will give api key again" — put it here as a string, or set it to "" to disable.
    private val apiKeyForThisRun: String = "" // <-- put your API key here, or leave blank to test UI only

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize UI components
        spinnerSoilType = view.findViewById(R.id.spinnerSoilType)
        sliderNitrogen = view.findViewById(R.id.sliderNitrogen)
        sliderPh = view.findViewById(R.id.sliderPh)
        sliderTemp = view.findViewById(R.id.sliderTemp)
        sliderRainfall = view.findViewById(R.id.sliderRainfall)
        sliderHumidity = view.findViewById(R.id.sliderHumidity)
        btnRecommend = view.findViewById(R.id.btnRecommend)

        tvNitrogenValue = view.findViewById(R.id.tvNitrogenValue)
        tvPhValue = view.findViewById(R.id.tvPhValue)
        tvTempValue = view.findViewById(R.id.tvTempValue)
        tvRainfallValue = view.findViewById(R.id.tvRainfallValue)
        tvHumidityValue = view.findViewById(R.id.tvHumidityValue)

        // Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, soilTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSoilType.adapter = adapter
        spinnerSoilType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                soilTypeValue = soilTypes[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        soilTypeValue = soilTypes[0]

        // SeekBars — set max already in XML; ensure initial text shows current value
        setupSeekBar(sliderNitrogen, tvNitrogenValue, 100) { progress ->
            nitrogenValue = progress
            tvNitrogenValue.text = "$progress%"
        }

        setupSeekBar(sliderPh, tvPhValue, 140) { progress ->
            phValue = progress / 10.0
            tvPhValue.text = String.format("%.1f", phValue)
        }

        setupSeekBar(sliderTemp, tvTempValue, 50) { progress ->
            tempValue = progress
            tvTempValue.text = "$progress°C"
        }

        setupSeekBar(sliderRainfall, tvRainfallValue, 3000) { progress ->
            rainfallValue = progress
            tvRainfallValue.text = "$progress mm"
        }

        setupSeekBar(sliderHumidity, tvHumidityValue, 100) { progress ->
            humidityValue = progress
            tvHumidityValue.text = "$progress%"
        }

        btnRecommend.setOnClickListener {
            // If API key is empty, prevent calling network and warn user (you said you'll provide it)
            if (apiKeyForThisRun.isBlank()) {
                Toast.makeText(requireContext(), "API key not provided. Set apiKeyForThisRun in HomeFragment.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val cropInputSummary = """
                Soil Type: $soilTypeValue,
                Nitrogen Content: $nitrogenValue%,
                pH Level: $phValue,
                Temperature: $tempValue°C,
                Rainfall: $rainfallValue mm,
                Humidity: $humidityValue%
            """.trimIndent()

            val intent = Intent(requireContext(), ResultActivity::class.java).apply {
                putExtra("inputSummary", cropInputSummary)
                putExtra("apiKey", apiKeyForThisRun)
            }
            startActivity(intent)
        }

        return view
    }

    private fun setupSeekBar(seekBar: SeekBar, textView: TextView, max: Int, onProgressChange: (Int) -> Unit) {
        seekBar.max = max
        // Ensure text reflects initial progress from XML or default
        onProgressChange(seekBar.progress)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChange(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }
}
