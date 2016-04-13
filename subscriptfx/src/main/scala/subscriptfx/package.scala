import subscript.language
import subscript.Predef._
import subscript.objectalgebra._


package object subscriptfx {
  /** Alias for SSPlatform.runAndWait. */
  script gui(task: => Unit) = SSPlatform.runAndWait(task)
}