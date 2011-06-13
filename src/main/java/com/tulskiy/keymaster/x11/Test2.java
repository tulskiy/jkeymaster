package com.tulskiy.keymaster.x11;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class Test2 {
    public static void main(String[] args) {
        final X11Provider provider = new X11Provider();
        provider.init();

        provider.register(KeyStroke.getKeyStroke("control alt B"), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getSource());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        provider.reset();
                        provider.stop();
                    }
                });
            }
        });
        provider.register(KeyStroke.getKeyStroke("control shift F1"), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getSource());
            }
        });

        provider.register(KeyStroke.getKeyStroke("control alt 0"), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getSource());
            }
        });

        provider.register(KeyStroke.getKeyStroke("control alt NUMPAD0"), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getSource());
            }
        });
    }
}
