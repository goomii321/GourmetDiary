package com.linda.gourmetdiary.adding

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.storage.FirebaseStorage
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.AddDiaryFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import com.linda.gourmetdiary.network.LoadApiStatus
import com.linda.gourmetdiary.util.*
import kotlinx.android.synthetic.main.add_diary_fragment.*
import java.io.ByteArrayOutputStream
import java.util.*


class AddDiaryFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    val viewModel by viewModels<AddDiaryViewModel> { getVmFactory() }

    lateinit var binding: AddDiaryFragmentBinding

    companion object {
        private const val REQUEST_CODE = 100
        private const val PERMISSION_CODE = 1001
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

        binding.testImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(DiaryApplication.instance.applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
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

        //choose date and time
        binding.eatingTime.setOnClickListener {
            getDate()
        }

        viewModel.nowTime.observe(viewLifecycleOwner, Observer {
            Logger.d("viewModel.nowTime.observe, it=$it")
            it?.let {
                binding.timeShow.text = it
            }
        })

        //set images
        viewModel.images.observe(viewLifecycleOwner, Observer {
            it?.let {
                (binding.addDiaryRecycler.adapter as AddDiaryAdapter).submitList(it)
                (binding.addDiaryRecycler.adapter as AddDiaryAdapter).notifyDataSetChanged()
                Logger.d("adapter images = $it")
            }
        })

        //navigate to home after post
        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == LoadApiStatus.DONE) {
                Handler().postDelayed({
                    findNavController().navigate(R.id.navigate_to_home)
                }, 1000)
            }
        })

        setSearchGoogle()
        setSpinner()
        setCheckbox()
        setSlideBar()

        //check post
        viewModel.invalidCheckout.observe(viewLifecycleOwner, Observer {
            checkAddData(it)
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
        viewModel.hour = calendar.get(Calendar.HOUR_OF_DAY)
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

    private fun checkAddData(value:Int){
        when(value){
            -1 -> Toast.makeText(context,getString(R.string.enter_food_name),Toast.LENGTH_SHORT).show()
            -2 -> Toast.makeText(context,getString(R.string.enter_food_time),Toast.LENGTH_SHORT).show()
            -3 -> Toast.makeText(context,getString(R.string.enter_store_name),Toast.LENGTH_SHORT).show()
            -4 -> Toast.makeText(context,getString(R.string.upload_img_msg),Toast.LENGTH_SHORT).show()
            -5 -> Toast.makeText(context,getString(R.string.upload_img_error),Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCheckbox() {
        val btnCheckedListener = CompoundButton.OnCheckedChangeListener{buttonView, isChecked ->
            when(buttonView.id){
                R.id.checkBox1 -> {
                    viewModel.diary.value?.store?.storeBooking = true
                    checkBox2.isChecked = false
                }
                R.id.checkBox2 ->{
                    viewModel.diary.value?.store?.storeBooking = false
                    checkBox1.isChecked = false
                }
            }
        }
        binding.checkBox1.setOnCheckedChangeListener(btnCheckedListener)
        binding.checkBox2.setOnCheckedChangeListener(btnCheckedListener)
    }

    private fun setSearchGoogle() {
        // set Address search
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.GoogleMapKey))
        }
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.store_location) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
        autocompleteFragment.setHint(getString(R.string.search_google))
        autocompleteFragment.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)?.textSize = 16.0f
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
//                Log.d("autocompleteFragment","$place")
                viewModel.diary.value?.store?.storeLocation = place.address
                viewModel.diary.value?.store?.storeName = place.name
                viewModel.diary.value?.store?.storeLocationId = place.id
                binding.storeNameInput.setText(place.name)
                binding.storeLocationInput.setText(place.address)
                getPlacePhoto("${place.id}")
            }

            override fun onError(status: Status) {
//                Log.d("autocompleteFragment", " status = ${status.statusMessage}")
            }
        })

        viewModel.storeStatus.observe(viewLifecycleOwner, Observer {
            if (it){
                autocompleteFragment.view?.visibility = View.VISIBLE
            } else {
                autocompleteFragment.view?.visibility = View.GONE
            }
        })
    }

    private fun setSpinner() {
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
                    0 -> viewModel.diary.value?.type = type[position]
                    1 -> viewModel.diary.value?.type = type[position]
                    2 -> viewModel.diary.value?.type = type[position]
                    else -> viewModel.diary.value?.type = type[position]
                }
            }
        }
    }

    private fun setSlideBar() {
        var ratingScore = viewModel.foodRating.value
        var healthyScore = viewModel.healthyScore.value

        binding.addRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ratingScore = progress
                binding.ratingText.text = "$ratingScore"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.diary.value?.food?.foodRate = ratingScore
            }
        })

        binding.addHealthy.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                healthyScore = progress
                binding.healthyText.text = "$healthyScore"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.diary.value?.food?.healthyScore = healthyScore
            }
        })
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
            saveUri?.let {
                viewModel.uploadImage(it)
                Logger.i("saveUri = $saveUri")
            }
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(context, getString(R.string.no_permission), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getPlacePhoto(placeId : String){
        viewModel.updateImageStatus.value = false

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.GoogleMapKey))
        }

        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        val fields = listOf(Place.Field.PHOTO_METADATAS)

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)
        var placesClient : PlacesClient = Places.createClient(DiaryApplication.instance)

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                // Get the photo metadata.
                val metada = place.photoMetadatas
                if (metada == null || metada.isEmpty()) {
//                    Log.w("placesClient", "No photo metadata.")
                    return@addOnSuccessListener
                }
                val photoMetadata = metada.first()
//                Log.d("photoMetadata","photoMetadata = ${metada}")

                // Get the attribution text.
                val attributions = photoMetadata?.attributions

                // Create a FetchPhotoRequest.
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(400) // Optional.
                    .build()
                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        convert2Uri(bitmap)

//                        binding.imageView4.setImageBitmap(bitmap)
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e("Placeexception", "Place not found: " + exception.message)
                            val statusCode = exception.statusCode
                        }
                    }
            }
    }

    private fun convert2Uri(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val filename = UUID.randomUUID().toString()
        val image = MutableLiveData<String>()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos)
        val data = baos.toByteArray()

        ref.putBytes(data)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    image.value = it.toString()
                    viewModel.diary.value?.store?.storeImage = it.toString()
//                    Log.d("convert2Uri","convert2Uri ${image.value}")
                    viewModel.updateImageStatus.value = true
                }
            }
            .addOnFailureListener{
//                Log.d("convert2Uri","convert2Uri ${it.message}")
                viewModel.updateImageStatus.value = false
            }
    }

}
