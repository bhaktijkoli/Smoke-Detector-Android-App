package com.bhaktijkoli.smokedetector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bhaktij on 01/06/18.
 */

public class WifiConnectRequest {

    private Context context;
    private String name;
    private String password;
    private ProgressDialog progressDialog;

    public WifiConnectRequest(Context context, String name, String password) {
        this.context = context;
        this.name = name;
        this.password = password;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new postConnectRequest().execute();
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
                        .add("ssid", name)
                        .add("password", password)
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
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Add WiFi");
            if (result != null && result.equals("1")) {
                builder.setMessage("Connected.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
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
