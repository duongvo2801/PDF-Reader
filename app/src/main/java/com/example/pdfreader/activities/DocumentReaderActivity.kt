package com.example.pdfreader.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfreader.R
import com.example.pdfreader.databinding.ActivityDocumentReaderBinding
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFTextShape
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream


class DocumentReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentReaderBinding
    private lateinit var webView: WebView
    private lateinit var main_table: TableLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = findViewById(R.id.webView)

        val filePath = intent.getStringExtra("path")
        if (filePath != null) {
            val file = File(filePath)
            if (file.exists()) {
                val fileExtension = file.extension.toLowerCase()
                if (fileExtension == "pdf") {
                    readPdfFile(file)
                } else if (fileExtension == "xlsx" || fileExtension == "xls") {
                    readExcelFile(file)
                } else if (fileExtension == "docx" || fileExtension == "doc") {
                    readWordFile(file)
                } else if (fileExtension == "pptx" || fileExtension == "ppt") {
                    readPowerPointFile(file)
                }
            }
        }
    }

    private fun readPdfFile(file: File) {
        // show file pdf UI
        binding.pdfview.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE


        val uriPath = Uri.fromFile(file)
        binding.pdfview.fromUri(uriPath).load()

    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(8, 8, 8, 8)
        return textView
    }

    private fun readExcelFile(file: File) {
        // show file excel Word
        webView.visibility = View.VISIBLE
        binding.pdfview.visibility = View.GONE

        val fileInputStream = FileInputStream(file)
        val workbook = WorkbookFactory.create(fileInputStream)

        val numberOfSheets = workbook.numberOfSheets
        val htmlContent = StringBuilder("<html><body>")

        for (i in 0 until numberOfSheets) {
            val sheet = workbook.getSheetAt(i)
            val sheetName = sheet.sheetName
            htmlContent.append("<h2>$sheetName</h2>")

            for (row in sheet) {
                htmlContent.append("<p>")

                for (cell in row) {
                    val cellValue = when (cell.cellType) {
                        org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                        org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue.toString()
                        else -> ""
                    }
                    htmlContent.append(cellValue).append(" | ")
                }


                htmlContent.append("</p>")
            }
        }

        fileInputStream.close()

        htmlContent.append("</body></html>")

        webView.loadDataWithBaseURL(null, htmlContent.toString(), "text/html", "UTF-8", null)

    }

    private fun readWordFile(file: File) {
        try {
            val fis = FileInputStream(file)
            val document = XWPFDocument(fis)
            val content = StringBuilder()

            for (paragraph in document.paragraphs) {
                if (paragraph.styleID == "Heading1") {
                    // Đây là một ví dụ về cách sử dụng thẻ HTML để in đậm tiêu đề và căn giữa
                    content.append("<strong>${paragraph.text}</strong>")
                } else {
                    content.append(paragraph.text)
                }
                content.append("<br>") // Thêm thẻ <br> để xuống dòng
            }

            // Đọc hình ảnh từ tài liệu Word và chuyển chúng thành HTML
            for (picture in document.allPictures) {
                val bytes = picture.data
                val base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)

                // Thêm kích thước vào thẻ <img>
                val imageTag = "<img src='data:image/png;base64,$base64Image' width='100%' height='auto'/>"
                content.append(imageTag)
            }

            fis.close()

            // show file pdf Word
            binding.pdfview.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE

            // Hiển thị nội dung Word trong WebView
            val webSettings = binding.webView.settings
            webSettings.javaScriptEnabled = true // Kích hoạt JavaScript nếu cần
            val htmlContent = "<html><body>${content.toString()}</body></html>"
            binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Can't read file", Toast.LENGTH_SHORT).show()
        }
    }



    private fun readPowerPointFile(file: File) {
        try {
            // show file excel Word
            webView.visibility = View.VISIBLE
            binding.pdfview.visibility = View.GONE

            val fis = FileInputStream(file)
            val ppt = XMLSlideShow(fis)
            val content = StringBuilder()

            for (slide in ppt.slides) {
                for (shape in slide) {
                    if (shape is XSLFTextShape) {
                        content.append(shape.text)
                    }
                }
                content.append("<br>") // Thêm thẻ <br> để xuống dòng sau mỗi slide
            }

            fis.close()

            // Hiển thị nội dung PowerPoint trong WebView
            val webSettings = binding.webView.settings
            webSettings.javaScriptEnabled = true // Kích hoạt JavaScript nếu cần
            val htmlContent = "<html><body>${content.toString()}</body></html>"
            binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Không thể đọc tệp PowerPoint", Toast.LENGTH_SHORT).show()
        }
    }



}
