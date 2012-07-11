package testdir

import org.scalacheck._
import scala.tools.testing.ScalacheckTest



object DumbProperties extends ScalacheckTest("Nothing") {
  val d = Dependency.v
}
