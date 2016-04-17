import subscript.language
import subscript.Predef._

import scala.collection.mutable

import subscript.vm.N_code_eventhandling

import javafx.event.{EventHandler, ActionEvent, Event}
import javafx.beans.property.ObjectProperty


package object subscriptfx {
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
    op2script(handlerProp: ObjectProperty[_ <: EventHandler[_]]) =
      var event: Event = null
      @{
        val handler = {e: Event =>
          event = e
          there.codeExecutor.executeAA
        }

        there.onActivate   {Listeners.listen  (handlerProp, handler)}
        there.onDeactivate {Listeners.unlisten(handlerProp, handler)}
      }: {..}
      ^event

    act2script(act: {def onAction: ObjectProperty[EventHandler[ActionEvent]]}) = act.onAction

  script gui(task: => Unit) = SSPlatform.runAndWait(task)

  // Interesting thing below: the foo code doens't give the compile-time
  // error, but the bar code does (when handler is set to EventHandler[Event]).
  // Maybe due to the fact that foo calls it from a macro.

  // script foo(hp: ObjectProperty[_ <: EventHandler[_]]) =
  //   {!hp.setValue(handler)!}

  // def bar[T <: EventHandler[_]](hp: ObjectProperty[T]) =
  //   {hp.setValue(handler.asInstanceOf[T])}
}