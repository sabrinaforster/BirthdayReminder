package at.htl.birthdayreminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import at.htl.birthdayreminder.entity.Contact;

public class LocalService extends Service {

    private NotificationManager notificationManager;
    private int NOTIFICATION = 1;
    private final IBinder binder = new LocalBinder();
    private List<Contact> contactList;

    public void setContactList(List<Contact> contactList)
    {
        this.contactList = contactList;
    }

    public class LocalBinder extends Binder{
        LocalService getService(){
            return LocalService.this;
        }
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent.getFlags() == 5) {
            showNotification();
            intent.setFlags(1);
        }
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(NOTIFICATION);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void showNotification() {
        Contact nextContact = getNextBirthdayContact(null);

        if (nextContact != null) {
            Date now = new Date();
            if(nextContact.getBirthday().after(now) == false &&
                    nextContact.getBirthday().before(now) == false &&
                    nextContact.getBirthday().getTime() < now.getTime())
            {
                return;
            }

            String name = nextContact.getName();
            long timeInMillis = System.currentTimeMillis();

            CharSequence text = getText(R.string.local_service_label);
            CharSequence content1 = getText(R.string.local_service_content1);
            CharSequence content2 = getText(R.string.local_service_content2);

            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(text)
                    .setWhen(timeInMillis)
                    .setContentTitle(text)
                    .setContentText(content1 + " " + name + " " + content2)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setVibrate(new long[]{1000, 1000})
                    .build();

            notificationManager.notify(NOTIFICATION, notification);
        }
    }

    public Contact getNextBirthdayContact(List<Contact> contacts) {
        if (contacts != null) {
            contactList = contacts;
        }
        if (contactList != null && contactList.size() > 0) {
            for (Contact contact : contactList) {
                Contact tempContact = new Contact(contact.getName(),
                        new Date(contact.getBirthday().getTime()));
                Date now = new Date();

                if (tempContact.getBirthday().after(now) ||
                        tempContact.getBirthday().getDate() == now.getDate() &&
                                tempContact.getBirthday().getMonth() == now.getMonth()) {
                    return tempContact;
                }
            }
            return contactList.get(contactList.size()-1);
        }
        return null;
    }
}
