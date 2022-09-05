package io.github.pravinyo.docscanner.common.dialogs

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import io.github.pravinyo.docscanner.ui.ShareOptionsBottomSheetFragment

class DialogsNavigator constructor(private val fragmentManager: FragmentManager) {

    fun showErrorDialog() {
        fragmentManager.beginTransaction()
                .add(ErrorDialogFragment.newInstance(), null)
                .commitAllowingStateLoss()
    }

    fun showFileExportOptionsDialog() {
        fragmentManager.let {
            ShareOptionsBottomSheetFragment.newInstance(Bundle()).apply {
                show(it, tag)
            }
        }
    }

    fun showTitleEditDialog(listener: ChangeTitleDialogFragment.TitleChangeListener){
        fragmentManager.beginTransaction()
            .add(ChangeTitleDialogFragment.newInstance(listener), null)
            .commitAllowingStateLoss()
    }
}