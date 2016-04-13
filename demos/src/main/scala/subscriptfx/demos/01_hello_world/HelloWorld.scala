package subscriptfx.demos.helloworld

import subscript.language
import subscript.Predef._

import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.BorderPane

import scalafx.stage.Stage

import subscriptfx._

// Main SubScriptFX Application
object HelloWorld extends SSFXApp {
  val stg = new MainStage  // GUI creation must be done from the event thread! HelloWorld is initialized from event thread.

  script live = stg  // MainStage is a process object, it will be implicitly converted to a script
}

class MainStage extends Stage with StageP {
  // View
  scene = new Scene {
    root = new BorderPane {
      padding = Insets(25)
      center = new Label("Hello World!")
    }
  }

  // Control
  script live = {..}
}
