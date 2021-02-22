package com.zayditech.cricerzfantasy;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MatchListAdapter extends ArrayAdapter<MatchList> {

    Context context;
    List<MatchList> rowItems;
    String value;
    ArrayList playerData;
    JSONArray team;
    private DatabaseReference teamRef;
    private GeneralMethods gms;
    public MatchListAdapter(Context context, int resourceId,
                            List<MatchList> items, String value) {
        super(context, resourceId, items);
        this.context = context;
        this.rowItems = items;
        this.value = value;
        playerData = new ArrayList();
        team = new JSONArray();
        gms = new GeneralMethods(context);
        teamRef = FirebaseDatabase.getInstance().getReference(gms.encodeIntoBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
    }

    /*private view holder class*/
    private class ViewHolder  {
        TextView firstTeam;
        TextView secondTeam;
        TextView date;
        TextView time;
    }
    int count = 0;

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        MatchList rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_team, null);
            holder = new ViewHolder();
            holder.firstTeam = (TextView) convertView.findViewById(R.id.firstTeam);
            holder.secondTeam = (TextView) convertView.findViewById(R.id.secondTeam);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.firstTeam.setText(rowItem.getFirstTeam());
        holder.secondTeam.setText(rowItem.getSecondTeam());
        holder.date.setText(String.valueOf(rowItem.getDate()));
        holder.time.setText(String.valueOf(rowItem.getTime()));

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
