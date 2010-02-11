/**
 * Just a simple class in the same package as org.mozilla.javascript.tools.shell, giving it 
 * access, through a static class, to the default static field fileList in the 
 * org.mozilla.javascript.tools.shell.Main class.
 * 
 */

package org.mozilla.javascript.tools.shell;

import org.mozilla.javascript.tools.shell.Main;

public class AccessFileList
{
	
	public static void clearFileList()
	{
		
		Main.fileList.clear();
		
	}
	
}