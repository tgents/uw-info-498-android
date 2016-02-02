package edu.uw.tgents.yama;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;

/**
 * Created by tgents on 2/2/2016.
 */
public class SendActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("phoneNo", null, "sms message", null, null);

    }
}
