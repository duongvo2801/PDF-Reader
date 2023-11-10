package com.example.pdfreader.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfreader.R
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.html2pdf.HtmlConverter
import java.io.*

class WordConverterActivity : AppCompatActivity() {

    private val PICK_PDF_FILE = 2
    private val storageDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator
    private val outputPDF = storageDir + "Converted_PDF.pdf"
    private var textView: TextView? = null
    private lateinit var fab: FloatingActionButton
    private var document: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get treeview and set its text
        textView = findViewById(R.id.textView)
        textView?.text = "Select a Word DOCX file..."

        // define click listener of floating button
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            try {
                // open Word file from file picker and convert to PDF
                openAndConvertFile(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openAndConvertFile(pickerInitialUri: Uri?) {
        // create a new intent to open document
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // mime types for MS Word documents
        val mimetypes = arrayOf(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword"
        )
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

        // start activity
        startActivityForResult(intent, PICK_PDF_FILE)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                document = intent.data

                // open the selected document into an Input stream
                try {
                    contentResolver.openInputStream(document!!)?.use { inputStream ->
                        // Convert Word document to PDF using iText
                        val os = FileOutputStream(File(outputPDF))
                        HtmlConverter.convertToPdf(inputStream, os)

                        // show PDF file location in toast as well as treeview (optional)
                        Toast.makeText(
                            this@WordConverterActivity,
                            "File saved in: $outputPDF",
                            Toast.LENGTH_LONG
                        ).show()

                        textView?.text = "PDF saved at: $outputPDF"

                        // view converted PDF
                        viewPDFFile()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@WordConverterActivity,
                        "File not found: " + e.message,
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@WordConverterActivity, e.message, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@WordConverterActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun viewPDFFile() {
        // load PDF into the PDFView
        val pdfView: PDFView = findViewById(R.id.pdfView)
        pdfView.fromFile(File(outputPDF)).load()
    }
}
