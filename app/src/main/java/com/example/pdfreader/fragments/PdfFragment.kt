package com.example.pdfreader.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.databinding.FragmentPdfBinding
import com.example.pdfreader.databinding.ItemFileBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.LoadFileFromDevice
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class PdfFragment : Fragment(){

    private lateinit var binding: FragmentPdfBinding
    private lateinit var item: ItemFileBinding
    private lateinit var adapter: FileAdapter



    // sort file
    private var currentSortOrder: SortOrder = SortOrder.ASCENDING
    var currentSortType: SortType = SortType.NAME

    enum class SortOrder {
        ASCENDING, // Tăng dần
        DESCENDING // Giảm dần
    }

    enum class SortType {
        NAME,
        SIZE,
        DATE
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPdfBinding.inflate(inflater, container, false)
        item = ItemFileBinding.inflate(inflater, container, false)

        adapter = FileAdapter(ArrayList(), requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcyPdfFile.layoutManager = LinearLayoutManager(context)
        binding.rcyPdfFile.adapter = adapter

        loadAllFile()

    }

    fun loadAllFile() {
        val fileHelper = LoadFileFromDevice(requireContext())
        val extensions = listOf("pdf")

        val adapter = FileAdapter(fileHelper.getAllFilesByExtensions(extensions), requireContext())
        binding.rcyPdfFile.adapter = adapter

    }

    fun loadPdfFileByPath() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val selectedFilePath = sharedPreferences.getString("selected_file_path_pdf", null)

        if (selectedFilePath != null) {
            val pdfFiles: MutableList<FileItem> = ArrayList()
            val file = File(selectedFilePath)
            if (file.exists()) {
                val fileName = file.name
                val dateModified = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(file.lastModified())
                val fileSize = getFileSize(file)
                val fileType = "pdf"
                val fileItem = FileItem(fileName, selectedFilePath, dateModified, fileSize, fileType)
                pdfFiles.add(fileItem)

                val adapter = FileAdapter(pdfFiles, requireContext())
                binding.rcyPdfFile.adapter = adapter
            }
        }
    }

    private fun getFileSize(file: File): String {
        val length = file.length()
        val lengthInKB = length / 1024
        return if (lengthInKB >= 1024) {
            val lengthInMB = lengthInKB / 1024
            String.format(Locale.US, "%.2f MB", lengthInMB.toFloat())
        } else {
            String.format(Locale.US, "%d KB", lengthInKB)
        }
    }



    fun loadFavoriteFilePdf() {
        val dbHelper = FileDBSQLite(requireContext())

        val fileType = "pdf"
        val listFileFavorite = dbHelper.getAllFileFavorite(fileType)
        val pdfFiles: MutableList<FileItem> = ArrayList()

        for (f in listFileFavorite) {
            var item = FileItem(f.namefile, f.pathfile, f.datefile, f.sizefile, f.typefile)
            pdfFiles.add(item)
        }

        val adapter = FileAdapter(pdfFiles, requireContext())
        binding.rcyPdfFile.adapter = adapter

    }



}