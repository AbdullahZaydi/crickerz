package com.zayditech.cricerzfantasy;

public class LeaderboardList {
    private int points;
    private String email;
    public LeaderboardList(String _email, int _points) {
        this.email = _email;
        this.points = _points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
