package zz.itcast.studentschedule.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ExcelDbHelper extends SQLiteOpenHelper {

	public ExcelDbHelper(Context context, String name,
						 int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table if not exists timetable(_id integer primary key autoincrement, " +
				"date integer, week varchar(20),content varchar(50),room varchar(20)," +
				"teacher varchar(10),ps text,grade varchar(20));");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
