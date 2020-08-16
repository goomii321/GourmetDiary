package com.linda.gourmetdiary.diaries.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.DetailDiaryFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.util.NONE
import kotlinx.android.synthetic.main.detail_diary_fragment.*

class DiaryDetailFragment : Fragment() {

    val viewModel by viewModels<DiaryDetailViewModel> { getVmFactory(DiaryDetailFragmentArgs.fromBundle(requireArguments()).diary) }

    lateinit var binding: DetailDiaryFragmentBinding

    companion object {
        private const val PERMISSION_REQUEST = 10
        private const val PERMISSION_CALL = 11
    }

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailDiaryFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.recyclerDetailGallery.adapter = DiaryGalleryAdapter(DiaryGalleryAdapter.OnClickListener{
            binding.enlargeView.visibility = View.VISIBLE
            binding.enlargeImage.visibility = View.VISIBLE
            viewModel.enlargeImage.value = it
        })

        binding.enlargeView.setOnClickListener {
            it.visibility = View.GONE
            binding.enlargeImage.visibility = View.GONE
        }

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerDetailGallery)

        setBookingText()
        setClipboard()

        //connect google map
        binding.locationText.setOnClickListener {
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

        //add phone call
        binding.phoneText.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(DiaryApplication.instance,Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
                requestPermissions(permissions, PERMISSION_CALL)
            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse(getString(R.string.tel_uri_text) + viewModel.diary.value?.store?.storePhone)
                startActivity(callIntent)
            }
        }

        return binding.root
    }

    private fun setClipboard() {
        binding.locationText.setOnLongClickListener {
            getClipboard(location_text.text.toString())
            Toast.makeText(context,getString(R.string.clipboard_text), Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        binding.phoneText.setOnLongClickListener {
            getClipboard(phone_text.text.toString())
            Toast.makeText(context,getString(R.string.clipboard_text), Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    private fun setBookingText() {
        when (viewModel.diary.value?.store?.storeBooking){
            true -> binding.bookingText.setText(R.string.can_booking)
            false -> binding.bookingText.setText(R.string.cannot_booking)
            else -> binding.bookingText.setText(R.string.no_data)
        }
    }

    private fun getClipboard(text: CharSequence){
    val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("location", text)
    clipboard.setPrimaryClip(clip)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST -> {
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                shouldShowRequestPermissionRationale(permissions[i])
                        if (requestAgain) {
                            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            PERMISSION_CALL -> {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (ActivityCompat.checkSelfPermission(DiaryApplication.instance.applicationContext,permissionArray[i])
                == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        var locationGps: Location? = null
        var locationNetwork: Location? = null
        // Create a Uri from an intent string. Use the result to create an Intent."google.streetview:cbll=46.414382,10.013988"
        val gmmIntentUri = Uri.parse("google.navigation:q="+ viewModel.diary.value?.store?.storeLocation)
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Make the Intent explicit by setting the Google Maps package

        var locationManager: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var hasGps :Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var hasNetwork: Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (hasGps && hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 0F,
                object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
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
            Toast.makeText(context, getString(R.string.no_internet_no_gps), Toast.LENGTH_LONG).show()
        }
    }
}

