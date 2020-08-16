package com.linda.gourmetdiary.diaries

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
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
import com.linda.gourmetdiary.databinding.DiariesFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.util.TimeConverters
import java.util.*

class DiariesFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    val viewModel by viewModels<DiariesViewModel> { getVmFactory() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DiariesFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.isLiveDataDesign = DiaryApplication.instance.isLiveDataDesign()
        binding.viewModel = viewModel

        binding.recyclerDiary.adapter = DiariesAdapter(viewModel, DiariesAdapter.OnClickListener{
            getDate()
        })

        viewModel.saveYear.observe(viewLifecycleOwner, Observer {
            var nowTime =
                "${viewModel.saveYear.value}-${viewModel.saveMonth.value}-${viewModel.saveDay.value} "
            var checkEndDay = TimeConverters.dateToTimestamp(nowTime, Locale.TAIWAN) + 86391428
            var checkStartDay = checkEndDay - 518348572
//            Log.i("eatingTimeCheck", "choose time is $nowTime and timestamp is = $checkEndDay and start day is ${TimeConverters.timestampToDate(checkStartDay,
//                Locale.TAIWAN)}")
            viewModel.getUsersResult(checkStartDay,checkEndDay)
        })

        viewModel.diary.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.assignData(it)
                DiariesAdapter(viewModel, DiariesAdapter.OnClickListener {}).notifyDataSetChanged()
                if(it.isEmpty()){
                    binding.noDiaryText.visibility = View.VISIBLE
                }
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

    private fun getDateCalendar() {
        val calendar = Calendar.getInstance()
        viewModel.calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
        viewModel.calendarMonth = calendar.get(Calendar.MONTH)
        viewModel.calendarYear = calendar.get(Calendar.YEAR)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.saveDay.value = dayOfMonth
        viewModel.saveMonth.value = month + 1
        viewModel.saveYear.value = year

        getDateCalendar()
    }

    private fun getDate() {
        getDateCalendar()
        activity?.let {
            DatePickerDialog(
                it,
                this,
                viewModel.calendarYear,
                viewModel.calendarMonth,
                viewModel.calendarDay
            ).show()
        }
    }
}
