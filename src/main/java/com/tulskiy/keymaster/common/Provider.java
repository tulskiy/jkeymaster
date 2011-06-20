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

    protected void fireEvent(Object source, ActionListener listener) {
        eventQueue.execute(new HotKeyEvent(source, listener));
    }

    private class HotKeyEvent implements Runnable {
        private Object source;
        private ActionListener listener;

        private HotKeyEvent(Object source, ActionListener listener) {
            this.source = source;
            this.listener = listener;
        }

        public void run() {
            listener.actionPerformed(new ActionEvent(source, 0, ""));
        }
    }

    protected class HotKey {
        public KeyStroke keyStroke;
        public ActionListener listener;

        public HotKey(KeyStroke keyStroke, ActionListener listener) {
            this.keyStroke = keyStroke;
            this.listener = listener;
        }
    }
}
