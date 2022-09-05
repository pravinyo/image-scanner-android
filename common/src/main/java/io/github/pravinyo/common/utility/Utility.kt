package io.github.pravinyo.common.utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.opencv.android.Utils
import org.opencv.core.Mat

object Utility {
    private var isLoaded:Boolean = false
    private const val NATIVE_LIBRARY_NAME = "opencv_java3"
    fun loadModule(){
        if(!isLoaded){
            System.loadLibrary(NATIVE_LIBRARY_NAME)
            isLoaded = true
        }
    }

    fun loadResource(context:Context, drawable:Int): Mat {
        return Utils.loadResource(context,drawable)
    }

    fun loadFileToMatAndGetAddress(filepath: String): Long {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inSampleSize = 4
        val bitmap = BitmapFactory.decodeFile(filepath, options)

        val mat = Mat()
        Utils.bitmapToMat(bitmap,mat)

        bitmap.recycle()
        return mat.nativeObjAddr
    }
}