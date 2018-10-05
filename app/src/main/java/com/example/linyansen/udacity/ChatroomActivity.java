package com.example.linyansen.udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linyansen.udacity.DBConnection.DBContract;
import com.example.linyansen.udacity.DBConnection.DBHelpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatroomActivity extends AppCompatActivity {
    String currentUserID = "";
    String currentFriendID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        initialize();
        show();
    }

    public void show()
    {
/*
        TextView a = (TextView) findViewById(R.id.query);

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        String query =
                "SELECT UserID, FollowedUserID FROM MsFollowing;";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            a.append(Integer.toString(cursor.getInt(0)));
            a.append(" ");
            a.append(Integer.toString(cursor.getInt(1)));
            a.append("\n");
        }
*/
        //a.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
    }

    public RelativeLayout getSpeechBubble(String text, int dir)
    {
        RelativeLayout parent = new RelativeLayout(this);
        parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setPadding(10,0,10,10);

        TextView chat = new TextView(this);
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        chat.setPadding(10, 10,10,10);
        chat.setText(text);
        chat.setTextColor(Color.BLACK);
        chat.setTextSize(12);
        chat.setMaxWidth(450);
        chat.setBackgroundResource(R.drawable.chat_bubble_radius);

        if(dir == Gravity.LEFT) {
            chat.setBackgroundColor(Color.parseColor("#9BDADE"));
            textViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }
        else {
            chat.setBackgroundColor(Color.parseColor("#FFFFFF"));
            textViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        chat.setLayoutParams(textViewParams);

        parent.addView(chat);

        return parent;
    }

    public void getHistoryChat()
    {
        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        String[] projection = {
                DBContract.MessageEntry.SENDER_ID_COLUMN,
                DBContract.MessageEntry.CONTENT_COLUMN
        };

        String WHERE_CLAUSE =
                "(" + DBContract.MessageEntry.SENDER_ID_COLUMN + " = '" + currentUserID +"' AND " +
                        DBContract.MessageEntry.RECEIVER_ID_COLUMN + " = '" + currentFriendID + "') OR " +
                        "(" + DBContract.MessageEntry.RECEIVER_ID_COLUMN + " = '" + currentUserID +"' AND " +
                        DBContract.MessageEntry.SENDER_ID_COLUMN + " = '" + currentFriendID + "')";

        Cursor cursor = db.query(
                DBContract.MessageEntry.TABLE_NAME,
                projection,
                WHERE_CLAUSE,
                null,
                null,
                null,
                null
        );

        LinearLayout chatLinearLayout = (LinearLayout) findViewById(R.id.chatLinearLayout);

        try
        {
            while(cursor.moveToNext())
            {
                if(cursor.getInt(0) == Integer.valueOf(currentUserID))
                    chatLinearLayout.addView(getSpeechBubble(cursor.getString(1), Gravity.RIGHT));
                else
                    chatLinearLayout.addView(getSpeechBubble(cursor.getString(1), Gravity.LEFT));
            }

            final ScrollView chatroomSV = (ScrollView) findViewById(R.id.chatroomScrollView);
            chatroomSV.post(new Runnable() {
                @Override
                public void run() {
                    chatroomSV.scrollTo(0, chatroomSV.getBottom());
                }
            });
        }
        finally
        {
            cursor.close();
        }
    }

    public void sendChat(View view)
    {
        EditText textMessageEditText = (EditText) findViewById(R.id.textMessageEditText);
        String textMessage = textMessageEditText.getText().toString();

        if(textMessage.trim().length() > 0) {
            DBHelpers helpers = new DBHelpers(this);
            SQLiteDatabase db = helpers.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBContract.MessageEntry.SENDER_ID_COLUMN, currentUserID);
            values.put(DBContract.MessageEntry.RECEIVER_ID_COLUMN, currentFriendID);
            values.put(DBContract.MessageEntry.CONTENT_COLUMN, textMessage);

            long insertTextMessageData = db.insert(DBContract.MessageEntry.TABLE_NAME, null, values);

            if (insertTextMessageData == -1) {
                Toast.makeText(this, "Chat was Failed to send, Retry!", Toast.LENGTH_SHORT).show();
            } else {
                LinearLayout parent = (LinearLayout) findViewById(R.id.chatLinearLayout);
                parent.addView(getSpeechBubble(textMessage, Gravity.RIGHT));

                final ScrollView chatroomSV = (ScrollView) findViewById(R.id.chatroomScrollView);
                chatroomSV.post(new Runnable() {
                    @Override
                    public void run() {
                        chatroomSV.scrollTo(0, chatroomSV.getBottom());
                    }
                });
            }

            textMessageEditText.setText("");
        }
    }


    public void getFriendName()
    {
        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        String[] projection = {
                DBContract.UserEntry.FULLNAME_COLUMN
        };

        String WHERE_CLAUSE =
                DBContract.UserEntry.UserID + " = '" + currentFriendID + "'";

        Cursor cursor = db.query(
                DBContract.UserEntry.TABLE_NAME,
                projection,
                WHERE_CLAUSE,
                null,
                null,
                null,
                null
        );

        cursor.moveToNext();

        String friendFullname = cursor.getString(0);

        TextView friendNameTextView = (TextView) findViewById(R.id.nameTextView);
        friendNameTextView.setText(friendFullname);
    }

    public void initialize()
    {
        currentFriendID = getIntent().getStringExtra("friendID");
        currentUserID = getIntent().getStringExtra("userID");
        getHistoryChat();
        getFriendName();
    }
}
