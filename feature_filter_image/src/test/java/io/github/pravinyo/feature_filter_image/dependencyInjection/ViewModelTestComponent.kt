package io.github.pravinyo.feature_filter_image.dependencyInjection

import dagger.Component
import io.github.pravinyo.feature_filter_image.presentation.FilterViewModelTest

@Component(modules = [ViewModelTestModule::class])
interface ViewModelTestComponent {
    fun inject(test : FilterViewModelTest)
}