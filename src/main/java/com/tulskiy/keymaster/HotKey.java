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

package com.tulskiy.keymaster;

import javax.swing.*;

/**
 * Internal representation of a hotkey. Either keyStroke or mediaKey should be set.
 * <p/>
 * Author: Denis Tulskiy
 * Date: 6/20/11
 */
public class HotKey {
    public final KeyStroke keyStroke;
    public final MediaKey mediaKey;

    public HotKey(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
        this.mediaKey = null;
    }

    public HotKey(MediaKey mediaKey) {
        this.keyStroke = null;
        this.mediaKey = mediaKey;
    }

    public boolean isMedia() {
        return mediaKey != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HotKey hotKey = (HotKey) o;

        if (keyStroke != null ? !keyStroke.equals(hotKey.keyStroke) : hotKey.keyStroke != null) return false;
        if (mediaKey != hotKey.mediaKey) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = keyStroke != null ? keyStroke.hashCode() : 0;
        result = 31 * result + (mediaKey != null ? mediaKey.hashCode() : 0);
        return result;
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
