package com.example.it_contest.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatMealResponse {
    @SerializedName("success") private boolean success;
    @SerializedName("foods")   private List<String> foods;
    @SerializedName("mind")    private Mind mind;

    public boolean isSuccess() { return success; }
    public List<String> getFoods() { return foods; }
    public Mind getMind() { return mind; }

    public static class Mind {
        @SerializedName("items")       private List<MindItem> items;
        @SerializedName("meal_score")  private double mealScore;
        @SerializedName("notes")       private String notes;
        @SerializedName("recommendation") private String recommendation;

        public List<MindItem> getItems() { return items; }
        public double getMealScore() { return mealScore; }
        public String getNotes() { return notes; }
        public String getRecommendation() { return recommendation; }
    }

    public static class MindItem {
        @SerializedName("food")  private String food;
        @SerializedName("score") private int score;
        @SerializedName("note")  private String note;

        public String getFood() { return food; }
        public int getScore() { return score; }
        public String getNote() { return note; }
    }
}
