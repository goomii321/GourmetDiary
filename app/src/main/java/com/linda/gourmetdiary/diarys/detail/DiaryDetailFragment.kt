package com.linda.gourmetdiary.diarys.detail

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
import com.linda.gourmetdiary.databinding.DetailDiaryFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import kotlinx.android.synthetic.main.detail_diary_fragment.*


class DiaryDetailFragment : Fragment() {

    val viewModel by viewModels<DiaryDetailViewModel> { getVmFactory(DiaryDetailFragmentArgs.fromBundle(requireArguments()).diary) }

    companion object {
        private val PERMISSION_REQUEST = 10
    }

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DetailDiaryFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.recyclerDetailGallery.adapter = DiaryGalleryAdapter()

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerDetailGallery)

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

        //set booking text
        when (viewModel.diary.value?.store?.storeBooking){
            true -> binding.bookingText.text = "可訂位"
            false -> binding.bookingText.text = "不可訂位"
            else -> binding.bookingText.text = "無資料"
        }

        binding.locationText.setOnLongClickListener {
            getClipboard(location_text.text.toString())
            Toast.makeText(context,"複製到剪貼簿", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        if (viewModel.diary.value?.store?.storeMinOrder == "無"){
            binding.minOrderDollarText.visibility = View.GONE
        }

        return binding.root
    }

    fun getClipboard(text: CharSequence){
        var clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clip = ClipData.newPlainText("location", text)
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
