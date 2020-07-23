package com.linda.gourmetdiary.template

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.linda.gourmetdiary.DiaryApplication

import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.databinding.TemplateFragmentBinding
import com.linda.gourmetdiary.ext.getVmFactory

class TemplateFragment : Fragment() {

    val viewModel by viewModels<TemplateViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = TemplateFragmentBinding.inflate(inflater,container,false)
        val autoAdapter = SearchAdapter()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.searchRecycler.adapter = autoAdapter

        binding.searchDiary.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText: String = binding.searchDiary.text.toString()
                viewModel.searchTemplate(searchText)
            }
        })

        //set EditText input


        viewModel.diary.observe(viewLifecycleOwner, Observer {
            Log.d("search","search $it")
            autoAdapter?.notifyDataSetChanged()
        })

        return binding.root
    }

}
