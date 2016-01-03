package com.tulskiy.keymaster.dbus;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;

/**
 * User: tulskiy
 * Date: 1/4/16
 */
public class GnomeMediaProviderTest {
    public static void main(String[] args) throws InterruptedException {
        final GnomeMediaProvider provider = new GnomeMediaProvider("test");

        provider.register(MediaKey.MEDIA_STOP, new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                provider.stop();
                System.exit(0);
            }
        });

        Thread.sleep(10000);
        provider.stop();
    }
}
