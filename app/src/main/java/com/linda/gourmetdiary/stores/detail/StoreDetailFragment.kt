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
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.DetailStoreFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory

import kotlinx.android.synthetic.main.detail_store_fragment.*

class StoreDetailFragment : Fragment() {

    val viewModel by viewModels<StoreDetailViewModel> { getVmFactory(StoreDetailFragmentArgs.fromBundle(requireArguments()).store) }

    companion object {
        private const val PERMISSION_REQUEST = 10
        private const val PERMISSION_CALL = 11
    }

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE)

    private lateinit var binding: DetailStoreFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailStoreFragmentBinding.inflate(inflater,container,false)

        val adapter = StoreDetailAdapter(StoreDetailAdapter.OnClickListener{
            viewModel.navigateToDiary(it)
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.foodOrderHistory.adapter = adapter

        //intent google map
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

        //intent phone call
        binding.phoneText.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(DiaryApplication.instance,Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
                requestPermissions(permissions, PERMISSION_CALL)
            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse(getString(R.string.tel_uri_text) + viewModel.store.value?.storePhone)
                startActivity(callIntent)
            }
        }

        viewModel.navigateToDiary.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToDiaryDetail(it))
                viewModel.onDiaryNavigated()
            }
        })

        setBookingText()

        return binding.root
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (ActivityCompat.checkSelfPermission(DiaryApplication.instance.applicationContext,
                    permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        var locationGps: Location? = null
        var locationNetwork: Location? = null
        // Create a Uri from an intent string. Use the result to create an Intent."google.streetview:cbll=46.414382,10.013988"
        val gmmIntentUri = Uri.parse(getString(R.string.intent_map_navigate)+ viewModel.store.value?.storeLocation)
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Make the Intent explicit by setting the Google Maps package

        val locationManager: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps :Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork: Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        mapIntent.setPackage(getString(R.string.set_map_package))

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

    private fun getClipboard(text: CharSequence){
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("location", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun setBookingText() {
        when (viewModel.store.value?.storeBooking){
            true -> binding.bookingText.text = getString(R.string.can_booking)
            false -> binding.bookingText.text = getString(R.string.cannot_booking)
            else -> binding.bookingText.text = getString(R.string.no_data)
        }
    }

}
