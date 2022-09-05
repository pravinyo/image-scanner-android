//package io.github.pravinyo.common.editor
//
//import android.graphics.Bitmap
//import io.github.pravinyo.common.data.CommandFactoryParam
//import io.github.pravinyo.common.data.CommandFactoryParam.SaveParam
//import io.github.pravinyo.common.data.CommandFactoryParam.UndoParam
//import io.github.pravinyo.common.data.OperationState
//import io.github.pravinyo.common.data.OperationType
//import io.github.pravinyo.common.data.ProcessError
//import io.github.pravinyo.common.domain.commands.CommandFactory
//import io.github.pravinyo.common.domain.history.History
//import io.github.pravinyo.common.domain.history.SnapshotFactory
//import io.github.pravinyo.common.domain.render.FileDataSource
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import org.opencv.android.Utils
//import org.opencv.core.CvException
//import org.opencv.core.Mat
//import org.opencv.imgproc.Imgproc
//import java.util.*
//import javax.inject.Inject
//import javax.inject.Provider
//import io.github.pravinyo.common.data.UnknownError as SUnknownType
//
//
//typealias executable = () -> Unit
//
//class EditorImpl @Inject constructor(
//        private val historyProvider: Provider<History>,
//        private val snapshotFactoryProvider: Provider<SnapshotFactory>,
//        private val commandFactoryProvider: Provider<CommandFactory>
//        ): EditorCore, EditorUI {
//    private lateinit var intermediateImage: Mat
//    private lateinit var originalImage:Mat
//    private lateinit var history: History
//    private lateinit var snapshotFactory: SnapshotFactory
//    private lateinit var commandFactory: CommandFactory
//    private val opsStack:Stack<OperationType> by lazy {  Stack() }
//    private val imageStack by lazy { Stack<Mat>() }
//    private val _editorState by lazy { MutableStateFlow<OperationState>(OperationState.Idle) }
//    private val runCommand: (executable) -> Unit by lazy { {
//        _editorState.value = OperationState.Processing
//        try{
//            it.invoke()
//        }catch (ex: Exception){
//            ex.printStackTrace()
//            _editorState.value = OperationState.Failure(reason = SUnknownType(ex.message ?: ""))
//        }
//        _editorState.value = OperationState.Idle
//    } }
//
//    override fun initialize(image: Mat){
//        _editorState.value = OperationState.Processing
//
//        intermediateImage = image.clone()
//        originalImage = image.clone()
//        imageStack.push(image.clone())
//        history = historyProvider.get()
//        snapshotFactory = snapshotFactoryProvider.get()
//        commandFactory = commandFactoryProvider.get()
//
//        _editorState.value = OperationState.Idle
//    }
//
//    override fun operationStack(): Stack<OperationType> = opsStack
//    override fun imageStack(): Stack<Mat> = imageStack
//    override fun history(): History = history
//    override fun observeState(): StateFlow<OperationState> = _editorState
//    override fun asCore(): EditorCore = this
//
//    override suspend infix fun execute(param: CommandFactoryParam){
//        runCommand{
//            val command = commandFactory.getCommand(param)
//            if(param != UndoParam){
//                history.push(command = command, snapshot = snapshotFactory.createSnapshot(command.name()))
//            }
//            command.execute()
//            _editorState.value = OperationState.Success
//        }
//    }
//
//    override fun undo(){
//        runCommand{
//            val undo = commandFactory.getCommand(UndoParam)
//            undo.execute()
//            _editorState.value = OperationState.Success
//        }
//    }
//
//    // for debug
//    override suspend infix fun save(name: String) = save(name, intermediateImage)
//
//    // for debug
//    private fun save(name: String, img: Mat) = FileDataSource(name).writeData(img)
//
//    override suspend infix fun saveFinal(name: String){
//        runCommand{
//            val saveCommand = commandFactory.getCommand(SaveParam(name, originalImage))
//            saveCommand.execute()
//            _editorState.value = OperationState.Success
//        }
//    }
//
//    override fun backup(): List<OperationType> = opsStack.toList()
//
//    override infix fun restoreFrom(state: List<OperationType>){
//        runCommand{
//            opsStack.clear()
//            opsStack.addAll(state)
//            _editorState.value = OperationState.Success
//        }
//    }
//
//    override fun imageStackSize(): Int = imageStack.size
//
//    override fun image(): Mat = intermediateImage
//
//    override fun image(image: Mat){
//        runCommand{
//            intermediateImage.release()
//            intermediateImage = image
//            _editorState.value = OperationState.Success
//        }
//    }
//
//    override suspend fun convertToBitmap(): Bitmap? {
//        var bmp: Bitmap? = null
//        val rgb = Mat()
//        if (intermediateImage.channels()>1){
//            Imgproc.cvtColor(intermediateImage, rgb, Imgproc.COLOR_BGR2RGB)
//        }else{
//            intermediateImage.copyTo(rgb)
//        }
//
//        try {
//            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.RGB_565)
//            Utils.matToBitmap(rgb, bmp)
//        } catch (e: CvException) {
//            _editorState.value = OperationState.Failure(reason = ProcessError(e.message ?: ""))
//        }
//        return bmp
//    }
//
//    override fun clear() {
//        intermediateImage.release()
//        originalImage.release()
//        opsStack.clear()
//        imageStack.clear()
//        history.clear()
//    }
//}