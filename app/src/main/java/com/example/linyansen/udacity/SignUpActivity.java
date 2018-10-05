package com.example.linyansen.udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.linyansen.udacity.DBConnection.DBContract;
import com.example.linyansen.udacity.DBConnection.DBHelpers;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public int inputValidation(int attributeIndex, ArrayList<String> attributes)
    {
        if(attributeIndex < 0)
        {
            Toast.makeText(this, "Gender must be checked", Toast.LENGTH_SHORT).show();
            return 0;
        }

        for(int i = 0; i < attributes.size(); i++)
        {
            if(attributes.get(i).length() < 1)
            {
                Toast.makeText(this, "All columns must be filled", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if(!attributes.get(1).equals(attributes.get(3)))
        {
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            return 0;
        }

        return 1;
    }

    public void register(View view)
    {
        EditText fullnameEditText = (EditText) findViewById(R.id.fullnameEditText);
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);

        RadioGroup genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        int genderCheckedId = genderRadioGroup.getCheckedRadioButtonId();

        ArrayList<String> UserAttribute = new ArrayList<String>();
        UserAttribute.add(usernameEditText.getText().toString());
        UserAttribute.add(passwordEditText.getText().toString());
        UserAttribute.add(fullnameEditText.getText().toString());
        UserAttribute.add(confirmPasswordEditText.getText().toString());

        if(inputValidation(genderCheckedId, UserAttribute) == 0)
            return;

        DBHelpers helpers = new DBHelpers(this);
        SQLiteDatabase db = helpers.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.UserEntry.USERNAME_COLUMN, UserAttribute.get(0));
        values.put(DBContract.UserEntry.PASSWORD_COLUMN, UserAttribute.get(1));
        values.put(DBContract.UserEntry.FULLNAME_COLUMN, UserAttribute.get(2));

        long insertUserData = db.insert(DBContract.UserEntry.TABLE_NAME, null, values);

        if(insertUserData == -1)
        {
            Toast.makeText(this, "Registration Failed, Try Again", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Registration Successfull", Toast.LENGTH_SHORT).show();
            goToSignInActivity();
        }
    }

    public void clearData()
    {
        EditText fullnameEditText = (EditText) findViewById(R.id.fullnameEditText);
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        RadioGroup genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);

        fullnameEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        genderRadioGroup.clearCheck();
    }

    public void goToSignInActivity(View view)
    {
        clearData();

        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void goToSignInActivity()
    {
        clearData();

        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
