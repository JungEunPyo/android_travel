package ddwu.mobile.finalproject.ma02_20160798;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TravelDBHelper extends SQLiteOpenHelper {
    final static String DB_NAME="TravelList.db";
    public final static String TABLE_NAME="travellist_table";
    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_ADDR = "address";
    public final static String COL_DETAILCONTENT = "detailContent";
    public final static String COL_IMAGEFILENAME = "imageFileName";
    public final static String COL_IMAGELINK = "imageLink";
    public final static String COL_MAPX = "mapX";
    public final static String COL_MAPY = "mapY";
    public final static String COL_MEMO = "memo";

    public TravelDBHelper(Context context){super(context, DB_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, "
                + COL_TITLE + " TEXT, " + COL_ADDR + " TEXT, " + COL_DETAILCONTENT + " TEXT, " + COL_IMAGEFILENAME + " TEXT, " + COL_IMAGELINK +" TEXT, " + COL_MAPX
                + " REAL, " + COL_MAPY + " REAL, " + COL_MEMO + " TEXT)";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
