package com.linda.gourmetdiary.dialog

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.NavigationDirections
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.databinding.DialogMessageBinding

class MessageDialog : AppCompatDialogFragment() {

    var iconRes: Drawable? = null
    var message: String? = null
    private val messageType by lazy {
        MessageDialogArgs.fromBundle(requireArguments()).messageTypeKey
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        init()
        val binding = DialogMessageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.dialog = this


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        Handler().postDelayed({ findNavController().navigate(NavigationDirections.navigateToHome()) }, 2000)
    }

    private fun init() {
        when (messageType) {
            MessageType.ADDED_SUCCESS -> {
                iconRes = DiaryApplication.instance.getDrawable(R.drawable.ic_success)
                message = getString(R.string.addto_success)
            }
            MessageType.MESSAGE -> {
                iconRes = DiaryApplication.instance.getDrawable(R.drawable.ic_launcher_foreground)
                message = messageType.value.message
            }
            else -> {

            }
        }
    }

    enum class MessageType(val value: Message) {
        ADDED_SUCCESS(Message()),
        MESSAGE(Message())
    }

    interface IMessage {
        var message: String
    }

    class Message : IMessage {
        private var _message = ""
        override var message: String
            get() = _message
            set(value) { _message = value }
    }
}