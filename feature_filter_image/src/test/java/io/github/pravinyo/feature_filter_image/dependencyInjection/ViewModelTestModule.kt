package io.github.pravinyo.feature_filter_image.dependencyInjection

import dagger.Module
import dagger.Provides
import io.github.pravinyo.common.editor.EditorUI
import io.github.pravinyo.feature_filter_image.presentation.FilterViewModel

@Module
class ViewModelTestModule {
    @Provides
    fun mockEditorUI(): EditorUI = FakeEditorUI()

    @Provides
    fun viewModel(editorUI: EditorUI): FilterViewModel {
        return FilterViewModel { editorUI }
    }
}