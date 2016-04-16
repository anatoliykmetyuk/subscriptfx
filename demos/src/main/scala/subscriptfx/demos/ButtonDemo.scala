package subscriptfx.demos

import subscript.language
import subscript.Predef._
import subscript.objectalgebra._

import javafx.event.{EventHandler, ActionEvent, Event}
import javafx.beans.property.ObjectProperty

import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Label, Button}
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.event.EventIncludes._

import scalafx.stage.Stage

import javafx.scene.input.MouseEvent

import subscriptfx._

// Main SubScriptFX Application
object ButtonDemo extends SSFXApp {
  val stg = new ButtonDemoStage  // GUI creation must be done from the event thread! HelloWorld is initialized from event thread.

  script live = stg  // MainStage is a process object, it will be implicitly converted to a script
}

class ButtonDemoStage extends Stage with StageP {
  // View
  val lbl  = new Label ("Hello World!")
  val btn1 = new Button("Button 1"    ) {disable = false}
  val btn2 = new Button("Button 2"    ) {disable = false}

  scene = new Scene {
    root = new BorderPane {
      padding = Insets(25)
      center  = lbl
      bottom  = new GridPane {
        add(btn1, 0, 0)
        add(btn2, 1, 0)
      }
    }
  }

  trait FXProcess extends SSProcess {
    override script..
      live = action

    script action: Any
  }

  class ScriptBtn(btn: Button) extends FXProcess {

    // `disable` belongs to Node, which is the parent of all the elements.
    // `disable` something and it will stop reacting on anything, not only clicks.
    // override script lifecycle =
    //   gui: {btn.disable = false} super.lifecycle^ gui: {btn.disable = true}

    script..
      genericTriggerFor[T <: Event](handlerProp: ObjectProperty[EventHandler[T]]) =
        var event: T = null.asInstanceOf[T]
        @{handlerProp.setValue(new EventHandler[T] {
          override def handle(e: T) {
            event = e
            there.codeExecutor.executeAA
          }
        })}: {..}
        ^event


      action = genericTriggerFor[ActionEvent](btn.onAction)
      mousePressed = genericTriggerFor[MouseEvent](btn.onMousePressed.asInstanceOf[ObjectProperty[EventHandler[MouseEvent]]])
      mouseEntered = genericTriggerFor[MouseEvent](btn.onMouseEntered.asInstanceOf[ObjectProperty[EventHandler[MouseEvent]]])

      // and so on...
  }

  val b1 = new ScriptBtn(btn1)
  val b2 = new ScriptBtn(btn2)

  // Control
  script..
    live = handling...

    handling =;+
      b1.mouseEntered ~~(e: MouseEvent)~~> println: e
      b2.mousePressed ~~(e: MouseEvent)~~> println: e


}
