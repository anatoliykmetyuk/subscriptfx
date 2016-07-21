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
  val searchButton = new Button("Go"    ) {enabled       = false; focusable = false}
  val cancelButton = new Button("Cancel") {enabled       = false; focusable = false}
  val   exitButton = new Button("Exit"  ) {enabled       = false; focusable = false}
  val searchLabel  = new Label("Search")  {preferredSize = new Dimension(45,26)}
  val searchTF     = new TextField        {preferredSize = new Dimension(100, 26)}
  
  val top          = new MainFrame {
    title          = "LookupFrame - Subscript"
    location       = new Point    (100,100)
    preferredSize  = new Dimension(500,300)
    contents       = new BorderPanel {
      add(new FlowPanel(searchLabel, searchTF, searchButton, cancelButton, exitButton), BorderPanel.Position.North) 
      add(outputTA, BorderPanel.Position.Center) 
    }
  }
  
  top.listenTo (searchTF.keys)
  val f = top.peer.getRootPane().getParent().asInstanceOf[javax.swing.JFrame]
  f.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE) // TBD: does not seem to work on MacOS
  
  def sleep(duration_ms: Long) = try {Thread.sleep(duration_ms)}
                                 catch {case e: InterruptedException => /*println("sleep interrupted")*/}
  def confirmExit: Boolean = Dialog.showConfirmation(null, "Are you sure?", "About to exit")==Dialog.Result.Yes
  
  override def  live = subscript.DSL._execute(liveScript)

  implicit script vkey(??k: Key.Value) = vkey2: top, ??k

  script..

    liveScript        = ... searchSequence || doExit

    searchCommand     = searchButton + Key.Enter
    cancelCommand     = cancelButton + Key.Escape 
    exitCommand       =   exitButton + windowClosing: top
    
    doExit            =   exitCommand @gui: {!confirmExit!} ~~(r:Boolean)~~> while (!r)
    cancelSearch      = cancelCommand showCanceledText
    
    searchSequence    = guard: searchTF, !searchTF.text.trim.isEmpty
                        searchCommand
                        showSearchingText searchInDatabase showSearchResults / cancelSearch
    
    showSearchingText = @gui: let outputTA.text = "Searching: "+searchTF.text
    showCanceledText  = @gui: let outputTA.text = "Searching Canceled"
    showSearchResults = @gui: let outputTA.text = "Results: 1, 2, 3"

    searchInDatabase  = {* sleep(5000) *} || progressMonitor
    
    progressMonitor   = ... @gui: {outputTA.text+=here.pass} do* sleep(200)
    
}