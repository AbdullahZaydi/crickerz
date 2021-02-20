package com.zayditech.cricerzfantasy;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class YourTeamListViewAdapter extends ArrayAdapter<RowItem> {

    Context context;
    DatabaseReference teamRef;
    List<RowItem> rowItems;
    int oldPos = -1;
    boolean isCaptainSelected = false;
    public YourTeamListViewAdapter(Context context, int resourceId,
                                   List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
        this.rowItems = items;
        GeneralMethods gms = new GeneralMethods(getContext());
        teamRef = FirebaseDatabase.getInstance().getReference(gms.encodeIntoBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
        TextView index;
        ImageView addBtn;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_3, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.index = (TextView) convertView.findViewById(R.id.index);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.addBtn = (ImageView) convertView.findViewById(R.id.addBtn);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        int pos = position+1;
        holder.index.setText(String.valueOf(pos));
        holder.txtDesc.setText(rowItem.getDesc().equals(null)
                || rowItem.getDesc().equals("null")
                || rowItem.getDesc().equals("") ? "Unknown" : rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());
        if(rowItem.getImageId().equals(null) || rowItem.getImageId().equals("")) {
            holder.imageView.setImageResource(R.drawable.cricerzlogo);
        }
        else {
            Picasso.get().load(rowItem.getImageId()).into(holder.imageView);
        }

        if(!isCaptainSelected) {
            teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String value = snapshot.getValue(String.class);
                        JSONArray jsonArray = new JSONArray(value);
                        GeneralMethods gms = new GeneralMethods(getContext());
                        int indexOfArr = gms.findInJSONArray(jsonArray, rowItem.getTitle());
                        if(indexOfArr != -1 && oldPos != getPosition(rowItem)) {
                            JSONObject jsonObject = jsonArray.getJSONObject(indexOfArr);
                            if(jsonObject.has("captain")) {
                                rowItems.get(getPosition(rowItem)).togglePlayerStatus();
                                notifyDataSetChanged();
                                isCaptainSelected = true;
                            }
                            oldPos = getPosition(rowItem);
                        }
                    }
                    catch (Exception ex) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        holder.addBtn.setTag(position);
        ImageView btn = (ImageView) convertView.findViewById(R.id.addBtn);
        if(rowItems.get(position).isPlayerAdded()) {
            btn.setImageResource(R.drawable.selected_captain);
        }
        else {
            btn.setImageResource(R.drawable.empty);
        }
        return convertView;
    }
}
