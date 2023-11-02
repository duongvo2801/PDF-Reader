package com.example.pdfreader.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfreader.databinding.ActivityDocumentReaderBinding
import java.io.File


class DocumentReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.getStringExtra("path")
        if (filePath != null) {
            val file = File(filePath)
            if (file.exists()) {
                val fileExtension = file.extension.toLowerCase()
                if (fileExtension == "pdf") {
                    readPdfFile(file)
                } else if (fileExtension == "docx" || fileExtension == "doc") {

                }
            }
        }
    }

    private fun readPdfFile(file: File) {
        val uriPath = Uri.fromFile(file)
        binding.pdfview.fromUri(uriPath).load()

        // show file pdf UI
        binding.pdfview.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE
    }

//    private fun convertWordToPdfAndDisplay(webView: WebView, wordFile: File) {
//        val fis = FileInputStream(wordFile)
//        val document = XWPFDocument(fis)
//
//        val htmlContent = StringBuilder()
//        for (paragraph in document.paragraphs) {
//            for (run in paragraph.runs) {
//                val text = run.text()
//                htmlContent.append("<p>$text</p>")
//            }
//        }
//
//        val htmlFile = File.createTempFile("word_to_pdf", ".html")
//        FileOutputStream(htmlFile).use { fos ->
//            fos.write(htmlContent.toString().toByteArray())
//        }
//
//        // Chuyển đổi HTML thành PDF
//        val pdfFile = File.createTempFile("word_to_pdf", ".pdf")
//        val pdfStream = FileOutputStream(pdfFile)
//        HtmlConverter.convertToPdf(FileInputStream(htmlFile), pdfStream)
//
//        // Hiển thị tệp PDF trong WebView
//        webView.visibility = View.VISIBLE
//        webView.loadUrl("file://" + pdfFile.absolutePath)
//    }


}
