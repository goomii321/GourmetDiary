package com.linda.gourmetdiary.adding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.appworks.school.stylish.ext.getVmFactory
import com.linda.gourmetdiary.data.Food
import com.linda.gourmetdiary.databinding.AddDiaryFragmentBinding


class AddDiaryFragment : Fragment() {

    val viewModel by viewModels<AddDiaryViewModel> { getVmFactory(AddDiaryFragmentArgs.fromBundle(requireArguments()).users) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AddDiaryFragmentBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        var ratingScore = viewModel.foodRating.value
        var healthyScore = viewModel.healthyScore.value

        binding.addRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ratingScore = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(context, "Score is $ratingScore .",Toast.LENGTH_SHORT).show()
//                viewModel.user.value?.diarys?.food?.foodRate = ratingScore
            }
        })

        binding.addHealthy.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                healthyScore = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(context, "Score is $healthyScore .",Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }

}
