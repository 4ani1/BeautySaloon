package com.example.deepthort;

public class Categories {
    private int id;
    private String categories_name;

    public Categories(int id, String categories_name) {
        this.id = id;
        this.categories_name = categories_name;
    }

    public Categories(String categories_name) {
        this.categories_name = categories_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategories_name() {
        return categories_name;
    }

    public void setCategories_name(String categories_name) {
        this.categories_name = categories_name;
    }
}
