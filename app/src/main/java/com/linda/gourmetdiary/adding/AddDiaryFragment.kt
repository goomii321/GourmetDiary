package com.linda.gourmetdiary.adding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import app.appworks.school.stylish.ext.getVmFactory
import com.linda.gourmetdiary.databinding.AddDiaryFragmentBinding


class AddDiaryFragment : Fragment() {

    val viewModel by viewModels<AddDiaryViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = AddDiaryFragmentBinding.inflate(inflater,container,false)

        return binding.root
    }

}
