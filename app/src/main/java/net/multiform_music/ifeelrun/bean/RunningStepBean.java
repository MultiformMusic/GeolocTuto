package net.multiform_music.ifeelrun.bean;

/**
 * Created by michel.dio on 03/05/2017.
 *
 * Classe représentant un step du running
 *
 */

public class RunningStepBean {

    private double latitude;
    private double longitude;
    private float accuracy;
    private float distance;
    private float vitesse;
    private double altitude;
    private float timeDelay;
    private long horodatage;
    private int idRunning;

    private String elevationUnity;

    public String getElevationUnity() {
        return elevationUnity;
    }

    public void setElevationUnity(String elevationUnity) {
        this.elevationUnity = elevationUnity;
    }

    public int getIdRunning() {
        return idRunning;
    }

    public void setIdRunning(int idRunning) {
        this.idRunning = idRunning;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(float timeDelay) {
        this.timeDelay = timeDelay;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getVitesse() {
        return vitesse;
    }

    public void setVitesse(float vitesse) {
        this.vitesse = vitesse;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(long horodatage) {
        this.horodatage = horodatage;
    }

}
