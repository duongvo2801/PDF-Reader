package com.example.pdfreader.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfreader.R
import com.example.pdfreader.adapters.ImageAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Float.min
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConvertPdfActivity : AppCompatActivity() {

    private lateinit var pdfDocument: PdfDocument
    lateinit var dexter : DexterBuilder
    private val images = ArrayList<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var capturedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGES_REQUEST = 100
        private const val CAMERA_PERMISSION_REQUEST = 101
    }

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_pdf)

        //
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager

        imageAdapter = ImageAdapter(images)
        recyclerView.adapter = imageAdapter

        getPermission()
        goBackHome()

        val botton = findViewById<ImageView>(R.id.ivSelectImage)
        botton.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, botton)
            popupMenu.menuInflater.inflate(R.menu.popup_convert, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.gallery -> {
                        pickImagesFromGallery()
                        Toast.makeText(this@ConvertPdfActivity, "" + item.title, Toast.LENGTH_SHORT)
                            .show()
                    }
                    R.id.camera ->{
//                        dispatchTakePictureIntent()
                        Toast.makeText(this@ConvertPdfActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            })
            popupMenu.show()
        }

        //
        pdfDocument = PdfDocument()


        // button convert
        val buttonConvertPdf = findViewById<Button>(R.id.buttonConvertPdf)
        buttonConvertPdf.setOnClickListener {
            convertImagesToPdf(images)
        }


    }

    // camera
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = createImageFile()
            if (photoFile != null) {
                capturedImageUri = FileProvider.getUriForFile(this, "com.example.pdfreader.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
                startActivityForResult(takePictureIntent, CAMERA_PERMISSION_REQUEST)
            }
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    // gallery
    private fun pickImagesFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGES_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val imageUris = ArrayList<String>()

                if (data.data != null) {
                    // Trường hợp chọn một ảnh
                    val selectedImageUri: Uri = data.data!!
                    imageUris.add(getRealPathFromURI(selectedImageUri))
                } else {
                    val clipData: ClipData? = data.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val uri: Uri = clipData.getItemAt(i).uri
                            imageUris.add(getRealPathFromURI(uri))
                        }
                    }
                }

                images.addAll(imageUris)
                imageAdapter.notifyDataSetChanged()
            }
        }

        // camera
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (capturedImageUri != null) {
                images.add(capturedImageUri.toString())
                imageAdapter.notifyDataSetChanged()
            }
        }

    }
    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val imagePath = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return imagePath ?: ""
    }


    private fun convertImagesToPdf(images: List<String>) {
        val pdfDocument = PdfDocument()
        val pdfDirectory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val pdfFileNameBase = "converted_images"
        val pdfExtension = "pdf"
        val pageSize = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 page size

        for ((index, imagePath) in images.withIndex()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)

            // Tính toán tỷ lệ giữa kích thước trang PDF và hình ảnh
            val pageWidth = pageSize.pageWidth
            val pageHeight = pageSize.pageHeight
            val imageWidth = bitmap.width
            val imageHeight = bitmap.height

            val scale = min(pageWidth.toFloat() / imageWidth, pageHeight.toFloat() / imageHeight)

            // Tạo PageInfo với kích thước trang phù hợp
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawBitmap(bitmap, null, RectF(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat()), null)
            pdfDocument.finishPage(page)
            bitmap.recycle()
        }

        var pdfFileName = "$pdfFileNameBase.$pdfExtension"

        var counter = 1
        while (File(pdfDirectory, pdfFileName).exists()) {
            pdfFileName = "$pdfFileNameBase${counter++}.$pdfExtension"
        }

        val pdfFile = File(pdfDirectory, pdfFileName)

        try {
            val fileOutputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            // Display a message or perform any further actions here
            Toast.makeText(this, "PDF created successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getPermission() {
        dexter = Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {

                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(this@ConvertPdfActivity, "Permissions Granted", Toast.LENGTH_SHORT).show()
                        } else {
                            AlertDialog.Builder(this@ConvertPdfActivity).apply {
                                setMessage("please allow the required permissions")
                                    .setCancelable(false)
                                    .setPositiveButton("Settings") { _, _ ->
                                        val reqIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .apply {
                                                val uri = Uri.fromParts("package", packageName, null)
                                                data = uri
                                            }
                                        resultLauncher.launch(reqIntent)
                                    }
                                // setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                                val alert = this.create()
                                alert.show()
                            }
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            }).withErrorListener{
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
        dexter.check()
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> dexter.check()
    }


    private fun goBackHome() {
        val buttonBack = findViewById<ImageView>(R.id.ivHomeBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
    }


}