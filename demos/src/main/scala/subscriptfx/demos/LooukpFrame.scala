package subscriptfx.demos

import subscript.language
import subscript.Predef._
import subscript.objectalgebra._

import javafx.{event => jfx}
import javafx.event.{EventHandler, ActionEvent, Event}
import javafx.beans.property.ObjectProperty

import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, GridPane, FlowPane}
import scalafx.event.EventIncludes._
import scalafx.Includes._

import scalafx.stage.Stage
import scalafx.{event => sfx}

import javafx.scene.input.MouseEvent

import subscriptfx._
import subscriptfx.Macros._

object LookupFrame extends SSFXApp {
  val stg = new LookupFrameStage

  script live = stg
}

class LookupFrameStage extends Stage with StageP {
  title  = "LookupFrame - Subscript"
  x      = 100
  y      = 100
  width  = 500
  height = 300

  val outputTA     = new TextArea        {editable      = false}
  val searchButton = new Button("Go")    {disable       = false}
  val searchLabel  = new Label("Search")
  val searchTF     = new TextField

  scene = new Scene {
    root = new BorderPane {
      center = outputTA
      top    = new FlowPane(2, 0) { // Horizontal gap and vertical gap between children are passed to the constructor
        padding = Insets(10)  // Pad that amount of pixels from each side
        alignment = Pos.Center  // Place the elements in the center, as in the original Swing LookupFrame
        children ++= Seq(searchLabel, searchTF, searchButton)
      }
    }
  }

  script..
    live = {..}

}
