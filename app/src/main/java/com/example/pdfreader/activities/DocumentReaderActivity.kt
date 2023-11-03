package com.example.pdfreader.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfreader.R
import com.example.pdfreader.databinding.ActivityDocumentReaderBinding
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream


class DocumentReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentReaderBinding
    private lateinit var webView: WebView


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

    private fun readExcelFile(file: File) {
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

        webView.visibility = View.VISIBLE
        binding.pdfview.visibility = View.GONE
    }




}
