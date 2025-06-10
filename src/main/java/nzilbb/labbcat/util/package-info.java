/**
 * Command line utilities.
 * <p>In particular, the {@link CommandLine} utility is the <q>main class</q> for
 * <i>nzilbb.labbcat.jar</i>, so is easy to invoke from the command line for performing
 * ad-hoc API requests: e.g.
 * <p><code>java -jar nzilbb.labbcat.jar --indent --labbcaturl=&hellip; <b>getLayer orthography</b></code>
 * <p> &hellip; might print:
 * <pre>{
 *     "title": "LaBB-CAT",
 *     "version": "20200212.1030",
 *     "code": 0,
 *     "errors": [],
 *     "messages": [],
 *     "model": {
 *         "id": "orthography",
 *         "parentId": "transcript",
 *         "description": "Standard Orthography",
 *         "alignment": 0,
 *         "peers": false,
 *         "peersOverlap": false,
 *         "parentIncludes": false,
 *         "saturated": false,
 *         "type": "string",
 *         "validLabels": {},
 *         "category": null
 *     }
 * }</pre>
 */
package nzilbb.labbcat.util;
