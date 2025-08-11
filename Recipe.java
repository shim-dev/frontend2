package com.example.it_contest;

public class Recipe {
    public final String title;
    public final String tags;
    public final String desc;      // ✅ 요약/설명
    public final String meta;
    public final long createdAt;
    public final int views;
    public final int imageRes;

    public Recipe(String title, String tags, String desc, String meta, long createdAt, int views, int imageRes) {
        this.title = title;
        this.tags = tags;
        this.desc = desc;          // ✅
        this.meta = meta;
        this.createdAt = createdAt;
        this.views = views;
        this.imageRes = imageRes;
    }
}
