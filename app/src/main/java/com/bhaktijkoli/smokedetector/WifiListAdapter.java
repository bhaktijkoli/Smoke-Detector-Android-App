package com.bhaktijkoli.smokedetector;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bhaktij on 31/05/18.
 */

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.WifiListViewHolder> {
    JSONArray data;
    public WifiListAdapter(JSONArray data)
    {
        this.data = data;
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
            case 2: return "Secured with WPA";
            case 4: return "Secured with WPA2";
            case 7: return "None";
            case 8: return "Secured with WPA/WPA2";
            default: return "Undefined";
        }
    }

    public class WifiListViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSecurity;
        public WifiListViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSecurity = (TextView) itemView.findViewById(R.id.tvSecurity);
        }
    }
}
