package com.tulskiy.keymaster.common;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public interface Provider {
    public void init();

    public boolean stop();

    public void registerMediaKeyListener(ActionListener listener);

    public boolean register(String keyCode, ActionListener listener);

    public boolean unregisterAll();
}
