package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper myHelper;
    ListView lvRDV;
    SharedPreferences preferences;
    boolean music;
    boolean redColor;
    boolean blueColor;
    boolean greenColor;
    boolean oneDay;
    boolean twoDays;
    boolean oneWeek;
    boolean english;
    boolean french;
    MediaPlayer player;

    static String CHANNEL_ID= "channel_01";
    static int NOTIFICATION_ID=100;
    static int REQUEST_CODE= 200;

    int numberDaysNotif = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("ID",0);
        changeColorTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myHelper = new DatabaseHelper(this);
        myHelper.open();

        lvRDV = (ListView) findViewById(R.id.lvRDV);
        lvRDV.setEmptyView(findViewById(R.id.ivEmpty));

        player = MediaPlayer.create(this,R.raw.wii_music);

        lvRDV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String idItem= ((TextView)view.findViewById(R.id.tvIdDeItem)).getText().toString();
                String descriptionItem= ((TextView)view.findViewById(R.id.description)).getText().toString();
                String dateItem= ((TextView)view.findViewById(R.id.tvDate)).getText().toString();
                String timeItem= ((TextView)view.findViewById(R.id.time)).getText().toString();
                String contactItem= ((TextView)view.findViewById(R.id.contact)).getText().toString();
                String addressItem= ((TextView)view.findViewById(R.id.address)).getText().toString();
                String phoneNumberItem= ((TextView)view.findViewById(R.id.phone_number)).getText().toString();
                RDV pRDV= new RDV(Integer.parseInt(idItem),descriptionItem, dateItem, timeItem, contactItem, addressItem, phoneNumberItem);
                Intent intent = new Intent(getApplicationContext(), activity_RDV_details.class);
                intent.putExtra("SelectedRdv",pRDV);


                intent.putExtra("fromAdd",false);
                startActivity(intent);
            }
        });

        chargeData();

        registerForContextMenu(lvRDV);

        music = preferences.getBoolean("music", false);
        oneDay = preferences.getBoolean("oneDay", true);
        twoDays = preferences.getBoolean("twoDays", false);
        oneWeek = preferences.getBoolean("oneWeek", false);
        english = preferences.getBoolean("english", true);
        french = preferences.getBoolean("french", false);
        if (music) {
            stopMusic();
            startMusic();
        }
        else{
            stopMusic();
        }

        if(oneDay)
        {
            numberDaysNotif = 1;
        }
        else if(twoDays)
        {
            numberDaysNotif = 2;
        }
        else
        {
            numberDaysNotif = 7;
        }

        createNotificationChannel();

        Log.d("date", dayBefore("4-16-2022",0));

        getNotifRDV();

    }

    public void setPreferences(){
        Intent intent= new Intent(this,PreferencesActivity.class);
        preferences= getSharedPreferences("prefs",MODE_PRIVATE);
        intent.putExtra("music",preferences.getBoolean("music",music));
        intent.putExtra("red",preferences.getBoolean("red",redColor));
        intent.putExtra("blue",preferences.getBoolean("blue",blueColor));
        intent.putExtra("green",preferences.getBoolean("green",greenColor));
        intent.putExtra("oneDay",preferences.getBoolean("oneDay",oneDay));
        intent.putExtra("twoDays",preferences.getBoolean("twoDays",twoDays));
        intent.putExtra("oneWeek",preferences.getBoolean("oneWeek",oneWeek));
        intent.putExtra("english", preferences.getBoolean("english", english));
        intent.putExtra("french", preferences.getBoolean("french", french));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.rdv_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.preferences:
            {
                setPreferences();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addRDV(View view)
    {
        Intent intent=new Intent(this, activity_RDV_details.class);
        intent.putExtra("fromAdd",true);
        startActivity(intent);
    }

    public void chargeData(){
        final String[] from = new String[]{DatabaseHelper._ID, DatabaseHelper.DESCRIPTION,
                DatabaseHelper.DATE ,DatabaseHelper.TIME
                ,DatabaseHelper.CONTACT ,DatabaseHelper.ADDRESS, DatabaseHelper.PHONE_NUMBER, DatabaseHelper.DONE};
        final int[]to= new int[]{R.id.tvIdDeItem,R.id.description,R.id.tvDate,R.id.time,R.id.contact, R.id.address,
                R.id.phone_number};

        Cursor c = myHelper.getAllRDV();
        SimpleCursorAdapter adapter= new SimpleCursorAdapter(this,R.layout.rdv_item_view,c,from,to,0);
        adapter.notifyDataSetChanged();
        lvRDV.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu,menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        if (item.getItemId()==R.id.delete){
            myHelper.delete(info.id);
            chargeData();
            return true;
        }
        else if(item.getItemId()==R.id.share)
        {
            shareMethod(info.id);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void shareMethod(long _id)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, myHelper.getShareTextFromDataBase(_id));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share App"));
    }

    public void startMusic() {
        Intent serviceIntent= new Intent(this,MusicService.class);
        startService(serviceIntent);
    }

    public void stopMusic() {
        stopService(new Intent(this,MusicService.class));
    }

    void changeColorTheme()
    {
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

    private void createNotificationChannel() {
        // Create a NotificationChannel, only for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RDV Notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("Channel for notification RDV");
            // register the channel
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification() {
        Intent intent= new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=
                PendingIntent.getActivity(this,REQUEST_CODE,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("RDV Soon")
                .setContentText("You have a RDV soon")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        // notificationId : unique identifier to define
        notificationManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    public String dayBefore(String date, int numberOfDaysBefore){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_MONTH, -numberOfDaysBefore);
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
        return sdf1.format(c.getTime());
    }

    public void getNotifRDV(){
        Cursor c = myHelper.getAllDate();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            String date = c.getString(0);
            Log.d("dateInDatabase", date);
            String dateBefore = dayBefore(date, numberDaysNotif);
            Log.d("dateInDatabase", dateBefore);
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDate =  new SimpleDateFormat("MM-dd-yyyy");
            String strCurrentTime = simpleDate.format(currentTime);
            Log.d("dateInDatabase", strCurrentTime);
            if(strCurrentTime.equals(dateBefore) || strCurrentTime.equals(dayBefore(date, 0)))
            {
                showNotification();
            }
        }
    }

}