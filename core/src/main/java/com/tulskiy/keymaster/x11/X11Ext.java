/*
 * Copyright (c) 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tulskiy.keymaster.x11;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.unix.X11.Display;

/**
 * Author: Denis Tulskiy
 * Date: 7/14/11
 */
@SuppressWarnings({"UnusedDeclaration"})
public interface X11Ext extends Library {
    public static X11Ext Lib = (X11Ext) Native.loadLibrary("X11", X11Ext.class);

    public int XkbSetDetectableAutoRepeat(Display display, boolean detectable, Memory supported_rtrn);

}
