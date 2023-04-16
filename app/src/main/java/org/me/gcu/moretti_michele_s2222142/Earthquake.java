//
// Name                 Michele Moretti
// Student ID           S2222142
// Programme of Study   Computing
//
package org.me.gcu.moretti_michele_s2222142;

import java.io.Serializable;
import java.util.Date;

public class Earthquake implements Serializable {

    private String title;
    private String description;
    private String link;
    private Date pubDate;
    private String latitude;
    private String longitude;
    private Double magnitude;
    private String location;


 public Earthquake(){

     title = "";
     description = "";
     link = "";
     pubDate = new Date();
     latitude = "";
     longitude = "";
     magnitude = 0.0;
     location = "";


 }

 public Earthquake(String atitle, String adescription, String alink, Date apubDate, String alatitude, String alongitude, Double amagnitude, String alocation){


     title = atitle;
     description = adescription;
     link = alink;
     pubDate = apubDate;
     latitude = alatitude;
     longitude = alongitude;
     magnitude = amagnitude;
     location = alocation;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public String toString() {

        return getTitle();
    }


}