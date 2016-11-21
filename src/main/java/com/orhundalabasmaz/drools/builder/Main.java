package com.orhundalabasmaz.drools.builder;

import java.io.File;

/**
 * @author Orhun Dalabasmaz
 */
public class Main {

	public static void main(String... args) throws Exception {
		try {
			if (args.length != 2) {
				throw new UnsupportedOperationException("srcDir and outFile must be specified!");
			}
			SimpleLogger.info("DroolsBuilder is running...");
			String srcDir = args[0];
			String outFile = args[1];
			buildPackage(srcDir, outFile);
		} finally {
			SimpleLogger.info("DroolsBuilder is completed.");
		}
	}

	private static void buildPackage(String srcDir, String outFile) {
		DroolsBuilder task = new DroolsBuilder();
		task.setSrcDir(new File(srcDir));
		task.setToFile(new File(outFile));
		task.build();
	}
}
