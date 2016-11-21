package com.orhundalabasmaz.drools.builder;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.drools.core.util.StringUtils;

import java.io.File;

/**
 * @author Orhun Dalabasmaz
 */
public class AntRunner extends Task {
	private String packName;
	private String srcDir;
	private String outDir;

	@Override
	public void execute() {
		super.execute();
		try {
			log("AntRunner is running...");
			validate();
			buildPackage();
		} finally {
			log("AntRunner is completed.");
		}
	}

	private void validate() {
		if (StringUtils.isEmpty(srcDir)) {
			throw new BuildException("srcDir must be defined!");
		}
		if (StringUtils.isEmpty(outDir)) {
			throw new BuildException("outDir must be defined!");
		}
		if (StringUtils.isEmpty(packName)) {
			throw new BuildException("packName must be defined!");
		}
	}

	private void buildPackage() {
		DroolsBuilder task = new DroolsBuilder();
		task.setSrcDir(new File(srcDir));
		task.setToFile(new File(outDir + File.separator + packName + ".pkg"));
		task.build();
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public void setOutDir(String outDir) {
		this.outDir = outDir;
	}
}
