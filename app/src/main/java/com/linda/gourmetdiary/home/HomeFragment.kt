package com.linda.gourmetdiary.home

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.appworks.school.stylish.ext.getVmFactory

import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var isOpen = false
        val fabOpen = AnimationUtils.loadAnimation(context,R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(context,R.anim.fab_close)
        val fabRotate = AnimationUtils.loadAnimation(context,R.anim.rotate_clockwise)

        val binding = HomeFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.addFbtn.setOnClickListener {
            if (isOpen){
                binding.reminder.startAnimation(fabClose)
                binding.addDefault.startAnimation(fabClose)
                binding.addFbtn.startAnimation(fabRotate)

                isOpen = false
            } else {
                binding.reminder.startAnimation(fabOpen)
                binding.addDefault.startAnimation(fabOpen)
                binding.addFbtn.startAnimation(fabRotate)

                isOpen = true
            }
        }

        binding.addDefault.setOnClickListener {
            if (isOpen){
                findNavController().navigate(R.id.navigate_to_add)
            }
        }


        return binding.root
    }
}
