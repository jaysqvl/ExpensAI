package com.example.cmpt362_finalproject.api.demo

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cmpt362_finalproject.R

class DemoAPIFragment : Fragment(R.layout.fragment_example) {

    private val viewModel: DemoAPIViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe LiveData for TextService responses
        viewModel.textResponse.observe(viewLifecycleOwner) { response ->
            Log.d("DemoAPIFragment", "Text Response: $response")
            Toast.makeText(requireContext(), "Text Response: ${response.output}", Toast.LENGTH_SHORT).show()
        }

        // Observe LiveData for VisionService responses
        viewModel.visionResponse.observe(viewLifecycleOwner) { response ->
            Log.d("DemoAPIFragment", "Vision Response: $response")
            Toast.makeText(requireContext(), "Vision Response: ${response.output}", Toast.LENGTH_SHORT).show()
        }

        // Observe LiveData for errors
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("DemoAPIFragment", "Error: $errorMessage")
            Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
        }

        // Example: Fetch a TextService response
        viewModel.fetchTextResponse("summary", "Sample transaction data for testing")

        // Example: Fetch a VisionService response
        val sampleImageBase64 = Base64.encodeToString("SampleImageData".toByteArray(), Base64.DEFAULT)
        viewModel.fetchVisionResponse(sampleImageBase64)
    }
}
