package com.tan.mymemo;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by beau- on 14/01/2017.
 */

@Parcel
public class Memo {

    @SerializedName("filename")
    private String fileName;
    @SerializedName("color")
    private int color;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("last_modified")
    private String lastModified;

    @ParcelConstructor
    public Memo(String fileName, int color, String title, String description, String lastModified) {
        this.fileName = fileName;
        this.color = color;
        this.title = title;
        this.description = description;
        this.lastModified = lastModified;
    }

    public Memo() {}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
