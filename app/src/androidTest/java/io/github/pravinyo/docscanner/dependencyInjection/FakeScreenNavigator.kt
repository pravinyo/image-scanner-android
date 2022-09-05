package io.github.pravinyo.docscanner.dependencyInjection

import io.github.pravinyo.docscanner.common.ScreensNavigator

class FakeScreenNavigator: ScreensNavigator {
    override fun navigateBack() {
    }

    override fun navigateToEditPage(fileId: String){
    }
}