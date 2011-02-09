package uk.co.darrenhurley.rhino;

import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;

import java.io.*;

/**
 * The shell program.
 * 
 * Can execute scripts interactively or in batch mode at the command line. An
 * example of controlling the JavaScript engine.
 * 
 * @author Norris Boyd
 */
public class Shell {

	/**
	 * Main entry point.
	 * 
	 * Process arguments as would a normal Java program. Also create a new
	 * Context and associate it with the current thread. Then set up the
	 * execution environment and begin to execute scripts.
	 * 
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		// Associate a new Context with this thread
		Context cx = Context.enter();
		try {
			// Use the Gloabl context (includes shell commands)
			ScriptableObject global = new Global(cx);
			// add standard JS objects
			cx.initStandardObjects(global);

			int length = args.length - 1;
			Object[] array = new Object[length];
			// pull out the arguments after the source file
			System.arraycopy(args, 1, array, 0, length);
			Scriptable argsObj = cx.newArray(global, array);
			// store in out global scope
			global.defineProperty("arguments", argsObj,
					ScriptableObject.DONTENUM);

			// read the specified file
			String filename = args[0];
			FileReader in = new FileReader(filename);
			// Here we evalute the entire contents of the file as
			// a script. Text is printed only if the print() function
			// is called.
			cx.evaluateReader(global, in, filename, 1, null);
		} finally {
			Context.exit();
		}
	}

}
