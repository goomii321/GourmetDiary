package com.linda.gourmetdiary.stores

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.MainViewModel
import com.linda.gourmetdiary.NavigationDirections
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.StoresFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import java.io.ByteArrayOutputStream


class StoresFragment : Fragment() {

    val viewModel by viewModels<StoresViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = StoresFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.isLiveDataDesign = DiaryApplication.instance.isLiveDataDesign()
        binding.viewModel = viewModel

        binding.storeListRecycler.adapter = StoresAdapter(StoresAdapter.OnClickListener{
            viewModel.navigateToDetail(it)
        })

        binding.layoutSwipeRefreshStore.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.refreshStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.layoutSwipeRefreshStore.isRefreshing = it
            }
        })

        viewModel.liveStore.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.viewModel = viewModel
            }
        })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToStoreDetail(it))
                viewModel.onDetailNavigated()
            }
        })

        ViewModelProvider(requireActivity()).get(MainViewModel::class.java).apply {
            refresh.observe(viewLifecycleOwner, Observer {
                it?.let {
//                    viewModel.refresh()
                    onRefreshed()
                }
            })
        }

//        getPlacePhoto()

        return binding.root
    }

    fun getPlacePhoto(){
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.GoogleMapKey))
        }

        // Define a Place ID.
        val placeId = "ChIJ0WB2rr6rQjQReOccQdUp0qA"

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
                    Log.w("placesClient", "No photo metadata.")
                    return@addOnSuccessListener
                }
                val photoMetadata = metada.first()
                Log.d("photoMetadata","photoMetadata = ${metada}")

                // Get the attribution text.
                val attributions = photoMetadata?.attributions

                // Create a FetchPhotoRequest.
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build()
                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        convert2Uri(bitmap)
                        Log.d("convert2Uri","convert2Uri = ${convert2Uri(bitmap)}")
//                        binding.imageView4.setImageBitmap(bitmap)
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e("Placeexception", "Place not found: " + exception.message)
                            val statusCode = exception.statusCode
                        }
                    }
            }
    }

    fun convert2Uri(bitmap: Bitmap): Uri {
        val baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos)
        val data = baos.toByteArray()

        val path: String = MediaStore.Images.Media.insertImage(
            DiaryApplication.instance.contentResolver,
            bitmap,
            "Title","tt"
        )
        
        return Uri.parse(path)
    }

}
