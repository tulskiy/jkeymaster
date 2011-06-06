package com.tulskiy.keymaster.windows;

import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
@Library("user32")
public class MSG extends StructObject {
	public MSG() {
		super();
	}
	public MSG(Pointer pointer) {
		super(pointer);
	}
	/// C type : void*
	@Field(0)
	public Pointer<? > hwnd() {
		return this.io.getPointerField(this, 0);
	}
	/// C type : void*
	@Field(0)
	public MSG hwnd(Pointer<? > hwnd) {
		this.io.setPointerField(this, 0, hwnd);
		return this;
	}
	@Field(1)
	public int message() {
		return this.io.getIntField(this, 1);
	}
	@Field(1)
	public MSG message(int message) {
		this.io.setIntField(this, 1, message);
		return this;
	}
	@Field(2)
	public int wParam() {
		return this.io.getIntField(this, 2);
	}
	@Field(2)
	public MSG wParam(int wParam) {
		this.io.setIntField(this, 2, wParam);
		return this;
	}
	@CLong
	@Field(3)
	public long lParam() {
		return this.io.getCLongField(this, 3);
	}
	@CLong
	@Field(3)
	public MSG lParam(long lParam) {
		this.io.setCLongField(this, 3, lParam);
		return this;
	}
	@Field(4)
	public int time() {
		return this.io.getIntField(this, 4);
	}
	@Field(4)
	public MSG time(int time) {
		this.io.setIntField(this, 4, time);
		return this;
	}
	/// C type : POINT
	@Field(5)
	public POINT pt() {
		return this.io.getNativeObjectField(this, 5);
	}
}
