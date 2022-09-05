package io.github.pravinyo.feature_page_preview.presentation

import OperationType
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.github.pravinyo.common.data.Event
import io.github.pravinyo.common.dependencyInjection.EditorModule
import io.github.pravinyo.common.editor.EditorUI
import io.github.pravinyo.common.editor.UICommand
import io.github.pravinyo.common.presentation.SavedStateViewModel
import io.github.pravinyo.feature_page_preview.data.FileExportType
import kotlinx.coroutines.*
import org.opencv.core.Mat
import transformations.FixedRotationDirection
import transformations.RotationTransformParameters
import java.io.File

class PageViewModel : SavedStateViewModel(){
    private var _image =  MutableLiveData<Mat>()
    private var _imageAddress =  MutableLiveData<Long>()
    val image: LiveData<Mat> get() = _image

    private val editor: EditorUI by lazy { EditorModule.uiImageEditor() }
    private var _filePath = MutableLiveData<String>()

    private var _label = MutableLiveData<Event<String>>()
    val label: LiveData<Event<String>> = _label

    private var _navigateToEditScreen = MutableLiveData<Event<Boolean>>()
    val navigateToEditUI: LiveData<Event<Boolean>> = _navigateToEditScreen

    private var _navigateToPreviousScreen = MutableLiveData<Event<Boolean>>()
    val navigateToPreviousScreen: LiveData<Event<Boolean>> = _navigateToPreviousScreen

    private var _editTitle = MutableLiveData<Event<Boolean>>()
    val editTitle: LiveData<Event<Boolean>> = _editTitle

    private var _generateDocument = MutableLiveData<Event<FileExportType>>()
    val generateDocument: LiveData<Event<FileExportType>> get() = _generateDocument

    fun initialize(address: Long, filePath: String) {
        val mat = Mat(address)
        _image.value = mat
        _imageAddress.value = address
        editor.initialize(mat)

        _filePath.value = filePath
    }

    fun state() = editor.observeState()
    fun rotateLeft() = rotate(
        OperationType.RotationTransform(
            RotationTransformParameters.FixedDirection(FixedRotationDirection.DIRECTION_CLOCKWISE_270)
        )
    )
    fun rotateRight() = rotate(
        OperationType.RotationTransform(
            RotationTransformParameters.FixedDirection(FixedRotationDirection.DIRECTION_CLOCKWISE_90)
        )
    )

    private fun rotate(operationType: OperationType){
        viewModelScope.launch(Dispatchers.Main) {
            val command = UICommand(
                operationInfo = operationType
            )
            editor execute command
        }
    }

    fun navigateBackScreen(){
        _navigateToPreviousScreen.value = Event(true)
    }

    fun generateDocument(type: FileExportType){
        _generateDocument.value = Event(type)
    }

    fun editTitle(){
        _editTitle.value = Event(true)
    }

    fun editPage(){
        _navigateToEditScreen.value = Event(true)
    }

    fun labelChange(label:String){
        _label.value = Event(label)
    }

    fun file(): File? {
        _filePath.value?.let { path->
            return File(path)
        }

        return null
    }

    @ExperimentalCoroutinesApi
    suspend fun image(): Bitmap? = coroutineScope {
        return@coroutineScope withContext(Dispatchers.IO){
            var image: Bitmap? = null
            try {
                image =  editor.convertToBitmap()
            }catch (exp : Exception){
                exp.printStackTrace()
            }
            if (!isActive) return@withContext null

            return@withContext image
        }
    }

    fun undo(){
        viewModelScope.launch(Dispatchers.IO) {
            editor.undo()
        }
    }

    override fun init(savedStateHandle: SavedStateHandle) {
        _imageAddress = savedStateHandle.getLiveData("originalImage")
        _filePath = savedStateHandle.getLiveData("filePath")
        _imageAddress.value?.let { imageAddress ->
            if (_image.value == null){
                initialize(imageAddress,_filePath.value!!)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}