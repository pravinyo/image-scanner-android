package io.github.pravinyo.docscanner.common.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import io.github.pravinyo.docscanner.R

class ErrorDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).let {
            it.setTitle(R.string.error_dialog_title)
            it.setMessage(R.string.error_dialog_message)
            it.setPositiveButton(R.string.error_dialog_button_caption) { _, _ -> dismiss() }
            it.create()
        }
    }

    companion object {
        fun newInstance(): ErrorDialogFragment {
            return ErrorDialogFragment()
        }
    }
}