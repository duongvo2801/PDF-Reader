package com.example.pdfreader.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfreader.databinding.ActivityDocumentReaderBinding
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream


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
                    readWordFile(file)
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

    private fun readWordFile(file: File) {
        try {
            val fis = FileInputStream(file)
            val document = XWPFDocument(fis)
            val content = StringBuilder()

            for (paragraph in document.paragraphs) {
                content.append(paragraph.text)
            }
            fis.close()

            // show file pdf Word
            binding.pdfview.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE

            // Hiển thị nội dung Word trong WebView
            val webSettings = binding.webView.settings
            webSettings.javaScriptEnabled = true // Kích hoạt JavaScript nếu cần
            binding.webView.loadDataWithBaseURL(null, content.toString(), "text/html", "UTF-8", null)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Can't read file", Toast.LENGTH_SHORT).show()
        }
    }

}
