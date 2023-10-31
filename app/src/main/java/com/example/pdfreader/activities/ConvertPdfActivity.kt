package com.example.pdfreader.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

class ConvertPdfActivity : AppCompatActivity() {

    lateinit var dexter : DexterBuilder
    private val images = ArrayList<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    companion object {
        private const val PICK_IMAGES_REQUEST = 100
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
                    R.id.camera ->
                        Toast.makeText(this@ConvertPdfActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                }
                true
            })
            popupMenu.show()
        }

//        val buttonConvertPdf = findViewById<ImageView>(R.id.buttonConvertPdf)
//        buttonConvertPdf.setOnClickListener {
//            Toast.makeText(this@ConvertPdfActivity, "Click" , Toast.LENGTH_SHORT).show()
//        }


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


}