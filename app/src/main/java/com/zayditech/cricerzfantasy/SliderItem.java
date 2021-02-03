package com.zayditech.cricerzfantasy;

public class SliderItem {
    private String imageURL;
    private String desc;

    public SliderItem(String imageURL, String desc) {
        this.imageURL = imageURL;
        this.desc = desc;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageId(String imageId) {
        this.imageURL = imageURL;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
