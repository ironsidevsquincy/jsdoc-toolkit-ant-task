/**
 * JSDocToolkit is ant Ant task for JSDocToolkit, a JavaScript documentation tool.
 *
 * Usage:
 *  	<taskdef name="jsdoctoolkit"
 *  		classname="uk.co.darrenhurley.ant.tasks.JsDocToolkit"
 *  		classpath="/path/to/jsdoctoolkit.jar;/path/to/js.jar"/>
 *
 *  	<jsdoctoolkit jsdochome="/path/to/jsdoc_toolkit-1.4.0/" template="sunny" outputdir="/output/dir/">
 *		<source file="/a/js/soucefile.js" />
 *			<source file="/another/js/soucefile.js" />
 *		</jsdoctoolkit>
 *
 * Created 14th February 2008
 * 
 * @author Darren Hurley
 */

package uk.co.darrenhurley.ant.tasks;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.Path;
import uk.co.darrenhurley.ant.types.*;
import uk.co.darrenhurley.rhino.*;


import java.util.*;
import java.io.*;

import org.apache.tools.ant.DirectoryScanner;

public class JsDocToolkit extends Task {

	private String jsDocHome, template, outputDir;
	private Vector<Source> sources = new Vector<Source>();
	private Vector<Arg> args = new Vector<Arg>();
	private Vector<FileSet> fileSets = new Vector<FileSet>();
	private Vector<FileList> fileLists = new Vector<FileList>();
	private Vector<Path> paths = new Vector<Path>();
	// optional properties
	private String encoding = "UTF-8", extensions = "js", config = "",
			log = "", inputDir = "";
	private int depth = -1;
	private boolean isUnderscoredFunctions = false,
			isUndocumentedFunctions = false, isPrivate = false,
			isVerbose = false, isSuppressSourceOut = false;

	/**
	 * Method invoked by Ant to actually run the task
	 */
	public void execute() throws BuildException {
		// create the array of commands to pass to rhino
		String[] cmdArray = createCmdArray();
		try {
			Shell.main(cmdArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receive a nested source from the ant task
	 * 
	 * @param source
	 *            a source object for the source nested element
	 */
	public void addSource(Source source) {
		if (!sources.contains(source)) {
			sources.add(source);
		}
	}

	/**
	 * Receive a nested arg element from the ant task
	 * 
	 * @param arg
	 *            An argument (name/value pair) to pass to the toolkit; it can
	 *            be accessed in JsDoc as JSDOC.opt.D.argName
	 */
	public void addArg(Arg arg) {
		if (!args.contains(arg)) {
			args.add(arg);
		}
	}

	/**
	 * Receive a nested source from the ant task
	 * 
	 * @param fileSet
	 *            Returned from the native FileSet element
	 */
	public void addFileSet(FileSet fileSet) {
		if (!fileSets.contains(fileSet)) {
			fileSets.add(fileSet);
		}
	}

	/**
	 * Receive a nested FileList source from the ant task
	 * 
	 * @param fileList
	 *            Returned from the native FileList element
	 */
	public void addFileList(FileList fileList) {
		if (!fileLists.contains(fileList)) {
			fileLists.add(fileList);
		}
	}

	/**
	 * Receive a nested Path source from the ant task
	 * 
	 * @param path
	 *            Returned from the native Path element
	 */
	public void addPath(Path path) {
		if (!paths.contains(path)) {
			paths.add(path);
		}
	}

	/**
	 * Create the array of commands to pass to rhino engine
	 * 
	 * @return a string[] of commands to pass to the rhino engine
	 */
	private String[] createCmdArray() throws BuildException {
		// set command parameters
		Vector<String> cmdVector = new Vector<String>();
		// return if certain attributes are not present
		if ((jsDocHome == null) || (template == null) || (outputDir == null)) {
			throw new BuildException(
					"jsdochome, template and outputdir are all compulsory attributes");
		}
		// if a config file is supplied, return without adding other parameters
		if (config != "") {
			cmdVector.add("-c=" + config);
			// append -j argument, so it works with version 2
			cmdVector.add("-j=" + jsDocHome + "app/run.js");
			return cmdVector.toArray(new String[0]);
		}
		// the main jsodc js
		cmdVector.add(jsDocHome + "app/run.js");
		// which template to use
		cmdVector.add("-t=" + jsDocHome + "templates/" + template);
		// where to put the docs
		cmdVector.add("-d=" + outputDir);
		// the encoding to use
		cmdVector.add("-e=" + encoding);
		// file extensions to use
		cmdVector.add("-x=" + extensions);
		if (isUndocumentedFunctions) {
			cmdVector.add("-a");
		}
		if (isSuppressSourceOut) {
			cmdVector.add("-s");
		}
		if (isUnderscoredFunctions) {
			cmdVector.add("-A");
		}
		if (isPrivate) {
			cmdVector.add("-p");
		}
		if (isVerbose) {
			cmdVector.add("-v");
		}
		if (log != "") {
			cmdVector.add("-o=" + this.log);
		}
		if (inputDir != "") {
			// if recursion depth attribute not set, recurse all the way
			if (depth == -1) {
				cmdVector.add("-r");
			} else {
				cmdVector.add("-r=" + this.depth);
			}
			cmdVector.add(inputDir);
		} else if (sources.size() != 0 || fileSets.size() != 0 || fileLists.size() != 0 || paths.size() != 0) {
			// Loop through sources
			for (int i = 0; i < sources.size(); i++) {
				// Get current source, and add it to the cmdVector
				cmdVector.add(sources.elementAt(i).getFile());
			}
			// Loop through fileSets
			for (int i = 0; i < fileSets.size(); i++) {
				FileSet fs = fileSets.elementAt(i);
				// Ummm....?
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
				// Get base directory from fileset
				File dir = ds.getBasedir();
				// Get included files from fileset
				String[] srcs = ds.getIncludedFiles();
				// Loop through files
				for (int j = 0; j < srcs.length; j++) {
					// Make file object from base directory and filename
					File temp = new File(dir, srcs[j]);
					// Call the JSMin class with this file
					cmdVector.add(temp.getAbsolutePath());
				}
			}
			// Loop through fileLists
			for (int i = 0; i < fileLists.size(); i++) {
				FileList fs = fileLists.elementAt(i);
				// Get included files from filelist
				String[] srcs = fs.getFiles(getProject());
				// Get base directory from filelist
				File dir = fs.getDir(getProject());
				// Loop through files
				for (int j = 0; j < srcs.length; j++) {
					// Make file object from base directory and filename
					File temp = new File(dir, srcs[j]);
					// Call the JSMin class with this file
					cmdVector.add(temp.getAbsolutePath());
				}
			}
			// Loop through paths
			for (int i = 0; i < paths.size(); i++) {
				Path path = paths.elementAt(i);
				String[] srcs = path.list();
				for(int j = 0; j < srcs.length; j++) {
					cmdVector.add(srcs[j]);
				}
			}

		} else {
			throw new BuildException(
					"You must specify a inputdir attribute or source/fileset child element(s)");
		}
		if (args.size() != 0) {
			for (int i = 0; i < args.size(); i++) {
				cmdVector.add("-D=" + args.elementAt(i).getName() + ":"
						+ args.elementAt(i).getValue());
			}
		}
		// append -j argument, so it works with version 2
		cmdVector.add("-j=" + jsDocHome + "app/run.js");
		return cmdVector.toArray(new String[0]);

	}

	/**
	 * Compulsory attribute jsdochome, the home directory of the jsdoctoolkit.
	 * 
	 * @param jsDocHome
	 *            a string of the path to the home directory of the
	 *            jsdoctoolkit.
	 */
	public void setJsdochome(String jsDocHome) {
		this.jsDocHome = jsDocHome;
	}

	/**
	 * Compulsory attribute template, the name of the template to use (e.g.
	 * sunny, sweet, etc).
	 * 
	 * @param template
	 *            a string of the name of the template to use
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * Compulsory attribute outputdir, where to put the created docs.
	 * 
	 * @param outputDir
	 *            a string of the path to where the generated docs are placed
	 */
	public void setOutputdir(String outputDir) {
		this.outputDir = outputDir;
	}

	/**
	 * Either this inputdir attribute is required, or source nested elements.
	 * 
	 * @param inputDir
	 *            a String of the path to the directory containing the files.
	 */
	public void setInputdir(String inputDir) {
		this.inputDir = inputDir;
	}

	// optional attributes
	/**
	 * Optional attribute encoding, sets the encoding of the input and output
	 * files. Defaults to UTF-8.
	 * 
	 * @param encoding
	 *            a string of the encoding to use
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Optional choice to suppress the output of js source files. Defaults to
	 * 'false', otherwise known as allow JsDoc-toolkit links to raw js source.
	 * 
	 * @param isSuppressSourceOut
	 *            a boolean value of whether to prevent the raw source from
	 *            being included with the documentation (true) or allow it to be
	 *            included (false, default).
	 */
	public void setSuppresssourceout(Boolean isSuppressSourceOut) {
		this.isSuppressSourceOut = isSuppressSourceOut;
	}

	/**
	 * Optional attribute depth, sets the depth to descend below the inputDir.
	 * Defaults to 10.
	 * 
	 * @param depth
	 */
	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	/**
	 * Optional attribute includeundocumented, sets whether to generated docs
	 * for undocumented functions
	 * 
	 * @param isUndocumentedFunctions
	 *            a boolean value of whether to show undocumented functions or
	 *            not.
	 */
	public void setIncludeundocumented(Boolean isUndocumentedFunctions) {
		this.isUndocumentedFunctions = isUndocumentedFunctions;
	}

	/**
	 * Optional attribute includeunderscored, sets whether to generate docs for
	 * underscored functions.
	 * 
	 * @param isUnderscoredFunctions
	 *            a boolean value of whether to show underscored functions or
	 *            not
	 */
	public void setIncludeunderscored(Boolean isUnderscoredFunctions) {
		this.isUnderscoredFunctions = isUnderscoredFunctions;
	}

	/**
	 * Optional attribute includeprivate, sets whether to generate docs for
	 * private functions.
	 * 
	 * @param isPrivate
	 *            a boolean value of whether to show private functions or not
	 */
	public void setIncludeprivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	/**
	 * Optional verbose attribute, sets whether it should be verbose.
	 * 
	 * @param isVerbose
	 *            a boolean value, setting whether to be verbose
	 */
	public void setVerbose(Boolean isVerbose) {
		this.isVerbose = isVerbose;
	}

	/**
	 * Optional attribute extensions, sets what file extensions to use. Defaults
	 * to js.
	 * 
	 * @param extensions
	 *            a string of comma separated extensions to include
	 */
	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	/**
	 * Optional attribute conf, points to a (JSON style) config file that
	 * encapsulates all the parameters, therefore if this is supplied all other
	 * parameters are ignored.
	 * 
	 * @param config
	 *            a string of the path to the config file
	 */
	public void setConfig(String config) {
		this.config = config;
	}

	/**
	 * Optional attribute log, a path to a log file
	 * 
	 * @param log
	 *            a string of the path to the log file
	 */
	public void setLog(String log) {
		this.log = log;
	}
}
