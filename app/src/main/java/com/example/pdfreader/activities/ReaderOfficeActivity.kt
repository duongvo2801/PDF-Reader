package com.example.pdfreader.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfreader.R
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream

class ReaderOfficeActivity : AppCompatActivity() {
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_office)

        textView = findViewById(R.id.textView)
        val filePath = intent.getStringExtra("path")
        if (filePath != null) {
            val file = File(filePath)
            if (file.exists()) {
                val fis = FileInputStream(file)
                val document = XWPFDocument(fis)
                val content = StringBuilder()
                for (paragraph in document.paragraphs) {
                    content.append(paragraph.text)
                }
                fis.close()
                textView.text = content.toString()
            }
        }
    }


}