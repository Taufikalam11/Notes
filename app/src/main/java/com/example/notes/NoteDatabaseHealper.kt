package com.example.notes

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy

class NoteDatabaseHealper(context:Context):SQLiteOpenHelper(context, DATABASE_NAME,null,
    DATABASE_VERSION) {

    companion object{
        private const val  DATABASE_NAME = "notesapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val  COLUMN_TITLE= "title"
        private const val  COLUMN_CONTENT= "content"
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(db: SQLiteDatabase?) {
     val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT,$COLUMN_CONTENT TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?,oldVersion: Int,newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)

    }

    fun insertNote(note:Note){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
        }

        db.insert(TABLE_NAME,null,values)
        db.close()
    }

    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val  query = "SELECT*FROM $TABLE_NAME"
        val cursor = db.rawQuery(query,null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

            val note = Note(id,title,content)
            notesList.add(note)
        }

        cursor.close()
        db.close()
        return notesList

    }

    fun updateNote(note:Note){
        val  db = writableDatabase
        val value = ContentValues().apply {
            put(COLUMN_TITLE,note.title)
            put(COLUMN_CONTENT,note.title)
        }

        val whereClause = "$COLUMN_ID=?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME,value,whereClause,whereArgs)
        db.close()
    }

    fun getNoteByID(notesId:Int):Note{
        val  db = readableDatabase
        val query = "SELECT*FROM $TABLE_NAME WHERE $COLUMN_ID=$notesId"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()

        val  id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val  title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val  content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        cursor.close()
        db.close()
        return Note(id,title,content)
    }

    fun deleteNote(noted:Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID=?"
        val whereArgs = arrayOf(noted.toString())
        db.delete(TABLE_NAME,whereClause,whereArgs)
        db.close()
    }
}