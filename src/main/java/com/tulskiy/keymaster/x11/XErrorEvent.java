package com.tulskiy.keymaster.x11;

import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
@Library("libX11")
public class XErrorEvent extends StructObject {
	public XErrorEvent() {
		super();
	}
	public XErrorEvent(Pointer pointer) {
		super(pointer);
	}
	@Field(0)
	public int type() {
		return this.io.getIntField(this, 0);
	}
	@Field(0)
	public XErrorEvent type(int type) {
		this.io.setIntField(this, 0, type);
		return this;
	}
	/**
	 * Display the event was read from<br>
	 * C type : Display*
	 */
	@Field(1)
	public Pointer<LibX11.XDisplay> display() {
		return this.io.getPointerField(this, 1);
	}
	/**
	 * Display the event was read from<br>
	 * C type : Display*
	 */
	@Field(1)
	public XErrorEvent display(Pointer<LibX11.XDisplay> display) {
		this.io.setPointerField(this, 1, display);
		return this;
	}
	/**
	 * resource id<br>
	 * C type : XID
	 */
	@CLong
	@Field(2)
	public long resourceid() {
		return this.io.getCLongField(this, 2);
	}
	/**
	 * resource id<br>
	 * C type : XID
	 */
	@CLong
	@Field(2)
	public XErrorEvent resourceid(long resourceid) {
		this.io.setCLongField(this, 2, resourceid);
		return this;
	}
	/// serial number of failed request
	@CLong
	@Field(3)
	public long serial() {
		return this.io.getCLongField(this, 3);
	}
	/// serial number of failed request
	@CLong
	@Field(3)
	public XErrorEvent serial(long serial) {
		this.io.setCLongField(this, 3, serial);
		return this;
	}
	/// error code of failed request
	@Field(4)
	public byte error_code() {
		return this.io.getByteField(this, 4);
	}
	/// error code of failed request
	@Field(4)
	public XErrorEvent error_code(byte error_code) {
		this.io.setByteField(this, 4, error_code);
		return this;
	}
	/// Major op-code of failed request
	@Field(5)
	public byte request_code() {
		return this.io.getByteField(this, 5);
	}
	/// Major op-code of failed request
	@Field(5)
	public XErrorEvent request_code(byte request_code) {
		this.io.setByteField(this, 5, request_code);
		return this;
	}
	/// Minor op-code of failed request
	@Field(6)
	public byte minor_code() {
		return this.io.getByteField(this, 6);
	}
	/// Minor op-code of failed request
	@Field(6)
	public XErrorEvent minor_code(byte minor_code) {
		this.io.setByteField(this, 6, minor_code);
		return this;
	}
}