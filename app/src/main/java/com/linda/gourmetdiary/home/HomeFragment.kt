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

        val binding = HomeFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.dailyDiary.adapter = HomeAdapter(HomeAdapter.OnClickListener{
            findNavController().navigate(NavigationDirections.navigateToDiaryDetail(it))
            viewModel.onDetailNavigated()
        })

        binding.addFbtn.setOnClickListener {
            if (isOpen){
                binding.template.startAnimation(fabClose)
                binding.addDefault.startAnimation(fabClose)
                binding.addFbtn.startAnimation(unfabRotate)
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

        binding.addDefault.setOnClickListener {
            if (isOpen){
                if (viewModel.isLoggedIn){
                    findNavController().navigate(R.id.navigate_to_add)
                } else {
                    Toast.makeText(context,"請重新登入",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.template.setOnClickListener {
            if (isOpen){
                if (viewModel.isLoggedIn){
                    findNavController().navigate(R.id.navigate_to_template)
                } else {
                    Toast.makeText(context,"請重新登入",Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.helloStatus.observe(viewLifecycleOwner, Observer {
//            Log.d("helloStatus","helloStatus = $it")
            when(it){
                -1 -> {
                    binding.title.text = "早安:)，睡飽就該開吃囉！"
                    binding.homeBackground.setImageResource(R.drawable.home_breakfast)
                }
                -2 -> {
                    binding.title.text = "生活不是吃，就是在找吃的。"
                    binding.homeBackground.setImageResource(R.drawable.home_lunch)
                }
                -3 -> {
                    binding.title.text = "點心是裝在另一個胃袋裡啦;)"
                    binding.homeBackground.setImageResource(R.drawable.home_dessert)
                }
                -4 -> {
                    binding.title.text = "今天辛苦啦~快吃頓好吃的晚餐犒勞自己吧！"
                    binding.homeBackground.setImageResource(R.drawable.home_dinner)
                }
                else -> {
                    binding.title.text = "晚安Zzz...要吃宵夜也要注意健康，\n才可以一直吃一直吃~"
                    binding.homeBackground.setImageResource(R.drawable.home_midnight)
                }
            }
        })

        if(viewModel.healthyScore.value == null){
            binding.view3.visibility = View.GONE
            binding.healthyReminder.visibility = View.GONE
        } else {
            binding.view3.visibility = View.VISIBLE
            binding.healthyReminder.visibility = View.VISIBLE
        }

        viewModel.sameStoreStatus.observe(viewLifecycleOwner, Observer {
            viewModel.countText.value = viewModel.count.value.toString()
            viewModel.listStoreText.value = viewModel.listStore.value
            if ( it == true){
                binding.view2.visibility = View.VISIBLE
                binding.homeReminder.text = "最近七天你已吃過 ${viewModel.count.value} 次${viewModel.listStoreText.value}囉!!"
            }
//            Log.d("getSameStore"," $it ; 0000listStore = ${viewModel.listStore.value}; count = ${viewModel.count.value}")
        })

        viewModel.healthyScore.observe(viewLifecycleOwner, Observer {
            viewModel.healthyScoreText.value = viewModel.healthyScore.value
            if (viewModel.scoreAverage <= 6.0){
                binding.view3.visibility = View.VISIBLE
                binding.healthyReminder.visibility = View.VISIBLE
                binding.healthyReminder.text = "最近的健康度只有 ${viewModel.healthyScoreText.value} ，是不是該吃點蔬果啦~"
            }
        })

        return binding.root
    }

    override fun onDestroy() {
        viewModel.clearReminder()
        super.onDestroy()
    }
}
