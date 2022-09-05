package io.github.pravinyo.feature_filter_image.presentation

import OperationType
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.github.pravinyo.common.dependencyInjection.EditorModule
import io.github.pravinyo.common.editor.EditorUI
import io.github.pravinyo.common.editor.UICommand
import io.github.pravinyo.common.presentation.SavedStateViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import org.opencv.core.Mat

class FilterViewModel : SavedStateViewModel() {

    private var _image = MutableLiveData<Mat>()
    private var _imageAddress = MutableLiveData<Long>()
    val image: LiveData<Mat> get() = _image

    private val imageConvertingLock = Mutex()
    private val editor: EditorUI by lazy { EditorModule.uiImageEditor() }

    fun initialize(address: Long) {
        val mat = Mat(address)
        _image.value = mat
        _imageAddress.value = address
        editor.initialize(mat)
    }

    fun state() = editor.observeState()

    fun filter(operationType: OperationType) {
        viewModelScope.launch(Dispatchers.IO) {
            val command = UICommand(
                operationInfo = operationType
            )
            editor execute command
        }
    }

    fun negativeFilter() {
        viewModelScope.launch(Dispatchers.IO) {
            val command = UICommand(operationInfo = OperationType.NegativeFilter)
            editor execute command
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun image(): Bitmap? = coroutineScope {
        return@coroutineScope withContext(Dispatchers.IO) {
            val image: Bitmap?
            try {
                imageConvertingLock.lock()
                image = editor.convertToBitmap()
            } finally {
                imageConvertingLock.unlock()
            }
            return@withContext image
        }
    }

    fun undo() {
        viewModelScope.launch(Dispatchers.IO) {
            editor.undo()
        }
    }

    override fun init(savedStateHandle: SavedStateHandle) {
        _imageAddress = savedStateHandle.getLiveData("originalImage")
        _imageAddress.value?.let { imageAddress ->
            if (_image.value == null) {
                initialize(imageAddress)
            }
        }
    }
}