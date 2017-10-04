package com.ngolam.cleancode;

import java.util.Iterator;

public interface ArgumentMarshaler {
	public void set(Iterator<String> currentArgument) throws ArgsException;
	public Object get();
}

