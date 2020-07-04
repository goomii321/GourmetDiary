package com.linda.gourmetdiary.stores

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import app.appworks.school.stylish.ext.getVmFactory

import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.databinding.StoresFragmentBinding

class StoresFragment : Fragment() {

    val viewModel by viewModels<StoresViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = StoresFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

}
