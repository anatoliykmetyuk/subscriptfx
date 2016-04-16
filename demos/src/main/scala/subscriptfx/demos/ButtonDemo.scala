package subscriptfx.demos

import subscript.language
import subscript.Predef._
import subscript.objectalgebra._

import javafx.event.{EventHandler, ActionEvent}

import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Label, Button}
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.event.EventIncludes._

import scalafx.stage.Stage

import subscriptfx._

// Main SubScriptFX Application
object ButtonDemo extends SSFXApp {
  val stg = new ButtonDemoStage  // GUI creation must be done from the event thread! HelloWorld is initialized from event thread.

  script live = stg  // MainStage is a process object, it will be implicitly converted to a script
}

class ButtonDemoStage extends Stage with StageP {
  // View
  val lbl  = new Label ("Hello World!")
  val btn1 = new Button("Button 1"    ) {disable = true}
  val btn2 = new Button("Button 2"    ) {disable = true}

  scene = new Scene {
    root = new BorderPane {
      padding = Insets(25)
      center  = lbl
      bottom  = new GridPane {
        add(btn1, 0, 0)
        add(btn2, 1, 0)
      }
      //   onAction = handle {lbl.text = scala.util.Random.alphanumeric.take(10).mkString}
      // }
    }
  }

  // btn1.disable <== !btn2.disable
  // btn2.onAction = handle {btn2.disable = true }
  // btn1.onAction = handle {btn2.disable = false}

  implicit class ScriptBtn(btn: Button) extends SSProcess {
    script live =
      gui: {btn.disable = false}
      @{
        btn.onAction = (handle {there.codeExecutor.executeAA
      }: EventHandler[ActionEvent])}: {..}
      gui: {btn.disable = true}
  }

  // Control
  script live =
    ScriptBtn(btn1) ScriptBtn(btn2) ...


}
