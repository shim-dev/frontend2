package com.example.it_contest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.VH> {

    public interface OnItemClick {
        void onClick(Recipe recipe);
    }

    private final List<Recipe> list;
    private final OnItemClick onItemClick;

    public RecipeCardAdapter(List<Recipe> list) {
        this(list, null);
    }

    public RecipeCardAdapter(List<Recipe> list, OnItemClick onItemClick) {
        this.list = list;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Recipe r = list.get(pos);
        h.tvTitle.setText(r.title);
        h.tvTags.setText(r.tags);
        h.tvDesc.setText(r.desc);     // ✅ 설명 바인딩
        h.tvMeta.setText(r.meta);

        h.ivThumb.setImageResource(
                r.imageRes != 0 ? r.imageRes : android.R.drawable.ic_menu_gallery
        );

        if (onItemClick != null) {
            h.itemView.setOnClickListener(v -> onItemClick.onClick(r));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle, tvTags, tvDesc, tvMeta;  // ✅ tvDesc 포함

        VH(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTags  = itemView.findViewById(R.id.tvTags);
            tvDesc  = itemView.findViewById(R.id.tvDesc);   // ✅
            tvMeta  = itemView.findViewById(R.id.tvMeta);
        }
    }
}
