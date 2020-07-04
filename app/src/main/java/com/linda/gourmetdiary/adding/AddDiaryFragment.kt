package com.linda.gourmetdiary.adding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import app.appworks.school.stylish.ext.getVmFactory
import com.linda.gourmetdiary.databinding.AddDiaryFragmentBinding


class AddDiaryFragment : Fragment() {

    val viewModel by viewModels<AddDiaryViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = AddDiaryFragmentBinding.inflate(inflater,container,false)
        var visibleStatus = false

        binding.arrow1.setOnClickListener {
            if (visibleStatus){
                binding.foodTitle.visibility = View.GONE
                binding.comboTitle.visibility = View.GONE

                visibleStatus = false
            } else {
                binding.foodTitle.visibility = View.VISIBLE
                binding.comboTitle.visibility = View.VISIBLE

                visibleStatus = true
            }
        }

        binding.arrow2.setOnClickListener {
            if (visibleStatus){
                binding.storeName.visibility = View.GONE
                binding.storePhone.visibility = View.GONE
                binding.storeLocation.visibility = View.GONE
                visibleStatus = false
            } else {
                binding.storeName.visibility = View.VISIBLE
                binding.storePhone.visibility = View.VISIBLE
                binding.storeLocation.visibility = View.VISIBLE
                visibleStatus = true
            }

        }

        return binding.root
    }

}
