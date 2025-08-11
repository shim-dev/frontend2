package com.example.it_contest;

public class PointHistoryItem {
    private String title;
    private String point;
    private String dateDetail;
    private String type;

    public PointHistoryItem(String title, String point, String dateDetail, String type) {
        this.title = title;
        this.point = point;
        this.dateDetail = dateDetail;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getPoint() { return point; }
    public String getDateDetail() { return dateDetail; }
    public String getType() { return type; }
}
