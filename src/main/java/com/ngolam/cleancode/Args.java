package com.ngolam.cleancode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Args {
	private String schema;
	private String[] args;
	private boolean valid;
	private Set<Character> unexpectedArguments = new TreeSet<Character>();
	private Map<Character, ArgumentMarshaler> booleanArgs =
			new HashMap<Character, ArgumentMarshaler>();
	private Map<Character, ArgumentMarshaler> stringArgs =
			new HashMap<Character, ArgumentMarshaler>();
	private Map<Character, ArgumentMarshaler> intArgs =
			new HashMap<Character, ArgumentMarshaler>();
	private int currentArgument;
	private Set<Character> argsFound;
	private char errorArgument = '\0';

	enum ErrorCode {
		OK, MISSING_STRING, INVALID_INTEGER, MISSING_INTEGER
	}

	private ErrorCode errorCode = ErrorCode.OK;
	private String errorParameter;

	public Args(String schema, String[] args) throws ParseException, ArgsException {
		this.schema = schema;
		this.args = args;
		valid = parse();
	}

	private boolean parse() throws ParseException, ArgsException {
		if (schema.length() == 0 && args.length == 0)
			return true;
		parseSchema();
		parseArguments();
		return valid;
	}

	private boolean parseSchema() throws ParseException {
		for (String element : schema.split(",")) {
			if (element.length() > 0) {
				String trimmedElement = element.trim();
				parseSchemaElement(trimmedElement);
			}
		}
		return true;
	}

	private void parseSchemaElement(String element) throws ParseException {
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);
		if (isBooleanSchemaElement(elementTail))
			parseBooleanSchemaElement(elementId);
		else if (isIntegerSchemaElement(elementTail))
			parseIntegerSchemaElement(elementId);
		else if (isStringSchemaElement(elementTail))
			parseStringSchemaElement(elementId);

	}

	private void validateSchemaElementId(char elementId) throws ParseException {
		if (!Character.isLetter(elementId)) {
			throw new ParseException("Bad character:" + elementId + "in Args format: " + schema,  0);
		}
	}

	private void parseStringSchemaElement(char elementId) {
		stringArgs.put(elementId, new StringArgumentMarshaler());
	}

	private boolean isStringSchemaElement(String elementTail) {
		return elementTail.equals("*");
	}

	private boolean isBooleanSchemaElement(String elementTail) {
		return elementTail.length() == 0;
	}

	private void parseBooleanSchemaElement(char elementId) {
		booleanArgs.put(elementId, new BooleanArgumentMarshaler());
	}

	private boolean isIntegerSchemaElement(String elementTail) {
		return elementTail.equals("#");
	}

	private void parseIntegerSchemaElement(char elementId) {
		intArgs.put(elementId, new IntegerArgumentMarshaler());
	}

	private boolean parseArguments() throws ArgsException {
		for (currentArgument = 0; currentArgument < args.length; currentArgument++)
		{
			String arg = args[currentArgument];
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
		boolean set = true;
		if (isBoolean(argChar))
			setBooleanArg(argChar, true);
		else if (isString(argChar))
			setStringArg(argChar);
		else if (isInteger(argChar))
			setIntegerArg(argChar);
		else
			set = false;
		return set;
	}

	private void setStringArg(char argChar) throws ArgsException {
		currentArgument++;
		try {
			stringArgs.get(argChar).getString(args[currentArgument]);
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
			errorArgument = argChar;
			errorCode = ErrorCode.MISSING_STRING;
			throw new ArgsException();
		}
	}

	private void setIntegerArg(char argChar) throws ArgsException {
		currentArgument++;
		String parameter = null;
		try {
			parameter = args[currentArgument];
			intArgs.get(argChar).setInteger(Integer.parseInt(args[currentArgument]));
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
			errorArgument = argChar;
			errorCode = ErrorCode.MISSING_INTEGER;
			throw new ArgsException();
		} catch (NumberFormatException e) {
			valid = false;
			errorArgument = argChar;
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_INTEGER;
			throw new ArgsException();
		}
	}

	private boolean isString(char argChar) {
		return stringArgs.containsKey(argChar);
	}

	private void setBooleanArg(char argChar, boolean value) {
		booleanArgs.get(argChar).setBoolean(value);
	}

	private boolean isBoolean(char argChar) {
		return booleanArgs.containsKey(argChar);
	}

	private boolean isInteger(char argChar) {
		return intArgs.containsKey(argChar);
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
		if (unexpectedArguments.size() > 0) {
			return unexpectedArgumentMessage();
		} else
			switch (errorCode) {
				case MISSING_STRING:
					return String.format("Could not find string parameter for -%c.", errorArgument);
				case OK:
					throw new Exception("TILT: Should not get here.");
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
		ArgumentMarshaler am = booleanArgs.get(arg);
		return am != null && am.getBoolean();
	}


	public String getString(char arg) {
		ArgumentMarshaler am = stringArgs.get(arg);
		return am != null ? "" : am.getString();
	}

	public int getInt(char arg) {
		ArgumentMarshaler am = intArgs.get(arg);
		return am == null ? 0 : am.getInteger();
	}


	public boolean isValid() {
		return valid;
	}
}
