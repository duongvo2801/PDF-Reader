package com.example.pdfreader.utils

import android.content.Context
import android.os.Environment
import com.example.pdfreader.entities.FileItem
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoadAllFile(private val context: Context) {
    fun getAllFilesbyExtensions(extensions: List<String>): List<FileItem> {
        val allFiles: MutableList<FileItem> = ArrayList()

        // Get internal storage directory
        val internalFilesDir = context?.filesDir
        if (internalFilesDir != null) {
            getFilesRecursively(internalFilesDir, allFiles, extensions)
        }

        // Get external storage directory
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        getFilesRecursively(externalStorageDirectory, allFiles, extensions)
        return allFiles
    }

    fun roundDecimalWithDecimalFormat(number: Double, decimalPlaces: Int): String {
        val pattern = "#.${"#".repeat(decimalPlaces)}"
        val decimalFormat = DecimalFormat(pattern)
        return decimalFormat.format(number)
    }

    private fun getFilesRecursively(directory: File, files: MutableList<FileItem>, extensions: List<String>) {
        val filesInDirectory = directory.listFiles()

        if (filesInDirectory != null) {
            for (file in filesInDirectory) {
                if (file.isDirectory) {
                    // Recursive call for subdirectories
                    getFilesRecursively(file, files, extensions)
                } else if (file.isFile && isFileWithExtensions(file, extensions)) {
                    val lastModifiedTime = file.lastModified()
                    // Convert the timestamp to a human-readable date format
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val formattedDate = sdf.format(Date(lastModifiedTime))
                    files.add(FileItem(file.name, file.path, formattedDate, roundDecimalWithDecimalFormat((file.length() / 1024.0), 2).toString() + "Kb"))
                }
            }
        }
    }

    private fun isFileWithExtensions(file: File, extensions: List<String>): Boolean {
        for (ext in extensions) {
            if (file.extension.equals(ext, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

}