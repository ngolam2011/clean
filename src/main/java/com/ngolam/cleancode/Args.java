package com.ngolam.cleancode;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class Args {
	private String schema;
	private boolean valid;
	private Set<Character> unexpectedArguments = new TreeSet<Character>();
	private Map<Character, ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();
	private Set<Character> argsFound;
	private Iterator<String> currentArgument;
	private char errorArgument = '\0';
	private List<String> argsList;

	enum ErrorCode {
		OK, MISSING_STRING, INVALID_INTEGER, MISSING_INTEGER, MISSING_DOUBLE, INVALID_DOUBLE, UNEXPECTED_ARGUMENT
	}

	private ErrorCode errorCode = ErrorCode.OK;
	private String errorParameter;
	private char errorArgumentId;

	public Args(String schema, String[] args) throws ParseException, ArgsException {
		this.schema = schema;
		argsList = Arrays.asList(args);
		valid = parse();
	}

	private boolean parse() throws ParseException, ArgsException {
		if (schema.length() == 0 && argsList.size() == 0)
			return true;
		parseSchema();
		try {
			parseArguments();
		} catch(ArgsException e) {

		}
		return valid;
	}

	private boolean parseSchema() throws ArgsException {
		for (String element : schema.split(",")) {
			if (element.length() > 0) {
				String trimmedElement = element.trim();
				parseSchemaElement(trimmedElement);
			}
		}
		return true;
	}

	private void parseSchemaElement(String element) throws ArgsException {
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);
		if (elementTail.length() == 0)
			marshalers.put(elementId, new BooleanArgumentMarshaler());
		else if (elementTail.equals("#"))
			marshalers.put(elementId, new IntegerArgumentMarshaler());
		else if (elementTail.equals("*"))
			marshalers.put(elementId, new StringArgumentMarshaler());
		else if (elementTail.equals("##"))
			marshalers.put(elementId, new DoubleArgumentMarshaler());
		else {
			throw new ArgsException(String.format("Argument: %c has invalid format: %s.", elementId, elementTail));
		}

	}

	private void validateSchemaElementId(char elementId) throws ArgsException {
		if (!Character.isLetter(elementId)) {
			throw new ArgsException("Bad character:" + elementId + "in Args format: " + schema);
		}
	}


	private boolean isStringSchemaElement(String elementTail) {
		ArgumentMarshaler m = marshalers.get(elementTail);
		return m instanceof StringArgumentMarshaler;
	}

	private boolean isBooleanSchemaElement(String elementTail) {
		ArgumentMarshaler m = marshalers.get(elementTail);
		return m instanceof BooleanArgumentMarshaler;
	}


	private boolean isIntegerSchemaElement(String elementTail) {
		ArgumentMarshaler m = marshalers.get(elementTail);
		return m instanceof IntegerArgumentMarshaler;
	}


	private boolean parseArguments() throws ArgsException {
		for (currentArgument = argsList.iterator(); currentArgument.hasNext();)
		{
			String arg = currentArgument.next();
			parseArgument(arg);
		}
		return true;
	}

	private void parseArgument(String arg) throws ArgsException {
		if (arg.startsWith("-"))
			parseElements(arg);
	}

	private void parseElements(String arg) throws ArgsException {
		for (int i = 1; i < arg.length(); i++)
			parseElement(arg.charAt(i));
	}

	private void parseElement(char argChar) throws ArgsException {
		if (setArgument(argChar)) {
			argsFound.add(argChar);
		}
		else {
			unexpectedArguments.add(argChar);
			valid = false;
		}
	}

	private boolean setArgument(char argChar) throws ArgsException {
		ArgumentMarshaler m = marshalers.get(argChar);
		if (m == null)
			return false;
		try {
			m.set(currentArgument);
			return true;
		} catch (ArgsException e) {
			valid = false;
			errorArgumentId = argChar;
			throw e;
		}
	}



	public int cardinality() {
		return argsFound.size();
	}

	public String usage() {
		if (schema.length() > 0)
			return "-[" + schema + "]";
		else
			return "";
	}

	public String errorMessage() throws Exception {
		switch (errorCode) {
			case OK:
				throw new Exception("TILT: Should not get here.");
			case UNEXPECTED_ARGUMENT:
				return unexpectedArgumentMessage();
			case INVALID_INTEGER:
				return String.format("Argument -%c expectes an integer but was '%s'.", errorArgumentId, errorParameter);
			case INVALID_DOUBLE:
				return String.format("Argument -%c expectes an double but was '%s'.", errorArgumentId, errorParameter);
			case MISSING_DOUBLE:
				return String.format("Could not find double parameter for -%c.", errorArgumentId);
		}
		return "";
	}

	private String unexpectedArgumentMessage() {
		StringBuffer message = new StringBuffer("Argument(s) -");
		for (char c : unexpectedArguments) {
			message.append(c);
		}
		message.append(" unexpected.");

		return message.toString();
	}


	public boolean getBoolean(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		boolean b = false;
		try {
			b = am != null && (Boolean) am.get();
		} catch (ClassCastException e) {
			b = false;
		}
		return b;
	}


	public String getString(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		try {
			return am == null ? "" : (String) am.get();
		} catch (ClassCastException e) {
			return "";
		}
	}

	public int getInt(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		try {

			return am == null ? 0 : (Integer) am.get();
		} catch (Exception e) {
			return 0;
		}
	}

	public double getDouble(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		try {
			return am == null ? 0 : (Double) am.get();
		} catch (Exception e) {
			return 0.0;
		}
	}


	public boolean isValid() {
		return valid;
	}
}
