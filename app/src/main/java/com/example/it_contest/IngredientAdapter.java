package com.example.it_contest;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.it_contest.model.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private final List<Ingredient> ingredientList;

    public IngredientAdapter(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);

        // 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(ingredient.getImage())
                .into(holder.imgIngredient);

        // 재료 이름
        holder.tvName.setText(ingredient.getName());

        // 태그 최대 3개 표시
        if (ingredient.getTags() != null) {
            if (ingredient.getTags().size() > 0) {
                holder.tag1.setText("#" + ingredient.getTags().get(0));
                holder.tag1.setVisibility(View.VISIBLE);
            } else holder.tag1.setVisibility(View.GONE);

            if (ingredient.getTags().size() > 1) {
                holder.tag2.setText("#" + ingredient.getTags().get(1));
                holder.tag2.setVisibility(View.VISIBLE);
            } else holder.tag2.setVisibility(View.GONE);

            if (ingredient.getTags().size() > 2) {
                holder.tag3.setText("#" + ingredient.getTags().get(2));
                holder.tag3.setVisibility(View.VISIBLE);
            } else holder.tag3.setVisibility(View.GONE);
        } else {
            holder.tag1.setVisibility(View.GONE);
            holder.tag2.setVisibility(View.GONE);
            holder.tag3.setVisibility(View.GONE);
        }

        // 이동 버튼 → 구매 링크 열기
        holder.btnMove.setOnClickListener(v -> {
            if (ingredient.getUrl() != null && !ingredient.getUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ingredient.getUrl()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIngredient;
        TextView tvName, tag1, tag2, tag3;
        Button btnMove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIngredient = itemView.findViewById(R.id.img_ingredient);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            tag3 = itemView.findViewById(R.id.tag3);
            btnMove = itemView.findViewById(R.id.btn_move);
        }
    }
}
