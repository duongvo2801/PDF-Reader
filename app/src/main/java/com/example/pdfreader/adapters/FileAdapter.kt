package com.example.pdfreader.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfreader.R
import com.example.pdfreader.activities.DocumentReaderActivity
import com.example.pdfreader.databinding.ItemFileBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.sqlite.FileModel


class FileAdapter(private var allFiles: List<FileItem>, private val context : Context) :
    RecyclerView.Adapter<FileAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }


    fun setFilteredList(allFiles: List<FileItem>) {
        this.allFiles = allFiles
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pdfFile = allFiles[position]
        val dbHelper = FileDBSQLite(context)
        holder.bind(pdfFile,dbHelper)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DocumentReaderActivity::class.java)
            intent.putExtra("path", pdfFile.path)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return allFiles.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemFileBinding.bind(itemView)

        fun bind(fileItem: FileItem, dbHelper: FileDBSQLite) {

            binding.tvNameFile.text = fileItem.name
            binding.dateFile.text = fileItem.datefile
            binding.fileSize.text = fileItem.sizefile
            if(fileItem.name.endsWith(".pdf")) {
                binding.imageFile.setImageResource(R.drawable.icon_pdf)
            }
            else if(fileItem.name.endsWith(".doc") || fileItem.name.endsWith(".docx")) {
                binding.imageFile.setImageResource(R.drawable.icon_word)
            }
            else if(fileItem.name.endsWith(".xls") || fileItem.name.endsWith(".xlsx")) {
                binding.imageFile.setImageResource(R.drawable.icon_excel)
            }
            else if(fileItem.name.endsWith(".ppt") || fileItem.name.endsWith(".pptx")) {
                binding.imageFile.setImageResource(R.drawable.icon_ppt)
            }
            if(dbHelper?.getFile(fileItem.path) == null) {
                binding.btnFavorite.setBackgroundResource(R.drawable.baseline_star_border_24)
            }
            else {
                binding.btnFavorite.setBackgroundResource(R.drawable.baseline_star_24)
            }


            binding.btnFavorite.setOnClickListener{
                if(dbHelper?.getFile(fileItem.path) == null) {
                    var file = FileModel()
                    file.namefile = fileItem.name
                    file.pathfile = fileItem.path
                    file.datefile = fileItem.datefile
                    file.sizefile = fileItem.sizefile
                    dbHelper?.addFile(file)
                    binding.btnFavorite.setBackgroundResource(R.drawable.baseline_star_24)
                }
                else {
                    //remove
                    binding.btnFavorite.setBackgroundResource(R.drawable.baseline_star_border_24)
                    dbHelper?.delete(fileItem.path)
                }

            }

            // Set click listeners or other operations if needed
        }

    }
}
