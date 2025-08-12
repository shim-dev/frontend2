// src/main/java/com/example/it_contest/model/CreateRecipeRequest.java

package com.example.it_contest.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class CreateRecipeRequest implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("desc")
    private String desc;

    @SerializedName("keywords")
    private List<String> keywords;

    @SerializedName("time")
    private String time;

    @SerializedName("level")
    private String level;

    @SerializedName("imageUrls")
    private List<String> imageUrls;

    // ✅ 추가된 필드
    @SerializedName("serving")
    private int serving;

    @SerializedName("steps")
    private List<String> steps;

    @SerializedName("ingredients")
    private List<String> ingredients;

    // ✅ 모든 필드를 포함하는 새로운 생성자
    public CreateRecipeRequest(String name, String desc, List<String> keywords, String time, String level, List<String> imageUrls, int serving, List<String> steps, List<String> ingredients) {
        this.name = name;
        this.desc = desc;
        this.keywords = keywords;
        this.time = time;
        this.level = level;
        this.imageUrls = imageUrls;
        this.serving = serving;
        this.steps = steps;
        this.ingredients = ingredients;
    }
}