package com.zayditech.cricerzfantasy;

public class MatchList {
    private String firstTeam;
    private String secondTeam;
    private String date;
    private String time;
    private int unique_id;
    public MatchList(String _firstTeam, String _secondTeam, String _date, String _time, int _unique_id) {
        this.firstTeam = _firstTeam;
        this.secondTeam = _secondTeam;
        this.date = _date;
        this.time = _time;
        this.unique_id = _unique_id;
    }

    public int getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(int unique_id) {
        this.unique_id = unique_id;
    }

    public String getFirstTeam() {
        return firstTeam;
    }

    public void setFirstTeam(String firstTeam) {
        this.firstTeam = firstTeam;
    }

    public String getSecondTeam() {
        return secondTeam;
    }

    public void setSecondTeam(String secondTeam) {
        this.secondTeam = secondTeam;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
