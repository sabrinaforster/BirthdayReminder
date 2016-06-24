package at.htl.birthdayreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.sql.Time;

/**
 * Created by Sabrina on 17.06.2016.
 */

public class Pref extends AppCompatActivity {
    public static final String HOURS = "pref_hours";
    public static final String MINUTES = "pref_minutes";
    private static final String TIME = "pref_time";
    private Time time = new Time(8, 0, 0);
    private Context context;

    public Pref(Context context) {
        this.context = context;
    }

    public String getHours(){
        SharedPreferences sharedPref =
                context.getSharedPreferences(TIME, Context.MODE_PRIVATE);
        String hours = sharedPref.getString(HOURS, "");
        if (hours == "") {
            return String.format("%02d", time.getHours());
        }
        return hours;
    }
    public int getHoursInt(){
        try {
            return Integer.parseInt(getHours());
        } catch (Exception e) {
            return time.getHours();
        }
    }
    public String getMinutes(){
        SharedPreferences sharedPref =
                context.getSharedPreferences(TIME, Context.MODE_PRIVATE);
        String minutes = sharedPref.getString(MINUTES, "");
        if (minutes == "") {
            return String.format("%02d", time.getMinutes());
        }
        return minutes;
    }
    public int getMinutesInt(){
        try {return Integer.parseInt(getMinutes());}
        catch (Exception e) {
            return time.getMinutes();
        }
    }
}
