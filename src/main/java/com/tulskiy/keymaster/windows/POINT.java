package com.tulskiy.keymaster.windows;

import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
@Library("user32")
public class POINT extends StructObject {
	public POINT() {
		super();
	}
	public POINT(Pointer pointer) {
		super(pointer);
	}
	@Field(0)
	public int x() {
		return this.io.getIntField(this, 0);
	}
	@Field(0)
	public POINT x(int x) {
		this.io.setIntField(this, 0, x);
		return this;
	}
	@Field(1)
	public int y() {
		return this.io.getIntField(this, 1);
	}
	@Field(1)
	public POINT y(int y) {
		this.io.setIntField(this, 1, y);
		return this;
	}
}
