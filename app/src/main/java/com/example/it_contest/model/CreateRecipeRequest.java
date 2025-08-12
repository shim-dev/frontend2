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

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("serving")
    private int serving;

    @SerializedName("steps")
    private List<String> steps;

    @SerializedName("ingredients")
    private List<String> ingredients;

    public CreateRecipeRequest(String name, String desc, List<String> keywords, String time, String level, String imageUrl, int serving, List<String> steps, List<String> ingredients) {
        this.name = name;
        this.desc = desc;
        this.keywords = keywords;
        this.time = time;
        this.level = level;
        this.imageUrl = imageUrl;
        this.serving = serving;
        this.steps = steps;
        this.ingredients = ingredients;// ✅ 닉네임 초기화
    }
}