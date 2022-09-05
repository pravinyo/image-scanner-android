package io.github.pravinyo.docscanner.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.whenResumed
import coil.load
import io.github.pravinyo.common.data.OperationState
import io.github.pravinyo.common.utility.Utility
import io.github.pravinyo.common.utility.getFiles
import io.github.pravinyo.common.utility.saveImage
import io.github.pravinyo.docscanner.common.ScreensNavigator
import io.github.pravinyo.docscanner.common.ScreensNavigatorImpl
import io.github.pravinyo.docscanner.common.dialogs.DialogsNavigator
import io.github.pravinyo.docscanner.common.fragments.BaseFragment
import io.github.pravinyo.docscanner.databinding.FilterFragmentBinding
import io.github.pravinyo.feature_filter_image.presentation.FilterViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class FilterFragment : BaseFragment() {
    private var _binding: FilterFragmentBinding? = null
    private val binding get() = _binding!!
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var viewModel: FilterViewModel

    private val screensNavigator: ScreensNavigator by lazy { ScreensNavigatorImpl(this.activity as AppCompatActivity) }
    private val dialogsNavigator: DialogsNavigator by lazy { DialogsNavigator(this.parentFragmentManager) }

    private lateinit var filesId: String

    override fun onAttach(context: Context) {
        Utility.loadModule()
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = FilterViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FilterFragmentBinding.inflate(inflater)
        return binding.root
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val arg = arguments?.getString("fileId", "")
        if (arg.isNullOrBlank()) {
            screensNavigator.navigateBack()
        } else {
            filesId = arg
        }

        coroutineScope.launch {
            setup()
            observeState()
        }
    }

    @ExperimentalCoroutinesApi
    private fun setup() {
        _binding?.let { binding ->
            loadImageIfNot()
            refreshImage()

            binding.backButton.setOnClickListener {
                screensNavigator.navigateBack()
            }

            binding.undoButton.setOnClickListener {
                Toast.makeText(requireContext(), "undoing", Toast.LENGTH_SHORT).show()
                viewModel.undo()
            }

            binding.contrastButton.setOnClickListener {
                Toast.makeText(requireContext(), "negative filter", Toast.LENGTH_SHORT).show()
                viewModel.negativeFilter()
                refreshImage()
            }

            binding.saveButton.setOnClickListener {
                coroutineScope.launch(Dispatchers.Main) {
                    val image = viewModel.image()
                    if (image == null) {
                        dialogsNavigator.showErrorDialog()
                    } else {
                        Toast.makeText(requireContext(), "started saving!!", Toast.LENGTH_SHORT).show()
                        withContext(Dispatchers.IO) {
                            requireContext().saveImage(image, "processed_image", filesId)
                        }
                        Toast.makeText(requireContext(), "saved!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadImageIfNot() {
        if (viewModel.image.value == null) {
            val imageFilePath: String = requireContext()
                .getFiles(filesId).first()

            val imageMatAddress = Utility.loadFileToMatAndGetAddress(imageFilePath)
            viewModel.initialize(imageMatAddress)
        }
    }

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    private suspend fun observeState() {
        _binding?.let { binding ->
            viewModel.state().collect { state ->
                whenResumed {
                    when (state) {
                        OperationState.Idle -> {
                            binding.controlButtons.isEnabled = false
                        }
                        OperationState.Processing -> {
                            binding.controlButtons.isEnabled = true
                        }
                        OperationState.Success -> {
                            refreshImage()
                        }
                        is OperationState.Failure -> {
                            dialogsNavigator.showErrorDialog()
                        }
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun refreshImage() {
        coroutineScope.launch(Dispatchers.Main) {
            binding.displayImage.load(viewModel.image())
        }
    }

    override fun onBackPressed(callback: OnBackPressedCallback) {
        callback.isEnabled = false
        screensNavigator.navigateBack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
    }
}