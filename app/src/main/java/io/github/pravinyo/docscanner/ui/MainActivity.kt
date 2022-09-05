package io.github.pravinyo.docscanner.ui

import androidx.fragment.app.Fragment
import io.github.pravinyo.docscanner.R
import io.github.pravinyo.docscanner.common.activities.BaseActivity
import io.github.pravinyo.feature_page_preview.data.FileExportType

class MainActivity : BaseActivity(), ShareOptionsBottomSheetFragment.ItemClickListener {
    override val layoutResId: Int = R.layout.activity_main

    private var shareClickListener: ShareClickListener? = null

    override fun onItemClick(item: String) {
        if(item == "Picture"){
            shareClickListener?.onShareItemClick(FileExportType.FILE_AS_PICTURE)
        }else if (item == "PDF"){
            shareClickListener?.onShareItemClick(FileExportType.FILE_AS_PDF)
        }
    }

    interface ShareClickListener {
        fun onShareItemClick(type: FileExportType)
    }

    fun registerShareListener(context: Fragment) {
        if (context is ShareClickListener) {
            shareClickListener = context
        } else {
            throw RuntimeException(
                    "$context must implement ShareClickListener"
            )
        }
    }

    fun unRegisterShareListener() {
        shareClickListener = null
    }
}