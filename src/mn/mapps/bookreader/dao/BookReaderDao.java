package mn.mapps.bookreader.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BookReaderDao extends SQLiteOpenHelper {

	private static int DATABASE_VERSION = 1;
	private static String DATABASE_NAME = "bookreader.db";
	private static String TABLE_NAME = "book";
	private static String category_name = "category_name";
	private static String subText = "subText";
	private static String book_id = "book_id";
	private static String book_name = "book_name";
	private static String author_name = "author_name";
	private static String description = "description";
	private static String price = "price";
	private static String genre = "genre";
	private static String language = "language";
	private static String publisher = "publisher";
	private static String published_year = "published_year";
	private static String rating = "rating";
	private static String rating_count = "rating_count";
	private static String isNew = "isNew";
	private static String isBookmarked = "isBookmarked";
	private static String added_to_mapps_date = "added_to_mapps_date";

	public BookReaderDao(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
