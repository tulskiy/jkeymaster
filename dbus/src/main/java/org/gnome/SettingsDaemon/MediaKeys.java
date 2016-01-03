package org.gnome.SettingsDaemon;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;

public interface MediaKeys extends DBusInterface {
    void GrabMediaPlayerKeys(String s, UInt32 time);

    void ReleaseMediaPlayerKeys(String s);

    class MediaPlayerKeyPressed extends DBusSignal {
        public final String key;

        public MediaPlayerKeyPressed(String path, String app, String key)
                throws DBusException {
            super(path, app, key);
            this.key = key;
        }
    }
}

