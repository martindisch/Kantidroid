package com.example.kontingentprototype;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "kontManager";
	private static final String TABLE_KONT = "kontingent";

	// Columns
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_KONT_AV = "kont_av";
	private static final String KEY_KONT_US = "kont_us";
	private static final String KEY_DATES = "dates";

	// constructor
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// creating db
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_KONT_TABLE = "CREATE TABLE " + TABLE_KONT + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_KONT_AV
				+ " TEXT," + KEY_KONT_US + " TEXT," + KEY_DATES + " TEXT" + ")";
		db.execSQL(CREATE_KONT_TABLE);
	}

	// upgrading db
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// drop wenn ältere table verfügbar
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_KONT);
		// create table again
		onCreate(db);
	}

	/**
	 * All CRUD Operations
	 */

	// Add Fach
	public void addFach(Fach fach) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, fach.getName());
		values.put(KEY_KONT_AV, fach.getKont_av());
		values.put(KEY_KONT_US, "0");
		values.put(KEY_DATES, "empty");

		// inserting
		db.insert(TABLE_KONT, null, values);
		db.close();
	}

	// Get Fach
	public Fach getFach(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_KONT, new String[] { KEY_ID, KEY_NAME,
				KEY_KONT_AV, KEY_KONT_US, KEY_DATES }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		Fach fach = new Fach(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4));

		cursor.close();
		db.close();

		return fach;
	}

	// Get all fächer
	public List<Fach> getAllFaecher(Context context) {
		SharedPreferences settings = context.getSharedPreferences("mysettings",
				context.MODE_PRIVATE);
		String sSorting = " ORDER BY name";

		int iSorting = settings.getInt("sorting", 0);
		switch (iSorting) {
		case 0:
			sSorting = " ORDER BY name";
			break;
		case 1:
			sSorting = " ORDER BY kont_us DESC";
			break;
		case 2:
			sSorting = " ORDER BY kont_us ASC";
			break;
		}

		List<Fach> fachList = new ArrayList<Fach>();
		String selectQuery = "SELECT  * FROM " + TABLE_KONT + sSorting;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Fach fach = new Fach();
				fach.setID(Integer.parseInt(cursor.getString(0)));
				fach.setName(cursor.getString(1));
				fach.setKont_av(cursor.getString(2));
				fach.setKont_us(cursor.getString(3));
				fach.setDates(cursor.getString(4));
				// adding fach to list
				fachList.add(fach);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();

		return fachList;
	}

	// get Fach count
	public int getFachCount() {
		String countQuery = "SELECT  * FROM " + TABLE_KONT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int i = cursor.getCount();

		cursor.close();
		db.close();

		return i;
	}

	// update entry
	public int updateFach(Fach fach) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, fach.getName());
		values.put(KEY_KONT_AV, fach.getKont_av());
		values.put(KEY_KONT_US, fach.getKont_us());
		values.put(KEY_DATES, fach.getDates());

		return db.update(TABLE_KONT, values, KEY_ID + " = ?",
				new String[] { String.valueOf(fach.getID()) });
	}

	// delete entry
	public void deleteFach(Fach fach) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_KONT, KEY_ID + " = ?",
				new String[] { String.valueOf(fach.getID()) });
		db.close();
	}

}
