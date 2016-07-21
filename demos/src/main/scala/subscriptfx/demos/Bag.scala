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
import scalafx.stage.WindowEvent
import scalafx.Includes._

import scalafx.stage.Stage
import scalafx.{event => sfx}

import scalafx.scene.input.MouseEvent
import scalafx.scene.input.{KeyCode, KeyEvent}

import subscriptfx._
import subscriptfx.Macros._

object Bag extends SSFXApp {
  val stg = new BagStage

  script live = stg
}

class BagStage extends Stage with StageP {self =>
  title  = "Bag - Subscript"
  x      = 100
  y      = 100
  width  = 300
  height = 300
  
  val lA = new Label ("A")
  val lB = new Label ("B")
  val pA = new Button("+")    {disable = true}
  val pB = new Button("+")    {disable = true}
  val mA = new Button("-")    {disable = true}
  val mB = new Button("-")    {disable = true}
  val cA = new Label ("" )
  val cB = new Label ("" )
  val  X = new Button("Exit") {disable = true}

  val bagLabel = new Label("Bag")
  val outputTA = new TextArea {editable = false}
  
  
  scene = new Scene {
    root = new BorderPane {
      top    = new FlowPane(2, 0) {children ++= Seq(lA, pA, mA, cA)}
      center = new FlowPane(2, 0) {children ++= Seq(lB, pB, mB, cB)}
      bottom = outputTA
    }
  }
  
  var ca = 0
  var cb = 0
  def dA(d: Int) = {ca+=d; cA.text = ca.toString}
  def dB(d: Int) = {cb+=d; cB.text = cb.toString}

  script..
    live = bag

    bag: Any =;+ A [bag&ax]
                 B [bag&bx]
        
    A   = pA @gui: dA(+1)
    ax  = mA @gui: dA(-1)
    B   = pB @gui: dB(+1)
    bx  = mB @gui: dB(-1)
}
