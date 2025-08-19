package com.example.it_contest;

// SearchResultAdapter.java (새로운 자바 클래스 파일)
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;




// ... 다른 import ...
import com.example.it_contest.network.FoodSearchResult; // <<< 이 줄이 있는지 확인!
import java.util.List;
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final List<FoodSearchResult> foodList;
    private final OnItemAddListener listener;

    public interface OnItemAddListener {
        void onItemAdd(FoodSearchResult food);
    }

    public SearchResultAdapter(List<FoodSearchResult> foodList, OnItemAddListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodSearchResult food = foodList.get(position);
        holder.foodEmoji.setText(food.getEmoji());
        holder.foodName.setText(food.getName());
        holder.foodScore.setText("⭐ " + food.getScore() + " 점");
        holder.addButton.setOnClickListener(v -> listener.onItemAdd(food));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodEmoji, foodName, foodScore, addButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodEmoji = itemView.findViewById(R.id.foodEmoji);
            foodName = itemView.findViewById(R.id.foodName);
            foodScore = itemView.findViewById(R.id.foodScore);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }
}