package com.scar.lms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class RatingDTO {

    private String username;
    private String comment;
    private double points;
    private Date time;

    // Constructor mặc định (không tham số)
    public RatingDTO() {
    }

    // Constructor với các tham số
    public RatingDTO(String username, String comment, double points, Date time) {
        this.username = username;
        this.comment = comment;
        this.points = points;
        this.time = time;
    }

    // Getter và Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RatingDTO{" +
                "username='" + username + '\'' +
                ", comment='" + comment + '\'' +
                ", points=" + points +
                ", time=" + time +
                '}';
    }
}
