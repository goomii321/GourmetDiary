package com.linda.gourmetdiary.diarys

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.MainViewModel
import com.linda.gourmetdiary.NavigationDirections
import com.linda.gourmetdiary.data.Diarys4Day
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.databinding.DiarysFragmentBinding
import kotlinx.android.synthetic.main.detail_diary_fragment.*
import java.util.*

class DiarysFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    val viewModel by viewModels<DiarysViewModel> { getVmFactory() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DiarysFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.isLiveDataDesign = DiaryApplication.instance.isLiveDataDesign()
        binding.viewModel = viewModel

        binding.recyclerDiary.adapter = DiarysAdapter(viewModel, DiarysAdapter.OnClickListener{
            getDate()
        })

        viewModel.diary.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.assignData(it)
                viewModel.onDataAssigned()
            }
        })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToDiaryDetail(it))
                viewModel.onDetailNavigated()
            }
        })

        binding.layoutSwipeRefreshDiary.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.refreshStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.layoutSwipeRefreshDiary.isRefreshing = it
            }
        })

        viewModel.liveDiary.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.viewModel = viewModel
            }
        })

        ViewModelProvider(requireActivity()).get(MainViewModel::class.java).apply {
            refresh.observe(viewLifecycleOwner, Observer {
                it?.let {
                    viewModel.refresh()
                    onRefreshed()
                }
            })
        }

        return binding.root
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.saveDay.value = dayOfMonth
        viewModel.saveMonth.value = month + 1
        viewModel.saveYear.value = year

        getDateTimeCalendar()
    }

    private fun getDateTimeCalendar() {
        val calendar = Calendar.getInstance()
        viewModel.day = calendar.get(Calendar.DAY_OF_MONTH)
        viewModel.month = calendar.get(Calendar.MONTH)
        viewModel.year = calendar.get(Calendar.YEAR)
    }

    private fun getDate() {
        getDateTimeCalendar()
        activity?.let {
            DatePickerDialog(
                it,
                this,
                viewModel.year,
                viewModel.month,
                viewModel.day
            ).show()
        }
    }
}
