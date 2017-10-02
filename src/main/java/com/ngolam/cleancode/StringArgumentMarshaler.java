package com.ngolam.cleancode;

public class StringArgumentMarshaler extends ArgumentMarshaler {
	private String stringValue = "";
	@Override
	public void set(String s) {
		stringValue = s;
	}

	@Override
	public Object get() {
		return stringValue;
	}

}
