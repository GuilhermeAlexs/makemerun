package com.br.makemerun.database;

import org.achartengine.model.XYSeries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class StatsDB extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "goalDB";

    private static final String TABLE_STATS = "stats";

    private static final String ID = "id";
    private static final String SUBGOAL = "subgoal_id";
    private static final String SPRINT_TYPE = "sprint_type";
    private static final String X_VALUE = "x_value";
    private static final String Y_VALUE = "y_value";
    
    public static final int WALKING_SPRINT = 0;
    public static final int RUNNING_SPRINT = 1;

    private static final String CREATE_TABLE_STATS = "CREATE TABLE "
    		+ TABLE_STATS + "(" +
    		ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
    		SUBGOAL + " INTEGER," +
    		SPRINT_TYPE + " INTEGER," +
    		X_VALUE + " FLOAT," +
    		Y_VALUE + " FLOAT)";

    public StatsDB (Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(CREATE_TABLE_STATS);
		}catch(SQLException e){
			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
		onCreate(db);
	}
	
	public XYSeries getStatsBySubgoal(int subgoal, int sprintType){
		SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STATS + " WHERE " + SUBGOAL + " == " + subgoal + " AND " + SPRINT_TYPE + " == " + sprintType, null);
        XYSeries series = new XYSeries("Speed");

        while(cursor.moveToNext())
        	series.add(cursor.getDouble(cursor.getColumnIndex(X_VALUE)), cursor.getDouble(cursor.getColumnIndex(Y_VALUE)));

        if(sprintType == WALKING_SPRINT)
        	series.setTitle("Speed Walking");
        else
        	series.setTitle("Speed Running");

        db.close();
        
        if(series.getItemCount() == 0)
        	series = null;

        return series;
    }

	public void insertStats(int subgoal, int sprintType, XYSeries series) {
		SQLiteDatabase db = this.getWritableDatabase();

		for(int i = 0; i < series.getItemCount(); i++){
			ContentValues values = new ContentValues();
			values.put(SUBGOAL, subgoal);
			values.put(SPRINT_TYPE, sprintType);
			values.put(X_VALUE, series.getX(i));
			values.put(Y_VALUE, series.getY(i));

			db.insert(TABLE_STATS, null, values);
		}

		db.close();
	}

	public void deleteStats(int subgoal){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_STATS, SUBGOAL + " == " + subgoal, null);
		db.close();
	}
	
	public void deleteAll(){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_STATS, null, null);
		db.close();
	}
}
