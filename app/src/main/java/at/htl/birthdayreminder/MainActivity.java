package at.htl.birthdayreminder;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import at.htl.birthdayreminder.entity.Contact;
import at.htl.birthdayreminder.entity.ContactsAdapter;

//https://www.youtube.com/watch?v=sn9geMORhNE
//http://stackoverflow.com/questions/8579883/get-birthday-for-each-contact-in-android-application
public class MainActivity extends AppCompatActivity{
    private List<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;

    private String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private String BIRTHDAY = ContactsContract.CommonDataKinds.Event.START_DATE;

    private int permRequestCode = 200;
    private LocalService boundService;
    boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        contactsAdapter = new ContactsAdapter(contactList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);

        prepareContactData();
        doBindService();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void prepareContactData() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //M is for Marshmallow
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            String[] perms = {"android.permission.READ_CONTACTS"};
            requestPermissions(perms, permRequestCode);
        }
        Cursor cursor = getContactsBirthdays();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
            String bDay = cursor.getString(cursor.getColumnIndex(BIRTHDAY));

            Date date = new Date();
            try{
                DateFormat dateFormatNormal = new SimpleDateFormat(getString(R.string.dateFormatNormal));
                date = dateFormatNormal.parse(bDay);
            } catch (ParseException e) {
                e.printStackTrace();
                try {
                    DateFormat dateFormatSkype = new SimpleDateFormat(getString(R.string.dateFormatSkype));
                    date = dateFormatSkype.parse(bDay);
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }
            Contact contact = new Contact(name, date);
            contactList.add(contact);
        }
        Collections.sort(contactList);
        contactsAdapter.notifyDataSetChanged();
    }

    private void addTestData() {
        Contact zuckerberg = new Contact("Mark Zuckerberg", new Date(84, Calendar.MAY, 14));
        contactList.add(zuckerberg);

        Contact gosling = new Contact("James Gosling", new Date(55, Calendar.MAY, 19));
        contactList.add(gosling);

        Contact jobs = new Contact("Steve Jobs", new Date(55, Calendar.FEBRUARY, 24));
        contactList.add(jobs);
    }

    private Cursor getContactsBirthdays() {
        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                DISPLAY_NAME,
                BIRTHDAY
        };

        String where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;

        String[] selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };
        String sortOrder = null;
        return managedQuery(uri, projection, where, selectionArgs, sortOrder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                boolean contactAccepted =
                        grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boundService = ((LocalService.LocalBinder) service).getService();
            boundService.setContactList(contactList);
            setAlarmManager();
        }

        public void setAlarmManager(){
            Contact nextContact = boundService.getNextBirthdayContact(contactList);
            Date now = new Date();
            Date nextDate = null;
            if (nextContact != null) {
                nextDate = nextContact.getBirthday();
            }
            if (nextDate != null) {
                Calendar calender = Calendar.getInstance();

                Pref pref = new Pref(getApplicationContext());
                calender.set(now.getYear()+1900, nextDate.getMonth(), nextDate.getDate(),
                        pref.getHoursInt(), pref.getMinutesInt(), 0);

                Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, 0);
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            boundService = null;
        }
    };

    private  void doBindService(){
        bindService(new Intent(
                MainActivity.this, LocalService.class),
                connection,
                Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void doUnbindService(){
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
