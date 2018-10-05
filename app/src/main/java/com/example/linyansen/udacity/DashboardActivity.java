package com.example.linyansen.udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.net.http.SslCertificate;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linyansen.udacity.DBConnection.DBContract;
import com.example.linyansen.udacity.DBConnection.DBHelpers;

import org.w3c.dom.Text;

public class DashboardActivity extends AppCompatActivity {

    public String currentUserId = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }

    public void initialize()
    {
        currentUserId = getIntent().getStringExtra("id");
        closeUserWindow(new View(this));

        //============= INITIALIZING USER'S PROFILE
        showUserProfile();
        //============= INITIALIZING USER'S FRIEND LIST
        showFriendList();
        //============= INITIALIZING USER'S CHAT LIST
        showChatList();

        RelativeLayout profileLayout = (RelativeLayout) findViewById(R.id.userProfileLayout);
        ScrollView chatLayout = (ScrollView) findViewById(R.id.chatListLayout);
        LinearLayout friendLayout = (LinearLayout) findViewById(R.id.friendListLayout);

        profileLayout.setVisibility(View.VISIBLE);
        chatLayout.setVisibility(View.GONE);
        friendLayout.setVisibility(View.GONE);
    }

    public void showUserProfile()
    {
        TextView fullnameTextView = (TextView) findViewById(R.id.fullnameTextView);
        TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        TextView followersTextView = (TextView) findViewById(R.id.followersTextView);
        TextView followingTextView = (TextView) findViewById(R.id.followingTextView);
        TextView ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        TextView statusMessageTextView = (TextView) findViewById(R.id.statusMessageTextView);

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        String[] projection = {
                DBContract.UserEntry.FULLNAME_COLUMN,
                DBContract.UserEntry.USERNAME_COLUMN,
                DBContract.UserEntry.FOLLOWERS_COLUMN,
                DBContract.UserEntry.FOLLOWING_COLUMN,
                DBContract.UserEntry.RATING_COLUMN,
                DBContract.UserEntry.STATUS_COLUMN
        };

        Cursor cursor = db.query(
                DBContract.UserEntry.TABLE_NAME,
                projection,
                DBContract.UserEntry.UserID + " = " + "'" + currentUserId + "'",
                null,
                null,
                null,
                null
        );

        int fullnameColumnIndex = cursor.getColumnIndex(DBContract.UserEntry.FULLNAME_COLUMN);
        int usernameColumnIndex = cursor.getColumnIndex(DBContract.UserEntry.USERNAME_COLUMN);
        int followersColumnIndex = cursor.getColumnIndex(DBContract.UserEntry.FOLLOWERS_COLUMN);
        int followingColumnIndex = cursor.getColumnIndex(DBContract.UserEntry.FOLLOWING_COLUMN);
        int ratingColumnIndex = cursor.getColumnIndex(DBContract.UserEntry.RATING_COLUMN);
        int statusColumnIndex = cursor.getColumnIndex(DBContract.UserEntry.STATUS_COLUMN);

        cursor.moveToNext();

        try
        {
            fullnameTextView.setText(cursor.getString(fullnameColumnIndex));
            usernameTextView.setText(cursor.getString(usernameColumnIndex));
            followersTextView.setText(cursor.getInt(followersColumnIndex) + "");
            followingTextView.setText(cursor.getInt(followingColumnIndex) + "");
            ratingTextView.setText(cursor.getInt(ratingColumnIndex) + "");
            statusMessageTextView.setText(cursor.getString(statusColumnIndex));
        }
        finally
        {
            cursor.close();
        }

    }

    public void showFriendList()
    {
        LinearLayout friendListLinearLayout = (LinearLayout) findViewById(R.id.friendListLinearLayout);

        friendListLinearLayout.removeAllViews();

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        String query =
                "SELECT " + DBContract.UserEntry.UserID + ", " +
                DBContract.UserEntry.FULLNAME_COLUMN + ", " +
                DBContract.UserEntry.STATUS_COLUMN +
                        " FROM " + DBContract.UserEntry.TABLE_NAME +
                        " WHERE " + DBContract.UserEntry.UserID +" IN(" +
                "SELECT " + DBContract.FollowingEntry.FOLLOWED_USER_ID_COLUMN +
                        " FROM MsFollowing mf JOIN MsUser mu ON mf.UserID = mu." + DBContract.UserEntry.UserID +
                        " JOIN MsUser mus ON mf.FollowedUserID = mus." + DBContract.UserEntry.UserID +
                        " WHERE " + DBContract.FollowingEntry.USER_ID_COLUMN + " = '" + currentUserId + "');";

        Cursor cursor = db.rawQuery(query,null);

        int userIdIndexColumn = cursor.getColumnIndex(DBContract.UserEntry.UserID);
        int fullnameIndexColumn = cursor.getColumnIndex(DBContract.UserEntry.FULLNAME_COLUMN);
        int statusIndexColumn = cursor.getColumnIndex(DBContract.UserEntry.STATUS_COLUMN);

        String fullname, status;
        int userId;

        try
        {
            while(cursor.moveToNext())
            {
                fullname = cursor.getString(fullnameIndexColumn);
                status = cursor.getString(statusIndexColumn);
                userId = cursor.getInt(userIdIndexColumn);

                final int finalUserId = userId;

                //Setting Parent Linear Layout
                final LinearLayout parentLayout = new LinearLayout(this);
                parentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                parentLayout.setOrientation(LinearLayout.HORIZONTAL);
                parentLayout.setPadding(15,15,15,15);
                parentLayout.setGravity(Gravity.CENTER);
                parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openUserWindow(finalUserId);
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

                friendListLinearLayout.addView(parentLayout);
            }
        }
        finally
        {
            cursor.close();
        }
    }

    public void goToChatroomActivity(int friendID)
    {
        Intent intent = new Intent(DashboardActivity.this, ChatroomActivity.class);
        intent.putExtra("userID", currentUserId);
        intent.putExtra("friendID", String.valueOf(friendID));
        startActivity(intent);
    }

    public void showChatList()
    {
        LinearLayout userListLinearLayout = (LinearLayout) findViewById(R.id.chatListLinearLayout);

        //Clear Linear Layout Element
        userListLinearLayout.removeAllViews();

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        String[] projection = {
                DBContract.UserEntry.FULLNAME_COLUMN,
                DBContract.UserEntry.STATUS_COLUMN,
                DBContract.UserEntry.UserID
        };

        String subquery1 =
                "SELECT DISTINCT ReceiverID as currID FROM MsMessage WHERE SenderID = '" + currentUserId + "'";
        String subquery2 =
                "SELECT DISTINCT SenderID as currID FROM MsMessage WHERE ReceiverID = '" + currentUserId + "'";
        String query =
                "SELECT DISTINCT Fullname, Status, " + DBContract.UserEntry.UserID + " FROM MsUser " +
                        "WHERE " + DBContract.UserEntry.UserID + " IN(" + subquery1 + ") OR " +
                        DBContract.UserEntry.UserID + " IN(" + subquery2 + ");";

        Cursor cursor = db.rawQuery(query, null);

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
                        goToChatroomActivity(userIdFinal);
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

    public void hideAndShowLayout(View view)
    {
        RelativeLayout profileLayout = (RelativeLayout) findViewById(R.id.userProfileLayout);
        ScrollView chatLayout = (ScrollView) findViewById(R.id.chatListLayout);
        LinearLayout friendLayout = (LinearLayout) findViewById(R.id.friendListLayout);
        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);

        if(view == findViewById(R.id.profileButton))
        {
            profileLayout.setVisibility(View.VISIBLE);
            chatLayout.setVisibility(View.GONE);
            friendLayout.setVisibility(View.GONE);
            titleTextView.setText("DASHBOARD");
        }
        else if(view == findViewById(R.id.chatListButton))
        {
            profileLayout.setVisibility(View.GONE);
            chatLayout.setVisibility(View.VISIBLE);
            friendLayout.setVisibility(View.GONE);
            titleTextView.setText("CHATS");
        }
        else if(view == findViewById(R.id.friendListButton))
        {
            profileLayout.setVisibility(View.GONE);
            chatLayout.setVisibility(View.GONE);
            friendLayout.setVisibility(View.VISIBLE);
            titleTextView.setText("FRIEND LIST");
        }
    }

    public void goToFriendSearchActivity(View view)
    {
        Intent intent = new Intent(DashboardActivity.this, FriendSearchActivity.class);
        intent.putExtra("id", currentUserId);
        startActivity(intent);
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

        //Get User's Rating's Data from Database
        String[] projectionRating = {
                DBContract.RatingEntry.RATED_USER_ID_COLUMN
        };

        String HAS_RATED_WHERE_CLAUSE =
                DBContract.RatingEntry.USER_ID_COLUMN + " = '" + currentUserId + "' AND " +
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
        TextView fullnameTextView = (TextView) findViewById(R.id.currentUserFullname);
        TextView usernameTextView = (TextView) findViewById(R.id.currentUserUsername);

        LinearLayout chatButton = (LinearLayout) findViewById(R.id.chatButtonPopUp);
        LinearLayout rateButton = (LinearLayout) findViewById(R.id.rateButtonPopUp);
        final LinearLayout unrateButton = (LinearLayout) findViewById(R.id.unrateButtonPopUp);

        ImageView closeButton = (ImageView) findViewById(R.id.closeButton);


        int fullnameIndexColumn = cursorUserInformation.getColumnIndex(DBContract.UserEntry.FULLNAME_COLUMN);
        int usernameIndexColumn = cursorUserInformation.getColumnIndex(DBContract.UserEntry.USERNAME_COLUMN);

        cursorUserInformation.moveToNext();

        fullnameTextView.setText(cursorUserInformation.getString(fullnameIndexColumn));
        usernameTextView.setText(cursorUserInformation.getString(usernameIndexColumn));

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

    public void rateUser(int friendID)
    {
        LinearLayout rateButton = (LinearLayout) findViewById(R.id.rateButtonPopUp);
        LinearLayout unrateButton = (LinearLayout) findViewById(R.id.unrateButtonPopUp);
        rateButton.setVisibility(View.GONE);
        unrateButton.setVisibility(View.VISIBLE);

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getWritableDatabase();
        SQLiteDatabase read = helpers.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.RatingEntry.USER_ID_COLUMN, currentUserId);
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
        LinearLayout rateButton = (LinearLayout) findViewById(R.id.rateButtonPopUp);
        LinearLayout unrateButton = (LinearLayout) findViewById(R.id.unrateButtonPopUp);

        unrateButton.setVisibility(View.GONE);
        rateButton.setVisibility(View.VISIBLE);

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getWritableDatabase();
        SQLiteDatabase read = helpers.getReadableDatabase();

        String query =
                "DELETE FROM " + DBContract.RatingEntry.TABLE_NAME +
                        " WHERE " + DBContract.RatingEntry.USER_ID_COLUMN + " = '" + currentUserId + "' AND " +
                        DBContract.RatingEntry.RATED_USER_ID_COLUMN + " = '" + friendID + "'";

        db.execSQL(query);

        query = "SELECT Rating FROM MsUser WHERE " + DBContract.UserEntry.UserID + " = '" + friendID + "';";
        Cursor cursor = read.rawQuery(query, null);

        cursor.moveToNext();

        query = "UPDATE MsUser SET Rating = " + (cursor.getInt(0) - 1) + " WHERE " + DBContract.UserEntry.UserID + " = '" + friendID + "';";;
        db.execSQL(query);
    }
}
