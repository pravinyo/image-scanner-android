package io.github.pravinyo.docscanner.common

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import io.github.pravinyo.docscanner.R


class ScreensNavigatorImpl constructor(
    private val activity: AppCompatActivity
) : ScreensNavigator {

    override fun navigateBack() {
        activity.onBackPressed()
    }

    override fun navigateToEditPage(fileId: String) {
        val bundle = bundleOf("fileId" to fileId)
        activity.findNavController(R.id.navHostFragment)
            .navigate(R.id.action_pageFragment_to_filterFragment, bundle)
    }
}