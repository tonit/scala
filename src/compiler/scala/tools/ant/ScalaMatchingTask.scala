/*                     __                                               *\
**     ________ ___   / /  ___     Scala Ant Tasks                      **
**    / __/ __// _ | / /  / _ |    (c) 2005-2011, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scala.tools.ant

import java.io.{ File, InputStream, FileWriter }

import org.apache.tools.ant.{ Task, BuildException }
import org.apache.tools.ant.taskdefs.MatchingTask
import org.apache.tools.ant.types.{ Path, Reference }

trait ScalaTask {
  self: Task =>

  /** Generates a build error. Error location will be the
   *  current task in the ant file.
   *
   * @param message A message describing the error.
   * @throws BuildException A build error exception thrown in every case.
   */
   protected def buildError(message: String): Nothing =
     throw new BuildException(message, getLocation())
}

abstract class ScalaMatchingTask extends MatchingTask with ScalaTask {
}
