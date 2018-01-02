package net.multiform_music.ifeelrun.bus;

import de.greenrobot.event.EventBus;

/**
 * Created by Michel on 30/04/2017.
 *
 * Bus GPS publiant la mise à jour des coordonnées par GPSService
 *
 */

public class GpsBus extends EventBus {

    static GpsBus instance;

    public static GpsBus getInstance() {
        if (instance == null) {
            instance = new GpsBus();
        }

        return instance;
    }
}
