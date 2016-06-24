package at.htl.birthdayreminder;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

// https://www.youtube.com/watch?v=xv_JJbjDQ3M
public class SettingsActivity extends AppCompatActivity {

    private final String HOURS = Pref.HOURS;
    private final String MINUTES = Pref.MINUTES;

    private static final int DIALOG_ID = 0;
    private TextView reminderTime;
    private Pref pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = new Pref(getApplicationContext());

        CardView cardView = (CardView) findViewById(R.id.cv);
        reminderTime = (TextView) findViewById(R.id.reminderTime);
        ShowPreferences();

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }

    private void ShowPreferences() {
        reminderTime.setText(pref.getHours()+":"+pref.getMinutes());
    }

    public void SavedPreferences(int hours, int minutes){
        SharedPreferences sharedPref =
                getSharedPreferences("pref_time", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(HOURS, String.format("%02d", hours));
        editor.putString(MINUTES, String.format("%02d", minutes));
        editor.apply();

        CharSequence saveText = getString(R.string.saveText);
        Toast.makeText(SettingsActivity.this, saveText, Toast.LENGTH_SHORT).show();

        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            return new TimePickerDialog(SettingsActivity.this,
                    timePickerListener,
                    Integer.valueOf(pref.getHours()),
                    Integer.valueOf(pref.getMinutes()),
                    true); //24 hours
        }
        return null;
    }

    protected TimePickerDialog.OnTimeSetListener timePickerListener =
        new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SavedPreferences(hourOfDay, minute);
                ShowPreferences();
            }
        };
}
