package com.example.pdfreader.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class FileDBSQLite(context : Context) : SQLiteOpenHelper(context, DBNAME, null, DB_VERSION) {
    companion object {
        private val DBNAME = "file_db"
        private val DB_VERSION = 1
        private val TABLE_NAME = "favorite_file"
        private val ID = "id"
        private val FILE_NAME = "filename"
        private val FILE_PATH = "filepath"
        private val FILE_DATE = "filedate"
        private val FILE_SIZE = "filesize"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $FILE_NAME TEXT, $FILE_PATH TEXT, $FILE_DATE TEXT, $FILE_SIZE TEXT);"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun getAllFileFavorite() : List<FileModel> {
        val fileFavList = ArrayList<FileModel>()
        val db = writableDatabase
        val selectQuery = "SELECT *FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val file = FileModel()
                    file.id = Integer.parseInt(cursor.getLong(cursor.getColumnIndexOrThrow(ID)).toString())
                    file.namefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_NAME))
                    file.pathfile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_PATH))
                    file.datefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_DATE))
                    file.sizefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_SIZE))
                    fileFavList.add(file)

                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return fileFavList
    }
    fun getFile(path : String) : FileModel? {

        val db = writableDatabase
        val selectQuery = "SELECT *FROM $TABLE_NAME WHERE $FILE_PATH = '$path'"
        val cursor = db.rawQuery(selectQuery, null)
        if(cursor.moveToFirst()) {
            var file = FileModel()

            file.id = Integer.parseInt(cursor.getLong(cursor.getColumnIndexOrThrow(ID)).toString())
            file.namefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_NAME))
            file.pathfile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_PATH))
            file.datefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_DATE))
            file.sizefile = cursor.getString(cursor.getColumnIndexOrThrow(FILE_SIZE))
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
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)
    }

    fun delete(path : String) : Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, "filepath=?", arrayOf(path))
        db.close()
        return (Integer.parseInt("$_success") != -1)
    }

}
