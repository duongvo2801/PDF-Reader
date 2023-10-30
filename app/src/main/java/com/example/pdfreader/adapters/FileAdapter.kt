package com.example.pdfreader.adapters

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfreader.R
import com.example.pdfreader.activities.DocumentReaderActivity
import com.example.pdfreader.databinding.ItemFileBinding
import com.example.pdfreader.entities.FileItem
import com.example.pdfreader.sqlite.FileDBSQLite
import com.example.pdfreader.sqlite.FileModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class FileAdapter(private var allFiles: List<FileItem>, private val context: Context) :
    RecyclerView.Adapter<FileAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view, context)
    }

    fun updateFileList(newFiles: List<FileItem>) {
        allFiles = newFiles.toList()
        notifyDataSetChanged()
    }

    fun getAllFiles(): List<FileItem> {
        return allFiles
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

    class ViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

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
            binding.moreFile.setOnClickListener { view ->
                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.popup_action, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_send_email -> {
                            sendEmail(fileItem)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_copy -> {
                            copyFile(fileItem)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_rename -> {
                            renameFile(fileItem)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_delete -> {
                            deleteFile(fileItem) // Pass the current file item
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }

                popupMenu.show()
            }
        }

        private fun copyFile(fileItem: FileItem) {
            // file source
            val sourceFile = File(fileItem.path)

            if (!sourceFile.exists()) {
                Toast.makeText(context, "Source file does not exist", Toast.LENGTH_SHORT).show()
                return
            }

            val destinationDirectory = File("/storage/emulated/0/Copy-File")

            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs()
            }

            val destinationFile = File(destinationDirectory, sourceFile.name)

            if (sourceFile != destinationFile) {
                try {
                    val sourceChannel = FileInputStream(sourceFile).channel
                    val destinationChannel = FileOutputStream(destinationFile).channel

                    sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel)

                    sourceChannel.close()
                    destinationChannel.close()

                    Toast.makeText(context, "File copied successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to copy the file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Source and destination are the same", Toast.LENGTH_SHORT).show()
            }
        }


        private fun sendEmail(fileItem: FileItem) {
            val file = File(fileItem.path)

            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    file
                )

                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "application/pdf"
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Email body")

                try {
                    context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show()
            }
        }


        private fun renameFile(fileItem: FileItem) {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_rename)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val renameEditText = dialog.findViewById<EditText>(R.id.renameEditText)
            val renameButton = dialog.findViewById<Button>(R.id.renameButton)

            renameButton.setOnClickListener {
                val newFileName = renameEditText.text.toString()

                if (newFileName.isNotBlank()) {
                    val oldFile = File(fileItem.path)
                    val newFile = File(oldFile.parentFile, newFileName)

                    if (oldFile.renameTo(newFile)) {
                        Toast.makeText(context, "Rename successful", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Can't rename file", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter new name", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }


        private fun deleteFile(fileItem: FileItem) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete file")
            builder.setMessage("Are you sure you want to delete this file?")

            builder.setPositiveButton("Yes") { _, _ ->
                val file = File(fileItem.path)
                if (file.exists()) {
                    if (file.delete()) {
                        Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to delete the file", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("No") { _, _ ->
                //
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

    }
}



