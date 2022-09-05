package io.github.pravinyo.docscanner.ui

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.whenResumed
import coil.load
import io.github.pravinyo.common.data.OperationState
import io.github.pravinyo.common.data.observeEvent
import io.github.pravinyo.common.utility.Utility
import io.github.pravinyo.common.utility.getFiles
import io.github.pravinyo.common.utility.saveImage
import io.github.pravinyo.docscanner.R
import io.github.pravinyo.docscanner.common.ScreensNavigator
import io.github.pravinyo.docscanner.common.ScreensNavigatorImpl
import io.github.pravinyo.docscanner.common.dialogs.ChangeTitleDialogFragment
import io.github.pravinyo.docscanner.common.dialogs.DialogsNavigator
import io.github.pravinyo.docscanner.common.fragments.BaseFragment
import io.github.pravinyo.docscanner.databinding.PageFragmentBinding
import io.github.pravinyo.feature_page_preview.data.FileExportType
import io.github.pravinyo.feature_page_preview.domain.GenerateDocument
import io.github.pravinyo.feature_page_preview.presentation.PageViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.io.File


class PageFragment: BaseFragment(), MainActivity.ShareClickListener {

    private var _binding: PageFragmentBinding? = null
    private val binding get() = _binding!!
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var viewModel: PageViewModel
    private var isStateChanged = false

    private val screensNavigator: ScreensNavigator by lazy {ScreensNavigatorImpl(this.activity as AppCompatActivity)}
    private val dialogsNavigator: DialogsNavigator by lazy { DialogsNavigator(this.parentFragmentManager) }
    private val generateDocument by lazy { GenerateDocument() }

    private var filesId: String = "f59fdd66-74e9-4684-9d39-fac8b4efb91a"

    override fun onAttach(context: Context) {
        Utility.loadModule()
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = PageViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageFragmentBinding.inflate(inflater)
        return binding.root
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
        observeViewModel()
        coroutineScope.launch {
            observeState()
        }
    }

    private fun observeViewModel() {
        viewModel.label.observeEvent(viewLifecycleOwner) { label ->
            binding.toolbarTitle.text = label
        }

        viewModel.navigateToPreviousScreen.observeEvent(viewLifecycleOwner) { navigateBack ->
            if (navigateBack) {
                screensNavigator.navigateBack()
            }
        }

        viewModel.generateDocument.observeEvent(viewLifecycleOwner) { type ->
            Timber.d("Event: $type")
            coroutineScope.launch {
                generateDocument.setFile(viewModel.file())

                val file: File? = when (type) {
                    FileExportType.FILE_AS_PICTURE -> generateDocument.getPicture()
                    FileExportType.FILE_AS_PDF -> generateDocument.getPDF(requireContext(), filesId)
                    else -> {
                        throw IllegalStateException("Enum is invalid")
                    }
                }

                shareFile(file)
            }
        }

        viewModel.editTitle.observeEvent(viewLifecycleOwner) { changeTitle ->
            if (changeTitle) {
                dialogsNavigator.showTitleEditDialog(listener = object :
                    ChangeTitleDialogFragment.TitleChangeListener {
                    override fun onTitleChange(title: String) {
                        viewModel.labelChange(title)
                    }
                })
            }
        }

        viewModel.navigateToEditUI.observeEvent(viewLifecycleOwner) { editPage ->
            if (editPage) {
                screensNavigator.navigateToEditPage(filesId)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun setup(){
        _binding?.let { binding ->

            loadImageIfNotLoaded()
            refreshImage()

            viewModel.labelChange("Page 1")

            binding.toolbarBackButton.setOnClickListener {
                viewModel.navigateBackScreen()
            }

            binding.toolbarEditButton.setOnClickListener {
                viewModel.editTitle()
            }

            binding.editButton.setOnClickListener {
                viewModel.editPage()
            }

            binding.rotateLeftButton.setOnClickListener {
                isStateChanged = true
                viewModel.rotateLeft()
            }

            binding.rotateRightButton.setOnClickListener {
                isStateChanged = true
                viewModel.rotateRight()
            }

            binding.shareButton.setOnClickListener {
                dialogsNavigator.showFileExportOptionsDialog()
            }
        }
    }

    private fun loadImageIfNotLoaded() {
        if(viewModel.image.value == null){
            val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.sample)
            requireContext().saveImage(bitmap, "sample.png", filesId)

            val imageFilePath: String = requireContext()
                    .getFiles(filesId).first()

            val imageMatAddress = Utility.loadFileToMatAndGetAddress(imageFilePath)
            viewModel.initialize(imageMatAddress,imageFilePath)
        }
    }

    private fun shareFile(fileToShare: File?) {
        Timber.d("Starting to share file $fileToShare")

        fileToShare?.let { file ->

            val uri: Uri? = try {
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
            } catch (e: IllegalArgumentException) {
                Timber.e("the selected file can't be shared: $file")
                null
            }

            if (uri != null) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, requireActivity().contentResolver.getType(uri))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
        }
    }

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    private suspend fun observeState(){
        _binding?.let {binding ->

            viewModel.state().collect { state ->
                Timber.d("state is => $state")
                whenResumed {
                    when(state){
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onBackPressed(callback: OnBackPressedCallback) {
        callback.isEnabled = false
        if (isStateChanged){
            coroutineScope.launch(Dispatchers.Main) {
                saveImage()
            }
        }
        viewModel.navigateBackScreen()
    }

    @ExperimentalCoroutinesApi
    private suspend fun saveImage() {
        val image = viewModel.image()
        if (image == null){
            dialogsNavigator.showErrorDialog()
        }else{
            withContext(Dispatchers.IO){
                requireContext().saveImage(image,"processed_image",filesId)
            }
            Toast.makeText(requireContext(),"saved!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).registerShareListener(this)
    }

    override fun onStop() {
        (requireActivity() as MainActivity).unRegisterShareListener()
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
    }

    override fun onShareItemClick(type: FileExportType) {
        Timber.d("received click event for file share")
        viewModel.generateDocument(type)
    }
}