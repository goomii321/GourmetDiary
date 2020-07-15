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
import android.widget.DatePicker
import android.widget.SeekBar
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.AddDiaryFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.util.Logger
import com.linda.gourmetdiary.util.TimeConverters
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

    lateinit var binding: AddDiaryFragmentBinding

    companion object {
        private val PERMISSION_REQUEST = 10
        private val REQUEST_CODE = 100
        private val PERMISSION_CODE = 1001
    }

    var saveUri: Uri? = null
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

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
            Log.i("eatingTimeCheck", "time is = $test ")
            viewModel.user.value?.diarys?.eatingTime = test
        })

        //set images
        viewModel.images.observe(viewLifecycleOwner, Observer {
            Log.i("images", "images = $it")
            it?.let {
                (binding.addDiaryRecycler.adapter as AddDiaryAdapter).submitList(it)
                (binding.addDiaryRecycler.adapter as AddDiaryAdapter).notifyDataSetChanged()
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "${it}", Toast.LENGTH_SHORT).show()
            if (it == LoadApiStatus.DONE) {
                Handler().postDelayed({
                    findNavController().navigate(R.id.navigate_to_home)
                }, 1000)
            }
        })

        //connect google map
        binding.storeLocation.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission(permissions)) {
                    getLocation()
                } else {
                    requestPermissions(permissions, PERMISSION_REQUEST)
                }
            } else {
                getLocation()
            }
        }

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
            PERMISSION_REQUEST -> {
                var allSuccess = true
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        allSuccess = false
                        val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                        if (requestAgain) {
                            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                        }
                    }
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
                        viewModel.user.value?.diarys?.mainImage = image.value
                        viewModel.user.value?.diarys?.images =
                            listOf(listOf(image.value).toString())
                        firstPhoto = false
                    } else {
                        viewModel.user.value?.diarys?.images =
                            listOf(listOf(image.value).toString())
                    }
                    Logger.d("viewModel mainImage = ${viewModel.user.value?.diarys?.mainImage}; images = ${viewModel.user.value?.diarys?.images}")

                    viewModel.images.value?.add(it.toString())
                    viewModel.images.value = viewModel.images.value
                }
            }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (ActivityCompat.checkSelfPermission(DiaryApplication.instance.applicationContext,permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        var locationGps: Location? = null
        var locationNetwork: Location? = null
        // Create a Uri from an intent string. Use the result to create an Intent."google.streetview:cbll=46.414382,10.013988"
        val gmmIntentUri = Uri.parse("geo:0,0?")
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Make the Intent explicit by setting the Google Maps package

        var locationManager: LocationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        var hasGps :Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var hasNetwork: Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (hasGps && hasNetwork) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F,
                object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            Log.d(
                                "locationGps",
                                "locationGps = ${locationGps?.latitude} ; ${locationGps?.longitude}"
                            )
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

            val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null){
                locationGps = localGpsLocation
            }
            startActivity(mapIntent)
        } else {
            Toast.makeText(context, "未開啟定位或網路功能", Toast.LENGTH_LONG).show()
        }
    }
}
