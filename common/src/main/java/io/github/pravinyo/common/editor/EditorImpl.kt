package io.github.pravinyo.common.editor

import OperationType
import android.graphics.Bitmap
import commands.ImageContrastAdjustCommand
import commands.NegativeFilterCommand
import commands.RotateCommand
import commands.UnsharpMaskBoostFilterCommand
import editor.ImageEditor
import factory.ContrastEnhancementFactory
import factory.FilterFactory
import factory.TransformationFactory
import io.github.pravinyo.common.data.OperationState
import io.github.pravinyo.common.data.ProcessError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.opencv.android.Utils
import org.opencv.core.CvException
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import io.github.pravinyo.common.data.UnknownError as SUnknownType

typealias executable = () -> Unit

class EditorImpl constructor(
    private val imageEditor: ImageEditor,
    private val filterFactory: FilterFactory,
    private val contrastEnhancementFactory: ContrastEnhancementFactory,
    private val transformationFactory: TransformationFactory
) : EditorUI {

    private val _editorState by lazy { MutableStateFlow<OperationState>(OperationState.Idle) }
    private val runCommand: (executable) -> Unit by lazy {
        {
            _editorState.value = OperationState.Processing
            try {
                it.invoke()
                _editorState.value = OperationState.Success
            } catch (ex: Exception) {
                ex.printStackTrace()
                _editorState.value = OperationState.Failure(reason = SUnknownType(ex.message ?: ""))
            }
            _editorState.value = OperationState.Idle
        }
    }

    override fun initialize(image: Mat) {
        _editorState.value = OperationState.Processing
        imageEditor.setActiveImage(image.clone())
        _editorState.value = OperationState.Idle
    }

    override fun observeState(): StateFlow<OperationState> = _editorState


    override infix fun execute(command: UICommand) {
        runCommand {
            when (val parameters = command.operationInfo) {
                is OperationType.UnsharpMaskBoostFilter -> {
                    val editorCommand = UnsharpMaskBoostFilterCommand(
                        imageEditor,
                        filterFactory,
                        parameters.unsharpMaskParameters
                    )

                    imageEditor.takeCommand(editorCommand)
                }

                is OperationType.NegativeFilter -> {
                    val editorCommand = NegativeFilterCommand(
                        imageEditor,
                        filterFactory
                    )

                    imageEditor.takeCommand(editorCommand)
                }

                is OperationType.RotationTransform -> {
                    val editorCommand = RotateCommand(
                        imageEditor,
                        transformationFactory,
                        parameters.rotationTransformParameters
                    )
                    imageEditor.takeCommand(editorCommand)
                }

                is OperationType.ImAdjustEnhancement -> {
                    val editorCommand = ImageContrastAdjustCommand(
                        imageEditor,
                        contrastEnhancementFactory,
                        parameters.imageContrastAdjustParameters
                    )
                    imageEditor.takeCommand(editorCommand)
                }
                else -> {

                }
            }
        }
    }

    override fun undo() {
        runCommand {
            imageEditor.undoChanges()
        }
    }


    override infix fun save(name: String) = save(name, imageEditor.getActiveImage())

    private fun save(name: String, img: Mat) {
        runCommand {
//            save your image to disk
        }
    }

    override suspend fun convertToBitmap(): Bitmap? {
        var bmp: Bitmap? = null
        val rgb = Mat()
        if (imageEditor.getActiveImage().channels() > 1) {
            Imgproc.cvtColor(imageEditor.getActiveImage(), rgb, Imgproc.COLOR_BGR2RGB)
        } else {
            imageEditor.getActiveImage().copyTo(rgb)
        }

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.RGB_565)
            Utils.matToBitmap(rgb, bmp)
        } catch (e: CvException) {
            _editorState.value = OperationState.Failure(reason = ProcessError(e.message ?: ""))
        }
        return bmp
    }
}

data class UICommand(
    val operationInfo: OperationType
)
