package com.example.pdfreader.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class FileDBSQLite(context : Context) : SQLiteOpenHelper(context, DBNAME, null, DB_VERSION) {
    companion object {
        private val DBNAME = "file_db"
        private val DB_VERSION = 2
        private val TABLE_NAME = "favorite_file"
        private val ID = "id"
        private val FILE_NAME = "filename"
        private val FILE_PATH = "filepath"
        private val FILE_DATE = "filedate"
        private val FILE_SIZE = "filesize"
        private val FILE_TYPE = "filetype"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $FILE_NAME TEXT, $FILE_PATH TEXT, $FILE_DATE TEXT, $FILE_SIZE TEXT, $FILE_TYPE TEXT);"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $FILE_TYPE TEXT;")
        }
    }

    //
    fun getAllFileFavorite(type: String) : List<FileModel> {
        val fileFavList = ArrayList<FileModel>()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $FILE_TYPE = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(type))
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val file = FileModel()
                    file.id = Integer.parseInt(cursor.getLong(cursor.getColumnIndexOrThrow(ID)).toString())
                    file.namefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_NAME))
                    file.pathfile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_PATH))
                    file.datefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_DATE))
                    file.sizefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_SIZE))
                    file.typefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_TYPE))
                    fileFavList.add(file)

                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return fileFavList
    }


    fun getFile(path : String,  type: String) : FileModel? {

        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $FILE_PATH = ? AND $FILE_TYPE = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(path, type))
        if(cursor.moveToFirst()) {
            var file = FileModel()

            file.id = Integer.parseInt(cursor.getLong(cursor.getColumnIndexOrThrow(ID)).toString())
            file.namefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_NAME))
            file.pathfile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_PATH))
            file.datefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_DATE))
            file.sizefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_SIZE))
            file.typefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_TYPE))
            return file

        }
        else {
            return null
        }
        cursor.close()

    }
    fun addFile(file : FileModel) : Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(FILE_NAME, file.namefile)
        values.put(FILE_PATH, file.pathfile)
        values.put(FILE_DATE, file.datefile)
        values.put(FILE_SIZE, file.sizefile)
        values.put(FILE_TYPE, file.typefile)

        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)
    }

    fun delete(path: String, type: String): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, "$FILE_PATH=? AND $FILE_TYPE=?", arrayOf(path, type))
        db.close()
//        return (Integer.parseInt("$_success") != -1)

        if (_success > 0) {
            Log.d("FileDBSQLite", "Đã xóa tệp có đường dẫn: $path và loại: $type")
            return true
        } else {
            Log.e("FileDBSQLite", "Không thể xóa tệp có đường dẫn: $path và loại: $type")
            return false
        }
    }

}
