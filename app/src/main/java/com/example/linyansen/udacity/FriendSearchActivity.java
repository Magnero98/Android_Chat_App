package com.example.linyansen.udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.DngCreator;
import android.net.http.SslCertificate;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linyansen.udacity.DBConnection.DBContract;
import com.example.linyansen.udacity.DBConnection.DBHelpers;

public class FriendSearchActivity extends AppCompatActivity {

    public String currentUserID = "noUser";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }

    public void showUserList(View view)
    {
        EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        String searchKeyword = searchEditText.getText().toString();

        LinearLayout userListLinearLayout = (LinearLayout) findViewById(R.id.friendListLinearLayout);

        //Clear Linear Layout Element
        userListLinearLayout.removeAllViews();

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        String[] projection = {
                DBContract.UserEntry.FULLNAME_COLUMN,
                DBContract.UserEntry.STATUS_COLUMN,
                DBContract.UserEntry.UserID
        };

        String WHERE_CLAUSE =
                DBContract.UserEntry.UserID + " != '" + currentUserID +
                "' AND (" + DBContract.UserEntry.USERNAME_COLUMN + " LIKE '%" + searchKeyword + "%' OR " +
                DBContract.UserEntry.FULLNAME_COLUMN + " LIKE '%" + searchKeyword + "%')";

        Cursor cursor = db.query(
                DBContract.UserEntry.TABLE_NAME,
                projection,
                WHERE_CLAUSE,
                null,
                null,
                null,
                null
        );

        int fullnameIndexColumn = cursor.getColumnIndex(DBContract.UserEntry.FULLNAME_COLUMN);
        int statusIndexColumn = cursor.getColumnIndex(DBContract.UserEntry.STATUS_COLUMN);
        int userIDIndexColumn = cursor.getColumnIndex(DBContract.UserEntry.UserID);

        String fullname, status;
        int userId;

        try
        {
            while(cursor.moveToNext())
            {
                fullname = cursor.getString(fullnameIndexColumn);
                status = cursor.getString(statusIndexColumn);
                userId = cursor.getInt(userIDIndexColumn);

                //Setting Parent Linear Layout
                final LinearLayout parentLayout = new LinearLayout(this);
                parentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                parentLayout.setOrientation(LinearLayout.HORIZONTAL);
                parentLayout.setPadding(15,15,15,15);
                parentLayout.setGravity(Gravity.CENTER);

                final int userIdFinal = userId;

                parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        openUserWindow(userIdFinal);
                    }
                });

                //Setting User Profile Pic Image View
                ImageView profilePic = new ImageView(this);
                profilePic.setBackgroundResource(R.drawable.chat_list_image_radius);
                profilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                profilePic.setLayoutParams(new LinearLayout.LayoutParams(90, 90));
                profilePic.setImageResource(R.drawable.user);

                //Setting Name and status Linear Layout
                LinearLayout textLayout = new LinearLayout(this);
                textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                textLayout.setOrientation(LinearLayout.VERTICAL);
                textLayout.setPadding(15,0,15,0);

                //Setting Name TextView
                TextView nameTextView = new TextView(this);
                nameTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                nameTextView.setText(fullname);
                nameTextView.setTextSize(17);
                nameTextView.setTextColor(Color.WHITE);

                //Setting Status TextView
                TextView statusTextView = new TextView(this);
                statusTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                statusTextView.setText(status);
                statusTextView.setTextSize(12);
                statusTextView.setTextColor(Color.GRAY);

                //Placing element inside layout
                textLayout.addView(nameTextView);
                textLayout.addView(statusTextView);

                parentLayout.addView(profilePic);
                parentLayout.addView(textLayout);

                userListLinearLayout.addView(parentLayout);
            }
        }
        finally
        {
            cursor.close();
        }
    }

    public void openUserWindow(final int friendID)
    {
        RelativeLayout coveringLayout = (RelativeLayout) findViewById(R.id.coveringLayout);
        coveringLayout.setVisibility(View.VISIBLE);
        LinearLayout userProfileLinearLayout = (LinearLayout) findViewById(R.id.userProfileLinearLayout);
        userProfileLinearLayout.setVisibility(View.VISIBLE);


        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        //Get User's Information from Database
        String[] projectionFriend = {
                DBContract.UserEntry.FULLNAME_COLUMN,
                DBContract.UserEntry.USERNAME_COLUMN
        };

        Cursor cursorUserInformation = db.query(
                DBContract.UserEntry.TABLE_NAME,
                projectionFriend,
                DBContract.UserEntry.UserID + " = '" + friendID + "'",
                null,
                null,
                null,
                null
        );

        //Get User's Follower's Data from Database
        String[] projectionFollower = {
                DBContract.FollowingEntry.FOLLOWED_USER_ID_COLUMN
        };

        String IS_FRIEND_WHERE_CLAUSE =
                DBContract.FollowingEntry.USER_ID_COLUMN + " = '" + currentUserID + "' AND " +
                        DBContract.FollowingEntry.FOLLOWED_USER_ID_COLUMN + " = '" + friendID + "'";

        Cursor cursorIsFriend = db.query(
                DBContract.FollowingEntry.TABLE_NAME,
                projectionFollower,
                IS_FRIEND_WHERE_CLAUSE,
                null,
                null,
                null,
                null
        );

        //Get User's Rating's Data from Database
        String[] projectionRating = {
                DBContract.RatingEntry.RATED_USER_ID_COLUMN
        };

        String HAS_RATED_WHERE_CLAUSE =
                DBContract.RatingEntry.USER_ID_COLUMN + " = '" + currentUserID + "' AND " +
                        DBContract.RatingEntry.RATED_USER_ID_COLUMN + " = '" + friendID + "'";

        Cursor cursorHasRated = db.query(
                DBContract.RatingEntry.TABLE_NAME,
                projectionRating,
                HAS_RATED_WHERE_CLAUSE,
                null,
                null,
                null,
                null
        );

        //Initializing View's Value from Pop up Window
        TextView fullnameTextView = (TextView) findViewById(R.id.fullnameTextView);
        TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);

        LinearLayout addButton = (LinearLayout) findViewById(R.id.addButton);
        LinearLayout chatButton = (LinearLayout) findViewById(R.id.chatButton);
        LinearLayout rateButton = (LinearLayout) findViewById(R.id.rateButton);
        final LinearLayout unrateButton = (LinearLayout) findViewById(R.id.unrateButton);

        ImageView closeButton = (ImageView) findViewById(R.id.closeButton);


        int fullnameIndexColumn = cursorUserInformation.getColumnIndex(DBContract.UserEntry.FULLNAME_COLUMN);
        int usernameIndexColumn = cursorUserInformation.getColumnIndex(DBContract.UserEntry.USERNAME_COLUMN);

        cursorUserInformation.moveToNext();

        fullnameTextView.setText(cursorUserInformation.getString(fullnameIndexColumn));
        usernameTextView.setText(cursorUserInformation.getString(usernameIndexColumn));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend(friendID);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatroomActivity(friendID);
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateUser(friendID);
            }
        });

        unrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unrateUser(friendID);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeUserWindow(view);
            }
        });

        if (cursorIsFriend.getCount() < 1)
        {
            chatButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
        }
        else
        {
            addButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.VISIBLE);
        }


        if (cursorHasRated.getCount() < 1)
        {
            rateButton.setVisibility(View.VISIBLE);
            unrateButton.setVisibility(View.GONE);
        }
        else
        {
            rateButton.setVisibility(View.GONE);
            unrateButton.setVisibility(View.VISIBLE);
        }
    }

    public void closeUserWindow(View view)
    {
        //hidding pop up element
        LinearLayout userProfileLinearLayout = (LinearLayout) findViewById(R.id.userProfileLinearLayout);
        userProfileLinearLayout.setVisibility(View.GONE);
        RelativeLayout coveringLayout = (RelativeLayout) findViewById(R.id.coveringLayout);
        coveringLayout.setVisibility(View.GONE);
    }

    public void goToChatroomActivity(int friendID)
    {
        Intent intent = new Intent(FriendSearchActivity.this, ChatroomActivity.class);
        intent.putExtra("userID", currentUserID);
        intent.putExtra("friendID", String.valueOf(friendID));
        startActivity(intent);
    }

    public void addFriend(int friendID)
    {
        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getWritableDatabase();
        SQLiteDatabase read = helpers.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.FollowingEntry.USER_ID_COLUMN, currentUserID);
        values.put(DBContract.FollowingEntry.FOLLOWED_USER_ID_COLUMN, friendID);

        long insertFollowingData = db.insert(DBContract.FollowingEntry.TABLE_NAME, null, values);

        if(insertFollowingData == -1)
        {
            Toast.makeText(this, "Failed to Add This User", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LinearLayout addButton = (LinearLayout) findViewById(R.id.addButton);
            LinearLayout chatButton = (LinearLayout) findViewById(R.id.chatButton);

            addButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.VISIBLE);
        }

        String query =
                "SELECT Followers FROM MsUser WHERE " + DBContract.UserEntry.UserID + " = '" + friendID + "';";
        Cursor cursor = read.rawQuery(query, null);

        cursor.moveToNext();

        query =
                "UPDATE MsUser SET Followers = " + (cursor.getInt(0) + 1) + " WHERE " + DBContract.UserEntry.UserID + " = '" + friendID + "'";
        db.execSQL(query);

        query =
                "SELECT Following FROM MsUser WHERE " + DBContract.UserEntry.UserID + " = '" + currentUserID + "';";
        cursor = read.rawQuery(query, null);

        cursor.moveToNext();

        query =
                "UPDATE MsUser SET Following = " + (cursor.getInt(0) + 1) + " WHERE " + DBContract.UserEntry.UserID + " = '" + currentUserID + "'";
        db.execSQL(query);
    }

    public void rateUser(int friendID)
    {
        LinearLayout rateButton = (LinearLayout) findViewById(R.id.rateButton);
        LinearLayout unrateButton = (LinearLayout) findViewById(R.id.unrateButton);
        rateButton.setVisibility(View.GONE);
        unrateButton.setVisibility(View.VISIBLE);

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getWritableDatabase();
        SQLiteDatabase read = helpers.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.RatingEntry.USER_ID_COLUMN, currentUserID);
        values.put(DBContract.RatingEntry.RATED_USER_ID_COLUMN, friendID);

        db.insert(DBContract.RatingEntry.TABLE_NAME, null, values);

        String query = "SELECT Rating FROM MsUser WHERE " + DBContract.UserEntry.UserID + " = '" + friendID + "';";
        Cursor cursor = read.rawQuery(query, null);

        cursor.moveToNext();

        query = "UPDATE MsUser SET Rating = " + (cursor.getInt(0) + 1) + " WHERE " + DBContract.UserEntry.UserID + " = '" + friendID + "';";
        db.execSQL(query);
    }

    public void unrateUser(int friendID)
    {
        LinearLayout rateButton = (LinearLayout) findViewById(R.id.rateButton);
        LinearLayout unrateButton = (LinearLayout) findViewById(R.id.unrateButton);

        unrateButton.setVisibility(View.GONE);
        rateButton.setVisibility(View.VISIBLE);

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getWritableDatabase();
        SQLiteDatabase read = helpers.getReadableDatabase();

        String query =
                "DELETE FROM " + DBContract.RatingEntry.TABLE_NAME +
                        " WHERE " + DBContract.RatingEntry.USER_ID_COLUMN + " = '" + currentUserID + "' AND " +
                        DBContract.RatingEntry.RATED_USER_ID_COLUMN + " = '" + friendID + "'";

        db.execSQL(query);

        query = "SELECT Rating FROM MsUser WHERE " + DBContract.UserEntry.UserID + " = '" + friendID + "';";
        Cursor cursor = read.rawQuery(query, null);

        cursor.moveToNext();

        query = "UPDATE MsUser SET Rating = " + (cursor.getInt(0) - 1) + " WHERE " + DBContract.UserEntry.UserID + " = '" + friendID + "';";;
        db.execSQL(query);
    }

    public void initialize()
    {
        //getting user ID from intent
        currentUserID = getIntent().getStringExtra("id");
        closeUserWindow(new View(this));
    }
}
