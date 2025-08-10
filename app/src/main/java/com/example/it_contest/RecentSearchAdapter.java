package com.example.it_contest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {

    private List<String> searchList;
    private OnDeleteClickListener deleteClickListener;

    // 삭제 클릭 리스너
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public RecentSearchAdapter(List<String> searchList, OnDeleteClickListener listener) {
        this.searchList = searchList;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvKeyword.setText(searchList.get(position));

        // 삭제 버튼 클릭 시
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvKeyword;
        ImageView btnDelete, ivClock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKeyword = itemView.findViewById(R.id.tv_keyword);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            ivClock = itemView.findViewById(R.id.iv_clock); // 시계 아이콘
        }
    }
}
