package com.example.pptreader.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.databinding.FragmentExcelBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.LoadFileFromDevice


class ExcelFragment : Fragment() {

    private lateinit var binding: FragmentExcelBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExcelBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now you can safely access views using binding
        loadAllFileExcel()
    }


    fun loadAllFileExcel() {
//        binding.rcyExcelFile.adapter = null
        binding.rcyExcelFile.layoutManager = LinearLayoutManager(context)
        val fileHelper = LoadFileFromDevice(requireContext())

        val adapter = FileAdapter(fileHelper.getAllFilesbyExtension("xlsx"), requireContext())
        binding.rcyExcelFile.adapter = adapter
    }


    fun loadFavoriteFileExcel() {
//        binding.rcyExcelFile.adapter = null
        val dbHelper = FileDBSQLite(requireContext())
        val listFileFavorite = dbHelper.getAllFileFavorite()
        val pdfFiles: MutableList<FileItem> = ArrayList()

        for (f in listFileFavorite) {
            var item = FileItem(f.namefile, f.pathfile, f.datefile, f.sizefile)
            pdfFiles.add(item)
        }

        val adapter = FileAdapter(pdfFiles, requireContext())
        binding.rcyExcelFile.adapter = adapter


    }
}