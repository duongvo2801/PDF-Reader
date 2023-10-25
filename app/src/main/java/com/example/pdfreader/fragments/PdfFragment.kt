package com.example.pdfreader.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.databinding.FragmentPdfBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.loadFileFromDevice


class PdfFragment : Fragment(){

    private lateinit var binding: FragmentPdfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPdfBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now you can safely access views using binding
        loadAllFilePDF()
    }


    fun loadAllFilePDF() {
        binding.rcyPdfFile.adapter = null
        binding.rcyPdfFile.layoutManager = LinearLayoutManager(context)
        val fileHelper = loadFileFromDevice(requireContext())

        val adapter = FileAdapter(fileHelper.getAllFilesbyExtension("pdf"), requireContext())
        binding.rcyPdfFile.adapter = adapter
    }


    // get data recycleview 1 fill to recycleview 2
    fun loadFavoriteFilePdf() {
        binding.rcyPdfFile.adapter = null
        val dbHelper = FileDBSQLite(requireContext())
        val listFileFavorite = dbHelper.getAllFileFavorite()
        val pdfFiles: MutableList<FileItem> = ArrayList()

        for (f in listFileFavorite) {
            var item = FileItem(f.namefile, f.pathfile, f.datefile, f.sizefile)
            pdfFiles.add(item)
        }

        val adapter = FileAdapter(pdfFiles, requireContext())
        binding.rcyPdfFile.adapter = adapter

    }



}