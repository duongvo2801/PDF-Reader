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
    fun getAllFilesByExtensions(extensions: List<String>): List<FileItem> {
        val filteredFiles: MutableList<FileItem> = ArrayList()

        // Get internal storage directory
        val internalFilesDir = context.filesDir
        if (internalFilesDir != null) {
            getFilesRecursively(internalFilesDir, filteredFiles, extensions)
        }

        // Get external storage directory
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        getFilesRecursively(externalStorageDirectory, filteredFiles, extensions)

        return filteredFiles
    }

    fun roundDecimalWithDecimalFormat(number: Double, decimalPlaces: Int): String {
        val pattern = "#.${"#".repeat(decimalPlaces)}"
        val decimalFormat = DecimalFormat(pattern)
        return decimalFormat.format(number)
    }
    private fun getFilesRecursively(directory: File, filteredFiles: MutableList<FileItem>, extensions: List<String>) {
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    // Recursive call for subdirectories
                    getFilesRecursively(file, filteredFiles, extensions)
                } else if (file.isFile) {
                    val fileExtension = file.extension.toLowerCase(Locale.ROOT)
                    if (extensions.contains(fileExtension)) {
                        val lastModifiedTime = file.lastModified()
                        // Convert the timestamp to a human-readable date format
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDate = sdf.format(Date(lastModifiedTime))
                        filteredFiles.add(FileItem(file.name, file.path, formattedDate, roundDecimalWithDecimalFormat((file.length() / 1024.0), 2).toString() + "Kb", getFileType(file.name)))
                    }
                }
            }
        }
    }

    fun getFileType(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf(".")

        if (lastDotIndex == -1 || lastDotIndex == 0 || lastDotIndex == fileName.length - 1) {
        }

        val fileExtension = fileName.substring(lastDotIndex + 1)

        return when (fileExtension.toLowerCase()) {
            "pdf" -> "pdf"
            "doc", "docx" -> "word"
            "xls", "xlsx" -> "excel"
            "ppt", "pptx" -> "ppt"
            else -> "other"
        }
    }

}