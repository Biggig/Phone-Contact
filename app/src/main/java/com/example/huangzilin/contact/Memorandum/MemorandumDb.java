package com.example.huangzilin.contact.Memorandum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MemorandumDb {
    private DBOpenHandler dbOpenHandler;
    public MemorandumDb(Context context) {
        this.dbOpenHandler = new DBOpenHandler(context, "memorandumDb.db3", null, 1);
    }

    public void insert(ContentValues cv) {
        SQLiteDatabase db = dbOpenHandler.getWritableDatabase();
        db.insert("things", null, cv);
        db.close();
    }

    public int delete(String where, String[] whereArgs) {
        SQLiteDatabase db = dbOpenHandler.getWritableDatabase();
        int ret = db.delete("things", where, whereArgs);
        db.close();
        return ret;//影响的记录数
    }

    public int update( ContentValues cv, String where, String[] whereArgs) {
        SQLiteDatabase db = dbOpenHandler.getWritableDatabase();
        int ret = db.update("things", cv, where, whereArgs);
        db.close();
        return ret;
    }

    public Cursor query(String[] projection, String where, String[] whereArgs, String sortOrder) {
        SQLiteDatabase db = dbOpenHandler.getWritableDatabase();
        Cursor cursor = db.query("things", projection, where, whereArgs, null, null, sortOrder, null);
        //db.close();
        return cursor;
    }

    public long getCount() {
        SQLiteDatabase db = dbOpenHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from things", null);
        cursor.moveToFirst();
        db.close();
        return cursor.getLong(0);
    }
}

