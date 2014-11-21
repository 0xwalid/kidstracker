package com.example.kidstracker;

public class Region {
    public int id;
    public String name;
    public String from;
    public String to;
    public String days;
    public Double lat;
    public Double lng;
    public int radius;

    public Region() {
    }

    public Region(int id, String name, String from, String to, Double lat, Double lng, String days, int radius) {
        super();
        this.id = id;
        this.from = from;
        this.to = to;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.days = days;
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "Brand";
    }
}
