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

import com.example.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateTeamAdapter extends ArrayAdapter<PlayerList> {

    Context context;
    List<PlayerList> rowItems;
    String value;
    ArrayList playerData;
    JSONArray team;
    boolean isCaptainSelected = false;
    String selectedCaptain = "";
    public CreateTeamAdapter(Context context, int resourceId,
                             List<PlayerList> items, String value) {
        super(context, resourceId, items);
        this.context = context;
        this.rowItems = items;
        this.value = value;
        playerData = new ArrayList();
        team = new JSONArray();
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
    boolean firstTime = true;
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
                        TastyToast.error(getContext(),"You've already selected captain!",
                                TastyToast.LENGTH_SHORT,
                                TastyToast.SHAPE_RECTANGLE,
                                false);
                        break;
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
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
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
