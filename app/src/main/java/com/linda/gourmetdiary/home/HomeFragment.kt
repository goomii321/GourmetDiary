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
import com.linda.gourmetdiary.NavigationDirections

import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.HomeFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.util.Logger.i
import java.util.logging.Logger

class HomeFragment : Fragment() {

    val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    private lateinit var binding: HomeFragmentBinding
    var isOpen = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.dailyDiary.adapter = HomeAdapter(HomeAdapter.OnClickListener{
            viewModel.navigateToDetail(it)
        })

        binding.addFbtn.setOnClickListener {
            setButtonAni()
        }

        binding.addDefault.setOnClickListener {
            checkUserStatus()
        }

        binding.template.setOnClickListener {
            checkUserStatus()
        }

        viewModel.greetingStatus.observe(viewLifecycleOwner, Observer {
            setGreetingText(it)
        })

        viewModel.sameStoreStatus.observe(viewLifecycleOwner, Observer {
            viewModel.countText.value = viewModel.count.value.toString()
            viewModel.listStoreText.value = viewModel.listStore.value
            if ( it == true){
                binding.view2.visibility = View.VISIBLE
                binding.homeReminder.text = "最近七天你已吃過 ${viewModel.count.value} 次${viewModel.listStoreText.value}囉!!"
            }
        })

        viewModel.healthyScore.observe(viewLifecycleOwner, Observer {
            viewModel.healthyScoreText.value = viewModel.healthyScore.value
            if (viewModel.scoreAverage <= 6.0){
                viewModel.healthyScoreStatus.value = true
                binding.healthyReminder.text = "最近的健康度只有 ${viewModel.healthyScoreText.value} ，是不是該吃點蔬果啦~"
            }
        })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToDiaryDetail(it))
                viewModel.onDetailNavigated()
            }
        })

        return binding.root
    }

    private fun setButtonAni() {

        val fabOpen = AnimationUtils.loadAnimation(context,R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(context,R.anim.fab_close)
        val fabRotate = AnimationUtils.loadAnimation(context,R.anim.rotate_clockwise)
        val fabAntiRotate = AnimationUtils.loadAnimation(context,R.anim.rotate_anticlockwise)

        if (isOpen){
            binding.template.startAnimation(fabClose)
            binding.addDefault.startAnimation(fabClose)
            binding.addFbtn.startAnimation(fabAntiRotate)
            binding.templateText.startAnimation(fabClose)
            binding.diaryText.startAnimation(fabClose)

            isOpen = false
        } else {
            binding.template.startAnimation(fabOpen)
            binding.addDefault.startAnimation(fabOpen)
            binding.addFbtn.startAnimation(fabRotate)
            binding.templateText.startAnimation(fabOpen)
            binding.diaryText.startAnimation(fabOpen)

            isOpen = true
        }
    }

    private fun checkUserStatus() {
        if (isOpen){
            if (viewModel.isLoggedIn){
                findNavController().navigate(R.id.navigate_to_add)
            } else {
                Toast.makeText(context,getString(R.string.log_try_again),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setGreetingText(value:Int) {
        when(value){
            -1 -> {
                binding.title.text = getString(R.string.morning_greeting)
                binding.homeBackground.setImageResource(R.drawable.home_breakfast)
            }
            -2 -> {
                binding.title.text = getString(R.string.noon_greeting)
                binding.homeBackground.setImageResource(R.drawable.home_lunch)
            }
            -3 -> {
                binding.title.text = getString(R.string.afternoon_greeting)
                binding.homeBackground.setImageResource(R.drawable.home_dessert)
            }
            -4 -> {
                binding.title.text = getString(R.string.night_greeting)
                binding.homeBackground.setImageResource(R.drawable.home_dinner)
            }
            else -> {
                binding.title.text = getString(R.string.midnight_greeting)
                binding.homeBackground.setImageResource(R.drawable.home_midnight)
            }
        }
    }

    override fun onDestroy() {
        viewModel.clearReminder()
        super.onDestroy()
    }
}
