package com.linda.gourmetdiary.profile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.databinding.ProfileFragmentBinding
import com.linda.gourmetdiary.util.TimeConverters
import kotlinx.android.synthetic.main.profile_fragment.*
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    val viewModel by viewModels<ProfileViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ProfileFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val entries: MutableList<BarEntry> = ArrayList()
        val label: MutableList<String> = mutableListOf()

        viewModel.diary.observe(viewLifecycleOwner, Observer { diarys ->
            Log.d("Linda", "viewModel.diary.observe, diarys=$diarys")
            diarys?.let { diary ->
                for (i in 0..diary.size.minus(1)!!) {
                    Log.d("Linda", "diarys[$i].food?.price=${diarys[i].food?.price}")
                    diarys[i].food?.price?.toFloat()?.let { price ->
                        Log.d("Linda", "price=$price")
                        BarEntry(i.toFloat(), price)
                    }?.let { barEntry ->
                        Log.d("Linda", "barEntry=$barEntry")
                        entries.add(barEntry)
                    }
                }
//                Log.d("Linda","entries=$entries, entries.size=${entries.size}")
                val dataSet = BarDataSet(entries, "每日花費")
                dataSet.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.kachi)

                // Controlling X axis
                val xAxis = binding.chart.xAxis
                // Set the xAxis position to bottom. Default is top
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                //Customizing x axis value
                val months = arrayOf("M", "T", "W", "T", "F", "S", "S", "A", "A", "A")
                val formatter = IAxisValueFormatter { value, axis -> months[value.toInt()] }
                xAxis.granularity = 1f // minimum axis-step (interval) is 1

                // Controlling right side of y axis
                val yAxisRight = binding.chart.axisRight
                yAxisRight.isEnabled = false

                // Controlling left side of y axis
                val yAxisLeft = binding.chart.axisLeft
                yAxisLeft.granularity = 1f
                // Setting Data
                val data = BarData(dataSet)
                binding.chart.data = data
                binding.chart.invalidate()
            }
        })

        return binding.root
    }
}