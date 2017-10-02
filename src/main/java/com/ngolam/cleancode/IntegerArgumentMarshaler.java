package com.ngolam.cleancode;

public class IntegerArgumentMarshaler extends ArgumentMarshaler {
	private int intValue = 0;

	@Override
	public void set(String s) throws ArgsException {
		try {
			intValue = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new ArgsException();
		}
	}

	@Override
	public Object get() {
		return intValue;
	}

}
