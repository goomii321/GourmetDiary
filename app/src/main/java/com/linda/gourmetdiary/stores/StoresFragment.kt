package com.linda.gourmetdiary.stores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.MainViewModel
import com.linda.gourmetdiary.NavigationDirections
import com.linda.gourmetdiary.ext.getVmFactory

import com.linda.gourmetdiary.databinding.StoresFragmentBinding
import com.linda.gourmetdiary.util.Logger

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
                Logger.d("navigate $it")
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
