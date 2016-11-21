package com.orhundalabasmaz.drools.builder;

import java.io.File;

/**
 * @author Orhun Dalabasmaz
 */
public class Main {
	private static final String packageName = "SomKuralPaketi";
	private static final String ROOT = "C:\\data\\drools\\";

	public static void main(String... args) throws Exception {
		try {
			SimpleLogger.info("DroolsBuilder is running...");
			buildPackage();
		} finally {
			SimpleLogger.info("DroolsBuilder is completed.");
		}
	}

	private static void buildPackage() {
		DroolsBuilder task = new DroolsBuilder();
		task.setSrcDir(new File(ROOT + packageName));
		task.setToFile(new File(ROOT + packageName + ".pkg"));
		task.build();
	}
}
