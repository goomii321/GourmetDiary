package com.linda.gourmetdiary.usermanager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.linda.gourmetdiary.LoginActivity
import com.linda.gourmetdiary.MainActivity
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.LogOutDialogBinding
import com.linda.gourmetdiary.ext.getVmFactory

class LogOutDialog : DialogFragment() {

    private val viewModel by viewModels<LogOutViewModel> { getVmFactory() }

    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        val binding = LogOutDialogBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            dismiss()
            viewModel.onLeft()
        })
        binding.noBtn.setOnClickListener {
            dismiss()
        }
        binding.yesBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context,LoginActivity::class.java))
            MainActivity().finish()
        }
        return binding.root
    }
}