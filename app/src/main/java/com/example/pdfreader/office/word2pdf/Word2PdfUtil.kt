package com.example.pdfreader.office.word2pdf

import com.aspose.words.Document
import com.aspose.words.SaveFormat
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File


object Word2PdfUtil {

    @Throws(Exception::class)
    fun wordBytes2PdfBytes(wordBytes: ByteArray): ByteArray {
        val document = Document(ByteArrayInputStream(wordBytes))
        val outputStream = ByteArrayOutputStream()
        document.save(outputStream, SaveFormat.PDF)
        return outputStream.toByteArray()
    }

    @Throws(Exception::class)
    fun wordBytes2PdfFile(wordBytes: ByteArray, pdfFilePath: String): File {
        val document = Document(ByteArrayInputStream(wordBytes))
        document.save(pdfFilePath, SaveFormat.PDF)
        return File(pdfFilePath)
    }
}
