package net.multiform_music.geoloctuto.bean;

/**
 * Created by michel.dio on 03/05/2017.
 *
 * Classe représentant un running
 *
 */

public class RunningBean {

    private int id;
    private String placeName;
    private String city;
    private String activity;
    private String comment;
    private String date;
    private double velocity;
    private double distance;
    private double denivele;
    private int nbrPoints;
    private String temps;
    private int calory;

    // données d'unités
    private String unitVelocity;
    private String unitDistance;
    private String unitElevation;

    public String getUnitVelocity() {
        return unitVelocity;
    }

    public void setUnitVelocity(String unitVelocity) {
        this.unitVelocity = unitVelocity;
    }

    public String getUnitDistance() {
        return unitDistance;
    }

    public void setUnitDistance(String unitDistance) {
        this.unitDistance = unitDistance;
    }

    public String getUnitElevation() {
        return unitElevation;
    }

    public void setUnitElevation(String unitElevation) {
        this.unitElevation = unitElevation;
    }

    public int getNbrPoints() {
        return nbrPoints;
    }

    public void setNbrPoints(int nbrPoints) {
        this.nbrPoints = nbrPoints;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDenivele() {
        return denivele;
    }

    public void setDenivele(double denivele) {
        this.denivele = denivele;
    }


    public String getTemps() {
        return temps;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }


    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }


    public int getCalory() {
        return calory;
    }

    public void setCalory(int calory) {
        this.calory = calory;
    }

}
