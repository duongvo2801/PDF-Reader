package com.example.pdfreader.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfreader.R
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.databinding.FragmentPdfBinding
import com.example.pdfreader.databinding.ItemFileBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.loadFileFromDevice
import com.google.android.material.bottomsheet.BottomSheetDialog


class PdfFragment : Fragment(){

    private lateinit var binding: FragmentPdfBinding
    private lateinit var item: ItemFileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPdfBinding.inflate(inflater, container, false)
        item = ItemFileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now you can safely access views using binding
        loadAllFilePDF()


    }

    @SuppressLint("MissingInflatedId")
    fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.bottom_dialog)

            findViewById<LinearLayout>(R.id.copyLinearLayout)?.setOnClickListener {
                Toast.makeText(context, "Copy is Clicked ", Toast.LENGTH_LONG).show();
                dismiss()
            }

            findViewById<LinearLayout>(R.id.shareLinearLayout)?.setOnClickListener {
                // Handle share action
            }

            findViewById<LinearLayout>(R.id.uploadLinearLayout)?.setOnClickListener {
                // Handle upload action
            }

            findViewById<LinearLayout>(R.id.download)?.setOnClickListener {
                // Handle download action
            }

            findViewById<LinearLayout>(R.id.delete)?.setOnClickListener {
                // Handle delete action
            }
        }

        bottomSheetDialog.show()
    }



    fun loadAllFilePDF() {
        binding.rcyPdfFile.adapter = null
        binding.rcyPdfFile.layoutManager = LinearLayoutManager(context)
        val fileHelper = loadFileFromDevice(requireContext())

        val adapter = FileAdapter(fileHelper.getAllFilesbyExtension("pdf"), requireContext())
        binding.rcyPdfFile.adapter = adapter

    }

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