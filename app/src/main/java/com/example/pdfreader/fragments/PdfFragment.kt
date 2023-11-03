package com.example.pdfreader.fragments

import android.content.SharedPreferences
import android.os.Bundle
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


class PdfFragment : Fragment(){

    private lateinit var binding: FragmentPdfBinding
    private lateinit var item: ItemFileBinding
    private lateinit var adapter: FileAdapter

    private lateinit var sharedPreferences: SharedPreferences


//    // sort file
//    private var currentSortOrder: SortOrder = SortOrder.ASCENDING
//    var currentSortType: SortType = SortType.NAME
//
//    enum class SortOrder {
//        ASCENDING, // Tăng dần
//        DESCENDING // Giảm dần
//    }
//
//    enum class SortType {
//        NAME,
//        SIZE,
//        DATE
//    }



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


        loadAllFilePDF()

    }



    fun loadAllFilePDF() {
        binding.rcyPdfFile.layoutManager = LinearLayoutManager(context)
        val fileHelper = LoadFileFromDevice(requireContext())
        val extensions = listOf("pdf")

        val adapter = FileAdapter(fileHelper.getAllFilesByExtensions(extensions), requireContext())
        binding.rcyPdfFile.adapter = adapter

    }

    fun loadRecentFilePdf() {
        binding.rcyPdfFile.adapter = null
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

    fun loadFavoriteFilePdf() {
        binding.rcyPdfFile.adapter = null
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