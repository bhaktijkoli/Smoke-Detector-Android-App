package com.bhaktijkoli.smokedetector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

/**
 * Created by bhaktij on 31/05/18.
 */

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.WifiListViewHolder> {
    private Context context;
    private JSONArray data;
    private OkHttpClient okHttpClient;

    public WifiListAdapter(Context context, JSONArray data) {
        this.context = context;
        this.data = data;
        this.okHttpClient = new OkHttpClient();
    }

    @Override
    public WifiListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wifi_list_item_layout, parent, false);
        return new WifiListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WifiListViewHolder holder, int position) {
        String name = "";
        int security = 0;
        try {
            JSONObject jsonObject = data.getJSONObject(position);
            name = jsonObject.getString("name");
            security = jsonObject.getInt("encryption");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.tvName.setText(name);
        holder.tvSecurity.setText(getSecurityString(security));
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    public String getSecurityString(int n) {
        switch (n) {
            case 2:
                return "Secured with WPA";
            case 4:
                return "Secured with WPA2";
            case 7:
                return "None";
            case 8:
                return "Secured with WPA/WPA2";
            default:
                return "Undefined";
        }
    }

    public class WifiListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvSecurity;

        public WifiListViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSecurity = (TextView) itemView.findViewById(R.id.tvSecurity);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try {
                final JSONObject jsonObject = data.getJSONObject(getAdapterPosition());
                final String name = jsonObject.getString("name");
                final int security = jsonObject.getInt("encryption");
                if (security == 7) {
                    new WifiConnectRequest(context, name, "");
                } else {
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View dialogView = layoutInflater.inflate(R.layout.dialog_password, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(dialogView);
                    final EditText etPassword = (EditText) dialogView.findViewById(R.id.etPassword);
                    final CheckBox checkShowPassword = (CheckBox) dialogView.findViewById(R.id.checkShowPassword);
                    alertDialogBuilder.setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            new WifiConnectRequest(context, name, etPassword.getText().toString());
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    checkShowPassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (checkShowPassword.isChecked()) {
                                etPassword.setTransformationMethod(null);
                            } else {
                                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                            }
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
