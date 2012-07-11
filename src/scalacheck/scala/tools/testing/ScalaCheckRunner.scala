package scala.tools.testing

import org.junit.runner.{
  Runner,
  RunWith,
  Description,
  Result => JResult
}
import org.junit.runner.notification.{
  RunNotifier,
  Failure => JFailure
}
import beans.BeanProperty
import org.scalacheck.{Prop, Test, Properties, Pretty}
import org.junit.runner.notification.RunListener

/** A JUnit runner for scalacheck.  Lets us run scalacheck 
 * properties tests using JUnit laziness in ant.
 * @author J. Suereth
 */
class ScalaCheckRunner(scalacheckClass: Class[_]) extends Runner {
  // TODO - Make this a WeakReference that caP{n be regenerated so we can GC this.
  private val testProps: Properties = 
    try {
      val field = scalacheckClass getField "MODULE$"
      (field get null).asInstanceOf[Properties]
    } catch {
      case e: Exception =>
        sys error ("Unable to load scalacheck object: " + scalacheckClass.toString)
        throw e
    }
  /** Map containing test name -> description + property mappings. */
  private lazy val testPropDescMap: Map[String, (Description, Prop)] =
    testProps.properties.map { case (name, prop) =>      
      val propDesc = Description.createTestDescription(scalacheckClass, name)
      name -> (propDesc -> prop)
    } {collection.breakOut}
  
  /** The JUnit description of the scalacheck test. */
  @BeanProperty
  lazy val description = {
    val desc = Description.createSuiteDescription(scalacheckClass)
    for {
      (name, (d, prop)) <- testPropDescMap
    } desc addChild d
    desc
  }
  
  /** Callback from Scalacheck to notify the world of what happened, in a very uninformative way. */
  class MyTestCallback(notifier: RunNotifier, results: RunListener) extends Test.TestCallback {
    override def onPropEval(name: String, threadIdx: Int, succeeded: Int, discarded: Int): Unit =
      notifier fireTestStarted testPropDescMap(name)._1

    /** Called whenever a property has finished testing */
    override def onTestResult(name: String, result: Test.Result): Unit = {
      val desc = testPropDescMap(name)._1
      result.status match {
        case f: Test.Failed =>
          val failure = new JFailure(desc, new PropertyFailureTestException(name, f))
          notifier fireTestFailure failure
          results testFailure failure
        case Test.PropException(_, e, _) => 
          val failure = new JFailure(desc, e)
          notifier fireTestFailure failure
          results testFailure failure
        case Test.GenException(e) =>
          val failure = new JFailure(desc, e)
          notifier fireTestFailure failure
          results testFailure failure
        // TODO - Is exhausted a failure?
        case Test.Exhausted =>
          notifier fireTestIgnored desc
          results testIgnored desc
        case _ => 
          notifier fireTestFinished desc
          results testFinished desc
      }
    }
  }
  /** Runs all tests. */    
  def run(notifier: RunNotifier ): Unit = {
    val result = new JResult
    val rlistener = result.createListener
    val params = new {
      override val testCallback = new MyTestCallback(notifier, rlistener)
    } with Test.Parameters.Default
    notifier fireTestRunStarted description
    rlistener testRunStarted description
    Test.checkProperties(params, testProps)
    rlistener testRunFinished result
    notifier fireTestRunFinished result
  }
}




