package at.htl.birthdayreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Sabrina on 17.06.2016.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, LocalService.class);
        intent1.setFlags(5);
        context.startService(intent1);
    }
}
