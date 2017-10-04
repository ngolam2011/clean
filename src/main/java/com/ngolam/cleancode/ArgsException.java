package com.ngolam.cleancode;

import com.ngolam.cleancode.Args.ErrorCode;

public class ArgsException extends Exception {
	private char errorArgumentId = '\0';
	private String errorParameter = "TILT";
	private ErrorCode errorCode = ErrorCode.OK;

	public ArgsException() {}

	public ArgsException(String message) {
		super(message);
	}
}
