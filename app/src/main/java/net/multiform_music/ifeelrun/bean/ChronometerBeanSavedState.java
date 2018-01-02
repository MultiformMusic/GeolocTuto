package net.multiform_music.ifeelrun.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michel on 09/05/2017.
 *
 * Bean de sauvegarde de l'état du {@link net.multiform_music.ifeelrun.service.ChronometerService}
 *
 */

public class ChronometerBeanSavedState implements Parcelable {

    private int seconds;
    private int minutes;
    private int hours;

    private long startTimeMillis;
    private long pauseTime;

    public ChronometerBeanSavedState() {

    }

    public ChronometerBeanSavedState(int hours, int minutes, int seconds, long startTimeMillis, long pauseTime) {

        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;

        this.startTimeMillis = startTimeMillis;
        this.pauseTime = pauseTime;
    }

    public static final Creator<ChronometerBeanSavedState> CREATOR = new Creator<ChronometerBeanSavedState>() {
        @Override
        public ChronometerBeanSavedState createFromParcel(Parcel in) {
            return new ChronometerBeanSavedState(in);
        }

        @Override
        public ChronometerBeanSavedState[] newArray(int size) {
            return new ChronometerBeanSavedState[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(hours);
        parcel.writeInt(minutes);
        parcel.writeInt(seconds);

        parcel.writeLong(startTimeMillis);
        parcel.writeLong(pauseTime);
    }

    private ChronometerBeanSavedState(Parcel in) {

        hours = in.readInt();
        minutes = in.readInt();
        seconds = in.readInt();

        startTimeMillis = in.readLong();
        pauseTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public int getHours() {
        return hours;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }


    public long getPauseTime() {
        return pauseTime;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public void setPauseTime(long pauseTime) {
        this.pauseTime = pauseTime;
    }
}
