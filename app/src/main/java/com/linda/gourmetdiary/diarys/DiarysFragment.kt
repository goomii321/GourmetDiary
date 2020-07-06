package com.linda.gourmetdiary.diarys

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.linda.gourmetdiary.ext.getVmFactory

import com.linda.gourmetdiary.databinding.DiarysFragmentBinding
import com.linda.gourmetdiary.util.Logger

class DiarysFragment : Fragment() {

    val viewModel by viewModels<DiarysViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DiarysFragmentBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.recyclerDiary.adapter = DiarysAdapter(DiarysAdapter.OnClickListener{
            Logger.d("click, it = $it")
        })

        viewModel.diary.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.i("observe diary list = $it")
            }
        })

        return binding.root
    }

}
