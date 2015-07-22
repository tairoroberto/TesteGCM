package br.com.trmasolucoes.testegcm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

import br.com.trmasolucoes.testegcm.util.AndroidSystemUtil;
import br.com.trmasolucoes.testegcm.util.HttpConnectionUtil;


public class MainActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "Script";

    private String SENDER_ID = "846065425374";
    private String regId;
    private TextView txtRegistrationId;

    private GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRegistrationId = (TextView) findViewById(R.id.txtId);
        
        //Verifica se tem suporte a Google Play Sevices
        if(checkPalyServices()){
            gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
            regId = AndroidSystemUtil.getRegistrationId(MainActivity.this);
            
            if(regId.trim().length() == 0){
                RegisterIdInBackGround registerIdInBackGround = new RegisterIdInBackGround();
                registerIdInBackGround.execute();
            }
        }
    }

    public boolean checkPalyServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, MainActivity.this, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                Toast.makeText(MainActivity.this, "Sem suporte a Google Play Services", Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPalyServices();
    }


    /*******************************************************************************************/
    /**                   Class to make insert event in system                               */
    /*****************************************************************************************/
    private class RegisterIdInBackGround extends AsyncTask<Object,Void,Object> {
            @Override
        protected String doInBackground(Object[] params) {
            String msg = "";

            try {
                if(gcm == null){
                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                }

                regId = gcm.register(SENDER_ID);
                msg = "Register Id: " + regId;
                String feedback = HttpConnectionUtil.sendRegistrationIdToBackend(regId);
                Log.i(TAG,"FEEDBACK: " + feedback );

                AndroidSystemUtil.storeRegistrationId(MainActivity.this,regId);

            }catch (Exception e){
                Log.i(TAG, "ERROR: " + e.getMessage());
            }
            return msg;
        }

        @Override
        protected void onPostExecute(Object msg) {
            super.onPostExecute(msg);
            txtRegistrationId.setText((String)msg);
        }
    }
}
