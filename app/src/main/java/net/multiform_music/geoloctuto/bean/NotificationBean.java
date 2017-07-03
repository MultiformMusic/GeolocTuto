package net.multiform_music.geoloctuto.bean;

/**
 * Created by Michel on 29/05/2017.
 *
 */

public class NotificationBean {

    public String vitesse;
    public String distance;
    public String heure;
    public String minute;
    public String seconde;
    public String altitude;
    public String denivele;

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSeconde() {
        return seconde;
    }

    public void setSeconde(String seconde) {
        this.seconde = seconde;
    }

    public String getVitesse() {
        return vitesse;
    }

    public void setVitesse(String vitesse) {
        this.vitesse = vitesse;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getDenivele() {
        return denivele;
    }

    public void setDenivele(String denivele) {
        this.denivele = denivele;
    }
}
