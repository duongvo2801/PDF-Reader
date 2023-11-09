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
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.LoadFileFromDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class PdfFragment : Fragment(){

    private lateinit var binding: FragmentPdfBinding
//    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPdfBinding.inflate(inflater, container, false)
        binding.rcyPdfFile.layoutManager = LinearLayoutManager(context)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        refreshLayout = view.findViewById(R.id.refreshLayout)
//
//        refreshLayout.setOnRefreshListener {
//
//            Handler().postDelayed(Runnable {
//            refreshLayout.isRefreshing = false
//            }, 2000)
//        }

        loadAllFile()

    }

    fun loadAllFile() {
        CoroutineScope(Dispatchers.IO).launch {
            val fileHelper = LoadFileFromDevice(requireContext())
            val extensions = listOf("pdf")
            val adapter = FileAdapter(fileHelper.getAllFilesByExtensions(extensions), requireContext())

            withContext(Dispatchers.Main) {
                binding.rcyPdfFile.adapter = adapter
            }
        }

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