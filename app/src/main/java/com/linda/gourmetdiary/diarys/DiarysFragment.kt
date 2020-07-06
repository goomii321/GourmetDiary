package com.linda.gourmetdiary.diarys

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.linda.gourmetdiary.ext.getVmFactory

import com.linda.gourmetdiary.databinding.DiarysFragmentBinding

class DiarysFragment : Fragment() {

    val viewModel by viewModels<DiarysViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DiarysFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

}
