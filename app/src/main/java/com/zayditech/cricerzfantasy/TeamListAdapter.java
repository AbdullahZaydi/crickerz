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

public class TeamListAdapter extends ArrayAdapter<PlayerList> {

    Context context;
    List<PlayerList> rowItems;
    String value;
    ArrayList playerData;
    JSONArray team;
    Tab1Fragment.SendMessage SM;
    int wktKeeperLength = 0;
    int batsmanLength = 0;
    int allRounderLength = 0;
    int bowlerLength = 0;
    int budget = 100;

    public TeamListAdapter(Context context, int resourceId,
                           List<PlayerList> items, String value, Tab1Fragment.SendMessage _sm) {
        super(context, resourceId, items);
        this.context = context;
        this.rowItems = items;
        this.value = value;
        this.SM = _sm;
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
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=(Integer) v.getTag();
                boolean forAdded = false;
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(!playerData.contains(rowItems.get(position).getTitle())) {
                            if(jsonObject.getString("name").equals(rowItems.get(position).getTitle()))
                            {
                                int pid = jsonObject.getInt("pid");
                                JSONObject jsonObj = new JSONObject();
                                jsonObj.put("pid",pid);
                                jsonObj.put("name",jsonObject.getString("name"));
                                jsonObj.put("imageURL",jsonObject.getString("imageURL"));
                                jsonObj.put("playingRole", jsonObject.getString("playingRole"));
                                jsonObj.put("value", jsonObject.getString("value"));
                                boolean canBeAdded = false;
                                if(jsonObject.getString("playingRole").equals("Wicket Keeper")) {
                                    wktKeeperLength++;
                                    if(wktKeeperLength > 1)  {
                                        Toast toast = Toast.makeText(context, "You can only select 1 wicket keeper", Toast.LENGTH_SHORT);
                                        View view = toast.getView();
                                        view.getBackground().setColorFilter(finalConvertView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                        TextView text = view.findViewById(android.R.id.message);
                                        text.setTextColor(finalConvertView.getResources().getColor(R.color.white));
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                    else {
                                        canBeAdded= true;
                                    }
                                }
                                if(jsonObject.getString("playingRole").equals("Batsman")) {
                                    batsmanLength++;
                                    if(batsmanLength > 5)  {
                                        Toast toast = Toast.makeText(context, "You can only select 4-5 Batsman", Toast.LENGTH_SHORT);
                                        View view = toast.getView();
                                        view.getBackground().setColorFilter(finalConvertView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                        TextView text = view.findViewById(android.R.id.message);
                                        text.setTextColor(finalConvertView.getResources().getColor(R.color.white));
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                    else {
                                        canBeAdded= true;
                                    }
                                }
                                if(jsonObject.getString("playingRole").equals("All Rounder")) {
                                    allRounderLength++;
                                    if(allRounderLength > 4)  {
                                        Toast toast = Toast.makeText(context, "You can only select 1-4 All Rounders", Toast.LENGTH_SHORT);
                                        View view = toast.getView();
                                        view.getBackground().setColorFilter(finalConvertView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                        TextView text = view.findViewById(android.R.id.message);
                                        text.setTextColor(finalConvertView.getResources().getColor(R.color.white));
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                    else {
                                        canBeAdded= true;
                                    }
                                }
                                if(jsonObject.getString("playingRole").equals("Bowler")) {
                                    bowlerLength++;
                                    if(bowlerLength > 4)  {
                                        Toast toast = Toast.makeText(context, "You can only select 2-5 Bowlers", Toast.LENGTH_SHORT);
                                        View view = toast.getView();
                                        view.getBackground().setColorFilter(finalConvertView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                        TextView text = view.findViewById(android.R.id.message);
                                        text.setTextColor(finalConvertView.getResources().getColor(R.color.white));
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                    else {
                                        canBeAdded= true;
                                    }
                                }
                                if(canBeAdded) {
                                    if(team.length() > 10) {
                                        Toast toast = Toast.makeText(context, "You can only select 11 Players", Toast.LENGTH_SHORT);
                                        View view = toast.getView();
                                        view.getBackground().setColorFilter(finalConvertView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                        TextView text = view.findViewById(android.R.id.message);
                                        text.setTextColor(finalConvertView.getResources().getColor(R.color.white));
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                    else {
                                        playerData.add(rowItems.get(position).getTitle());
                                        int temp = budget - jsonObject.getInt("value");
                                        if(temp >= 0) {
                                            team.put(jsonObj);
                                            budget = budget - jsonObject.getInt("value");
                                            mOnBudgetChangeListener.onBudgetChanged(budget);
                                            rowItems.get(position).togglePlayerStatus();
                                            notifyDataSetChanged();
                                            forAdded = true;
                                        }
                                        else {
                                            Toast toast = Toast.makeText(context, "You don't have enough budget!", Toast.LENGTH_SHORT);
                                            View view = toast.getView();
                                            view.getBackground().setColorFilter(finalConvertView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                            TextView text = view.findViewById(android.R.id.message);
                                            text.setTextColor(finalConvertView.getResources().getColor(R.color.white));
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if(!forAdded) {
                                for (int j = 0; j < team.length(); j++) {
                                    JSONObject newJsonObject = team.getJSONObject(j);
                                    if(newJsonObject.getString("name").equals(rowItems.get(position).getTitle())) {
                                        team.remove(j);
                                        playerData.remove(rowItems.get(position).getTitle());
                                        budget += jsonObject.getInt("value");
                                        mOnBudgetChangeListener.onBudgetChanged(budget);
                                        rowItems.get(position).togglePlayerStatus();
                                        notifyDataSetChanged();
                                        if(newJsonObject.getString("playingRole").equals("Wicket Keeper")) {
                                            wktKeeperLength = 0;
                                        }

                                        if(newJsonObject.getString("playingRole").equals("Bowler")) {
                                            if(bowlerLength >= 5) {
                                                bowlerLength = 4;
                                            }
                                            else {
                                                bowlerLength--;
                                            }
                                        }

                                        if(newJsonObject.getString("playingRole").equals("Batsman")) {
                                            if(batsmanLength >= 4) {
                                                batsmanLength = 3;
                                            }
                                            else {
                                                batsmanLength--;
                                            }
                                        }

                                        if(newJsonObject.getString("playingRole").equals("All Rounder")) {
                                            if(allRounderLength >= 4) {
                                                allRounderLength = 3;
                                            }
                                            else {
                                                allRounderLength--;
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    SM.sendData(team.toString());
                }
                catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        });
        holder.addBtn.setTag(position);
        ImageView btn = (ImageView) finalConvertView.findViewById(R.id.addBtn);
        if(rowItems.get(position).isPlayerAdded()) {
            btn.setImageResource(R.drawable.remove);
        }
        else {
            btn.setImageResource(R.drawable.add);
        }
        return convertView;
    }

    public interface OnBudgetChangeListener{
        public void onBudgetChanged(int budget);
    }
    OnBudgetChangeListener mOnBudgetChangeListener;
    public void setOnBudgetChangeListener(OnBudgetChangeListener onBudgetChangeListener){
        mOnBudgetChangeListener = onBudgetChangeListener;
    }
}
