package com.tulskiy.keymaster.x11;

import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
@Library("libX11")
public class XAnyEvent extends StructObject {
    public XAnyEvent() {
        super();
    }

    public XAnyEvent(Pointer pointer) {
        super(pointer);
    }

    @Field(0)
    public int type() {
        return this.io.getIntField(this, 0);
    }

    @Field(0)
    public XAnyEvent type(int type) {
        this.io.setIntField(this, 0, type);
        return this;
    }

    /// # of last request processed by server
    @CLong
    @Field(1)
    public long serial() {
        return this.io.getCLongField(this, 1);
    }

    /// # of last request processed by server
    @CLong
    @Field(1)
    public XAnyEvent serial(long serial) {
        this.io.setCLongField(this, 1, serial);
        return this;
    }

    /// true if this came from a SendEvent request
    @Field(2)
    public int send_event() {
        return this.io.getIntField(this, 2);
    }

    /// true if this came from a SendEvent request
    @Field(2)
    public XAnyEvent send_event(int send_event) {
        this.io.setIntField(this, 2, send_event);
        return this;
    }

    /**
     * Display the event was read from<br>
     * C type : Display*
     */
    @Field(3)
    public Pointer<LibX11.XDisplay> display() {
        return this.io.getPointerField(this, 3);
    }

    /**
     * Display the event was read from<br>
     * C type : Display*
     */
    @Field(3)
    public XAnyEvent display(Pointer<LibX11.XDisplay> display) {
        this.io.setPointerField(this, 3, display);
        return this;
    }

    /**
     * window on which event was requested in event mask<br>
     * C type : Window
     */
    @CLong
    @Field(4)
    public long window() {
        return this.io.getCLongField(this, 4);
    }

    /**
     * window on which event was requested in event mask<br>
     * C type : Window
     */
    @CLong
    @Field(4)
    public XAnyEvent window(long window) {
        this.io.setCLongField(this, 4, window);
        return this;
    }
}