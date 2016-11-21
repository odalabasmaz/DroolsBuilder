package com.orhundalabasmaz.drools.builder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.tools.ant.BuildException;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;

import java.io.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Orhun Dalabasmaz
 */
public class DroolsBuilder {
	private static final String EXT_BRL = "brl";
	private static final String EXT_XML = "xml";
	private static final String EXT_RFM = "rfm";
	private static final String EXT_RF = "rf";
	private static final String EXT_DRL = "drl";
	private static final String EXT_MODEL_DRL = "model.drl";
	private static final String EXT_DSL = "dsl";
	private static final String EXT_DSLR = "dslr";
	private static final String EXT_XLS = "xls";
	private static final String EXT_PACKAGE = "package";
	private static final String EXT_FUNCTION = "function";

	private static final String CHARSET = "ISO-8859-9";

	private static final String[] RULE_EXTENSIONS = new String[]{
			EXT_PACKAGE,
			EXT_FUNCTION,
			EXT_MODEL_DRL,
			EXT_DRL,
			EXT_DSL,
			EXT_XLS
	};

	private File srcDir;
	private File toFile;

	public void setSrcDir(File srcDir) {
		this.srcDir = srcDir;
	}

	public void setToFile(File toFile) {
		this.toFile = toFile;
	}

	public void build() {
		if (toFile == null) {
			SimpleLogger.error("Destination rulebase file does not specified.");
			return;
		}

		if (srcDir == null) {
			SimpleLogger.error("Source directory not specified.");
			return;
		}

		if (!srcDir.exists()) {
			SimpleLogger.error("Source directory does not exists." + srcDir.getAbsolutePath());
			return;
		}

		createWithKnowledgeBuilder();
		SimpleLogger.info("Build succeed.");
	}

	private void createWithKnowledgeBuilder() {
		KnowledgeBuilder kbuilder = getKnowledgeBuilder();
		compileAndAddFiles(kbuilder);
		Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(pkgs);
		for (KnowledgePackage pkg : pkgs) {
			serializeObject(pkg);
		}
	}

	private KnowledgeBuilder getKnowledgeBuilder() {
		PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
		return KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
	}

	private void compileAndAddFiles(KnowledgeBuilder kbuilder) {
		// get the list of files to be added to the rulebase
		final Set<File> files = getAllFiles();

		int curr = 0;
		int total = files.size();
		long begin = System.currentTimeMillis();
		SimpleLogger.info("compiling " + total + " rules into " + toFile.getName());
		for (final File file : files) {
			String fileName = file.getName();
			// compile rule file and add to the builder
			SimpleLogger.info("> " + "compiling \"" + fileName + "\" [" + (++curr + "/" + total) + "]");
			compileAndAddFile(kbuilder, file);
		}

		long end = System.currentTimeMillis();
		int duration = (int) (end - begin) / 1000;

		if (kbuilder.hasErrors()) {
			SimpleLogger.error(kbuilder.getErrors().toString());
			throw new BuildException("Error occurred during build: " + kbuilder.getErrors().toString());
		} else {
			SimpleLogger.info("compiling done successfully in " + duration + " sec.");
		}
	}

	private Set<File> getAllFiles() {
		final Set<File> files = new LinkedHashSet<>();
		for (String ext : RULE_EXTENSIONS) {
			files.addAll(getFiles(ext));
		}
		return files;
	}

	private Set<File> getDSLFiles() {
		final Set<File> files = new LinkedHashSet<>();
		files.addAll(getFiles(EXT_DSL));
		return files;
	}

	@SuppressWarnings("unchecked")
	private Set<File> getFiles(final String ext) {
		final File dir = new File(this.srcDir.getAbsolutePath());
		final Collection<File> fileCollection = (Collection<File>) FileUtils.listFiles(dir, new String[]{ext}, true);
		final Set<File> fileSet = new TreeSet<>(NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
		fileSet.addAll(fileCollection);
		return fileSet;
	}

	private void serializeObject(Object object) {
		try (FileOutputStream fout = new FileOutputStream(toFile)) {
			DroolsStreamUtils.streamOut(fout, object);
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}

	private void compileAndAddFile(KnowledgeBuilder kbuilder, File file) {
		final String fileName = file.getName();
		try (final FileInputStream fileInputStream = new FileInputStream(file);
		     final Reader fileReader = new InputStreamReader(fileInputStream, CHARSET)) {
			if (fileName.endsWith(EXT_BRL)) {
				kbuilder.add(ResourceFactory.newReaderResource(fileReader),
						ResourceType.BRL);

			} else if (fileName
					.endsWith(EXT_RFM)
					|| fileName
					.endsWith(EXT_RF)) {

				kbuilder.add(ResourceFactory.newReaderResource(fileReader),
						ResourceType.DRF);

			} else if (fileName.endsWith(EXT_XML)) {
				kbuilder.add(ResourceFactory.newReaderResource(fileReader),
						ResourceType.XDRL);
			} else if (fileName.endsWith(EXT_XLS)) {

				DecisionTableConfiguration dtableconfiguration = KnowledgeBuilderFactory
						.newDecisionTableConfiguration();
				dtableconfiguration.setInputType(DecisionTableInputType.XLS);

				kbuilder.add(ResourceFactory.newReaderResource(fileReader),
						ResourceType.DTABLE, dtableconfiguration);

				// } else if
				// (fileName.endsWith(EXT_DSL)) {
				//
				// kbuilder.add(ResourceFactory.newReaderResource(fileReader),
				// ResourceType.DSL);

			} else if (fileName.endsWith(EXT_DSLR)) {

				// Get the DSL too.
				Set<File> dslFiles = getDSLFiles();
				for (File dsl : dslFiles) {
					kbuilder.add(ResourceFactory.newFileResource(dsl),
							ResourceType.DSL);
				}

				kbuilder.add(ResourceFactory.newReaderResource(fileReader),
						ResourceType.DSLR);

			} else {
				kbuilder.add(ResourceFactory.newReaderResource(fileReader),
						ResourceType.DRL);
			}
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}

}
