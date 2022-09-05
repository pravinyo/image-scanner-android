package io.github.pravinyo.common.data

import org.opencv.core.Scalar

data class Color(
        val red:Double,
        val green:Double,
        val blue:Double){
    fun toScalar():Scalar{
        return Scalar(red,green,blue)
    }
}
