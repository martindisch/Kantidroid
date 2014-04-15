package com.martin.noten;

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
	private static final String DATABASE_NAME = "markManager";
	private static final String TABLE_MARKS = "noten";

	// Columns
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_NOTEN1 = "noten1";
	private static final String KEY_MATH_AVERAGE1 = "math_average1";
	private static final String KEY_REAL_AVERAGE1 = "real_average1";
	private static final String KEY_NOTEN2 = "noten2";
	private static final String KEY_MATH_AVERAGE2 = "math_average2";
	private static final String KEY_REAL_AVERAGE2 = "real_average2";
	private static final String KEY_ZEUGNIS = "zeugnis";
	private static final String KEY_PROMOTIONSRELEVANT = "promotionsrelevant";

	// constructor
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// creating db
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_KONT_TABLE = "CREATE TABLE " + TABLE_MARKS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_NOTEN1
				+ " Text," + KEY_MATH_AVERAGE1 + " Text," + KEY_REAL_AVERAGE1
				+ " Text," + KEY_NOTEN2 + " Text," + KEY_MATH_AVERAGE2
				+ " Text," + KEY_REAL_AVERAGE2 + " Text," + KEY_ZEUGNIS
				+ " Text," + KEY_PROMOTIONSRELEVANT + " Text" + ")";
		db.execSQL(CREATE_KONT_TABLE);
	}

	// upgrading db
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// drop wenn ältere table verfügbar
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKS);
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
		values.put(KEY_NOTEN1, "-");
		values.put(KEY_MATH_AVERAGE1, "-");
		values.put(KEY_REAL_AVERAGE1, "-");
		values.put(KEY_NOTEN2, "-");
		values.put(KEY_MATH_AVERAGE2, "-");
		values.put(KEY_REAL_AVERAGE2, "-");
		values.put(KEY_ZEUGNIS, "-");
		values.put(KEY_PROMOTIONSRELEVANT, fach.getPromotionsrelevant());

		// inserting
		db.insert(TABLE_MARKS, null, values);
		db.close();
	}

	// Get Fach
	public Fach getFach(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_MARKS, new String[] { KEY_ID, KEY_NAME,
				KEY_NOTEN1, KEY_MATH_AVERAGE1, KEY_REAL_AVERAGE1, KEY_NOTEN2,
				KEY_MATH_AVERAGE2, KEY_REAL_AVERAGE2, KEY_ZEUGNIS,
				KEY_PROMOTIONSRELEVANT }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		Fach fach = new Fach(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4), cursor.getString(5), cursor.getString(6),
				cursor.getString(7), cursor.getString(8), cursor.getString(9));

		cursor.close();
		db.close();

		return fach;
	}

	// Get all fächer
	public List<Fach> getAllFaecher(Context context, int iSemester) {
		SharedPreferences settings = context.getSharedPreferences(
				"MarkSettings", Context.MODE_PRIVATE);

		String sorting = null;

		if (iSemester == 1) {
			switch (settings.getInt("sorting", 1)) {
			case 0:
				sorting = " ORDER BY name";
				break;
			case 1:
				sorting = " ORDER BY math_average1 DESC";
				break;
			case 2:
				sorting = " ORDER BY math_average1 ASC";
				break;
			}
		} else if (iSemester == 2) {
			switch (settings.getInt("sorting", 1)) {
			case 0:
				sorting = " ORDER BY name";
				break;
			case 1:
				sorting = " ORDER BY math_average2 DESC";
				break;
			case 2:
				sorting = " ORDER BY math_average2 ASC";
				break;
			}
		} else {
			switch (settings.getInt("sorting", 1)) {
			case 0:
				sorting = " ORDER BY name";
				break;
			case 1:
				sorting = " ORDER BY zeugnis DESC";
				break;
			case 2:
				sorting = " ORDER BY zeugnis ASC";
				break;
			}
		}

		// String sorting = settings.getString("sort_order",
		// " ORDER BY kont_us DESC");

		// String sorting = " ORDER BY name";

		List<Fach> fachList = new ArrayList<Fach>();
		String selectQuery = "SELECT  * FROM " + TABLE_MARKS + sorting;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Fach fach = new Fach();
				fach.setID(Integer.parseInt(cursor.getString(0)));
				fach.setName(cursor.getString(1));
				fach.setNoten1(cursor.getString(2));
				fach.setMathAverage1(cursor.getString(3));
				fach.setRealAverage1(cursor.getString(4));
				fach.setNoten2(cursor.getString(5));
				fach.setMathAverage2(cursor.getString(6));
				fach.setRealAverage2(cursor.getString(7));
				fach.setZeugnis(cursor.getString(8));
				fach.setPromotionsrelevant(cursor.getString(9));
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
		String countQuery = "SELECT  * FROM " + TABLE_MARKS;
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
		values.put(KEY_NOTEN1, fach.getNoten1());
		values.put(KEY_MATH_AVERAGE1, fach.getMathAverage1());
		values.put(KEY_REAL_AVERAGE1, fach.getRealAverage1());
		values.put(KEY_NOTEN2, fach.getNoten2());
		values.put(KEY_MATH_AVERAGE2, fach.getMathAverage2());
		values.put(KEY_REAL_AVERAGE2, fach.getRealAverage2());
		values.put(KEY_ZEUGNIS, fach.getZeugnis());
		values.put(KEY_PROMOTIONSRELEVANT, fach.getPromotionsrelevant());

		int update = db.update(TABLE_MARKS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(fach.getID()) });
		db.close();
		return update;
	}

	// delete entry
	public void deleteFach(Fach fach) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_MARKS, KEY_ID + " = ?",
				new String[] { String.valueOf(fach.getID()) });
		db.close();
	}

}
