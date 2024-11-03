package com.example.vickey;
import java.util.List;

public class ContentItem {
    private String title;
    private List<String> imageUrlList;
//    private List<String> itemTextList;

    public ContentItem(String title, List<String> imageUrlList) { //, List<String> itemTextList
        this.title = title;
        this.imageUrlList = imageUrlList;
//        this.itemTextList = itemTextList;
    }

    public String getTitle() {
        return title;
    }

//    public List<String> getItemTextList() {
//        return itemTextList;
//    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }
}
