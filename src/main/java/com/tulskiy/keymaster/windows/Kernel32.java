package com.tulskiy.keymaster.windows;

import org.bridj.BridJ;
import org.bridj.ann.Library;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
@Library("kernel32")
public class Kernel32 {
    static {
        BridJ.register();
    }
    public static native int GetLastError();
    public static native long GetCurrentThreadId();
}
