package com.ngolam.cleancode;

import com.ngolam.cleancode.Args.ErrorCode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCreateWithNoSchemaOrArguments() throws Exception
    {
    	try {
    		new Args("", new String[] {"-x"});
    		fail();
    	} catch (ArgsException e) {
    		assertEquals(ErrorCode.UNEXPECTED_ARGUMENT, e.getMessage());
    	}
    }
}
