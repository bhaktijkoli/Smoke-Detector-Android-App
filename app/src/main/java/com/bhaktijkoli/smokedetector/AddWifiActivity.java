package com.bhaktijkoli.smokedetector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddWifiActivity extends AppCompatActivity {

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
               new HttpGetRequest().execute();
            }
        });
    }

    class HttpGetRequest extends AsyncTask<String, String, String> {
        public static final String REQUEST_URL = "http://192.168.4.1/connect";
        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params) {
            String responseString = "";
            String inputLine;
            try {
                URL myUrl = new URL(REQUEST_URL);
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("name", etSSID.getText().toString());
                jsonParam.put("password", etPassword.getText().toString());

                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                responseString = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                responseString = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("TAG", "onPostExecute: " + result);
            pDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(AddWifiActivity.this);
            builder.setTitle("Add WiFi");
            if (result != null && result.equals("1")) {
                builder.setMessage("Connected.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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
