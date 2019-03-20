package com.hty.browser;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = MainActivity.dir + File.separator + "webfav.db";
	private final static int VERSION = 1;
	static String TableName = "webfav";
	private SQLiteDatabase db;
	private static DBHelper mInstance = null;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	public static synchronized DBHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DBHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL("CREATE TABLE webfav (_id INTEGER PRIMARY KEY , website TEXT, title TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("DROP TABLE IF EXISTS battery");
		// onCreate(db);
		switch (newVersion) {
		case 6:
			break;
		}
	}

	public void insert(ContentValues values) {
		db = getWritableDatabase();
		db.insert(TableName, null, values);
		db.close();
	}

	public Cursor query(String s) {
		db = getWritableDatabase();
		Cursor c = null;
		if (s.equals("")) {
			c = db.query(TableName, null, null, null, null, null, "_id desc");
		} else {
            c = db.query(TableName, null, "website LIKE '%" + s + "%' or title LIKE '%" + s + "%'", null, null, null, "_id desc");
        }
		return c;
	}

	public void del(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(TableName, "_id=?", new String[] { String.valueOf(id) });
		// Log.e("id", id + "");
		// db.ExecuteNonQuery(CommandType.Text, "VACUUM");
	}

	@Override
	public void close() {
		if (db != null)
			db.close();
	}
}