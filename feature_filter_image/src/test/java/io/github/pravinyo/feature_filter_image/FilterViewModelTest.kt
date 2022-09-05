package io.github.pravinyo.feature_filter_image

import io.github.pravinyo.feature_filter_image.presentation.FilterViewModel
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FilterViewModelTest{
    private var viewModel: FilterViewModel? = null

    @Before
    fun setup(){
//        viewModel = FilterViewModel()
    }

    @Test
    @Throws(Exception::class)
    fun testLoadImageOnStartUp(){
//        val image = viewModel?.loadImage()
//        Assert.assertNotNull(image)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        viewModel = null
    }
}