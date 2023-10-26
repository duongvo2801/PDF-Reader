package com.example.pdfreader.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfreader.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File

class ConvertPdfActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "IMAGE_LIST"
    }

    lateinit var dexter : DexterBuilder
    private lateinit var buttonConvertPdf: Button
    private lateinit var progressBar: ProgressBar

    var folderPath: String = Environment.getDataDirectory().absolutePath + "/storage/emulated/0/pdf-reader"
    val directory = File("/sdcard/pdf-reader/")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_pdf)

        buttonConvertPdf = findViewById(R.id.buttonConvertPdf)
        progressBar = findViewById(R.id.progressConvert)

        getPermission()

        goBackHome()
        clickToolbar()
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
                            createFolder()
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

    private fun createFolder() {
        val folder = File(folderPath)
        if(!folder.exists()) {
            folder.mkdir()
        }
        directory.mkdir()
    }

    private fun goBackHome() {
        val buttonBack = findViewById<ImageView>(R.id.ivHomeBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun clickToolbar() {
        val botton = findViewById<ImageView>(R.id.ivSelectImage)
        botton.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, botton)
            popupMenu.menuInflater.inflate(R.menu.popup_convert, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.gallery ->
                        Toast.makeText(this@ConvertPdfActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                    R.id.camera ->
                        Toast.makeText(this@ConvertPdfActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                }
                true
            })
            popupMenu.show()
        }
    }


}