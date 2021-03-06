package com.ngolam.cleancode;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ngolam.cleancode.Args.ErrorCode;

public class IntegerArgumentMarshaler implements ArgumentMarshaler {
	private int intValue = 0;
	private ErrorCode errorCode;
	private String errorParameter;


	public Object get() {
		return intValue;
	}

	public void set(Iterator<String> currentArgument) throws ArgsException {
		String parameter = null;
		try {
			parameter = currentArgument.next();
			intValue = Integer.parseInt(parameter);
		} catch(NoSuchElementException e) {
			errorCode = ErrorCode.MISSING_INTEGER;
			throw new ArgsException();
		} catch(NumberFormatException e) {
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_INTEGER;
			throw e;
		}
	}

}
