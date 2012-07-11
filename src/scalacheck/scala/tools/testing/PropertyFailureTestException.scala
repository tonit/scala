package scala.tools.testing

import org.scalacheck.{Test,Pretty}

/** A wrapper around scalacheck failure to generate exceptions for JUnit, since it's exception happy. */
class PropertyFailureTestException(name: String, f: Test.Failed) extends Exception(
  "Property(" + name + ") failed with input: " + f.args.map(_.prettyArg(Pretty.defaultParams)).mkString(", ") + "\n" +
  "lables: " + f.labels.mkString(",")
)
