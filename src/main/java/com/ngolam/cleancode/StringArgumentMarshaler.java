package com.ngolam.cleancode;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ngolam.cleancode.Args.ErrorCode;

public class StringArgumentMarshaler implements ArgumentMarshaler {
	private String stringValue = "";
	private ErrorCode errorCode;

	public Object get() {
		return stringValue;
	}

	public void set(Iterator<String> currentArgument) throws ArgsException{
		try {
			stringValue = currentArgument.next();
		} catch(NoSuchElementException e) {
			errorCode = ErrorCode.MISSING_STRING;
			throw new ArgsException();

		}
	}

}
