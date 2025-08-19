package com.example.it_contest.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** 서버의 diet_records 문서 1개를 매핑 */
public class DietRecord {

    @SerializedName("_id")
    private String id;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("meal_type")
    private String mealType;

    @SerializedName("message")
    private String message;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("foods")
    private List<String> foods;

    @SerializedName("mind")
    private Mind mind;

    /** mind 객체 (필요 필드만 매핑) */
    public static class Mind {
        @SerializedName("meal_score")
        private double mealScore;

        @SerializedName("notes")
        private String notes;

        public double getMealScore() { return mealScore; }
        public String getNotes()   { return notes; }
    }

    // ------- Getters -------
    public String getId()        { return id; }
    public String getNickname()  { return nickname; }
    public String getMealType()  { return mealType; }
    public String getMessage()   { return message; }
    public String getTimestamp() { return timestamp; }
    public List<String> getFoods() { return foods; }
    public Mind getMind()        { return mind; }

    // ------- 편의 Getter (MainActivity에서 사용하기 좋게) -------
    /** mind가 null이어도 NPE 안 나도록 처리 */
    public double getMealScore() {
        return (mind != null) ? mind.getMealScore() : 0;
    }

    public String getMindNotes() {
        return (mind != null) ? mind.getNotes() : null;
    }
}
