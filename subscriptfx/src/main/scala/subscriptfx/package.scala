import subscript.language
import subscript.Predef._

import scala.collection.mutable

import subscript.vm.N_code_eventhandling

import javafx.event.{EventHandler, ActionEvent, Event}
import javafx.beans.property.ObjectProperty

import scalafx.Includes._
import scalafx.{event => sfx}


package object subscriptfx {
  /**
   * Keeps track of the handlers registered on certain ObjectProperty[EventHandler] from scripts.
   * Once an event happens on that EventHandler, all the registered handlers are invoked.
   */
  object Listeners {
    val listeners = mutable.Map[ObjectProperty[_ <: EventHandler[_]], mutable.Set[Event => Unit]]()
    
    def listen[T <: EventHandler[_]](hp: ObjectProperty[T], h: Event => Unit) {
      val handlers = listeners.getOrElseUpdate(hp, {
        val hdlrs = mutable.Set[Event => Unit]()
        if (hp.getValue ne null) throw new IllegalStateException("You cannot set the `on` listeners both from a SubScript script and from a Scala code")
        hp.setValue(new EventHandler[Event] {override def handle(e: Event) {hdlrs.foreach(_(e))}}.asInstanceOf[T])
        hdlrs
      })

      handlers += h
    }

    def unlisten(hp: ObjectProperty[_ <: EventHandler[_]], h: Event => Unit) {
      listeners(hp) -= h
    }
  }

  implicit script..

    /**
     * Converts an ObjectProperty[EventHandler] to a script. This script
     * will wait for an event to happen on this EventHandler, then it will
     * have success with the result value set to the event that have happened.
     */
    op2script(handlerProp: ObjectProperty[_ <: EventHandler[_]]) =
      var event: sfx.Event = null
      @{
        val handler = {e: Event =>
          event = e
          there.codeExecutor.executeAA
        }

        there.onActivate   {Listeners.listen  (handlerProp, handler)}
        there.onDeactivate {Listeners.unlisten(handlerProp, handler)}
      }: {..}
      ^event

    /**
     * Experimental: converts an object with an onAction event handler to a script.
     * `act` is equivalent to `act.onAction`.
     */
    act2script(act: {def onAction: ObjectProperty[EventHandler[ActionEvent]]}) = act.onAction

  /**
   * Invokes `task` on the GUI thread. Waits for the code to be executed,
   * then has success.
   */
  script gui(task: => Unit) = SSPlatform.runAndWait(task)


  // Interesting thing below: the foo code doens't give the compile-time
  // error, but the bar code does (when handler is set to EventHandler[Event]).
  // Maybe due to the fact that foo calls it from a macro.

  // script foo(hp: ObjectProperty[_ <: EventHandler[_]]) =
  //   {!hp.setValue(handler)!}

  // def bar[T <: EventHandler[_]](hp: ObjectProperty[T]) =
  //   {hp.setValue(handler.asInstanceOf[T])}
}