package io.github.pravinyo.common.dependencyInjection

import factory.ContrastEnhancementFactory
import factory.FilterFactory
import factory.TransformationFactory

object FactoryModule {

    fun getTransformationFactory() = TransformationFactory()

    fun getContrastEnhancementFactory() = ContrastEnhancementFactory()

    fun getFilterFactory() = FilterFactory(getContrastEnhancementFactory())
}