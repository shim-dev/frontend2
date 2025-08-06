package com.example.it_contest;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.it_contest.model.Recipe;
import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipeList;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvTitle.setText(recipe.getName());
        holder.tvDescription.setText(recipe.getDesc());
        holder.tvTime.setText(recipe.getTime() + " 분");
        holder.tvLevel.setText(recipe.getLevel());
        holder.tvServing.setText(recipe.getServing() + "인분");

        int radiusInPx = (int) (50 * holder.itemView.getResources().getDisplayMetrics().density);

        Glide.with(holder.itemView.getContext())
                .load(recipe.getImageUrl())
                .transform(new com.bumptech.glide.load.resource.bitmap.RoundedCorners(radiusInPx))
                .into(holder.imgRecipe);

        // ✅ 클릭 이벤트 추가 + 조회수 증가 API 호출
        holder.itemView.setOnClickListener(v -> {
            Log.d("RecipeAdapter", "Clicked: " + recipe.getName());

            // 조회수 증가 API 호출
            ApiService apiService = RetrofitClient.getApiService();
            apiService.increaseRecipeView(recipe.get_id()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("RecipeAdapter", "조회수 +1 성공");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("RecipeAdapter", "조회수 증가 실패", t);
                }
            });

            // 상세 페이지 이동
            Intent intent = new Intent(holder.itemView.getContext(), recipe_screen_activity.class);
            intent.putExtra("recipe", recipe); // Serializable이므로 가능
            holder.itemView.getContext().startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe;
        TextView tvTitle, tvDescription, tvTime, tvLevel, tvServing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.img_recipe);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvServing = itemView.findViewById(R.id.tv_serving);
        }
    }


}
