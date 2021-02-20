package com.zayditech.cricerzfantasy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CreateTeamAdapter extends ArrayAdapter<PlayerList> {

    Context context;
    List<PlayerList> rowItems;
    String value;
    ArrayList playerData;
    JSONArray team;
    boolean isCaptainSelected = false;
    String selectedCaptain = "";
    boolean updatingTeam = false;
    boolean creatingTeam = true;
    boolean ranOnce = false;
    Tab2Fragment tab2Fragment;
    int oldPos = -1;
    boolean captainFound = false;
    private DatabaseReference teamRef;
    public CreateTeamAdapter(Context context, int resourceId,
                             List<PlayerList> items, String value, Tab2Fragment _tab2Fragment) {
        super(context, resourceId, items);
        this.context = context;
        this.rowItems = items;
        this.value = value;
        playerData = new ArrayList();
        team = new JSONArray();
        this.tab2Fragment = _tab2Fragment;
        GeneralMethods gms = new GeneralMethods(getContext());
        teamRef = FirebaseDatabase.getInstance().getReference(gms.encodeIntoBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
    }

    /*private view holder class*/
    private class ViewHolder  {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
        ImageView addBtn;
        TextView value;
        TextView index;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PlayerList rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_4, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.addBtn = (ImageView) convertView.findViewById(R.id.addBtn);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            holder.index = (TextView) convertView.findViewById(R.id.index);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDesc.setText(rowItem.getDesc().equals(null)
            || rowItem.getDesc().equals("null")
            || rowItem.getDesc().equals("") ? "Unknown" : rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());
        holder.value.setText(String.valueOf(rowItem.getValue()));
        if(rowItem.getImageId().equals(null) || rowItem.getImageId().equals("")) {
            holder.imageView.setImageResource(R.drawable.cricerzlogo);
        }
        else {
            Picasso.get().load(rowItem.getImageId()).into(holder.imageView);
        }

        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String oldVal = snapshot.getValue(String.class);
                try {
                    JSONArray jsonArray = new JSONArray(oldVal);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(jsonObject.has("captain")) {
                            creatingTeam = false;
                            ranOnce = true;
                        }
                    }
                }
                catch (Exception ex) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        int pos = getPosition(rowItem) + 1;
        holder.index.setText(String.valueOf(pos));
        View finalConvertView = convertView;
        holder.addBtn.setOnClickListener(v -> {
            int position1 = (Integer) v.getTag();
            try {
                JSONArray jsonArray = new JSONArray(value);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(isCaptainSelected && !(selectedCaptain.equals(rowItems.get(position1).getTitle()))) {
                        TastyToast.makeText(getContext(),"You've already selected captain!",
                                TastyToast.LENGTH_SHORT,
                                TastyToast.ERROR);
                        break;
                    }

                    if(!updatingTeam) {
                        if(!creatingTeam) {
                            TastyToast.makeText(getContext(),"You've already selected captain previously. Please review your selection!",
                                    TastyToast.LENGTH_SHORT,
                                    TastyToast.ERROR);
                            break;
                        }
                    }

                    if (jsonObject.getString("name").equals(rowItems.get(position1).getTitle())) {
                        if(!rowItems.get(position1).isPlayerAdded()) {
                            jsonObject.put("captain", true);
                            isCaptainSelected = true;
                            selectedCaptain = jsonObject.getString("name");
                        }
                        else {
                            jsonObject.remove("captain");
                            isCaptainSelected = false;
                            selectedCaptain = "";
                        }
                        rowItems.get(position1).togglePlayerStatus();
                        notifyDataSetChanged();
                        GeneralMethods gms = new GeneralMethods(getContext());
                        jsonArray = gms.updateJsonArray(jsonArray, jsonObject);
                        tab2Fragment.setTeamJSON(jsonArray.toString());
                        value = jsonArray.toString();
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        try {
            if(!captainFound) {
                JSONArray jsonArray = new JSONArray(value);
                GeneralMethods gms = new GeneralMethods(getContext());
                int indexOfArr = gms.findInJSONArray(jsonArray, rowItem.getTitle());
                if(indexOfArr != -1 && oldPos != getPosition(rowItem)) {
                    JSONObject jsonObject = jsonArray.getJSONObject(indexOfArr);
                    if(jsonObject.has("captain")) {
                        isCaptainSelected = true;
                        selectedCaptain = jsonObject.getString("name");
                        rowItems.get(getPosition(rowItem)).togglePlayerStatus();
                        notifyDataSetChanged();
                        tab2Fragment.setTeamJSON(jsonArray.toString());
                        captainFound = true;
                        updatingTeam = true;
                    }
                    oldPos = getPosition(rowItem);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.addBtn.setTag(position);
        ImageView btn = (ImageView) finalConvertView.findViewById(R.id.addBtn);
        if(rowItems.get(position).isPlayerAdded()) {
            btn.setImageResource(R.drawable.selected_captain);
        }
        else {
            btn.setImageResource(R.drawable.captain);
        }
        return convertView;
    }
}
