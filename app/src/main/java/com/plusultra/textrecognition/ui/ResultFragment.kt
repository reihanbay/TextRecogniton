package com.plusultra.textrecognition.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.plusultra.textrecognition.R
import com.plusultra.textrecognition.databinding.FragmentResultBinding

class ResultFragment : Fragment() {
    private var binding : FragmentResultBinding? = null
    private val bind get() = binding!!
    private val data : ResultFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.tvResult.setText(data.text)
    }
}