package com.example.linyansen.udacity.DBConnection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.linyansen.udacity.DBConnection.DBContract;

/**
 * Created by Lin Yansen on 11/18/2017.
 */

public class DBHelpers extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Ontuts.db";
    public static final int DATABASE_VERSION = 2;

    public DBHelpers(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String SqliteQuery =
                "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME + "(" +
                        DBContract.UserEntry.UserID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.UserEntry.USERNAME_COLUMN + " VARCHAR(30)," +
                        DBContract.UserEntry.PASSWORD_COLUMN + " VARCHAR(30)," +
                        DBContract.UserEntry.FULLNAME_COLUMN + " VARCHAR(30)," +
                        DBContract.UserEntry.FOLLOWERS_COLUMN + " INTEGER DEFAULT 0," +
                        DBContract.UserEntry.FOLLOWING_COLUMN + " INTEGER DEFAULT 0," +
                        DBContract.UserEntry.RATING_COLUMN + " INTEGER DEFAULT 0," +
                        DBContract.UserEntry.STATUS_COLUMN + " VARCHAR(50) DEFAULT 'Write Some Message...'" +
                        ");";

        db.execSQL(SqliteQuery);

        SqliteQuery =
                "CREATE TABLE " + DBContract.MessageEntry.TABLE_NAME + "(" +
                        DBContract.MessageEntry.MessageID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.MessageEntry.SENDER_ID_COLUMN + " INTEGER REFERENCES MsUser(UserID)," +
                        DBContract.MessageEntry.RECEIVER_ID_COLUMN + " INTEGER REFERENCES MsUser(UserID)," +
                        DBContract.MessageEntry.TIME_COLUMN + " TEXT," +
                        DBContract.MessageEntry.READ_COLUMN + " INTEGER DEFAULT 0," +
                        DBContract.MessageEntry.CONTENT_COLUMN + " TEXT" +
                        ");";

        db.execSQL(SqliteQuery);

        SqliteQuery =
                "CREATE TABLE " + DBContract.FollowingEntry.TABLE_NAME + "(" +
                        DBContract.FollowingEntry.FollowingID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.FollowingEntry.USER_ID_COLUMN + " INTEGER REFERENCES MsUser(UserID)," +
                        DBContract.FollowingEntry.FOLLOWED_USER_ID_COLUMN + " INTEGER REFERENCES MsUser(UserID)" +
                        ");";

        db.execSQL(SqliteQuery);

        SqliteQuery =
                "CREATE TABLE " + DBContract.RatingEntry.TABLE_NAME + "(" +
                        DBContract.RatingEntry.RatingID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.RatingEntry.USER_ID_COLUMN + " INTEGER REFERENCES MsUser(UserID)," +
                        DBContract.RatingEntry.RATED_USER_ID_COLUMN + " INTEGER REFERENCES MsUser(UserID)" +
                        ");";

        db.execSQL(SqliteQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }
}
