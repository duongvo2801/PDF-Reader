package com.example.pdfreader.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.example.pdfreader.utils.loadFileFromDevice
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var rcyFile: RecyclerView
    private lateinit var searchView: SearchView
    private val mList = ArrayList<FileItem>()
    private lateinit var adapter: FileAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        rcyFile = findViewById(R.id.rcyFile)
        searchView = findViewById(R.id.searchView)


        loadAllFilePDF()
        itemSearch()

        goBackHome()
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.pdf)
    }

    fun loadAllFilePDF() {
//        rcyFile.adapter = null
        rcyFile.layoutManager = LinearLayoutManager(this)
        val fileHelper = loadFileFromDevice(this)

        val adapter = FileAdapter(fileHelper.getAllFilesbyExtension("pdf"), this)
        rcyFile.adapter = adapter

        Log.d("qqq", adapter.itemCount.toString())

    }

    fun itemSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = ArrayList<FileItem>()
                if(newText != null) {
                    for (i in mList) {
                        if(i.name.lowercase(Locale.ROOT).contains(newText)) {
                            filteredList.add(i)

                        }
                    }
                    if(filteredList.isEmpty()) {
                        Toast.makeText(applicationContext,
                            getString(R.string.data_found), Toast.LENGTH_LONG).show()
                    } else {
                        adapter.setFilteredList(filteredList)
                    }
                }

                return true
            }
        })
    }

    private fun goBackHome() {
        val buttonBack = findViewById<ImageView>(R.id.ivHomeBack)
        buttonBack.setOnClickListener {
           onBackPressed()
        }
    }


}
