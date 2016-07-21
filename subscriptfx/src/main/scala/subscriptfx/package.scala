import subscript.language
import subscript.Predef._
import subscript.objectalgebra.Trigger

import scala.collection.mutable

import subscript.vm.{N_code_eventhandling, Script}

import javafx.event.{EventHandler, ActionEvent, Event}
import javafx.beans.property.ObjectProperty
import javafx.{event => jfx}

import scalafx.Includes._
import scalafx.{event => sfx}
import scalafx.delegate.SFXDelegate
import scalafx.event.{EventHandlerDelegate, EventType}
import scalafx.scene.input.{KeyCode, KeyEvent}

import subscriptfx.Macros.jfxe2sfxe


package object subscriptfx {

  // Keeps track of the handlers registered on certain ObjectProperty[EventHandler] from scripts.
  // Once an event happens on that EventHandler, all the registered handlers are invoked.
  private[subscriptfx] object Listeners {
    // Every event-handling property such as `onAction` gets associated with a set of handlers to be invoked when it happens
    val listeners = mutable.Map[ObjectProperty[_ <: EventHandler[_]], mutable.Set[(_ <: jfx.Event) => Unit]]()
    
    def listen[J <: jfx.Event](hp: ObjectProperty[EventHandler[J]], h: J => Unit) {
      // Get the set of listeners associated with `hp`. Create it if not present.
      val handlers: mutable.Set[J => Unit] = listeners.getOrElseUpdate(hp, {
        val hdlrs = mutable.Set[J => Unit]()
        if (hp.getValue ne null) throw new IllegalStateException("You cannot set the `on` listeners both from a SubScript script and from a Scala code")  // Just in case user has already set this property manualy. We don't want to make him wonder why does his behaviour not work.
        hp.setValue(new EventHandler[J] {override def handle(e: J) {hdlrs.foreach(_(e))}})
        hdlrs.asInstanceOf[mutable.Set[(_ <: jfx.Event) => Unit]]  // Too bad Set is invariant in its type
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
          event = e  // Use a ScalaFX's implicit conversion `J => S` to wrap JavaFX event `e` into a ScalaFX event
          there.codeExecutor.executeAA
        }

        there.onActivate   {Listeners.listen  [J](handlerProp, handler)}
        there.onDeactivate {Listeners.unlisten[J](handlerProp, handler)}
      }: {..}
      ^event
  
    /**
     * Invokes `task` on the GUI thread. Waits for the code to be executed,
     * then has success.
     */
    gui(task: => Unit) = SSPlatform.runAndWait(task)

    /**
     * ScalaFX understands many more events than Swing. So it is necessary to specify
     * the events the Guard is interested in explicitly via `trigger` parameter, so that
     * it doesn't interfere with other application event handlers. For example, on a text field,
     * we may be interested only in the KeyTyped events when the user typed a key, but not an
     * event when the user releases the Enter key.
     * 
     * See more user-friendly version of this method below.
     */
    guard(comp: EventHandlerDelegate, test: => Boolean, trigger: EventHandlerDelegate => Script[Any] = eventTrigger(_, Event.ANY, (_: sfx.Event) => true)): Any =
      if test then ..? else ...
      trigger(comp)

    /**
     * Allows you to specify the events you're interested in directly via EventType class
     * instead of the trigger function.
     */
    guard[J <: jfx.Event, S <: SFXDelegate[J]](comp: EventHandlerDelegate, test: => Boolean, eType: sfx.EventType[J])(implicit jfs2sfx: J => S): Any =
      guard: comp, test, {c: EventHandlerDelegate => eventTrigger(c, eType, (_: S) => true)}


  type HasActionAndDisable = {
    def onAction: ObjectProperty[EventHandler[ActionEvent]]
    def disable_=(v: Boolean): Unit
  }
  /**
   * Experimental: converts an object with an onAction event handler to a script.
   * Disables the object if it is not listened to.
   */
  implicit script act2script(act: HasActionAndDisable) =
    @{
      there.onDeactivate {if (Listeners.listeners(act.onAction).isEmpty) act.disable_=(true)}
      there.onActivate   {act.disable_=(false)}
    }: act.onAction


  /**
   * Used to construct triggers that trigger on certain events.
   * 
   * @param obj the object that will listen to the events.
   * @param eType the type of the expected event. Usually the companion objects of the target event class have instances of the EventType class.
   * @param predicate defines on which events to trigger.
   * 
   * @return a Trigger implicitly converted to Script[Any]. Will trigger if the event of the
   * desired type arrives and if predicate(event) is true.
   */
  def eventTrigger[J <: jfx.Event, S <: SFXDelegate[J]](obj: EventHandlerDelegate, eType: sfx.EventType[J], predicate: S => Boolean)(implicit jfx2sfx: J => S): Script[Any] = {
    val trigger = new Trigger
    lazy val handler: EventHandler[J] = {e: S => if (predicate(e)) {
      trigger.trigger(e)
      obj.removeEventHandler[J](eType, handler)
    }}
    
    obj.addEventHandler[J](eType, handler)
    trigger
  }

  /**
   * Used to build implicit conversions from KeyCode to script, so that it is possible to write
   * things like KeyCode.Enter in a script.
   * 
   * @param obj an object that will listen to the keyboard.
   * @param k the particular key to listen to, can be obtained from the KeyCode object.
   * @param eType one of the fields defined in the KeyEvent object.
   */
  def keyCode2scriptBuilder(obj: EventHandlerDelegate, k: KeyCode, eType: EventType[javafx.scene.input.KeyEvent]): Script[Any] =
    eventTrigger(obj, eType, {e: KeyEvent => e.code == k})


  // Interesting thing below: the foo code doens't give the compile-time
  // error, but the bar code does (when handler is set to EventHandler[Event]).
  // Maybe due to the fact that foo calls it from a macro.

  // script foo(hp: ObjectProperty[_ <: EventHandler[_]]) =
  //   {!hp.setValue(handler)!}

  // def bar[T <: EventHandler[_]](hp: ObjectProperty[T]) =
  //   {hp.setValue(handler.asInstanceOf[T])}
}