package com.linda.gourmetdiary.diarys.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.linda.gourmetdiary.databinding.DetailDiaryFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory


class DiaryDetailFragment : Fragment() {

    val viewModel by viewModels<DiaryDetailViewModel> { getVmFactory(DiaryDetailFragmentArgs.fromBundle(requireArguments()).diary) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DetailDiaryFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.recyclerDetailGallery.adapter = DiaryGalleryAdapter()

        return binding.root
    }

}
