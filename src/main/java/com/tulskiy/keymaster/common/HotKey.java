package com.tulskiy.keymaster.common;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
* Author: Denis Tulskiy
* Date: 6/20/11
*/
public class HotKey {
    public KeyStroke keyStroke;
    public MediaKey mediaKey;
    public ActionListener listener;

    public HotKey(KeyStroke keyStroke, ActionListener listener) {
        this.keyStroke = keyStroke;
        this.listener = listener;
    }

    public HotKey(MediaKey mediaKey, ActionListener listener) {
        this.mediaKey = mediaKey;
        this.listener = listener;
    }

    public boolean isMedia() {
        return mediaKey != null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HotKey");
        if (keyStroke != null)
            sb.append("{").append(keyStroke.toString().replaceAll("pressed ", ""));
        if (mediaKey != null)
            sb.append("{").append(mediaKey);
        sb.append('}');
        return sb.toString();
    }
}
