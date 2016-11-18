package com.example.fonda.nytimessearch.models;

import android.widget.CheckBox;

import org.parceler.Parcel;

/**
 * Created by fonda on 11/16/16.
 */

@Parcel
public class Filters {

    String date;
    String sortOrder;

    public void setDate(String date) {
        this.date = date;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    /* CheckBox[] newsDeskValues;

    public CheckBox[] getNewsDeskValues() {
        return newsDeskValues;
    }*/

    public String getDate() {
        return date;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    // For Parceler
    public Filters() {}

    public Filters(String date, String sortOrder, CheckBox[] newDeskValues) {
            this.date = date;
            this.sortOrder = sortOrder;
            //this.newsDeskValues = (ArrayList<String>) jsonObject.getJSONArray("desk_types");
    }

    // Getting Spinner value
    //String value = spinner.getSelectedItem().toString();
//http://guides.codepath.com/android/Working-with-Input-Views#spinners
}
