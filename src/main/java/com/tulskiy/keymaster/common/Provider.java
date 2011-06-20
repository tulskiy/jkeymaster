package com.tulskiy.keymaster.common;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public abstract class Provider {
    public static final Logger logger = Logger.getLogger(Provider.class.getName());

    private ExecutorService eventQueue = Executors.newSingleThreadExecutor();

    public abstract void init();

    public abstract void stop();

    public abstract void reset();

    public abstract void register(KeyStroke keyCode, ActionListener listener);

    public abstract void register(MediaKey mediaKey, ActionListener listener);

    protected void fireEvent(HotKey hotKey) {
        eventQueue.execute(new HotKeyEvent(hotKey));
    }

    private class HotKeyEvent implements Runnable {
        private HotKey hotKey;

        private HotKeyEvent(HotKey hotKey) {
            this.hotKey = hotKey;
        }

        public void run() {
            hotKey.listener.actionPerformed(new ActionEvent(hotKey, 0, ""));
        }
    }

}
