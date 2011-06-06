package com.tulskiy.keymaster.x11;

import org.bridj.CLong;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Array;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Union;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
@Union
@Library("libX11")
public class XEvent extends StructObject {
    public XEvent() {
        super();
    }

    public XEvent(Pointer pointer) {
        super(pointer);
    }

    /// must not be changed; first element
    @Field(0)
    public int type() {
        return this.io.getIntField(this, 0);
    }

    /// must not be changed; first element
    @Field(0)
    public XEvent type(int type) {
        this.io.setIntField(this, 0, type);
        return this;
    }

    /// C type : XAnyEvent
    @Field(1)
    public XAnyEvent xany() {
        return this.io.getNativeObjectField(this, 1);
    }

    /// C type : XKeyEvent
    @Field(2)
    public XKeyEvent xkey() {
        return this.io.getNativeObjectField(this, 2);
    }

    /// C type : XButtonEvent
    @Field(3)
    public XButtonEvent xbutton() {
        return this.io.getNativeObjectField(this, 3);
    }

    /*/// C type : XMotionEvent
    @Field(4)
    public XMotionEvent xmotion() {
        return this.io.getNativeObjectField(this, 4);
    }

    /// C type : XCrossingEvent
    @Field(5)
    public XCrossingEvent xcrossing() {
        return this.io.getNativeObjectField(this, 5);
    }

    /// C type : XFocusChangeEvent
    @Field(6)
    public XFocusChangeEvent xfocus() {
        return this.io.getNativeObjectField(this, 6);
    }

    /// C type : XExposeEvent
    @Field(7)
    public XExposeEvent xexpose() {
        return this.io.getNativeObjectField(this, 7);
    }

    /// C type : XGraphicsExposeEvent
    @Field(8)
    public XGraphicsExposeEvent xgraphicsexpose() {
        return this.io.getNativeObjectField(this, 8);
    }

    /// C type : XNoExposeEvent
    @Field(9)
    public XNoExposeEvent xnoexpose() {
        return this.io.getNativeObjectField(this, 9);
    }

    /// C type : XVisibilityEvent
    @Field(10)
    public XVisibilityEvent xvisibility() {
        return this.io.getNativeObjectField(this, 10);
    }

    /// C type : XCreateWindowEvent
    @Field(11)
    public XCreateWindowEvent xcreatewindow() {
        return this.io.getNativeObjectField(this, 11);
    }

    /// C type : XDestroyWindowEvent
    @Field(12)
    public XDestroyWindowEvent xdestroywindow() {
        return this.io.getNativeObjectField(this, 12);
    }

    /// C type : XUnmapEvent
    @Field(13)
    public XUnmapEvent xunmap() {
        return this.io.getNativeObjectField(this, 13);
    }

    /// C type : XMapEvent
    @Field(14)
    public XMapEvent xmap() {
        return this.io.getNativeObjectField(this, 14);
    }

    /// C type : XMapRequestEvent
    @Field(15)
    public XMapRequestEvent xmaprequest() {
        return this.io.getNativeObjectField(this, 15);
    }

    /// C type : XReparentEvent
    @Field(16)
    public XReparentEvent xreparent() {
        return this.io.getNativeObjectField(this, 16);
    }

    /// C type : XConfigureEvent
    @Field(17)
    public XConfigureEvent xconfigure() {
        return this.io.getNativeObjectField(this, 17);
    }

    /// C type : XGravityEvent
    @Field(18)
    public XGravityEvent xgravity() {
        return this.io.getNativeObjectField(this, 18);
    }

    /// C type : XResizeRequestEvent
    @Field(19)
    public XResizeRequestEvent xresizerequest() {
        return this.io.getNativeObjectField(this, 19);
    }

    /// C type : XConfigureRequestEvent
    @Field(20)
    public XConfigureRequestEvent xconfigurerequest() {
        return this.io.getNativeObjectField(this, 20);
    }

    /// C type : XCirculateEvent
    @Field(21)
    public XCirculateEvent xcirculate() {
        return this.io.getNativeObjectField(this, 21);
    }

    /// C type : XCirculateRequestEvent
    @Field(22)
    public XCirculateRequestEvent xcirculaterequest() {
        return this.io.getNativeObjectField(this, 22);
    }

    /// C type : XPropertyEvent
    @Field(23)
    public XPropertyEvent xproperty() {
        return this.io.getNativeObjectField(this, 23);
    }

    /// C type : XSelectionClearEvent
    @Field(24)
    public XSelectionClearEvent xselectionclear() {
        return this.io.getNativeObjectField(this, 24);
    }

    /// C type : XSelectionRequestEvent
    @Field(25)
    public XSelectionRequestEvent xselectionrequest() {
        return this.io.getNativeObjectField(this, 25);
    }

    /// C type : XSelectionEvent
    @Field(26)
    public XSelectionEvent xselection() {
        return this.io.getNativeObjectField(this, 26);
    }

    /// C type : XColormapEvent
    @Field(27)
    public XColormapEvent xcolormap() {
        return this.io.getNativeObjectField(this, 27);
    }

    /// C type : XClientMessageEvent
    @Field(28)
    public XClientMessageEvent xclient() {
        return this.io.getNativeObjectField(this, 28);
    }

    /// C type : XMappingEvent
    @Field(29)
    public XMappingEvent xmapping() {
        return this.io.getNativeObjectField(this, 29);
    }

    /// C type : XErrorEvent
    @Field(30)
    public XErrorEvent xerror() {
        return this.io.getNativeObjectField(this, 30);
    }

    /// C type : XKeymapEvent
    @Field(31)
    public XKeymapEvent xkeymap() {
        return this.io.getNativeObjectField(this, 31);
    }

    /// C type : XGenericEvent
    @Field(32)
    public XGenericEvent xgeneric() {
        return this.io.getNativeObjectField(this, 32);
    }

    /// C type : XGenericEventCookie
    @Field(33)
    public XGenericEventCookie xcookie() {
        return this.io.getNativeObjectField(this, 33);
    }*/

    /// C type : long[24]
    @Array({24})
    @Field(34)
    public Pointer<CLong> pad() {
        return this.io.getPointerField(this, 34);
    }
}