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
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.databinding.TemplateFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.util.*
import java.util.*

class TemplateFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    val viewModel by viewModels<TemplateViewModel> { getVmFactory() }
    lateinit var binding: TemplateFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TemplateFragmentBinding.inflate(inflater,container,false)

        val autoAdapter = SearchAdapter(SearchAdapter.OnClickListener{ chooseDiary ->

            setEditText(chooseDiary)

            viewModel.editDiary.observe(viewLifecycleOwner, Observer { diary ->
                diary?.let {
                    it.mainImage = chooseDiary.mainImage
                    it.images = chooseDiary.images
                    it.store?.storeLocationId = chooseDiary.store?.storeLocationId
                    it.store?.storeLocation = chooseDiary.store?.storeLocation
                    viewModel.recyclerViewStatus.value = false
                }
            })
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.searchRecycler.adapter = autoAdapter

        //choose date and time
        binding.temEatingTime.setOnClickListener {
            getDate()
        }

        viewModel.nowTime.observe(viewLifecycleOwner, Observer {
            Logger.d("viewModel.nowTime.observe, it=$it")
            it?.let {
                binding.temEatingTimeText.setText(it)
            }
        })

        //Search EditText setting
        binding.searchDiaryEdit.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.recyclerViewStatus.value = true
                val searchText: String = binding.searchDiaryEdit.text.toString()
                if (searchText != ""){
                    viewModel.searchTemplate(searchText)
                }
            }
        })

        //set Spinner
        val type = arrayListOf(BREAKFAST, LUNCH, DINNER, DESSERT)
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

        viewModel.diary.observe(viewLifecycleOwner, Observer { diaryList ->
            diaryList?.let {
                autoAdapter.notifyDataSetChanged()
            }
        })

        viewModel.invalidCheckout.observe(viewLifecycleOwner, Observer {checkValue ->
            checkValue?.let {
                when(it){
                    -1 -> Toast.makeText(context,getString(R.string.enter_food_name), Toast.LENGTH_SHORT).show()
                    -2 -> Toast.makeText(context,getString(R.string.enter_food_time), Toast.LENGTH_SHORT).show()
                    -3 -> Toast.makeText(context,getString(R.string.enter_store_name), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.navigate2Home.observe(viewLifecycleOwner, Observer {navigateValue ->
            navigateValue?.let {
                if (it) {
                    Handler().postDelayed({
                        findNavController().navigate(R.id.navigate_to_home)
                    }, 1000)
                }
            }
        })

        return binding.root
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.saveDay.value = dayOfMonth
        viewModel.saveMonth.value = month + 1
        viewModel.saveYear.value = year

        getDateTimeCalendar()
        TimePickerDialog(activity, this, viewModel.calendarHour, viewModel.calendarMinute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.saveHour.value = hourOfDay
        viewModel.saveMinute.value = minute
    }

    private fun getDateTimeCalendar() {
        val calendar = Calendar.getInstance()
        viewModel.calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
        viewModel.calendarMonth = calendar.get(Calendar.MONTH)
        viewModel.calendarYear = calendar.get(Calendar.YEAR)
        viewModel.calendarHour = calendar.get(Calendar.HOUR)
        viewModel.calendarMinute = calendar.get(Calendar.MINUTE)
    }

    private fun getDate() {
        getDateTimeCalendar()
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

    private fun setEditText (diary: Diary) {
        diary.let {
            binding.temFoodNameText.setText("${it.food?.foodName}")
            binding.temFoodComboText.setText("${it.food?.foodCombo}")
            binding.temFoodPriceText.setText("${it.food?.price}")
            binding.temRatingText.setText("${it.food?.foodRate}")
            binding.temHealthyText.setText("${it.food?.healthyScore}")
            binding.temStoreNameText.setText("${it.store?.storeName}")
            binding.temStoreBranchText.setText("${it.store?.storeBranch}")
            binding.temStorePhoneText.setText("${it.store?.storePhone}")
            binding.temStoreMinOrderText.setText("${it.store?.storeMinOrder}")
            binding.temStoreOpenTimeText.setText("${it.store?.storeOpenTime}")
            binding.temContentText.setText("${it.food?.foodContent}")
            binding.temNextTimeMemoText.setText("${it.food?.nextTimeRemind}")

            when (it.store?.storeBooking) {
                true -> binding.temStoreBookingText.setText(R.string.can_booking)
                false -> binding.temStoreBookingText.setText(R.string.cannot_booking)
                else -> binding.temStoreBookingText.setText(R.string.no_data)
            }

            when(it.type) {
                BREAKFAST -> binding.foodType.setSelection(0)
                LUNCH -> binding.foodType.setSelection(1)
                DINNER -> binding.foodType.setSelection(2)
                else -> binding.foodType.setSelection(3)
            }
        }
    }
}
