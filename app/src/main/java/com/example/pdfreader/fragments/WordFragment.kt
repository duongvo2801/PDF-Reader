package com.example.pdfreader.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfreader.R
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.databinding.FragmentWordBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.loadFileFromDevice


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

        // Now you can safely access views using binding
        loadAllFileWord()
    }

    fun loadAllFileWord() {
        binding.rcyWordFile.adapter = null
        binding.rcyWordFile.layoutManager = LinearLayoutManager(context)
        val fileHelper = loadFileFromDevice(requireContext())

        val adapter = FileAdapter(fileHelper.getAllFilesbyExtension("doc"), requireContext())
        binding.rcyWordFile.adapter = adapter
    }

    fun loadFavoriteFileWord() {
        binding.rcyWordFile.adapter = null
        val dbHelper = FileDBSQLite(requireContext())
        val listFileFavorite = dbHelper.getAllFileFavorite()
        val pdfFiles: MutableList<FileItem> = ArrayList()

        for (f in listFileFavorite) {
            var item = FileItem(f.namefile, f.pathfile, f.datefile, f.sizefile)
            pdfFiles.add(item)
        }

        val adapter = FileAdapter(pdfFiles, requireContext())
        binding.rcyWordFile.adapter = adapter


    }

}