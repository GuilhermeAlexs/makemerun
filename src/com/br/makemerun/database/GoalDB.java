package com.br.makemerun.database;

import java.util.List;

import com.br.makemerun.model.Goal;
import com.br.makemerun.model.Subgoal;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GoalDB extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "goalDB";

    private static final String TABLE_GOAL= "goal";

    private static final String ID = "id";
    private static final String KM_GOAL = "km_goal";
    private static final String RUNNING_KM_BASE = "running_km_base";  
    private static final String SPEED_BASE = "speed_base";
    private static final String SPEED_DEVIATION = "speed_sd_base";
    private static final String GOAL_PROGRESS = "gal_progress";
    private static final String IS_CURRENT = "is_current";

    private static final String CREATE_TABLE_GOAL = "CREATE TABLE "
    		+ TABLE_GOAL + "(" +
    		ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
    		KM_GOAL + " FLOAT," +
    		RUNNING_KM_BASE + " FLOAT," +
    		SPEED_BASE + " FLOAT," +
    		SPEED_DEVIATION + " FLOAT," +
    		GOAL_PROGRESS + " INTEGER," +
    		IS_CURRENT + " BOOLEAN)";

    private Context ctx;

    public GoalDB (Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.ctx = context;
	}

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(CREATE_TABLE_GOAL);
		}catch(SQLException e){
			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOAL);
		onCreate(db);
	}

	public Goal getCurrentGoal(){
		SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GOAL + " WHERE " + IS_CURRENT + " == " + 1, null);

        if(!cursor.moveToNext())
        	return null;

    	Goal goal = new Goal();
    	goal.setId(cursor.getInt(cursor.getColumnIndex(ID)));
    	goal.setKm(cursor.getDouble(cursor.getColumnIndex(KM_GOAL)));
    	goal.setKmBase(cursor.getDouble(cursor.getColumnIndex(RUNNING_KM_BASE)));
    	goal.setSpeedBase(cursor.getDouble(cursor.getColumnIndex(SPEED_BASE)));
    	goal.setSpeedDeviation(cursor.getDouble(cursor.getColumnIndex(SPEED_DEVIATION)));
    	goal.setProgress(cursor.getInt(cursor.getColumnIndex(GOAL_PROGRESS)));
    	goal.setCurrent(cursor.getInt(cursor.getColumnIndex(IS_CURRENT)) == 1);

    	SubgoalDB subgoalDB = new SubgoalDB(ctx);
    	goal.setSubgoals(subgoalDB.getSubgoals());

        cursor.close();
 
        db.close();
        return goal;
    }

	public void insertGoal(Goal goal) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KM_GOAL, goal.getKm());
		values.put(RUNNING_KM_BASE, goal.getKmBase());
		values.put(SPEED_BASE, goal.getSpeedBase());
		values.put(SPEED_DEVIATION, goal.getSpeedDeviation());
		values.put(GOAL_PROGRESS, goal.getProgress());

		if(goal.isCurrent())
			values.put(IS_CURRENT, 1);
		else
			values.put(IS_CURRENT, 0);

		SubgoalDB subgoalDB = new SubgoalDB(ctx);

		List<Subgoal> subgoals = goal.getSubgoals();

		for(Subgoal subgoal: subgoals)
			subgoalDB.insertSubgoal(subgoal);

		db.insert(TABLE_GOAL, null, values);

		db.close();
	}

	public void updateGoal(Goal goal) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KM_GOAL, goal.getKm());
		values.put(RUNNING_KM_BASE, goal.getKmBase());
		values.put(SPEED_BASE, goal.getSpeedBase());
		values.put(SPEED_DEVIATION, goal.getSpeedDeviation());
		values.put(GOAL_PROGRESS, goal.getProgress());
		if(goal.isCurrent())
			values.put(IS_CURRENT, 1);
		else
			values.put(IS_CURRENT, 0);
		
		SubgoalDB subgoalDB = new SubgoalDB(ctx);

		List<Subgoal> subgoals = goal.getSubgoals();

		for(Subgoal subgoal: subgoals)
			subgoalDB.insertSubgoal(subgoal);

		db.update(TABLE_GOAL, values, ID + " == " + goal.getId(), null);
		db.close();
	}

	public void deleteGoal(Goal goal){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_GOAL, ID + " == " + goal.getId(), null);

		SubgoalDB subgoalDB = new SubgoalDB(ctx);
		subgoalDB.deleteAll();

		db.close();
	}
}
