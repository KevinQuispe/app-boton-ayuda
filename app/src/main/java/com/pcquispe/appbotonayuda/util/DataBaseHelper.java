package com.pcquispe.appbotonayuda.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "contact.db";
    public static final String TABLE_NAME = "contact_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "NUMBER";
    public static final String COL_4 = "ESTADO";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,NUMBER TEXT NOT NULL UNIQUE,ESTADO INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    }

    //metodo insertar o guardar
    public boolean insertData(String name, String number, Integer estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, number);
        contentValues.put(COL_4, estado);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //metodo listar datos
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT*FROM " + TABLE_NAME, null);
        return res;
    }

    public Cursor getDataById(Integer estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM student_table WHERE ID = " + estado;
        Cursor res = db.rawQuery(q, null);
        return res;
    }

    //metodo par actualizar data
    public boolean upateData(String id, String name, String number, Integer estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, number);
        contentValues.put(COL_4, estado);
        int result = db.update(TABLE_NAME, contentValues, "ID=?", new String[]{id});
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }
}

