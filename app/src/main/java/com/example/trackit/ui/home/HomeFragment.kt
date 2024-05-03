package com.example.trackit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackit.R
import com.example.trackit.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Find views by their IDs
        val buttonAbout = binding.buttonAbout
        val buttonProducts = binding.buttonProducts
        val imageView = binding.imageView
        val textView = binding.textView

        // Set onClickListeners for buttons if needed
        // buttonAbout.setOnClickListener { /* Your onClick logic */ }
        // buttonProducts.setOnClickListener { /* Your onClick logic */ }

        // Set image resource for the ImageView
        // imageView.setImageResource(R.drawable.your_image_resource)

        // Set text for the TextView
        textView.text = getString(R.string.app_description)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
