package io.github.pravinyo.docscanner.common.fragments

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    private lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed(callback)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }

    abstract fun onBackPressed(callback: OnBackPressedCallback)
}