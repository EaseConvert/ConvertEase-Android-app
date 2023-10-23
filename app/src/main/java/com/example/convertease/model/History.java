package com.example.convertease.model;

public class History {
    private int id;
   private String name;
    private String path;
    private String date;

    public History() {
        // No-argument constructor
    }

    public History(String name, String path, String date) {
        this.name = name;
        this.path = path;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
