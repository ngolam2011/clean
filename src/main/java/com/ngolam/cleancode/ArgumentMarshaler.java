package com.ngolam.cleancode;

public class ArgumentMarshaler {
	private boolean booleanValue = false;
	private String stringValue;
	private int integerValue;

	public void setBoolean(boolean value) {
		booleanValue = value;
	}

	public boolean getBoolean() {
		return booleanValue;
	}

	public void getString(String string) {
		stringValue = string;
	}

	public String getString() {
		return stringValue == null ? "" : stringValue;
	}

	public int getInteger() {
		return integerValue;
	}

	public void setInteger(int parseInt) {
		integerValue = parseInt;
	}

}

