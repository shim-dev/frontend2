package com.example.it_contest.model;

import java.io.Serializable;
import java.util.List;

public class Ingredient implements Serializable {  // ✅ 추가
    private String name;
    private String image;
    private String url;
    private List<String> tags;

    // Getter & Setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
