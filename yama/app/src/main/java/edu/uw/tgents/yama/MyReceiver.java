package edu.uw.tgents.yama;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

/**
 * Created by tgents on 2/3/2016.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("RECEIVER", intent.toString());

        SmsMessage[] messages = getMessagesFromIntent(intent);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean auto = sharedPref.getBoolean("auto_reply", false);
        String reply = sharedPref.getString("replytext", "");


        for (SmsMessage msg : messages) {
            Log.v("RECEIVER", msg.getOriginatingAddress() + ": " + msg.getMessageBody());
            String fromNum = msg.getOriginatingAddress();
            String textMsg = msg.getMessageBody();

            if (auto) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(fromNum, null, reply, null, null);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_terrain_24dp)
                    .setContentTitle("Text: " + fromNum)
                    .setContentText(textMsg)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);;

            Intent resultIntent = new Intent(context, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, builder.build());
        }
    }
}
