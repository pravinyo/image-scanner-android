package io.github.pravinyo.common.dependencyInjection

import editor.BackupManager
import editor.ImageEditor
import editor.StateManager
import io.github.pravinyo.common.editor.EditorImpl
import org.opencv.core.Mat

object EditorModule {

    fun uiImageEditor() = EditorImpl(
        imageEditor = imageEditor(),
        filterFactory = FactoryModule.getFilterFactory(),
        contrastEnhancementFactory = FactoryModule.getContrastEnhancementFactory(),
        transformationFactory = FactoryModule.getTransformationFactory()
    )

    fun imageEditor(): ImageEditor {
        return ImageEditor(
            image = Mat(),
            stateManager = getStateManager(),
            backupManager = getBackupManager()
        )
    }

    fun getStateManager() = StateManager()

    fun getBackupManager() = BackupManager()
}