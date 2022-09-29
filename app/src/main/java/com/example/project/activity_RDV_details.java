package com.example.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Currency;

public class activity_RDV_details extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACT_PERMISSION_CODE = 1;
    private static final int CALL_PERMISSION_CODE = 2;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    int year,month,day, hours, minutes;

    SimpleCursorAdapter adapter;

    SharedPreferences preferences;

    Button btnPickDate;
    Button btnPickTime;
    Button btnAddRDV;
    TextView tvId;
    EditText etDate;
    EditText etTime;
    EditText etDescription;
    EditText etContact;
    EditText etAddress;
    EditText etPhoneNumber;
    boolean fromAdd;

    boolean redColor;
    boolean blueColor;
    boolean greenColor;

    private DatabaseHelper myHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeColorTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rdv_details);
        btnPickDate=(Button)findViewById(R.id.btnPickDate);
        btnPickTime=(Button)findViewById(R.id.btnPickTime);
        btnAddRDV=(Button)findViewById(R.id.btnAddRDV);
        tvId =(TextView) findViewById(R.id.tvId);
        etDate= (EditText) findViewById(R.id.etDate);
        etTime= (EditText) findViewById(R.id.etTime);
        etDescription=(EditText) findViewById(R.id.etDescription);
        etContact=(EditText) findViewById(R.id.etContact);
        etAddress=(EditText) findViewById(R.id.etAddress);
        etPhoneNumber=(EditText) findViewById(R.id.etPhoneNumber);


        myHelper = new DatabaseHelper(this);
        myHelper.open();

        //Pour modifier RDV
        Intent intent = getIntent();
        fromAdd= intent.getBooleanExtra("fromAdd",false);
        if(!fromAdd){
            Bundle b= intent.getExtras();
            RDV selectedRDV= b.getParcelable("SelectedRdv");
            tvId.setText(String.valueOf(selectedRDV.getIdentifier()));
            etDescription.setText(selectedRDV.getDescription());
            etDate.setText(selectedRDV.getDate());
            etTime.setText(selectedRDV.getTime());
            etContact.setText(selectedRDV.getContact());
            etAddress.setText(selectedRDV.getAddress());
            etPhoneNumber.setText(selectedRDV.getPhoneNumber());
        }

        //Pour pickContact
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri contactData= data.getData();
                        Cursor c = getContentResolver().query(contactData, null, null, null, null);
                        if (c.moveToFirst()) {
                            EditText etContact_= findViewById(R.id.etContact);
                            String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                            etContact_.setText(name);
                        }
                    }
                });
    }


    public void pickDate(View view){
        showDatePicker();
    }

    public void pickTime(View view){
        showTimePicker();
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            etDate.setText(new StringBuilder().append(month + 1).
                    append("-").append(day).append("-").append(year));
        }
    };

    private void showDatePicker() {
        DatePickerFragment date= new DatePickerFragment();
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        Bundle args = new Bundle();
        args.putInt("year",year);
        args.putInt("month",month);
        args.putInt("day",day);
        date.setArguments(args);
        date.setCallBack(onDate);
        date.show(getSupportFragmentManager(),"Date Picker");
    }

    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            hours = hour;
            minutes = minute;

            etTime.setText(new StringBuilder().append(hours).append(":").append(minutes));
        }
    };

    private void showTimePicker() {
        TimePickerFragment time= new TimePickerFragment();
        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        Bundle args = new Bundle();
        args.putInt("hours",hours);
        args.putInt("minutes",minutes);
        time.setArguments(args);
        time.setCallBack(onTime);
        time.show(getSupportFragmentManager(),"Time Picker");
    }

    public void onCancelClick(View v){
        //Intent intent = new Intent(this,MainActivity.class);
        // startActivity(intent);
        finish();
    }

    public void saveRDV(View view) {
        String description = etDescription.getText().toString();
        String contact = etContact.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        String address = etAddress.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        RDV new_rdv = new RDV(description, date, time, contact, address, phoneNumber);
        if(address.matches(""))
        {
            new_rdv.setAddress("no address");
        }
        if(phoneNumber.matches(""))
        {
            new_rdv.setPhoneNumber("no phone number");
        }

        if(fromAdd)
        {
            myHelper.add(new_rdv);

            Intent main = new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
        else {
            int id = Integer.parseInt(tvId.getText().toString());
            RDV rdv = new RDV(id,description,date,time,contact,address,phoneNumber);
            myHelper.update(rdv);

            Intent main = new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case CONTACT_PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    someActivityResultLauncher.launch(intent);
                }
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
            case CALL_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    String phone = etPhoneNumber.getText().toString();
                    if(phone.isEmpty() || phone.equals("no phone number"))
                    {
                        Toast.makeText(getApplicationContext(), "Please enter phone number", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
                        }
                        else
                        {
                            String s = "tel:" + phone;
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(s));
                            startActivity(intent);
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
        }
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME}, null, null,null);
        //return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor>
                                       loader, Cursor data) {
        adapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor>
                                      loader) {
        adapter.swapCursor(null);
    }

    public void pickContact(View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    CONTACT_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            //startActivityForResult(intent, CONTACT_PICK_CODE);
            someActivityResultLauncher.launch(intent);
        }
    }

    public void launchMaps(View view) {
        String map = "http://maps.google.co.in/maps?q=" + etAddress.getText() ;
        Uri gmmIntentUri = Uri.parse(map);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        startActivity(mapIntent);

    }

    public void callPhoneNumber(View view)
    {
        String phone = etPhoneNumber.getText().toString();
        if(phone.isEmpty() || phone.equals("no phone number"))
        {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_LONG).show();
        }
        else
        {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
            }
            else
            {
                String s = "tel:" + phone;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(s));
                startActivity(intent);
            }
        }

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
