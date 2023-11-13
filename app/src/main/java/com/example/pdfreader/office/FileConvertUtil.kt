package com.example.pdfreader.office


import com.example.pdfreader.office.configs.MatchLicense
import com.example.pdfreader.office.word2pdf.Word2PdfUtil
import java.io.File

object FileConvertUtil {

    init {
        MatchLicense.init()
    }


    fun wordBytes2PdfBytes(wordBytes: ByteArray): ByteArray {
        return Word2PdfUtil.wordBytes2PdfBytes(wordBytes)
    }

    fun wordBytes2PdfFile(wordBytes: ByteArray, pdfFilePath: String): File {
        return Word2PdfUtil.wordBytes2PdfFile(wordBytes, pdfFilePath)
    }
}
