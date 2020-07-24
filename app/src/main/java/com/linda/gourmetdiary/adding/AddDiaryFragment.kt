package com.linda.gourmetdiary.adding

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.gms.common.api.internal.BackgroundDetector.initialize
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.storage.FirebaseStorage
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.MainActivity
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.AddDiaryFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.util.Logger
import com.linda.gourmetdiary.util.TimeConverters
import kotlinx.android.synthetic.main.add_diary_fragment.*
import java.util.*


class AddDiaryFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    val viewModel by viewModels<AddDiaryViewModel> { getVmFactory() }

    lateinit var binding: AddDiaryFragmentBinding

    companion object {
        private val REQUEST_CODE = 100
        private val PERMISSION_CODE = 1001
    }

    var saveUri: Uri? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddDiaryFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.addDiaryRecycler.adapter = AddDiaryAdapter()

        var ratingScore = viewModel.foodRating.value
        var healthyScore = viewModel.healthyScore.value

        binding.testImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(DiaryApplication.instance.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    openGallery()
                }
            } else {
                openGallery()
            }
        }

        //--- Slide bar ---
        binding.addRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ratingScore = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(context, "Score is $ratingScore .", Toast.LENGTH_SHORT).show()
                viewModel.user.value?.food?.foodRate = ratingScore
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
                viewModel.user.value?.food?.healthyScore = healthyScore
            }
        })

        //choose date and time
        binding.eatingTime.setOnClickListener {
            getDate()
        }

        viewModel.saveMinute.observe(viewLifecycleOwner, Observer {
            var nowTime =
                "${viewModel.saveYear.value}/${viewModel.saveMonth.value}/${viewModel.saveDay.value} " +
                        "${viewModel.saveHour.value}:${viewModel.saveMinute.value}"
            binding.timeShow.text = nowTime
            var test = TimeConverters.timeToTimestamp(nowTime, Locale.TAIWAN)
//            Log.i("eatingTimeCheck", "time is = $test ")
            viewModel.user.value?.eatingTime = test
        })

        //set images
        viewModel.images.observe(viewLifecycleOwner, Observer {
            Log.i("images", "images = $it")
            it?.let {
                (binding.addDiaryRecycler.adapter as AddDiaryAdapter).submitList(it)
                (binding.addDiaryRecycler.adapter as AddDiaryAdapter).notifyDataSetChanged()
            }
        })

        //navigate to home after post
        viewModel.status.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "${it}", Toast.LENGTH_SHORT).show()
            if (it == LoadApiStatus.DONE) {
                Handler().postDelayed({
                    findNavController().navigate(R.id.navigate_to_home)
                }, 1000)
            }
        })

        // set Address search
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.GoogleMapKey))
        }
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.store_location) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
        autocompleteFragment.setHint("點擊搜尋並取得地址")
        autocompleteFragment.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)?.textSize = 16.0f
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
                Log.d("autocompleteFragment","$place")
                viewModel.user.value?.store?.storeLocation = place.address
                viewModel.user.value?.store?.storeName = place.name
                binding.storeNameInput.setText(place.name)
            }

            override fun onError(status: Status) {
                Log.d("autocompleteFragment", " status = ${status.statusMessage}")
            }
        })

        viewModel.storeStatus.observe(viewLifecycleOwner, Observer {
            if (it){
                autocompleteFragment.view?.visibility = View.VISIBLE
            } else {
                autocompleteFragment.view?.visibility = View.GONE
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
                    0 -> viewModel.user.value?.type = type[position]
                    1 -> viewModel.user.value?.type = type[position]
                    2 -> viewModel.user.value?.type = type[position]
                    else -> viewModel.user.value?.type = type[position]
                }
            }

        }

        //set checkbox
        val btnCheckedListener = CompoundButton.OnCheckedChangeListener{buttonView, isChecked ->
            when(buttonView.id){
                R.id.checkBox1 -> {
                    viewModel.user.value?.store?.storeBooking = true
                    checkBox2.isChecked = false
                }
                R.id.checkBox2 ->{
                    viewModel.user.value?.store?.storeBooking = false
                    checkBox1.isChecked = false
                }
            }
            Log.i("CompoundButton","check ${viewModel.user.value?.store?.storeBooking}")
        }
        binding.checkBox1.setOnCheckedChangeListener(btnCheckedListener)
        binding.checkBox2.setOnCheckedChangeListener(btnCheckedListener)

        //check post
        viewModel.invalidCheckout.observe(viewLifecycleOwner, Observer {
          when(it){
              -1 -> Toast.makeText(context,"請輸入餐點名稱",Toast.LENGTH_SHORT).show()
              -2 -> Toast.makeText(context,"請輸入用餐時間",Toast.LENGTH_SHORT).show()
              -3 -> Toast.makeText(context,"請輸入餐廳名稱",Toast.LENGTH_SHORT).show()
              -4 -> Toast.makeText(context,"請上傳至少一張圖片",Toast.LENGTH_SHORT).show()
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            saveUri = data?.data
//            test_image.setImageURI(data?.data)
            uploadImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(context, "無權限操作", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage() {
        var firstPhoto = true
        val filename = UUID.randomUUID().toString()
        val image = MutableLiveData<String>()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(saveUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Logger.d("downloadUrl $it")
                    image.value = it.toString()
                    if (firstPhoto) {
                        viewModel.user.value?.mainImage = image.value
                        viewModel.user.value?.images = listOf(listOf(image.value).toString())
                        firstPhoto = false
                    } else {
                        viewModel.user.value?.images =
                            listOf(listOf(image.value).toString())
                    }
                    Logger.d("viewModel mainImage = ${viewModel.user.value?.mainImage}; images = ${viewModel.user.value?.images}")

                    viewModel.images.value?.add(it.toString())
                    viewModel.images.value = viewModel.images.value
                    viewModel.user.value?.images = viewModel.images.value
                }
            }
    }

}
