import subscript.language
import subscript.Predef._

import scala.collection.mutable

import subscript.vm.N_code_eventhandling

import javafx.event.{EventHandler, ActionEvent, Event}
import javafx.beans.property.ObjectProperty
import javafx.{event => jfx}

import scalafx.Includes._
import scalafx.{event => sfx}
import scalafx.delegate.SFXDelegate

import subscriptfx.Macros.jfxe2sfxe


package object subscriptfx {

  // Keeps track of the handlers registered on certain ObjectProperty[EventHandler] from scripts.
  // Once an event happens on that EventHandler, all the registered handlers are invoked.
  private[subscriptfx] object Listeners {
    // Every event-handling property such as `onAction` gets associated with a set of handlers to be invoked when it happens
    val listeners = mutable.Map[ObjectProperty[_ <: EventHandler[_]], mutable.Set[(_ <: jfx.Event) => Unit]]()
    
    def listen[J <: jfx.Event](hp: ObjectProperty[EventHandler[J]], h: J => Unit) {
      val handlers: mutable.Set[J => Unit] = listeners.getOrElseUpdate(hp, {
        val hdlrs = mutable.Set[J => Unit]()
        if (hp.getValue ne null) throw new IllegalStateException("You cannot set the `on` listeners both from a SubScript script and from a Scala code")
        hp.setValue(new EventHandler[J] {override def handle(e: J) {hdlrs.foreach(_(e))}})
        hdlrs.asInstanceOf[mutable.Set[(_ <: jfx.Event) => Unit]]
      }).asInstanceOf[mutable.Set[J => Unit]]

      handlers += h
    }

    def unlisten[J <: jfx.Event](hp: ObjectProperty[EventHandler[J]], h: J => Unit) {
      listeners(hp).asInstanceOf[mutable.Set[J => Unit]] -= h
    }
  }

  script..

    /**
     * Converts an ObjectProperty[EventHandler] to a script. This script
     * will wait for an event to happen on this EventHandler, then it will
     * have success with the result value set to the event that have happened.
     */
    op2script[J <: jfx.Event, S <: sfx.Event with SFXDelegate[J]](handlerProp: ObjectProperty[EventHandler[J]])(implicit c: J => S) =
      var event: S = null.asInstanceOf[S]
      @{
        val handler = {e: J =>
          event = e
          there.codeExecutor.executeAA
        }

        there.onActivate   {Listeners.listen  [J](handlerProp, handler)}
        there.onDeactivate {Listeners.unlisten[J](handlerProp, handler)}
      }: {..}
      ^event

    /**
     * Experimental: converts an object with an onAction event handler to a script.
     * `act` is equivalent to `act.onAction`.
     */
  
  implicit script act2script(act: {def onAction: ObjectProperty[EventHandler[ActionEvent]]}) = act.onAction

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