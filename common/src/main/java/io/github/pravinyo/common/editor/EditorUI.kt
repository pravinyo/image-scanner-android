package io.github.pravinyo.common.editor

import android.graphics.Bitmap
import io.github.pravinyo.common.data.OperationState
import kotlinx.coroutines.flow.StateFlow
import org.opencv.core.Mat


interface EditorUI {
    infix fun execute(param: UICommand)
    infix fun save(name:String)
    fun observeState(): StateFlow<OperationState>
    fun initialize(image: Mat)
    suspend fun convertToBitmap(): Bitmap?
    fun undo()
}