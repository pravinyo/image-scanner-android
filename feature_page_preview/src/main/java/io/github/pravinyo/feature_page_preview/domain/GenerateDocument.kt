package io.github.pravinyo.feature_page_preview.domain

import android.content.Context
import android.content.Context.PRINT_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo
import android.print.PrintAttributes
import android.print.PrintAttributes.Margins
import android.print.PrintAttributes.Resolution
import android.print.pdf.PrintedPdfDocument
import io.github.pravinyo.common.utility.getFolder
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GenerateDocument {
    private var _file: File? = null

    fun setFile(file: File?){
        _file = file
    }

    fun getPicture(): File?{
         return _file
    }

    fun getPDF(context: Context,fileId: String): File?{
        val file = _file ?: return null
        val filepath = file.absolutePath
        val document = pdfDocument(context)
        val page = getPage(document)
        val canvas = page.canvas
        val scaleBitmap = getScaledBitmapImage(filepath, page.info.pageWidth, page.info.pageHeight)

        canvas.drawBitmap(scaleBitmap, 0f,0f,paint())
        document.finishPage(page)
        try {
            return saveFile(context, document, fileId)
        } catch (e: IOException) {
            Timber.e("Proposal write error $e")
        }
        document.close()

        return null
    }

    private fun saveFile(context: Context,document: PdfDocument, fileId: String): File {
        val folder = context.getFolder(fileId)
        val file = File(folder,"sample.pdf")

        document.writeTo(FileOutputStream(file))
        return file
    }

    private fun getPage(document: PdfDocument): Page {
        val inch = 72
        val halfInch = inch / 2
        val pageInfo = PageInfo.Builder(inch * 8 + halfInch, inch * 11, 1).create()

        return document.startPage(pageInfo)
    }

    private fun getScaledBitmapImage(filepath: String, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(filepath, options)
        val scaleBitmap = Bitmap.createScaledBitmap(bitmap,width, height, true)
        bitmap.recycle()

        return scaleBitmap
    }

    private fun pdfDocument(context: Context): PdfDocument {
        val printAttrs = PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .setResolution(Resolution("zooey", PRINT_SERVICE, 300, 300))
                .setMinMargins(Margins.NO_MARGINS)
                .build()

        return PrintedPdfDocument(context, printAttrs)
    }

    private fun paint(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true

        return paint
    }
}