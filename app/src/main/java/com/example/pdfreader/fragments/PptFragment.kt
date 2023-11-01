package com.example.pptreader.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.databinding.FragmentPptBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.LoadFileFromDevice


class PptFragment : Fragment() {

    private lateinit var binding: FragmentPptBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPptBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now you can safely access views using binding
        loadAllFilePPT()
    }


    fun loadAllFilePPT() {
        binding.rcyPptFile.layoutManager = LinearLayoutManager(context)
        val fileHelper = LoadFileFromDevice(requireContext())
        val extensions = listOf("pptx")

        val adapter = FileAdapter(fileHelper.getAllFilesByExtensions(extensions), requireContext())
        binding.rcyPptFile.adapter = adapter
    }


    fun loadFavoriteFilePpt() {
        binding.rcyPptFile.adapter = null
        val dbHelper = FileDBSQLite(requireContext())
        val fileType = "ppt"
        val listFileFavorite = dbHelper.getAllFileFavorite(fileType)
        val pdfFiles: MutableList<FileItem> = ArrayList()

        for (f in listFileFavorite) {
            var item = FileItem(f.namefile, f.pathfile, f.datefile, f.sizefile, f.typefile)
            pdfFiles.add(item)
        }

        val adapter = FileAdapter(pdfFiles, requireContext())
        binding.rcyPptFile.adapter = adapter


    }
}