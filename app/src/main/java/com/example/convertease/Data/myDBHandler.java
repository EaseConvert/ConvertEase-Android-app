package com.example.convertease.Data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.convertease.Params.params;
import com.example.convertease.model.History;

import java.util.ArrayList;
import java.util.List;

public class myDBHandler extends SQLiteOpenHelper {
    public myDBHandler(Context context){
        super(context, params.DB_NAME,null,params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    String create = "CREATE TABLE " + params.TABLE_NAME + " (" +params.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +params.KEY_NAME + " TEXT, " +params.KEY_DATE + " DATE, " +params.KEY_PATH + " TEXT)";
    Log.d("dbHistory","Query is Running " + create);
    db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addHistory(History history){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(params.KEY_NAME, history.getName());
        values.put(params.KEY_DATE, history.getDate());
        values.put(params.KEY_PATH, history.getPath());

        db.insert(params.TABLE_NAME, null , values);
        Log.d( "dbHistory","Successfully Inserted!");
        db.close();
    }
    public List<History> getHistory(){
        List<History> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + params.TABLE_NAME;
        Cursor cursor = db.rawQuery(select,null);

        if(cursor.moveToFirst()){
            do {
                History history = new History();
                history.setId(cursor.getInt(0));
                history.setName(cursor.getString(1));
                history.setDate(cursor.getString(2));
                history.setPath(cursor.getString(3));

                historyList.add(history);
            }while(cursor.moveToNext());
        }
        return historyList;
    }
}
