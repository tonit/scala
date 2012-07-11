package scala.tools.testing

import org.scalacheck.Properties
import org.junit.runner.RunWith

/** A wrapper around scalacheck for the scala build so we can drive 
 * property checks with JUnit.
 *
 * @author J. Suereth
 */
@RunWith(classOf[ScalaCheckRunner])
abstract class ScalacheckTest(name: String) extends Properties(name)
