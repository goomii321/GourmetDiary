package com.linda.gourmetdiary.stores.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.linda.gourmetdiary.databinding.DetailDiaryFragmentBinding
import com.linda.gourmetdiary.databinding.DetailStoreFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory

import com.linda.gourmetdiary.databinding.StoresFragmentBinding

class StoreDetailFragment : Fragment() {

    val viewModel by viewModels<StoreDetailViewModel> { getVmFactory(StoreDetailFragmentArgs.fromBundle(requireArguments()).store) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DetailStoreFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        when (viewModel.store?.value?.storeBooking){
            true -> binding.bookingText.text = "可訂位"
            false -> binding.bookingText.text = "不可訂位"
            else -> binding.bookingText.text = "無資料"
        }

        return binding.root
    }

}
