package com.linda.gourmetdiary.stores.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.linda.gourmetdiary.ext.getVmFactory

import com.linda.gourmetdiary.databinding.StoresFragmentBinding

class StoreFragment : Fragment() {

    val viewModel by viewModels<StoreViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = StoresFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

}
