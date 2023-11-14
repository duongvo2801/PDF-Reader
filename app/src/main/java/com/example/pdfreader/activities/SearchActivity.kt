package com.example.pdfreader.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfreader.R
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.utils.LoadAllFile
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var rcyFile: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: FileAdapter
    private lateinit var originalList: List<FileItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        rcyFile = findViewById(R.id.rcyFile)
        searchView = findViewById(R.id.searchView)

        rcyFile.layoutManager = LinearLayoutManager(this)
        val fileHelper = LoadAllFile(this)

        //
        originalList = fileHelper.getAllFilesbyExtensions(listOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"))
        adapter = FileAdapter(originalList, this)

        rcyFile.adapter = adapter


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val filteredList = ArrayList<FileItem>()

                    if (newText.isEmpty()) {
                        adapter.updateFileList(originalList)
                    } else {
                        for (file in originalList) {
                            if (file.name.lowercase(Locale.ROOT)
                                    .contains(newText.lowercase(Locale.ROOT))
                            ) {
                                filteredList.add(file)
                            }
                        }

                        adapter.updateFileList(filteredList)


                        if (filteredList.isEmpty()) {
                            Toast.makeText( applicationContext, getString(R.string.data_found), Toast.LENGTH_LONG ).show()
                        }
                    }
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            adapter.updateFileList(originalList)
            true
        }

        goBackHome()
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.pdf)
    }

    private fun goBackHome() {
        val buttonBack = findViewById<ImageView>(R.id.ivHomeBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
    }
}

