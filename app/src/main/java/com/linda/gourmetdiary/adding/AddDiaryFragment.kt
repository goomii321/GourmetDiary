package com.linda.gourmetdiary.adding

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.SeekBar
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.MainActivity
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.databinding.AddDiaryFragmentBinding
import com.linda.gourmetdiary.util.Logger
import java.util.*


class AddDiaryFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    val viewModel by viewModels<AddDiaryViewModel> {
        getVmFactory(
            AddDiaryFragmentArgs.fromBundle(
                requireArguments()
            ).users?.diarys
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AddDiaryFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        var ratingScore = viewModel.foodRating.value
        var healthyScore = viewModel.healthyScore.value

        //--- Slide bar ---
        binding.addRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ratingScore = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(context, "Score is $ratingScore .", Toast.LENGTH_SHORT).show()
                viewModel.user.value?.diarys?.food?.foodRate = ratingScore
            }
        })

        binding.addHealthy.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                healthyScore = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(context, "Score is $healthyScore .", Toast.LENGTH_SHORT).show()
                viewModel.user.value?.diarys?.food?.healthyScore = healthyScore
            }
        })

        binding.eatingTime.setOnClickListener {
            getDate()
        }

        viewModel.saveMinute.observe(viewLifecycleOwner, Observer {
            binding.timeShow.text = "${viewModel.saveYear.value} / ${viewModel.saveMonth.value} / ${viewModel.saveDay.value}" +
                    " , ${viewModel.saveHour.value} : ${viewModel.saveMinute.value}."
        })


        viewModel.status.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "${it}", Toast.LENGTH_SHORT).show()
        })

        viewModel.user.observe(viewLifecycleOwner, Observer {
            Logger.d("user.oberve ${it}")
        })

        return binding.root
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.saveDay.value = dayOfMonth
        viewModel.saveMonth.value = month
        viewModel.saveYear.value = year

        getDateTimeCalendar()
        TimePickerDialog(activity, this, viewModel.hour, viewModel.minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.saveHour.value = hourOfDay
        viewModel.saveMinute.value = minute
    }

    private fun getDateTimeCalendar() {
        val calendar = Calendar.getInstance()
        viewModel.day = calendar.get(Calendar.DAY_OF_MONTH)
        viewModel.month = calendar.get(Calendar.MONTH)
        viewModel.year = calendar.get(Calendar.YEAR)
        viewModel.hour = calendar.get(Calendar.HOUR)
        viewModel.minute = calendar.get(Calendar.MINUTE)
    }

    private fun getDate() {
        getDateTimeCalendar()
        activity?.let { DatePickerDialog(it, this, viewModel.year, viewModel.month, viewModel.day).show() }
    }
}
