package com.example.it_contest.network;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class FoodSearchResult implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("score")
    private int score;

    @SerializedName("note")
    private String note;

    @SerializedName("emoji")
    private String emoji;

    // ✅ 필드용 생성자 (BreakfastBottomSheet에서 사용)
    public FoodSearchResult(String name, int score, String note, String emoji) {
        this.name = name;
        this.score = score;
        this.note = note;
        this.emoji = emoji;
    }

    // ✅ 기본 생성자 (Gson 역직렬화용)
    public FoodSearchResult() { }

    // -------- Getter --------
    public String getName() { return name; }
    public int getScore() { return score; }
    public String getNote() { return note; }
    public String getEmoji() { return emoji; }

    // -------- Parcelable --------
    protected FoodSearchResult(Parcel in) {
        name = in.readString();
        score = in.readInt();
        note = in.readString();
        emoji = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(score);
        dest.writeString(note);
        dest.writeString(emoji);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<FoodSearchResult> CREATOR = new Creator<FoodSearchResult>() {
        @Override
        public FoodSearchResult createFromParcel(Parcel in) {
            return new FoodSearchResult(in);
        }

        @Override
        public FoodSearchResult[] newArray(int size) {
            return new FoodSearchResult[size];
        }
    };
}

