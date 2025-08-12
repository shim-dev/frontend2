package com.example.it_contest.model;
import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable { // ✅ Serializable 추가

    private String _id;
    private String name;
    private List<String> keywords;
    private String desc;
    private Object time;
    //private String time;
    private String level;
    private int serving;
    private String imageUrl;  // ✅ String 타입 유지
    private String book;
    private List<String> steps;
    private List<Ingredient> ingredients;
    private Object score;
    private int views;

    // Getter & Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getTime() {
        if (time != null) {
            try {
                double value = Double.parseDouble(String.valueOf(time));
                if (value == (int) value) { // 소수점 없는 경우
                    return String.valueOf((int) value);
                } else {
                    return String.valueOf(value);
                }
            } catch (NumberFormatException e) {
                return String.valueOf(time);
            }
        }
        return "";
    }

    public void setTime(Object time) { this.time = time; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public int getServing() { return serving; }
    public void setServing(int serving) { this.serving = serving; }

    // ✅ 단일 문자열로 수정
    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBook() { return book; }
    public void setBook(String book) { this.book = book; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public int getScore() {
        if (score instanceof Number) {
            return ((Number) score).intValue(); // float든 int든 무조건 int로 변환
        }
        try {
            return Integer.parseInt(String.valueOf(score)); // 문자열일 경우 처리
        } catch (NumberFormatException e) {
            return 0; // 변환 실패 시 기본값
        }
    }
    public void setScore(Object score) {
        this.score = score;
    }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

}