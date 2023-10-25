package com.example.pdfreader.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfreader.databinding.ActivityDocumentReaderBinding
import java.io.File


class DocumentReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentReaderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentReaderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val path = intent.getStringExtra("path")

        val file: File = File(path)
        val uriPath : Uri = Uri.fromFile(file)
        binding.pdfview.fromUri(uriPath).load()


    }
}