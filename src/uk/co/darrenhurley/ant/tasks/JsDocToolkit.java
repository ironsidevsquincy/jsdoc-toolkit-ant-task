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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.mozilla.javascript.tools.shell.Main;
import java.util.Vector;
import org.mozilla.javascript.tools.shell.AccessFileList;

import uk.co.darrenhurley.ant.types.Source;

public class JsDocToolkit extends Task {

	private String jsDocHome, template, outputDir;
	private Vector<Source> sources = new Vector<Source>();
	// optional properties
	private String encoding = "UTF-8", extensions = "js", config = "",
			log = "", inputDir = "";
	private int depth = -1;
	private boolean isUnderscoredFunctions = false,
			isUndocumentedFunctions = false, isPrivate = false,
			isVerbose = false;

	/**
	 * Method invoked by Ant to actually run the task
	 */
	public void execute() throws BuildException {
		// set system property; the base dir
		System.setProperty("jsdoc.dir", this.jsDocHome);
		// create the array of commands to pass to rhino
		String [] cmdArray = createCMDArray();
		// call the rhino javascript engine
		Main.main(cmdArray);
		AccessFileList.clearFileList();
		/*JarClassLoader jarLoader = new JarClassLoader("/Users/darrenh/libs/java/js.jar");
		try {
			Class rhinoMain = jarLoader.loadClass("org.mozilla.javascript.tools.shell.Main", true);
			System.out.println(rhinoMain.toString());
			Object o = rhinoMain.newInstance();
			if(o instanceof Main){
				System.out.println("helo");
				Main rhinoEngine = (Main) o;
				rhinoEngine.main(cmdArray);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}*/
	}

	/**
	 * Recieve a nested source from the ant task
	 * 
	 * @param source
	 *            a source object for the source nested element
	 */
	public void addSource(Source source) {
		if (!this.sources.contains(source)) {
			this.sources.add(source);
		}
	}

	/**
	 * Create the array of commands to pass to rhino engine
	 * 
	 * @return a string[] of commands to pass to the rhino engine
	 */
	private String[] createCMDArray() throws BuildException {
		// return if certain attributes are not present
		if ((this.jsDocHome == null) || (this.template == null)
				|| (this.outputDir == null)) {
			throw new BuildException(
					"jsdochome, template and outputdir are all compulsory attributes");
		}
		// set command parameters
		Vector<String> cmdVector = new Vector<String>();
		// the main jsodc js
		cmdVector.add(this.jsDocHome + "app/run.js");
		if (this.config != "") {
			cmdVector.add("-c=" + this.config);
			// cmdVector.add(this.inputDir);
			return cmdVector.toArray(new String[0]);
		}

		// which template to use
		cmdVector.add("-t=" + this.jsDocHome + "templates/" + this.template);
		// where to put the docs
		cmdVector.add("-d=" + this.outputDir);
		// the encoding to use
		cmdVector.add("-e=" + this.encoding);
		// file extensions to use
		cmdVector.add("-x=" + this.extensions);
		if (this.isUndocumentedFunctions) {
			cmdVector.add("-a");
		}
		if (this.isUnderscoredFunctions) {
			cmdVector.add("-A");
		}
		if (this.isPrivate) {
			cmdVector.add("-p");
		}
		if (this.isVerbose) {
			cmdVector.add("-v");
		}
		if (this.log != "") {
			cmdVector.add("-o=" + this.log);
		}
		if (this.inputDir != "") {
			// if recursion depth attribute not set, recurse all the way
			if (this.depth == -1) {
				cmdVector.add("-r");
			} else {
				cmdVector.add("-r=" + this.depth);
			}
			cmdVector.add(this.inputDir);
		} else if (this.sources.size() != 0) {
			// Loop through sources
			for (int i = 0; i < sources.size(); i++) {
				// Get current source, and add it to the cmdVector
				cmdVector.add(sources.elementAt(i).getFile());
			}

		} else {
			throw new BuildException(
					"You must specify a inputdir attribute or source child element(s)");
		}
		// append -j argument, so it works with version 2
		cmdVector.add("-j=" + this.jsDocHome + "app/run.js");
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
	 *            a string of the path to where the genrated docs are placed
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
	 *            a string of comma seperated extensions to include
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
	 * Optioanl attribute log, a path to a log file
	 * 
	 * @param log
	 *            a string of the path to the log file
	 */
	public void setLog(String log) {
		this.log = log;
	}
}
