package net.multiform_music.geoloctuto.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michel on 09/05/2017.
 *
 * Bean transmis par {@link net.multiform_music.geoloctuto.service.ChronometerService} aux receivers
 *
 */

public class ChronometerBean implements Parcelable {

    private int seconds;
    private int minutes;
    private int hours;

    private int elapsedTime;
    private int chronoTime;
    private int derive;

    public ChronometerBean(int hours, int minutes, int seconds, int elapsedTime, int chronoTime, int derive) {

        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;

        this.elapsedTime = elapsedTime;
        this.chronoTime = chronoTime;
        this.derive = derive;
    }

    public static final Creator<ChronometerBean> CREATOR = new Creator<ChronometerBean>() {
        @Override
        public ChronometerBean createFromParcel(Parcel in) {
            return new ChronometerBean(in);
        }

        @Override
        public ChronometerBean[] newArray(int size) {
            return new ChronometerBean[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(hours);
        parcel.writeInt(minutes);
        parcel.writeInt(seconds);

        parcel.writeInt(elapsedTime);
        parcel.writeInt(chronoTime);
        parcel.writeInt(derive);
    }

    private ChronometerBean(Parcel in) {

        hours = in.readInt();
        minutes = in.readInt();
        seconds = in.readInt();

        elapsedTime = in.readInt();
        chronoTime = in.readInt();
        derive = in.readInt();
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


    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getChronoTime() {
        return chronoTime;
    }

    public int getDerive() {
        return derive;
    }

}
