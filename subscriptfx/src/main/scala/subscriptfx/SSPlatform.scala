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

  def invokeLater(r: Runnable): Unit = Platform.runLater(r)

  def invokeAndWait(r: Runnable): Unit = {
    val lock   = new Object
    invokeLater {new Runnable {
      override def run(): Unit = {
        r.run()
        lock.synchronized {lock.notify()}
      }
    }}
    lock.synchronized {lock.wait()}
  }

  script..
    /** Executes the task on the JavaFX event thread. Waits for it to finish before returning. */
    runAndWait(task: => Any) =
      val trigger = triggerFor(task)
      trigger && {!val t = trigger;Platform.runLater(t.task)!}  // runLater accepts a by-name parameter. So we need to materialize
                                                                // the trigger early, since its node will not be available on
                                                                // evaluation time. See: http://anatoliykmetyuk.github.io/blog/2016/04/13/subscript-values-in-by-name-calls.html
}