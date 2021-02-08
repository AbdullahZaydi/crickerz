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
    int captainLength = 0;
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
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PlayerList rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_2, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.addBtn = (ImageView) convertView.findViewById(R.id.addBtn);
            holder.addBtn.setVisibility(View.GONE);
            holder.value = (TextView) convertView.findViewById(R.id.value);
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
        View finalConvertView = convertView;
        final boolean[] captainIsSelected = {false};
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("name").equals(rowItems.get(position).getTitle())) {
                            if (!jsonObject.has("captain")) {
                                if(!captainIsSelected[0]) {
                                    jsonObject.put("captain", true);
                                    rowItems.get(position).togglePlayerStatus();
                                    notifyDataSetChanged();
                                    captainIsSelected[0] = true;
                                    jsonArray.remove(i);
                                    jsonArray.put(jsonObject);
                                    break;
                                }
                                else {
                                    Toast toast = Toast.makeText(context, "You can only select 1 Captain", Toast.LENGTH_SHORT);
                                    View view = toast.getView();
                                    view.getBackground().setColorFilter(finalConvertView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                    TextView text = view.findViewById(android.R.id.message);
                                    text.setTextColor(finalConvertView.getResources().getColor(R.color.white));
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                            else {
                                jsonObject.remove("captain");
                                rowItems.get(position).togglePlayerStatus();
                                notifyDataSetChanged();
                                captainIsSelected[0] = false;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.addBtn.setTag(position);
//        ImageView btn = (ImageView) finalConvertView.findViewById(R.id.addBtn);
//        if(rowItems.get(position).isPlayerAdded()) {
//            btn.setImageResource(R.drawable.remove);
//        }
//        else {
//            btn.setImageResource(R.drawable.captain);
//        }
        return convertView;
    }
}
