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
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
    lateinit var dexter: DexterBuilder
    private val images = ArrayList<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    //
    private lateinit var currentPhotoPath: String


    private val PICK_IMAGES_REQUEST = 100
    private val REQUEST_IMAGE_CAPTURE = 1


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_pdf)

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager

        imageAdapter = ImageAdapter(images)
        recyclerView.adapter = imageAdapter
        pdfDocument = PdfDocument()

        if (Environment.isExternalStorageManager()) {
            getPermission()
        } else {
            //request for the permission
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }




        goBackHome()
        popupMenu()
        convertPdf()

    }

    private fun convertPdf() {
        val buttonConvertPdf = findViewById<Button>(R.id.buttonConvertPdf)
        buttonConvertPdf.setOnClickListener {
            if (images.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_photo), Toast.LENGTH_SHORT).show()
            } else {
                convertImagesToPdf(images)
                images.clear()
            }
        }
    }

    private fun popupMenu() {
        val buttonSelectImage = findViewById<ImageView>(R.id.ivSelectImage)
        buttonSelectImage.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, buttonSelectImage)
            popupMenu.menuInflater.inflate(R.menu.popup_convert, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.gallery -> {
                        pickImagesFromGallery()
                    }
                    R.id.camera -> {
                        dispatchTakePictureIntent()
                    }
                }
                true
            })
            popupMenu.show()
        }
    }

    private fun pickImagesFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGES_REQUEST)
    }

    //camera
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.pdfreader.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "JPEG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()
        )
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val imageUris = ArrayList<String>()

                if (data.data != null) {
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            images.add(currentPhotoPath)
            imageAdapter.notifyDataSetChanged()
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
        Log.d("qqq", imagePath.toString())
    }

    private fun convertImagesToPdf(images: List<String>) {
        val pdfDocument = PdfDocument()
//        val pdfDirectory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val pdfFileNameBase = "converted_images"
        val pdfExtension = "pdf"
        val pageSize = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val pdfDirectory = File("/storage/emulated/0/convert-pdf")
        if (!pdfDirectory.exists()) {
            pdfDirectory.mkdir()
        }
        Log.d("qqq", pdfDirectory.toString())

        for ((index, imagePath) in images.withIndex()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)

            val pageWidth = pageSize.pageWidth
            val pageHeight = pageSize.pageHeight
            val imageWidth = bitmap.width
            val imageHeight = bitmap.height

            val scale = min(pageWidth.toFloat() / imageWidth, pageHeight.toFloat() / imageHeight)

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
            Toast.makeText(this, getString(R.string.file_created_successfully), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPermission() {
        dexter = Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(this@ConvertPdfActivity, R.string.permissions_granted, Toast.LENGTH_SHORT).show()
                        } else {
                            AlertDialog.Builder(this@ConvertPdfActivity).apply {
                                setMessage(getString(R.string.please_permissions))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.setting)) { _, _ ->
                                        val reqIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .apply {
                                                val uri = Uri.fromParts("package", packageName, null)
                                                data = uri
                                            }
                                        resultLauncher.launch(reqIntent)
                                    }
                                val alert = this.create()
                                alert.show()
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
        dexter.check()
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        dexter.check()
    }

    private fun goBackHome() {
        val buttonBack = findViewById<ImageView>(R.id.ivHomeBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
    }
}
