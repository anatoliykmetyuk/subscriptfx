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
import scalafx.Includes._

import scalafx.stage.Stage
import scalafx.{event => sfx}

import scalafx.scene.input.MouseEvent
import scalafx.scene.input.{KeyCode, KeyEvent}

import subscriptfx._
import subscriptfx.Macros._

object LookupFrame2 extends SSFXApp {
  val stg = new LookupFrame2Stage

  script live = stg
}

class LookupFrame2Stage extends Stage with StageP {
  title  = "LookupFrame2 - Subscript"
  x      = 100
  y      = 100
  width  = 500
  height = 300

  val outputTA     = new TextArea         {editable      = false}
  val searchButton = new Button("Go"    ) {disable       = true } //; focusable = false}
  val cancelButton = new Button("Cancel") {disable       = true } //; focusable = false}
  val   exitButton = new Button("Exit"  ) {disable       = true } //; focusable = false}
  val searchLabel  = new Label("Search")
  val searchTF     = new TextField
  
  scene = new Scene {
    root = new BorderPane {
      center = outputTA
      top    = new FlowPane(2, 0) { // Horizontal gap and vertical gap between children are passed to the constructor
        padding = Insets(10)  // Pad that amount of pixels from each side
        alignment = Pos.Center  // Place the elements in the center, as in the original Swing LookupFrame
        children ++= Seq(searchLabel, searchTF, searchButton, cancelButton, exitButton)
      }
    }
  }
  
  // def confirmExit: Boolean = Dialog.showConfirmation(null, "Are you sure?", "About to exit")==Dialog.Result.Yes

  implicit script keyCode2script(k: KeyCode) =
    keyCode2scriptBuilder(searchTF, k, KeyEvent.KeyReleased)

  script..
    live              = ... searchSequence //|| doExit

    searchCommand     = searchButton + KeyCode.Enter
    cancelCommand     = cancelButton + KeyCode.Escape 
    // exitCommand       =   exitButton + windowClosing: top
    
    // doExit            =   exitCommand @gui: {!confirmExit!} ~~(r:Boolean)~~> while (!r)
    cancelSearch      = cancelCommand showCanceledText
    
    searchSequence    = guard: searchTF, !searchTF.text().trim.isEmpty, KeyEvent.KeyTyped
                        searchCommand
                        showSearchingText searchInDatabase showSearchResults / cancelSearch
    
    showSearchingText = gui: {outputTA.text = "Searching: "+searchTF.text()+"\n"}
    showCanceledText  = gui: {outputTA.text = "Searching Canceled"}
    showSearchResults = gui: {outputTA.text = "Results: 1, 2, 3"}

    searchInDatabase  = sleep: 5000 || progressMonitor
    
    progressMonitor   = ... gui: {outputTA.text = outputTA.text() + here.pass + " "} sleep: 200
    
}