package com.ngolam.cleancode;

public abstract class ArgumentMarshaler {

	public abstract void set(String s) throws ArgsException;
	public abstract Object get();
}

