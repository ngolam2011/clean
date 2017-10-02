package com.ngolam.cleancode;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws ArgsException, ParseException
    {
        Args arg = new Args("l,p#,d*", args);
		boolean logging = arg.getBoolean('l');
		int port = arg.getInt('p');
		String directory = arg.getString('d');
		System.out.println(logging + " " + port + " " + directory);
    }
}
