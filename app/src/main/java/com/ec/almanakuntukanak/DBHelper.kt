package com.ec.almanakuntukanak

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "Almanak"
        private const val DATABASE_VERSION = 1

        const val entries = "entries"
        const val entry_id = "id"
        const val entry_name = "name"
        const val entry_jk = "jk"
        const val entry_tpl = "tpl"
        const val entry_tgl = "tgl"
        const val entry_nik = "nik"
        const val entry_kk = "kk"
        const val entry_jkn = "jkn"

        const val visits = "visits"
        const val visit_id = "id"
        const val visit_entry_id = "entry_id"
        const val visit_type = "type"
        const val visit_date = "date"
        const val visit_time = "time"
        const val visit_notes = "notes"
        const val visit_status = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $entries")
        val qUser = ("CREATE TABLE $entries (" +
            "$entry_id INTEGER PRIMARY KEY, " +
            "$entry_name TEXT, " +
            "$entry_jk INTEGER, " +
            "$entry_tpl TEXT, " +
            "$entry_tgl INTEGER, " +
            "$entry_nik TEXT, " +
            "$entry_kk TEXT, " +
            "$entry_jkn TEXT" + ")")
        db.execSQL(qUser)

        db.execSQL("DROP TABLE IF EXISTS $visits")
        val qVisits = ("CREATE TABLE $visits (" +
            "$visit_id INTEGER PRIMARY KEY, " +
            "$visit_entry_id INTEGER, " +
            "$visit_type INTEGER, " +
            "$visit_date INTEGER, " +
            "$visit_time TEXT, " +
            "$visit_notes TEXT, " +
            "$visit_status INTEGER" + ")")
        db.execSQL(qVisits)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $entries")
        db.execSQL("DROP TABLE IF EXISTS $visits")
        onCreate(db)
    }

    fun getEntries(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $entries", null)
    }

    fun getEntry(id: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $entries WHERE $entry_id = $id", null)
    }

    fun getLastEntry(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT MAX($entry_id) id FROM $entries", null)
    }

    fun addEntry(nama: String, jk: Int, tpl: String, tgl: Int, nik: String, kk: String, jkn: String) {
        val db = this.readableDatabase
        val values = ContentValues()
        values.put(entry_name, nama)
        values.put(entry_jk, jk)
        values.put(entry_tpl, tpl)
        values.put(entry_tgl, tgl)
        values.put(entry_nik, nik)
        values.put(entry_kk, kk)
        values.put(entry_jkn, jkn)
        db.insert(entries, null, values)
        db.close()
    }

    fun updEntry(id: Int, name: String, jk: Int, tpl: String, tgl: Int, nik: String, kk: String, jkn: String) {
        val db = this.readableDatabase
        val values = ContentValues()
        values.put(entry_name, name)
        values.put(entry_jk, jk)
        values.put(entry_tpl, tpl)
        values.put(entry_tgl, tgl)
        values.put(entry_nik, nik)
        values.put(entry_kk, kk)
        values.put(entry_jkn, jkn)
        db.update(entries, values, "$entry_id = ?", Array(1) { id.toString() })
        db.close()
    }

    fun delEntry(id: Int) {
        val db = this.writableDatabase
        db.delete(entries, "$entry_id = ?", Array(1) { id.toString() })
        db.close()
    }

    fun getVisit(id: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $visits WHERE $visit_id = $id", null)
    }

    fun getVisits(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $visits WHERE $visit_status < 10 ORDER BY $visit_date, $visit_time", null)
    }

    fun getVisits(type: Int, entry: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $visits WHERE $visit_type = $type and $visit_entry_id = $entry ORDER BY $visit_date, $visit_time", null)
    }

    fun getVisitByName(entry: Int, notes: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $visits WHERE $visit_notes = '$notes' and $visit_entry_id = $entry", null)
    }

    fun addVisit(entryId: Int, type: Int, date: Int, time: String, notes: String, status: Int) {
        val values = ContentValues()
        values.put(visit_entry_id, entryId)
        values.put(visit_type, type)
        values.put(visit_date, date)
        values.put(visit_time, time)
        values.put(visit_notes, notes)
        values.put(visit_status, status)

        val db = this.writableDatabase
        db.insert(visits, null, values)
        db.close()
    }

    fun updVisit(id: Int, entryId: Int, type: Int, date: Int, time: String, notes: String, status: Int) {
        val values = ContentValues()
        values.put(visit_entry_id, entryId)
        values.put(visit_type, type)
        values.put(visit_date, date)
        values.put(visit_time, time)
        values.put(visit_notes, notes)
        values.put(visit_status, status)

        val db = this.writableDatabase
        db.update(visits, values, "$visit_id = ?", Array(1) { id.toString() })
        db.close()
    }

    fun updVisitByName(entryId: Int, date: Int, time: String, notes: String) {
        val values = ContentValues()
        values.put(visit_entry_id, entryId)
        values.put(visit_date, date)
        values.put(visit_time, time)
        values.put(visit_status, 0)

        val db = this.writableDatabase
        db.update(visits, values, "$visit_notes = '$notes' and $visit_entry_id = $entryId", null)
        db.close()
    }

    fun updVisitAsDone(id: Int) {
        val values = ContentValues()
        values.put(visit_status, 1)

        val db = this.writableDatabase
        db.update(visits, values, "$visit_id = ?", Array(1) { id.toString() })
        db.close()
    }

    fun updVisitAsDoneByName(entryId: Int, notes: String) {
        val values = ContentValues()
        values.put(visit_status, 1)

        val db = this.writableDatabase
        db.update(visits, values, "$visit_notes = '$notes' and $visit_entry_id = $entryId", null)
        db.close()
    }

    fun turnOffVisit(id: Int) {
        val db = this.writableDatabase
        db.execSQL("update $visits set $visit_status = $visit_status + 10 where $visit_entry_id = $id")
        db.close()
    }

    fun turnOnVisit(id: Int) {
        val db = this.writableDatabase
        db.execSQL("update $visits set $visit_status = $visit_status - 10 where $visit_entry_id = $id")
        db.close()
    }

    fun snoozeVisit(id: String, date: Int) {
        val values = ContentValues()
        values.put(visit_date, date)

        val db = this.writableDatabase
        db.update(visits, values, "$visit_id = ?", Array(1) { id })
        db.close()
    }

    fun delVisit(id: Int) {
        val db = this.writableDatabase
        db.delete(visits, "$visit_id = ?", Array(1) { id.toString() })
        db.close()
    }

    fun delAllVisits(type: Int, entry: Int) {
        val db = this.writableDatabase
        db.delete(visits, "$visit_type = $type and $visit_entry_id = $entry", null)
        db.close()
    }
}