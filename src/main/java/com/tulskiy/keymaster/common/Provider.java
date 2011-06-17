package com.tulskiy.keymaster.common;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public interface Provider {
    public static final Logger logger = Logger.getLogger(Provider.class.getName());

    public void init();

    public void stop();

    public void reset();

    public void register(KeyStroke keyCode, ActionListener listener);

    public void register(MediaKey mediaKey, ActionListener listener);
}
