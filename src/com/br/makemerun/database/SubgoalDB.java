package com.br.makemerun.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.br.makemerun.model.Subgoal;

public class SubgoalDB extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "goalDB";

    private static final String TABLE_SUBGOAL= "subgoal";

    private static final String ID = "id";
    private static final String TOTAL_DISTANCE_RUNNING = "total_distance_running";
    private static final String TOTAL_DISTANCE_WALKING = "total_distance_walking";
    private static final String PARTIAL_DISTANCE_RUNNING = "partial_distance_running";
    private static final String PARTIAL_DISTANCE_WALKING = "partial_distance_walking";
    private static final String TOTAL_TIME = "total_time";   
    private static final String PARTIAL_TIME_RUNNING = "partial_time_running";
    private static final String PARTIAL_TIME_WALKING = "partial_time_walking";
    private static final String IS_COMPLETED = "is_completed";
    private static final String IS_LAST = "is_last";

    private static final String CREATE_TABLE_SUBGOAL = "CREATE TABLE "
    		+ TABLE_SUBGOAL + "(" +
    		ID + " INTEGER PRIMARY KEY NOT NULL," +
    		TOTAL_DISTANCE_RUNNING + " FLOAT NOT NULL," +
    		TOTAL_DISTANCE_WALKING + " FLOAT NOT NULL," +
    		PARTIAL_DISTANCE_RUNNING + " FLOAT NOT NULL," +
    		PARTIAL_DISTANCE_WALKING + " FLOAT NOT NULL," + 
    		TOTAL_TIME + " INTEGER DEFAULT NULL," +
    		PARTIAL_TIME_RUNNING + " INTEGER DEFAULT NULL," +
    		PARTIAL_TIME_WALKING + " INTEGER DEFAULT NULL," +
    		IS_COMPLETED + " BOOLEAN," +
    		IS_LAST + " BOOLEAN)";

    public SubgoalDB (Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(CREATE_TABLE_SUBGOAL);
		}catch(SQLException e){
			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBGOAL);
		onCreate(db);
	}

	public List<Subgoal> getSubgoals(){
		SQLiteDatabase db = this.getReadableDatabase();
    	List<Subgoal> subgoalList = new LinkedList<Subgoal>();
 
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBGOAL, null);

        while(cursor.moveToNext()){
        	Subgoal subgoal = new Subgoal();
        	subgoal.setId(cursor.getInt(cursor.getColumnIndex(ID)));

         	subgoal.setKmTotalRunning(cursor.getDouble(cursor.getColumnIndex(TOTAL_DISTANCE_RUNNING)));
         	subgoal.setKmTotalWalking(cursor.getDouble(cursor.getColumnIndex(TOTAL_DISTANCE_WALKING)));

        	subgoal.setKmPartialRunning(cursor.getDouble(cursor.getColumnIndex(PARTIAL_DISTANCE_RUNNING)));
        	subgoal.setKmPartialWalking(cursor.getDouble(cursor.getColumnIndex(PARTIAL_DISTANCE_WALKING)));

        	subgoal.setTotalTime(cursor.getLong(cursor.getColumnIndex(TOTAL_TIME)));
        	subgoal.setTotalRunningTime(cursor.getLong(cursor.getColumnIndex(PARTIAL_TIME_RUNNING)));
        	subgoal.setTotalWalkingTime(cursor.getLong(cursor.getColumnIndex(PARTIAL_TIME_WALKING)));

        	subgoal.setCompleted(cursor.getInt(cursor.getColumnIndex(IS_COMPLETED)) == 1);
        	subgoal.setLast(cursor.getInt(cursor.getColumnIndex(IS_LAST)) == 1);

        	subgoalList.add(subgoal);
        }

        cursor.close();

        db.close();
        return subgoalList;
    }

//    private static final String ID = "id";
//    private static final String TOTAL_DISTANCE_RUNNING = "total_distance_running";
//    private static final String TOTAL_DISTANCE_WALKING = "total_distance_walking";
//    private static final String PARTIAL_DISTANCE_RUNNING = "partial_distance_running";
//    private static final String PARTIAL_DISTANCE_WALKING = "partial_distance_walking";
//    private static final String TOTAL_TIME = "total_time";   
//    private static final String PARTIAL_TIME_RUNNING = "partial_time_running";
//    private static final String PARTIAL_TIME_WALKING = "partial_time_walking";
//    private static final String IS_COMPLETED = "is_completed";
//    private static final String IS_LAST = "is_last";

	public void insertSubgoal(Subgoal subgoal) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ID, subgoal.getId());
		values.put(TOTAL_DISTANCE_RUNNING, subgoal.getKmTotalRunning());
		values.put(TOTAL_DISTANCE_WALKING, subgoal.getKmTotalWalking());
		values.put(PARTIAL_DISTANCE_RUNNING, subgoal.getKmPartialRunning());
		values.put(PARTIAL_DISTANCE_WALKING, subgoal.getKmPartialWalking());
		values.put(TOTAL_TIME, subgoal.getTotalTime());
		values.put(PARTIAL_TIME_RUNNING, subgoal.getTotalRunningTime());
		values.put(PARTIAL_TIME_WALKING, subgoal.getTotalWalkingTime());
		values.put(IS_COMPLETED, subgoal.isCompleted());
		values.put(IS_LAST, subgoal.isLast());

		if(subgoal.isCompleted())
			values.put(IS_COMPLETED, 1);
		else
			values.put(IS_COMPLETED, 0);

		if(subgoal.isLast())
			values.put(IS_LAST, 1);
		else
			values.put(IS_LAST, 0);

		db.insert(TABLE_SUBGOAL, null, values);

		db.close();
	}
	
	public void updateSubgoal(Subgoal subgoal) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ID, subgoal.getId());
		values.put(TOTAL_DISTANCE_RUNNING, subgoal.getKmTotalRunning());
		values.put(TOTAL_DISTANCE_WALKING, subgoal.getKmTotalWalking());
		values.put(PARTIAL_DISTANCE_RUNNING, subgoal.getKmPartialRunning());
		values.put(PARTIAL_DISTANCE_WALKING, subgoal.getKmPartialWalking());
		values.put(TOTAL_TIME, subgoal.getTotalTime());
		values.put(PARTIAL_TIME_RUNNING, subgoal.getTotalRunningTime());
		values.put(PARTIAL_TIME_WALKING, subgoal.getTotalWalkingTime());
		values.put(IS_COMPLETED, subgoal.isCompleted());
		values.put(IS_LAST, subgoal.isLast());

		if(subgoal.isCompleted())
			values.put(IS_COMPLETED, 1);
		else
			values.put(IS_COMPLETED, 0);

		if(subgoal.isLast())
			values.put(IS_LAST, 1);
		else
			values.put(IS_LAST, 0);

		db.update(TABLE_SUBGOAL, values, ID + " == " + subgoal.getId(), null);
		db.close();
	}
	
	public void deleteSubgoal(Subgoal subgoal){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_SUBGOAL, ID + " == " + subgoal.getId(), null);
		db.close();
	}
	
	public void deleteAll(){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_SUBGOAL, null, null);
		db.close();
	}
}
