package com.linda.gourmetdiary.template

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.TemplateFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.util.TimeConverters
import java.util.*

class TemplateFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    val viewModel by viewModels<TemplateViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = TemplateFragmentBinding.inflate(inflater,container,false)
        val autoAdapter = SearchAdapter(SearchAdapter.OnClickListener{
            Log.d("SearchAdapter","click item data is : $it")

            //set EditText input
            binding.temFoodNameText.setText("${it.food?.foodName}")
            binding.temFoodComboText.setText("${it.food?.foodCombo}")
            binding.temFoodPriceText.setText("${it.food?.price}")
            binding.temRatingText.setText("${it.food?.foodRate}")
            binding.temHealthyText.setText("${it.food?.healthyScore}")

            binding.temStoreNameText.setText("${it.store?.storeName}")
            binding.temStoreBranchText.setText("${it.store?.storeBranch}")
            binding.temStorePhoneText.setText("${it.store?.storePhone}")

            when (it.store?.storeBooking) {
                true -> binding.temStoreBookingText.setText(R.string.can_booking)
                false -> binding.temStoreBookingText.setText(R.string.cannot_booking)
                else -> binding.temStoreBookingText.setText(R.string.no_data)
            }

            binding.temStoreMinOrderText.setText("${it.store?.storeMinOrder}")
            binding.temStoreOpenTimeText.setText("${it.store?.storeOpenTime}")
            binding.temContentText.setText("${it.food?.foodContent}")
            binding.temNextTimeMemoText.setText("${it.food?.nextTimeRemind}")

            when(it.type) {
                "早餐" -> binding.foodType.setSelection(0)
                "午餐" -> binding.foodType.setSelection(1)
                "晚餐" -> binding.foodType.setSelection(2)
                else -> binding.foodType.setSelection(3)
            }

            viewModel.editDiary.value?.mainImage = it.mainImage
            viewModel.editDiary.value?.images = it.images

            viewModel.editDiary.observe(viewLifecycleOwner, Observer {
                viewModel.recyclerViewStatus.value=false
            })
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.searchRecycler.adapter = autoAdapter

        //choose date and time
        binding.temEatingTime.setOnClickListener {
            getDate()
        }

        viewModel.saveMinute.observe(viewLifecycleOwner, Observer {
            var nowTime =
                "${viewModel.saveYear.value}/${viewModel.saveMonth.value}/${viewModel.saveDay.value} " +
                        "${viewModel.saveHour.value}:${viewModel.saveMinute.value}"
            binding.temEatingTimeText.setText(nowTime)
            var test = TimeConverters.timeToTimestamp(nowTime, Locale.TAIWAN)
//            Log.i("eatingTimeCheck", "time is = $test ")
            viewModel.editDiary.value?.eatingTime = test
        })

        //Search EditText setting
        binding.searchDiary.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.recyclerViewStatus.value = true
                val searchText: String = binding.searchDiary.text.toString()
                if (searchText != null || searchText != ""){
                    viewModel.searchTemplate(searchText)
                }
            }
        })

        //set Spinner
        val type = arrayListOf("早餐","午餐","晚餐","點心/宵夜")
        val spinnerAdapter = ArrayAdapter(DiaryApplication.instance,android.R.layout.simple_spinner_dropdown_item, type)
        binding.foodType.adapter = spinnerAdapter

        binding.foodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> viewModel.editDiary.value?.type = type[position]
                    1 -> viewModel.editDiary.value?.type = type[position]
                    2 -> viewModel.editDiary.value?.type = type[position]
                    else -> viewModel.editDiary.value?.type = type[position]
                }
            }

        }

        viewModel.diary.observe(viewLifecycleOwner, Observer {
            Log.d("search","search $it")
            autoAdapter?.notifyDataSetChanged()
        })

        viewModel.invalidCheckout.observe(viewLifecycleOwner, Observer {
            when(it){
                -1 -> Toast.makeText(context,"請輸入餐點名稱", Toast.LENGTH_SHORT).show()
                -2 -> Toast.makeText(context,"請輸入用餐時間", Toast.LENGTH_SHORT).show()
                -3 -> Toast.makeText(context,"請輸入餐廳名稱", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.navigate2Home.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Handler().postDelayed({
                    findNavController().navigate(R.id.navigate_to_home)
                }, 1000)
            }
        })

        return binding.root
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.saveDay.value = dayOfMonth
        viewModel.saveMonth.value = month + 1
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
