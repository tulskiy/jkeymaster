package com.tulskiy.keymaster;

import com.tulskiy.keymaster.common.Provider;
import com.tulskiy.keymaster.windows.WindowsProvider;
import com.tulskiy.keymaster.x11.X11Provider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class ProviderTest {
    public static void main(String[] args) {
        final Provider provider = new WindowsProvider();
        provider.init();

        provider.register(KeyStroke.getKeyStroke("control alt D"), new ActionListener() {
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

        provider.register(KeyStroke.getKeyStroke("control alt HOME"), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getSource());
            }
        });

        provider.register(KeyStroke.getKeyStroke("control alt PAGE_UP"), new ActionListener() {
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
