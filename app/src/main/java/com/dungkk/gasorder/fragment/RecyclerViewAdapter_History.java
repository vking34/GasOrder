package com.dungkk.gasorder.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dungkk.gasorder.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Admin on 2/6/2018.
 */

public class RecyclerViewAdapter_History extends RecyclerView.Adapter<RecyclerViewAdapter_History.RecyclerViewHolder>  {

    private JSONArray history;

    public RecyclerViewAdapter_History(JSONArray history) {
        this.history = history;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_list_history, parent, false);
        return new RecyclerViewAdapter_History.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        try {
            holder.date.setText(history.getJSONObject(position).getString("date"));
            holder.address.setText(history.getJSONObject(position).getString("address"));
            holder.phoneNumber.setText(history.getJSONObject(position).getString("phoneNumber"));
            holder.brand.setText(history.getJSONObject(position).getString("brand"));
            holder.size.setText(history.getJSONObject(position).getString("size"));
            holder.price.setText(history.getJSONObject(position).getString("price"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return history.length();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView date, address, phoneNumber, brand, size, price;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.htr_date);
            address = itemView.findViewById(R.id.htr_address);
            phoneNumber = itemView.findViewById(R.id.htr_phonenumber);
            brand = itemView.findViewById(R.id.htr_brand);
            size = itemView.findViewById(R.id.htr_size);
            price = itemView.findViewById(R.id.htr_price);
        }
    }

}
