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
import com.linda.gourmetdiary.data.Diaries4Day
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.databinding.ProfileFragmentBinding
import com.linda.gourmetdiary.util.TimeConverters
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    val viewModel by viewModels<ProfileViewModel> { getVmFactory() }

    private lateinit var binding:ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.diaryList.observe(viewLifecycleOwner, Observer { diary ->
            diary?.let {
                viewModel.assignDiaryData(diary)
            }
        })

        viewModel.diary4Day.observe(viewLifecycleOwner, Observer {
            it?.let { diaries ->
                viewModel.assignChartData(diaries)
                setBarChart()
            }
        })

        return binding.root
    }

    private fun setBarChart(){
        val dataSet = BarDataSet(viewModel.entries, getString(R.string.daily_cost))
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.kachi)

        // Controlling X axis
        val xAxis = binding.chart.xAxis
        // Set the xAxis position to bottom. Default is top
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //Customizing x axis value
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
            valueFormatter = IndexAxisValueFormatter(viewModel.label)
            labelCount = viewModel.xTitle.size
            setDrawLabels(true)
            setDrawGridLines(false)
        }
        binding.chart.description.text = ""
        binding.chart.setDrawGridBackground(false)
    }
}