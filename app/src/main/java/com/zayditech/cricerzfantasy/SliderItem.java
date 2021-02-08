package com.zayditech.cricerzfantasy;

public class SliderItem {
    private String team1;
    private String team2;
    private String date;
    private String day;
    private String desc;

    public SliderItem(String team1, String team2, String date, String day, String desc) {
        this.team1 = team1;
        this.team2 = team2;
        this.date = date;
        this.day = day;
        this.desc = desc;
    }
    public String getTeam1() {
        return team1;
    }
    public String getTeam2() {
        return team2;
    }
    public void setTeam1(String team1) {
        this.team1 = team1;
    }
    public void setTeam2(String team2) {
        this.team2 = team2;
    }
    public void setDay(String Day) {
        this.day = Day;
    }
    public void setDate(String Date) {
        this.date = Date;
    }
    public String getDesc() {
        return desc;
    }
    public String getDay() {
        return day;
    }
    public String getDate() {
        return date;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
