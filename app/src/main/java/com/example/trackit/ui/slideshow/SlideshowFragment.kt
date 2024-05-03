package com.example.trackit.ui.slideshow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.trackit.ui.Line1Activity
import com.example.trackit.R

class SlideshowFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        // Find the LINE 1 button
        val line1Button: Button = root.findViewById(R.id.button1)

        // Set click listener for LINE 1 button
        line1Button.setOnClickListener {
            // Start Line1Activity when LINE 1 button is clicked
            val intent = Intent(requireActivity(), Line1Activity::class.java)
            startActivity(intent)
        }

        return root
    }
}
