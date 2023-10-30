package com.example.pdfreader.utils

import android.content.Context
import android.os.Environment
import com.example.pdfreader.entities.FileItem
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoadFileFromDevice (private val context: Context){
    fun getAllFilesbyExtension(ext : String): List<FileItem> {
        val pdfFiles: MutableList<FileItem> = ArrayList()

        // Get internal storage directory
        val internalFilesDir = context?.filesDir
        if(internalFilesDir != null) {
            getPDFFilesRecursively(internalFilesDir, pdfFiles, ext)
        }

        // Get external storage directory
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        getPDFFilesRecursively(externalStorageDirectory, pdfFiles, ext)
        return pdfFiles
    }

    fun roundDecimalWithDecimalFormat(number: Double, decimalPlaces: Int): String {
        val pattern = "#.${"#".repeat(decimalPlaces)}"
        val decimalFormat = DecimalFormat(pattern)
        return decimalFormat.format(number)
    }
    private fun getPDFFilesRecursively(directory: File, pdfFiles: MutableList<FileItem>, ext: String) {
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    // Recursive call for subdirectories
                    getPDFFilesRecursively(file, pdfFiles, ext)
                } else if (file.isFile && file.extension.equals(ext, ignoreCase = true)) {
                    val lastModifiedTime = file.lastModified()
                    // Convert the timestamp to a human-readable date format
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val formattedDate = sdf.format(Date(lastModifiedTime))
                    pdfFiles.add(FileItem(file.name, file.path, formattedDate, roundDecimalWithDecimalFormat((file.length()/1024.0), 2).toString() + "Kb"))
                }
            }
        }
    }

}