package com.example.pdfreader.fragments

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
import com.example.pdfreader.entities.SortOrder
import com.example.pdfreader.entities.SortType
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.utils.LoadFileFromDevice


class PdfFragment : Fragment(){

    private lateinit var binding: FragmentPdfBinding
    private lateinit var item: ItemFileBinding
    private lateinit var adapter: FileAdapter

    // sort file
    private var currentSortOrder: SortOrder = SortOrder.ASCENDING
    private var currentSortType: SortType = SortType.NAME


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

//        binding.rcyPdfFile = view.findViewById(R.id.rcy_pdf_file)

        loadAllFilePDF()
        sortAndRefreshList()

        val newFiles = listOf<FileItem>()
        adapter.updateFileList(newFiles)

    }

     fun sortAndRefreshList() {
//        val fileHelper = LoadFileFromDevice(requireContext())
//        val pdfFiles = fileHelper.getAllFilesbyExtension("pdf")

//        when (currentSortType) {
//            SortType.NAME -> {
//                pdfFiles.sortWith(compareBy { it.name })
//            }
//            SortType.DATE -> {
//                pdfFiles.sortWith(compareBy { it.date })
//            }
//            SortType.SIZE -> {
//                pdfFiles.sortWith(compareBy { it.size })
//            }
//        }
//
//        if (currentSortOrder == SortOrder.DESCENDING) {
//            pdfFiles.reverse()
//        }
//
//        adapter.updateFileList(pdfFiles)
    }



//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//    }


    fun loadAllFilePDF() {
//        binding.rcyPdfFile.adapter = null
        binding.rcyPdfFile.layoutManager = LinearLayoutManager(context)
        val fileHelper = LoadFileFromDevice(requireContext())

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