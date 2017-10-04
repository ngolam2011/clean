package com.ngolam.cleancode;

import java.util.Iterator;

public class BooleanArgumentMarshaler implements ArgumentMarshaler {
	private boolean booleanValue = false;

	public Object get() {
		return booleanValue;
	}

	public void set(Iterator<String> currentArgument) {
		booleanValue = true;

	}

}
