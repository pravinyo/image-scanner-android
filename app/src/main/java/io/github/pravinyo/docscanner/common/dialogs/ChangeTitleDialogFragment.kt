package io.github.pravinyo.docscanner.common.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class ChangeTitleDialogFragment(listener: TitleChangeListener) : DialogFragment() {
    private var mListener: TitleChangeListener? = listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val input = TextInputEditText(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16,0,16,0)
        input.layoutParams = layoutParams
        input.hint = "page title"

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Title")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString()
                mListener?.onTitleChange(name)
                unRegisterListener()
            }
            .setNegativeButton("Cancel") { _, _ ->
                unRegisterListener()
            }.create()
    }

    interface TitleChangeListener{
        fun onTitleChange(title: String)
    }

    private fun unRegisterListener(){
        mListener = null
    }

    companion object {
        fun newInstance(listener: TitleChangeListener): ChangeTitleDialogFragment {
            return ChangeTitleDialogFragment(listener)
        }
    }
}