package io.github.pravinyo.feature_filter_image.dependencyInjection

import android.graphics.Bitmap
import io.github.pravinyo.common.data.OperationState
import io.github.pravinyo.common.domain.commands.UICommand
import io.github.pravinyo.common.editor.EditorUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.opencv.core.Mat

class FakeEditorUI : EditorUI {
    override fun execute(param: UICommand) {
        TODO("Not yet implemented")
    }

    override fun save(name: String) {
        TODO("Not yet implemented")
    }

    override fun observeState(): StateFlow<OperationState> {
        return MutableStateFlow(OperationState.Success)
    }

    override fun initialize(image: Mat) {

    }

    override fun convertToBitmap(): Bitmap? {
        return null
    }

    override fun undo() {
        TODO("Not yet implemented")
    }
}