package br.com.trmasolucoes.testegcm.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import br.com.trmasolucoes.testegcm.receivers.GcmBroadcastReceiver;
import br.com.trmasolucoes.testegcm.util.NotificationCustomUtil;

/**
 * Created by tairo on 21/07/15.
 */
public class GcmIntentService extends IntentService {

    public static final String TAG = "Script";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(GcmIntentService.this);
        String title, author, message, messageType = gcm.getMessageType(intent);
        
        if(extras != null){
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){
                Log.i("Script", "ERROR: " + extras.toString());
            }
            else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
                Log.i("Script", "DELETED: " + extras.toString());
            }
            else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
                title = extras.getString("title");
                author = extras.getString("author");
                message = extras.getString("message");

                NotificationCustomUtil.sendNotification(GcmIntentService.this, title, author, message);
            }
        }
        //libera o processador
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
