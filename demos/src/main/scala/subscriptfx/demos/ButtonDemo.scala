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

  script..
    live = handling...

    handling =;+
      btn1 ~~(_)~~> println("Button 1 is pressed")
      btn2 ~~(_)~~> println("Button 2 is pressed")
      btn1.onMouseEntered ~~(_)~~> println("Mouse entered in button 1")
      btn2.onMouseExited  ~~(_)~~> println("Mouse left button 2")


}
