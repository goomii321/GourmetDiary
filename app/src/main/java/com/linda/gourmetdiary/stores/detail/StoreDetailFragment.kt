package com.linda.gourmetdiary.stores.detail

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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.NavigationDirections
import com.linda.gourmetdiary.databinding.DetailDiaryFragmentBinding
import com.linda.gourmetdiary.databinding.DetailStoreFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory

import com.linda.gourmetdiary.databinding.StoresFragmentBinding
import com.linda.gourmetdiary.diarys.detail.DiaryDetailFragment
import kotlinx.android.synthetic.main.detail_store_fragment.*

class StoreDetailFragment : Fragment() {

    val viewModel by viewModels<StoreDetailViewModel> { getVmFactory(StoreDetailFragmentArgs.fromBundle(requireArguments()).store) }

    companion object {
        private val PERMISSION_REQUEST2 = 10
        private val PERMISSION_CALL = 11
    }

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DetailStoreFragmentBinding.inflate(inflater,container,false)
        val adapter = StoreDetailAdapter(StoreDetailAdapter.OnClickListener{
            viewModel.navigateToDiary(it)
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.foodOrderHistory.adapter = adapter

        //connect google map
        binding.locationText.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission(permissions)) {
                    getLocation()
                } else {
                    requestPermissions(permissions, PERMISSION_REQUEST2)
                }
            } else {
                getLocation()
            }
        }

        binding.locationText.setOnLongClickListener {
            getClipboard(location_text.text.toString())
            Toast.makeText(context,"複製到剪貼簿", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        if (viewModel.store?.value?.storeMinOrder == "無" || viewModel.store?.value?.storeMinOrder == ""){
            binding.dollarMin.visibility = View.GONE
        }

        viewModel.navigateToDiary.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToDiaryDetail(it))
                viewModel.onDiaryNavigated()
            }
        })

        when (viewModel.store.value?.storeBooking){
            true -> binding.bookingText.text = "可訂位"
            false -> binding.bookingText.text = "不可訂位"
            else -> binding.bookingText.text = "無資料"
        }

        //add phone call
        binding.phoneText.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(DiaryApplication.instance,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(permissions, PERMISSION_CALL)
            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + viewModel.store.value?.storePhone)
                startActivity(callIntent)
            }
        }

        return binding.root
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
        val gmmIntentUri = Uri.parse("google.navigation:q="+ viewModel.store.value?.storeLocation)
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

    fun getClipboard(text: CharSequence){
        var clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clip = ClipData.newPlainText("location", text)
        clipboard.setPrimaryClip(clip)
    }

}
