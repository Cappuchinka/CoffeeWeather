package ru.kapuchinka.coffeeweather;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "CoffeeCity.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE cities(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "city_name TEXT UNIQUE" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cities");
    }

    public Boolean insertCityData(String city) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("city_name", city);
        long result = DB.insert("cities", null, contentValues);

        return result != -1;
    }

    public Boolean deleteCity(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = DB.rawQuery("SELECT * FROM cities where name = ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = DB.delete("cities", "name=?", new String[]{name});
            return result != -1;
        } else {
            return false;
        }
    }

    public Cursor getCities()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("SELECT city_name FROM cities", null);
    }
}
