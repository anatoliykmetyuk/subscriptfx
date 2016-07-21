package subscriptfx.demos

import subscript.language
import subscript.Predef._
import subscript.objectalgebra._
import subscriptfx._
import subscriptfx.Macros._

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

import scalafx.scene.input.{KeyCode, KeyEvent}


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
  }
  
  val txfUrl = new TextField {
    text = browser.engine.location.value
    hgrow = Priority.Always
    vgrow = Priority.Never
  }

  scene = new Scene {
    fill = Color.LightGray
    root = new BorderPane {
      hgrow = Priority.Always
      vgrow = Priority.Always
      top = txfUrl
      center = browser
    }
  }

  script..
    live =;&&
      @gui: let browser.engine.load("http://code.google.com/p/scalafx/")
      callbacks...

    callbacks =;+
      browser.onAlert             ~~(e: WebEvent[_])~~> println("onAlert: " + e)
      browser.onStatusChanged     ~~(e: WebEvent[_])~~> println("onStatusChanged: " + e)
      browser.onResized           ~~(e: WebEvent[_])~~> println("onResized: " + e)
      browser.onVisibilityChanged ~~(e: WebEvent[_])~~> println("onVisibilityChanged: " + e)

      txfUrl @gui: let browser.engine.load(txfUrl.text.get)
}
