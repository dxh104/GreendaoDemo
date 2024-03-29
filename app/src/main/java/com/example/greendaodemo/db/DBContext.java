package com.example.greendaodemo.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

public class DBContext extends ContextWrapper {

    public static String dbPath = "";

    public DBContext(Context base, String dbPath) {
        super(base);
        if(!TextUtils.isEmpty(dbPath)){
            this.dbPath = dbPath;
        }
    }

    @Override
    public File getDatabasePath(String name){
        File dbDir = new File(dbPath);
        if(!dbDir.exists()){
            dbDir.mkdirs();
        }
        File dbFile = new File(dbPath, name);
        if(!dbFile.exists()){
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dbFile;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }
}
