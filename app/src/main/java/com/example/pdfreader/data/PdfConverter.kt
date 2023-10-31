package com.example.pdfreader.data

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

object PdfConverter  {
    fun convertImagesToPdf(images: List<Bitmap>, pdfFilePath: String) {
        val pdfDocument = PdfDocument()

        for (bitmap in images) {
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)
        }

        val pdfFile = File(pdfFilePath)
        pdfDocument.writeTo(FileOutputStream(pdfFile))
        pdfDocument.close()
    }
}