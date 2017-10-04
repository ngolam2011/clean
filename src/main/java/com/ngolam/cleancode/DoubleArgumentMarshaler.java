package com.ngolam.cleancode;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ngolam.cleancode.Args.ErrorCode;

public class DoubleArgumentMarshaler implements ArgumentMarshaler {

	private double doubleValue = 0;
	private ErrorCode errorCode;
	private String errorParameter;

	public void set(Iterator<String> currentArgument) throws ArgsException {

		String parameter = null;
		try {
			parameter = currentArgument.next();
			doubleValue = Double.parseDouble(parameter);
		} catch (NoSuchElementException e) {
			errorCode = ErrorCode.MISSING_DOUBLE;
			throw new ArgsException();
		} catch (NumberFormatException e) {
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_DOUBLE;
			throw new ArgsException();
		}
	}

	public Object get() {
		return doubleValue;
	}

}
