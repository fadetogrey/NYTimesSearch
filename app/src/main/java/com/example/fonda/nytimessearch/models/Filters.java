package com.example.fonda.nytimessearch.models;

import android.util.SparseBooleanArray;

import org.parceler.Parcel;

/**
 * Created by fonda on 11/16/16.
 */

@Parcel
public class Filters {

    // public static final String CB_ARTS = "\"Arts\"";
    // public static final String CB_FASHION = "\"Fashion & Styling\"";
    // public static final String CB_SPORTS = "\"Sports\"";
    public static final int CB_ARTS = 0;
    public static final int CB_FASHION = 1;
    public static final int CB_SPORTS = 2;

    public static final String NEWS_TOPICS[] = {"\"Arts\"","\"Fashion&Styling\"","\"Sports\""};
    String date;
    String sortOrder;
    SparseBooleanArray newsDeskValues;
    // ArrayList<Boolean> newsDeskValues;
    //JSONObject newsDeskValues;

    public void setDate(String date) {
        this.date = date;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setNewsDeskValues(int key, boolean value) {
        newsDeskValues.put(key, value);
    }

    public String getDate() {
        return date;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public boolean getNewsDeskValues(int key) {
        boolean v = false;
        v = (boolean) newsDeskValues.get(key);
        return v;
    }

    // For Parceler
    public Filters() {}

    public Filters(String date, String sortOrder, SparseBooleanArray newsDeskValues) {
            this.date = date;
            this.sortOrder = sortOrder;
            this.newsDeskValues = newsDeskValues;
    }
}
