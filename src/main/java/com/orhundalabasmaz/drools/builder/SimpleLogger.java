package com.orhundalabasmaz.drools.builder;

/**
 * @author Orhun Dalabasmaz
 */
public class SimpleLogger {

	private SimpleLogger() {
	}

	static void info(String msg) {
		System.out.println(msg);
	}

	static void error(String msg) {
		System.err.println(msg);
	}
}
