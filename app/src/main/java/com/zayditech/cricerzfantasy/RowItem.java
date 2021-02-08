package com.zayditech.cricerzfantasy;

public class RowItem {
    private String imageId;
    private String title;
    private String desc;

    public RowItem(String imageId, String title, String desc) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
    }
    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }

    private boolean playerStatus = false;
    public boolean isPlayerAdded() {return playerStatus;}

    public void togglePlayerStatus(){
        playerStatus = !playerStatus;
    }
}
