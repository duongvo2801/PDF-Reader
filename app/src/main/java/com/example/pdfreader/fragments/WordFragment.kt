package com.example.pdfreader.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.databinding.FragmentWordBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.LoadFileFromDevice


class WordFragment : Fragment() {
    private lateinit var binding: FragmentWordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loadAllFileWord()
    }

    fun loadAllFileWord() {
        binding.rcyWordFile.layoutManager = LinearLayoutManager(context)
        val fileHelper = LoadFileFromDevice(requireContext())
        val extensions = listOf("docx")

        val adapter = FileAdapter(fileHelper.getAllFilesByExtensions(extensions), requireContext())
        binding.rcyWordFile.adapter = adapter
    }


    fun loadFavoriteFileWord() {
        binding.rcyWordFile.adapter = null
        val dbHelper = FileDBSQLite(requireContext())
        val fileType = "word"
        val listFileFavorite = dbHelper.getAllFileFavorite(fileType)
        val pdfFiles: MutableList<FileItem> = ArrayList()

        for (f in listFileFavorite) {
            var item = FileItem(f.namefile, f.pathfile, f.datefile, f.sizefile, f.typefile)
            pdfFiles.add(item)
        }

        val adapter = FileAdapter(pdfFiles, requireContext())
        binding.rcyWordFile.adapter = adapter


    }

}