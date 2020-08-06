package com.linda.gourmetdiary.profile

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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.databinding.ProfileFragmentBinding
import com.linda.gourmetdiary.util.TimeConverters
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

        viewModel.diaryList.observe(viewLifecycleOwner, Observer { diary ->
            diary?.let {
                viewModel.assignDiaryData(diary)
            }
        })

        viewModel.diary4Day.observe(viewLifecycleOwner, Observer {
            it?.let { diaries ->

                val xTitle = mutableListOf<String>()

                for ((index, diary) in diaries.withIndex()) {

                    val title = TimeConverters.timestampToDate(diary.dayTitle!!, Locale.TAIWAN)

                    xTitle.add(title)
                    label.add(TimeConverters.timestampToDate(diary.dayTitle!!, Locale.TAIWAN))

                    var totalPriceForDay = 0
                    diary.diaries.forEach { item ->
                        totalPriceForDay += (item.food?.price?.toInt() ?: 0)
                    }
                    Log.d("Wayne","title=$title")
                    Log.d("Wayne","totalPriceForDay=$totalPriceForDay}")
                    Log.i("Wayne","----------------------------------------")

                    entries.add(BarEntry(index.toFloat(), totalPriceForDay.toFloat()))
                }
                Log.d("xTitle","xTitle = $xTitle ; size = ${xTitle.size}")

                /** 分隔線 **/

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
                xAxis.granularity = 2f // minimum axis-step (interval) is 1

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
                binding.chart.xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(label)
                    labelCount = xTitle.size
                    setDrawLabels(true)
                    setDrawGridLines(false)
                }
                binding.chart.description.text = ""
                binding.chart.setDrawGridBackground(false)
            }
        })

        return binding.root
    }
}