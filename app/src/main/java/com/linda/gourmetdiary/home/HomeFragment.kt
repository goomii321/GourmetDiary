package com.linda.gourmetdiary.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.HomeFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.util.UserManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class HomeFragment : Fragment() {

    val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var isOpen = false
        val fabOpen = AnimationUtils.loadAnimation(context,R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(context,R.anim.fab_close)
        val fabRotate = AnimationUtils.loadAnimation(context,R.anim.rotate_clockwise)
        val unfabRotate = AnimationUtils.loadAnimation(context,R.anim.rotate_anticlockwise)

        var timeNow = LocalTime.now()
        var timeMorning = LocalTime.parse("05:00:00.00")
        var timeNoon = LocalTime.parse("11:00:00.00")
        var timeAfternoon = LocalTime.parse("14:00:00.00")
        var timeNight = LocalTime.parse("18:00:00.00")
        Log.d("timeNow", "time is $timeNow, ${timeNow.isAfter(timeNoon)}, ${timeNow.isAfter(timeNight)}")

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
                binding.addFbtn.startAnimation(unfabRotate)

                isOpen = true
            }
        }

        binding.addDefault.setOnClickListener {
            if (isOpen){
                Log.i("addDefault","addDefault UserManager= ${UserManager.userData}; ${UserManager.userId};${UserManager.isLoggedIn}")
                if (viewModel.isLoggedIn){
                    findNavController().navigate(R.id.navigate_to_add)
                } else {
                    Toast.makeText(context,"請重新登入",Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (viewModel.listStore.value == null) {
            binding.view2.visibility = View.GONE
        }

        if(viewModel.healthyScore.value == null){
            binding.view3.visibility = View.GONE
        }

        viewModel.listStore.observe(viewLifecycleOwner, Observer {
            viewModel.countText.value = viewModel.count.value.toString()
            viewModel.listStoreText.value = viewModel.listStore.value
            binding.view2.visibility = View.VISIBLE
            binding.homeReminder.text = "最近七天你已吃過 ${viewModel.count.value} 次${viewModel.listStore.value}囉!!"
//            Log.d("getSameStore","0000listStore = ${viewModel.listStore.value}; count = ${viewModel.count.value}")
        })

        viewModel.healthyScore.observe(viewLifecycleOwner, Observer {
            viewModel.healthyScoreText.value = viewModel.healthyScore.value
            binding.view3.visibility = View.VISIBLE
            binding.healthyReminder.text = "最近的健康度只有 ${viewModel.healthyScoreText.value} ，是不是該吃點蔬果啦~"
        })

        return binding.root
    }

    override fun onDestroy() {
        viewModel.clearReminder()
        super.onDestroy()
    }
}
