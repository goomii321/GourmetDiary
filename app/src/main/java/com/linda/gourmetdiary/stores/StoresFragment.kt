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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.firebase.storage.FirebaseStorage
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.MainViewModel
import com.linda.gourmetdiary.NavigationDirections
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.StoresFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory
import java.io.ByteArrayOutputStream
import java.util.*


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
                if (it.isEmpty()){
                    binding.noStoreText.visibility = View.VISIBLE
                }
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

        return binding.root
    }
}
