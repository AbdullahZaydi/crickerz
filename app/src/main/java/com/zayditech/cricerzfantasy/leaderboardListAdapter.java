package com.zayditech.cricerzfantasy;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class leaderboardListAdapter extends ArrayAdapter<LeaderboardList> {
    Context context;
    List<LeaderboardList> rowItems;
    ArrayList playerData;
    JSONArray team;
    private DatabaseReference teamRef;
    private GeneralMethods gms;
    public leaderboardListAdapter(Context context, int resourceId,
                                  List<LeaderboardList> items) {
        super(context, resourceId, items);
        this.context = context;
        this.rowItems = items;
        playerData = new ArrayList();
        team = new JSONArray();
        gms = new GeneralMethods(context);
        teamRef = FirebaseDatabase.getInstance().getReference(gms.encodeIntoBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
    }

    /*private view holder class*/
    private class ViewHolder  {
        TextView email;
        TextView points;
    }
    int count = 0;

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LeaderboardList rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_leaderboard, null);
            holder = new ViewHolder();
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.points = (TextView) convertView.findViewById(R.id.points);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
            holder.email.setText(rowItem.getEmail());
            holder.points.setText(String.valueOf(rowItem.getPoints()));
//        holder.addBtn.setTag(position);
//        ImageView btn = (ImageView) finalConvertView.findViewById(R.id.addBtn);
//        if(rowItems.get(position).isPlayerAdded()) {
//            btn.setImageResource(R.drawable.remove);
//        }
//        else {
//            btn.setImageResource(R.drawable.add);
//        }
        return convertView;
    }
}
