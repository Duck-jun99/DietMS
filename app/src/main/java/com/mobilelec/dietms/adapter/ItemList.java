package com.mobilelec.dietms.adapter;

public class ItemList {
    public String title;
    public String text;
    public String created_date;
    public String published_date;
    public String image;

    public String meal;

    public ItemList(String title, String text, String createdDate, String publishedDate, String image, String meal) {
        this.title = title;
        this.text = text;
        this.created_date = createdDate;
        this.published_date = publishedDate;
        this.image = image;
        this.meal = meal;
    }
}
