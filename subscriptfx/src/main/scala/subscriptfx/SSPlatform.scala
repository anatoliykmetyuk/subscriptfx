package subscriptfx

import subscript.language
import subscript.Predef._
import subscript.objectalgebra._

import scalafx.application.Platform

object SSPlatform {

  case class ExecutionTrigger(rawTask: () => Unit) extends Trigger {
    def task = {rawTask(); trigger}
  }
  def triggerFor(task: => Unit) = ExecutionTrigger(() => task)

  script..
    runAndWait(task: => Unit) =
      val trigger = triggerFor(task)
      trigger && {!val t = trigger;Platform.runLater(t.task)!}  // runLater accepts a by-name parameter. So we need to materialize
                                                                // the trigger early, since its node will not be available on
                                                                // evaluation time.
}