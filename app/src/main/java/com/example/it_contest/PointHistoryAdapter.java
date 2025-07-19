package com.example.it_contest;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;
public class PointHistoryAdapter extends RecyclerView.Adapter<PointHistoryAdapter.ViewHolder> {

    private List<PointHistoryItem> itemList;

    public PointHistoryAdapter(List<PointHistoryItem> itemList) {
        this.itemList = new ArrayList<>(itemList); // 복사해서 저장
    }

    public void updateData(List<PointHistoryItem> newData) {
        itemList = new ArrayList<>(newData); // 복사본으로 교체
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, pointTextView, dateDetailTextView, typeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            pointTextView = itemView.findViewById(R.id.pointTextView);
            dateDetailTextView = itemView.findViewById(R.id.dateDetailTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.point_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PointHistoryItem item = itemList.get(position);

        holder.titleTextView.setText(item.getTitle());
        holder.pointTextView.setText(item.getPoint());
        holder.dateDetailTextView.setText(item.getDateDetail());
        holder.typeTextView.setText(item.getType());

        if (item.getPoint().startsWith("+")) {
            holder.pointTextView.setTextColor(Color.parseColor("#FF5A00")); // 적립
        } else {
            holder.pointTextView.setTextColor(Color.parseColor("#3399FF")); // 사용
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
