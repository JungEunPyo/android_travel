package ddwu.mobile.finalproject.ma02_20160798;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class TravelDBManager {
    TravelDBHelper helper = null;
    Cursor cursor = null;

    public TravelDBManager(Context context){
        helper = new TravelDBHelper(context);
    }

    public ArrayList<TravelDto> getAllTravelList(){
        ArrayList<TravelDto> travelList = new ArrayList();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + helper.TABLE_NAME, null);
        while(cursor.moveToNext()){
            long id = cursor.getInt(cursor.getColumnIndex(helper.COL_ID));
            String title = cursor.getString(cursor.getColumnIndex(helper.COL_TITLE));
            String addr = cursor.getString(cursor.getColumnIndex(helper.COL_ADDR));
            String detailContent = cursor.getString(cursor.getColumnIndex(helper.COL_DETAILCONTENT));
            String imageFileName = cursor.getString(cursor.getColumnIndex(helper.COL_IMAGEFILENAME));
            String imageFileLink = cursor.getString(cursor.getColumnIndex(helper.COL_IMAGELINK));
            Double mapX = cursor.getDouble(cursor.getColumnIndex(helper.COL_MAPX));
            Double mapY = cursor.getDouble(cursor.getColumnIndex(helper.COL_MAPY));
            String memo = cursor.getString(cursor.getColumnIndex(helper.COL_MEMO));
            travelList.add(new TravelDto(id, title, addr, detailContent, imageFileName, imageFileLink, mapX, mapY, memo));
        }
        cursor.close();
        helper.close();
        return travelList;
    }
    public boolean addTravelList(TravelDto newTravel){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(helper.COL_TITLE, newTravel.getTitle());
        values.put(helper.COL_ADDR, newTravel.getAddr());
        values.put(helper.COL_DETAILCONTENT, newTravel.getDetailContent());
        values.put(helper.COL_IMAGEFILENAME, newTravel.getImageFileName());
        values.put(helper.COL_IMAGELINK, newTravel.getImageLink());
        values.put(helper.COL_MAPX, newTravel.getMapX());
        values.put(helper.COL_MAPY, newTravel.getMapY());
        values.put(helper.COL_MEMO, newTravel.getMemo());

        long count = db.insert(helper.TABLE_NAME, null, values);
        if(count > 0)
            return true;
        else
            return false;
    }
    public boolean modifyTravelList(TravelDto travel){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(helper.COL_TITLE, travel.getTitle());
        values.put(helper.COL_DETAILCONTENT, travel.getDetailContent());
        values.put(helper.COL_MEMO, travel.getMemo());
        values.put(helper.COL_IMAGEFILENAME, travel.getImageFileName());
        String whereClause = helper.COL_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(travel.get_id())};

        int result = db.update(helper.TABLE_NAME, values, whereClause, whereArgs);
        helper.close();

        if(result > 0)
            return true;
        else
            return false;
    }

    public boolean removeMovie(Long id){
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = helper.COL_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        int result = db.delete(helper.TABLE_NAME, whereClause, whereArgs);
        helper.close();
        if(result > 0)
            return true;
        else
            return false;
    }

}
