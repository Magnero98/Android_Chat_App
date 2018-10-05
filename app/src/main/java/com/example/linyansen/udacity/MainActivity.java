package com.example.linyansen.udacity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linyansen.udacity.DBConnection.DBContract;
import com.example.linyansen.udacity.DBConnection.DBHelpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
{
    public static final int REMEMBER_ME = 1;
    public static final int DONT_REMEMBER_ME = 0;

    public int currentUserID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeData();
    }

    public void initializeData()
    {
        try
        {
            File path = getApplicationContext().getExternalFilesDir(null);
            File file = new File(path, "currentUser.txt");
            Scanner input = new Scanner(new FileReader(file));

            String line = input.nextLine();

            if(!line.trim().contains("noDataFound"))
            {
                String[] data = line.split("#");

                EditText username = (EditText) findViewById(R.id.usernameEditText);
                username.setText(data[0]);
                EditText password = (EditText) findViewById(R.id.passwordEditText);
                CheckBox rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);
                rememberMeCheckBox.setChecked(true);

                //Finding password from database
                DBHelpers helpers = new DBHelpers(this);
                SQLiteDatabase db = helpers.getReadableDatabase();

                String[] projection = {
                        DBContract.UserEntry.PASSWORD_COLUMN
                };

                String WHERE_CLAUSE =
                        DBContract.UserEntry.UserID + " = " + data[1]; // data[1] is an user's id

                Cursor cursor = db.query(
                        DBContract.UserEntry.TABLE_NAME,
                        projection,
                        WHERE_CLAUSE,
                        null,
                        null,
                        null,
                        null
                );

                try
                {
                    cursor.moveToNext();
                    //get password from database and assign it to password field
                    password.setText(cursor.getString(0));
                }
                finally
                {
                    cursor.close();
                }
            }

            input.close();
        }
        catch (Exception e)
        {
            changeCurrentUser(null, 0, DONT_REMEMBER_ME);
        }
    }

    public void changeCurrentUser(String username, int id, int action)
    {
        try
        {
            File path = getApplicationContext().getExternalFilesDir(null);
            File file = new File(path, "currentUser.txt");
            PrintWriter output = new PrintWriter(new FileWriter(file, false));
            switch (action)
            {
                case REMEMBER_ME:
                    output.println(username + "#" + id);
                    break;
                case DONT_REMEMBER_ME:
                    output.println("noDataFound");
                    break;
            }

            output.close();
        }
        catch (Exception e){}
    }

    public void signIn(View view)
    {
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getReadableDatabase();

        String[] projection ={
                DBContract.UserEntry.UserID,
                DBContract.UserEntry.USERNAME_COLUMN,
                DBContract.UserEntry.PASSWORD_COLUMN
        };


        Cursor cursor = db.query(
                DBContract.UserEntry.TABLE_NAME,
                projection,
                DBContract.UserEntry.USERNAME_COLUMN + " = '" + username + "'",
                null,
                null,
                null,
                null
        );

        int userIdColumnIndex = cursor.getColumnIndex(DBContract.UserEntry.UserID);
        int passwordColumnIndex = cursor.getColumnIndex(DBContract.UserEntry.PASSWORD_COLUMN);

        String currentPassword = "";

        cursor.moveToNext();

        try
        {
            currentUserID = cursor.getInt(userIdColumnIndex);
            currentPassword = cursor.getString(passwordColumnIndex);
        }
        catch (IndexOutOfBoundsException e)
        {
            TextView errorTextView = (TextView) findViewById(R.id.errorTextView);
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        if(currentPassword.equals(password))
        {
            CheckBox rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);

            if(rememberMeCheckBox.isChecked())
                changeCurrentUser(username, currentUserID, REMEMBER_ME);
            else
                changeCurrentUser(username.toString(), currentUserID, DONT_REMEMBER_ME);

            cursor.close();

            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("id", Integer.toString(currentUserID));

            startActivity(intent);
        }
        else
        {
            cursor.close();

            TextView errorTextView = (TextView) findViewById(R.id.errorTextView);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }

    public void clearData()
    {
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    public void goToSignUpActivity(View view)
    {
        clearData();

        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        intent.putExtra("id", Integer.toString(currentUserID));
        startActivity(intent);
    }
}
