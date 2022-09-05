package io.github.pravinyo.common.utility

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

private const val FILENAME = "document.pdf"

fun Context.documentExists(id: String) =
        File(this.getFolder(id), FILENAME).exists()

fun Context.getFiles(id: String): List<String> = this.getFolder(id).listFiles()!!.map { it.absolutePath }

fun Context.saveImage(bitmap: Bitmap, fileName: String, id: String) {
    val directory = this.getFolder(id)
    val file = File(directory, "$fileName.png")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
    outputStream.flush()
    outputStream.close()
}

fun Context.getFolder(fileId: String): File {
    val directory = File("${this.filesDir}/$fileId")
    if (!directory.exists()) {
        directory.mkdir()
    }

    return directory
}