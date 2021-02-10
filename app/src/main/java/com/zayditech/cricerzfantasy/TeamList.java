package com.zayditech.cricerzfantasy;

public class TeamList {
    private String imageId;
    private String title;
    private String desc;
    private int Value;
    private boolean playerSelected;
    public TeamList(String imageId, String title, String desc, int value, boolean isSelected) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.Value = value;
        this.playerSelected = isSelected;
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
    public void setValue(int value) {
        this.Value = value;
    }
    public int getValue() {
        return Value;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    private boolean playerStatus = false;
    public boolean isPlayerAdded() {return playerStatus;}
    public void togglePlayerStatus(){
        playerStatus = !playerStatus;
    }
    public boolean isPlayerSelected() {return playerSelected;}
    public void togglePlayerSelected(boolean PlayerSelected){
        playerSelected = PlayerSelected;
    }
}
