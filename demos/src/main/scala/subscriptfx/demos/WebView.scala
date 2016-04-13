package subscriptfx.demos

import subscript.language
import subscript.Predef._
import subscriptfx._

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.web._

import scalafx.stage.Stage
import scalafx.application.Platform


object WebDemo extends SSFXApp {
  val stg = new WebDemoStage
  script live = stg
}

class WebDemoStage extends Stage with StageP {
  title  = "ScalaFX Web Demo"
  width  = 800
  height = 600

  val browser = new WebView {
    hgrow = Priority.Always
    vgrow = Priority.Always
    onAlert = (e: WebEvent[_]) => println("onAlert: " + e)
    onStatusChanged = (e: WebEvent[_]) => println("onStatusChanged: " + e)
    onResized = (e: WebEvent[_]) => println("onResized: " + e)
    onVisibilityChanged = (e: WebEvent[_]) => println("onVisibilityChanged: " + e)
  }

  val engine = browser.engine
  engine.load("http://code.google.com/p/scalafx/")
  
  val txfUrl = new TextField {
    text = engine.location.value
    hgrow = Priority.Always
    vgrow = Priority.Never
  }
  txfUrl.onAction = handle {engine.load(txfUrl.text.get)}


  scene = new Scene {
    fill = Color.LightGray
    root = new BorderPane {
      hgrow = Priority.Always
      vgrow = Priority.Always
      top = txfUrl
      center = browser
    }
  }

  script live = {..}
}
