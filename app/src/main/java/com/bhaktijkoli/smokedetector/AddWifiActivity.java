package com.bhaktijkoli.smokedetector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddWifiActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json");

    private EditText etSSID;
    private EditText etPassword;
    private Button btnConnect;
    private CheckBox checkShowPassword;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WiFi Configuration");
        toolbar.setSubtitle("Add WiFi");
        setSupportActionBar(toolbar);
        etSSID = (EditText) findViewById(R.id.etSSID);
        etPassword = (EditText) findViewById(R.id.etPassword);
        checkShowPassword = (CheckBox) findViewById(R.id.checkShowPassword);
        btnConnect = (Button) findViewById(R.id.btnConnect);

        checkShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkShowPassword.isChecked()) {
                    etPassword.setTransformationMethod(null);
                } else {
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(AddWifiActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.show();
                new postConnectRequest().execute();
            }
        });
    }

    class postConnectRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                RequestBody formBody = new FormBody.Builder()
                        .add("ssid", etSSID.getText().toString())
                        .add("password", etPassword.getText().toString())
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.4.1/connect")
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Error";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(AddWifiActivity.class.toString(), "onPostExecute: " + result);
            pDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(AddWifiActivity.this);
            builder.setTitle("Add WiFi");
            if (result != null && result.equals("1")) {
                builder.setMessage("Connected.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
            } else {
                builder.setMessage("Failed to connect.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
            }
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


}