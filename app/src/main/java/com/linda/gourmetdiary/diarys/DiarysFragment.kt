package com.linda.gourmetdiary.diarys

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.DiarysFragmentBinding

class DiarysFragment : Fragment() {

    private lateinit var viewModel: DiarysViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DiarysFragmentBinding.inflate(inflater,container,false)

        return binding.root
    }

}
