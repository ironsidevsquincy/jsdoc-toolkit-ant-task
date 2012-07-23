JsDoc Toolkit Ant Task
======================

This project is a simple wrapper around the [JsDoc Toolkit](http://code.google.com/p/jsdoc-toolkit/) documentation program. It gives the user all the command line options, with the flexibility of using it within an [Ant](http://ant.apache.org/) build file.

About JsDoc Toolkit
-------------------

From the JsDoc Toolkit project page...

 > JsDoc Toolkit is an application, written in JavaScript, for automatically generating
 > template-formatted, multi-page HTML (or XML, JSON, or any other text-based)
 > documentation from commented JavaScript source code. 
 >
 > Based on the JSDoc.pm project, this was renamed "Jsdoc Toolkit" during development as it
 > grew into more than a simple version upgrade.

Unlike, say, JavaDoc, creating a tool to understand documented JavaScript has long been a diffcult problem, due to JavaScript's relatively loose programming style. The JsDoc Toolkit is another in a long line of tools to attempt to make documenting JavaScript intuituve and easy.

Unlike most other documentation tools, the JsDoc Toolkit program is written in JavaScript, running through the standalone [Rhino](http://www.mozilla.org/rhino/) engine.

Description
-----------

This customJs task makes it easier to create the JsDoc Toolkit documentation from the JavaScript, as it can be called from an Ant build file. All the [command line parameters](http://code.google.com/p/jsdoc-toolkit/wiki/CmdlineOptions) for JsDoc Toolkit are still present.

Parameters
----------

<table>
    <tr>
        <th>Attribute</th>
        <th>Description</th>
        <th>Required</th>
    </tr>
    <tr>
        <td>jsdochome</td>
        <td>The home directory of JsDoc Toolkit. Note, must include trailing slash</td>
        <td>Yes</td>
    </tr>
    <tr>
        <td>template</td>
        <td>The name of the template to use</td>
        <td>Yes</td>
    </tr>
    <tr>
        <td>outputdir</td>
        <td>Where the put the created documents, including trailing slash</td>
        <td>Yes</td>
    </tr>
    <tr>
        <td>inputdir</td>
        <td>The directory containing the JavaScript (or other) files</td>
        <td>Yes, unless a nested `source` element is supplied</td>
    </tr>
    <tr>
        <td>outputdir</td>
        <td>Where the put the created documents, including trailing slash</td>
        <td>Yes</td>
    </tr>
    <tr>
        <td>encoding</td>
        <td>Encoding of the input and output files</td>
        <td>No, defaults to `UTF-8`</td>
    </tr>
    <tr>
        <td>depth</td>
        <td>Recursive depth below the inputdir</td>
        <td>No, defaults to `10`, though ignored if no `inputdir` attribute is given</td>
    </tr>
    <tr>
        <td>includeundocumented</td>
        <td>Should undocumented functions be included</td>
        <td>No, defaults to `false`</td>
    </tr>
    <tr>
        <td>includeunderscored</td>
        <td>Should underscored functions be nicluded</td>
        <td>No, defaults to `false`</td>
    </tr>
    <tr>
        <td>includeprivate</td>
        <td>Should private functions be included</td>
        <td>No, defaults to `false`</td>
    </tr>
    <tr>
        <td>verbose</td>
        <td>Should the documentation be verbose</td>
        <td>No, defaults to `false`</td>
    </tr>
    <tr>
        <td>extensions</td>
        <td>Comma-seperated list of file extensions to include</td>
        <td>No, defaults to `js`</td>
    </tr>
    <tr>
        <td>log</td>
        <td>File to ouptut log info to</td>
        <td>No</td>
    </tr>
    <tr>
        <td>config</td>
        <td>Points to a JSON-styled file with all the command-line options present, overriding any supplied attributes</td>
        <td>No, but if supplied all other parameters are ignored</td>
    </tr>
</table>

Examples
--------

First define the jsdoctoolkit task

    <taskdef name="jsdoctoolkit" classname="uk.co.darrenhurley.ant.tasks.JsDocToolkit" classpath="/path/to/jsdoctoolkit.jar;/path/to/js.jar"/>

Note that `js.jar` must also be in the classpath.

Then call the task

    <jsdoctoolkit jsdochome="/path/to/jsdoc_toolkit-1.4.0/" template="sunny" outputdir="/output/dir/" inputdir="dir/of/javascript/files/" />

You can also use a nested `<fileset>` <http://ant.apache.org/manual/Types/fileset.html> or `<filelist>` <http://ant.apache.org/manual/Types/filelist.html> element to load files, or a custom `<source>` element, which accepts a `file` attribute

    <jsdoctoolkit jsdochome="/path/to/jsdoc_toolkit-1.4.0/" template="sunny" outputdir="/output/dir/">
	<source file="/a/js/soucefile.js" />
	<source file="/another/js/soucefile.js" />
    </jsdoctoolkit>

User-defined arguments can also be passed through (`-D` parameters on the command line) using a custom `<arg>` nested element,

    <jsdoctoolkit jsdochome="/path/to/jsdoc_toolkit-1.4.0/" template="sunny" outputdir="/output/dir/" inputdir="dir/of/javascript/files/">
        <arg name="argOne" value="foo" />
        <arg name="argTwo" value="bar" />
    </jsdoctoolkit>

There's also an `antlib.xml` file included, if that's your bag. To use it, make sure the `jsdoctoolkit-ant-task-*.jar` and rhino's jar is on the classpath, and add `xmlns:foo="antlib:uk.co.darrenhurley.ant"` to the project element. Then use the task as `<foo:jsdoctoolkit>`.

Troubleshooting
---------------

 * The ant task has been tested with Rhino 1.6r7

Support
-------

If you find any bugs, etc, or have any ideas for improvements, please raise an issue