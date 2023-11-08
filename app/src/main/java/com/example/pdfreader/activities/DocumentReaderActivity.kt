package com.example.pdfreader.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pdfreader.R
import com.example.pdfreader.databinding.ActivityDocumentReaderBinding
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFPictureShape
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.apache.poi.xslf.usermodel.XSLFTextShape
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream
import java.util.Base64


class DocumentReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentReaderBinding
    private lateinit var webView: WebView
    private lateinit var main_table: TableLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = findViewById(R.id.webView)
        main_table = findViewById(R.id.main_table)

        val filePath = intent.getStringExtra("path")
        if (filePath != null) {
            val file = File(filePath)
            if (file.exists()) {
                val fileExtension = file.extension.toLowerCase()
                if (fileExtension == "pdf") {
                    readPdfFile(file)
                } else if (fileExtension == "docx" || fileExtension == "doc") {
                    readWordFile(file)
                } else if (fileExtension == "xlsx" || fileExtension == "xls") {
                    val excelData = readExcelFile(file)

                    // Create a header row for the table
                    val headerRow = TableRow(this)
                    headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.excel))
                    headerRow.setPadding(20,15,20,15)

                    for (header in excelData[0]) {
                        val headerText = createTextView(header)
                        headerRow.addView(headerText)
                    }
                    main_table.addView(headerRow)
                    for (i in 1 until excelData.size) {
                        val dataRow = TableRow(this)
                        val rowData = excelData[i]

                        for (data in rowData) {
                            val dataText = createTextView(data)
                            dataRow.addView(dataText)
                        }

                        main_table.addView(dataRow)
                    }
                } else if (fileExtension == "pptx" || fileExtension == "ppt") {
                    try {
                        val ppt = XMLSlideShow(FileInputStream(file))
                        val slides = ppt.slides
                        val htmlContent = StringBuilder()

                        for (slide in slides) {
                            val slideHtml = convertSlideToHtml(slide)
                            htmlContent.append(slideHtml)
                        }

                        webView.loadDataWithBaseURL(null,
                            htmlContent.toString(), "text/html", "utf-8", null)
                        webView.settings.javaScriptEnabled = true
                        webView.webViewClient = WebViewClient()

                        binding.pdfview.visibility = View.GONE
                        binding.scrollView.visibility = View.GONE
                        binding.webView.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, getString(R.string.can_not_read_file), Toast.LENGTH_SHORT).show()
                    }
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


    private fun readExcelFile(file: File): List<List<String>> {
        try {
            val fileInputStream = FileInputStream(file)
            val workbook = WorkbookFactory.create(fileInputStream)

            val numberOfSheets = workbook.numberOfSheets
            val excelData = mutableListOf<List<String>>()

            for (i in 0 until numberOfSheets) {
                val sheet = workbook.getSheetAt(i)

                for (row in sheet) {
                    val rowData = mutableListOf<String>()
                    for (cell in row) {
                        val cellValue = when (cell.cellType) {
                            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                            org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue.toString()
                            else -> ""
                        }
                        rowData.add(cellValue)
                    }
                    excelData.add(rowData)
                }
            }
            binding.pdfview.visibility = View.GONE
            binding.webView.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE

            fileInputStream.close()
            return excelData
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.can_not_read_file), Toast.LENGTH_SHORT).show()
            return emptyList()
        }
    }
    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(20, 8, 20, 8)
        return textView
    }


    private fun readWordFile(file: File) {
        try {
            val fis = FileInputStream(file)
            val document = XWPFDocument(fis)
            val content = StringBuilder()

            for (paragraph in document.paragraphs) {
                if (paragraph.styleID == "Heading1") {
                    content.append("<strong>${paragraph.text}</strong>")
                } else {
                    content.append(paragraph.text)
                }
                content.append("<br>")
            }

            for (picture in document.allPictures) {
                val bytes = picture.data
                val base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)

                val imageTag = "<img src='data:image/png;base64,$base64Image' width='100%' height='auto'/>"
                content.append(imageTag)
            }

            fis.close()

            binding.pdfview.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE

            val webSettings = binding.webView.settings
            webSettings.javaScriptEnabled = true
            val htmlContent = "<html><body>${content.toString()}</body></html>"
            binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.can_not_read_file), Toast.LENGTH_SHORT).show()
        }
    }



    private fun convertSlideToHtml(slide: XSLFSlide): String {
        val shapes = slide.shapes
        val stringBuilder = StringBuilder()

        for (shape in shapes) {
            if (shape is XSLFPictureShape) {
                val pictureData = shape.pictureData
                val imageBase64 = Base64.getEncoder().encodeToString(pictureData.data)
                val mimeType = pictureData.contentType
                val imgTag = "<img src=\"data:$mimeType;base64,$imageBase64\">"
                stringBuilder.append(imgTag)
            } else if (shape is XSLFTextShape) {
                val text = shape.text
                stringBuilder.append("<p>").append(text).append("</p>")
            }
        }

        return stringBuilder.toString()
    }

//    private fun convertSlideToHtml(slide: XSLFSlide): String {
//        val shapes = slide.shapes
//        val stringBuilder = StringBuilder()
//
//        for (shape in shapes) {
//            if (shape is XSLFTextShape) {
//                for (paragraph in shape.textParagraphs) {
//                    val paragraphHtml = StringBuilder()
//                    for (run in paragraph.textRuns) {
//                        val text = textRun.getRawText()
//                        val fontSize = run.fontSize
//                        val fontFamily = run.fontFamily
//                        val color = run.fontColor
//                        val isBold = run.isBold
//                        val isItalic = run.isItalic
//
//                        val htmlStyle = buildHtmlStyle(fontSize, fontFamily,
//                            color.toString(), isBold, isItalic)
//
//                        val htmlText = "<span style=\"$htmlStyle\">$text</span>"
//                        paragraphHtml.append(htmlText)
//                    }
//
//                    stringBuilder.append("<p>$paragraphHtml</p>")
//                }
//            }
//        }
//
//        return stringBuilder.toString()
//    }
//    private fun buildHtmlStyle(fontSize: Double, fontFamily: String, color: String, isBold: Boolean, isItalic: Boolean): String {
//        val styleBuilder = StringBuilder()
//        styleBuilder.append("font-size: ${fontSize}px;")
//        styleBuilder.append("font-family: $fontFamily;")
//        styleBuilder.append("color: $color;")
//        if (isBold) {
//            styleBuilder.append("font-weight: bold;")
//        }
//        if (isItalic) {
//            styleBuilder.append("font-style: italic;")
//        }
//        return styleBuilder.toString()
//    }



}
