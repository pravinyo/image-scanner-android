package io.github.pravinyo.common.data

import org.opencv.core.Point

data class Coordinate(
        val x:Int,
        val y:Int
){
    fun toPoint():Point{
        return Point(x.toDouble(),y.toDouble())
    }
}