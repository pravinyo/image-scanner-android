package io.github.pravinyo.docscanner.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.pravinyo.docscanner.databinding.FileShareBottomSheetFragmentBinding

class ShareOptionsBottomSheetFragment: BottomSheetDialogFragment() {

    private var _binding: FileShareBottomSheetFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FileShareBottomSheetFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        binding.txtPicture.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick("Picture")
        }

        binding.txtPdf.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick("PDF")
        }
    }

    private var mListener: ItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener) {
            mListener = context
        } else {
            throw RuntimeException(
                    "$context must implement ItemClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface ItemClickListener {
        fun onItemClick(item: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): ShareOptionsBottomSheetFragment {
            val fragment = ShareOptionsBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}