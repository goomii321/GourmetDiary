package com.linda.gourmetdiary.template

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.TemplateFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory

class TemplateFragment : Fragment() {

    val viewModel by viewModels<TemplateViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = TemplateFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

}
