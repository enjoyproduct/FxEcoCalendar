package com.fxecocal.free.model;

/**
 * Created by dell17 on menu6/24/2016.
 */
public class SelectionModel {
    boolean isSelected;
    String title, type;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
