package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

public class PreferencesActivity extends AppCompatActivity {
    SharedPreferences preferences;
    CheckBox cbMusic;
    RadioButton rbRed;
    RadioButton rbBlue;
    RadioButton rbGreen;
    RadioButton rbOneDay;
    RadioButton rbTwoDays;
    RadioButton rbOneWeek;
    boolean redColor;
    boolean blueColor;
    boolean greenColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeColorTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        cbMusic= findViewById(R.id.cbMusic);
        rbRed= findViewById(R.id.rbRed);
        rbBlue= findViewById(R.id.rbBlue);
        rbGreen= findViewById(R.id.rbGreen);
        rbOneDay= findViewById(R.id.rbOneDay);
        rbTwoDays= findViewById(R.id.rbTwoDays);
        rbOneWeek= findViewById(R.id.rbOneWeek);
        boolean music= getIntent().getBooleanExtra("music",false);
        boolean red= getIntent().getBooleanExtra("red",false);
        boolean blue= getIntent().getBooleanExtra("blue",true);
        boolean green= getIntent().getBooleanExtra("green",false);
        boolean oneDay= getIntent().getBooleanExtra("oneDay",true);
        boolean twoDays= getIntent().getBooleanExtra("twoDays",false);
        boolean oneWeek= getIntent().getBooleanExtra("oneWeek",false);
        cbMusic.setChecked(music);
        rbRed.setChecked(red);
        rbBlue.setChecked(blue);
        rbGreen.setChecked(green);
        rbOneDay.setChecked(oneDay);
        rbTwoDays.setChecked(twoDays);
        rbOneWeek.setChecked(oneWeek);
    }

    public void onSave(View v){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("music",cbMusic.isChecked());
        editor.putBoolean("red", rbRed.isChecked());
        editor.putBoolean("blue", rbBlue.isChecked());
        editor.putBoolean("green", rbGreen.isChecked());
        editor.putBoolean("oneDay", rbOneDay.isChecked());
        editor.putBoolean("twoDays", rbTwoDays.isChecked());
        editor.putBoolean("oneWeek", rbOneWeek.isChecked());
        editor.apply();
        if(rbOneDay.isChecked())
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.textNotifOneD), Toast.LENGTH_SHORT).show();
        }
        else if(rbTwoDays.isChecked())
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.textNotifTwoD), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.textNotifOneW), Toast.LENGTH_SHORT).show();
        }
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    void changeColorTheme()
    {
        preferences = getSharedPreferences("ID",0);
        redColor = preferences.getBoolean("red", false);
        blueColor = preferences.getBoolean("blue", true);
        greenColor = preferences.getBoolean("green", false);
        if(redColor)
        {
            this.setTheme(R.style.red);
        }
        else if(blueColor)
        {
            this.setTheme(R.style.blue);
        }
        else if(greenColor)
        {
            this.setTheme(R.style.green);
        }
    }

}