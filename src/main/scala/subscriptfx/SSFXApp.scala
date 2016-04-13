package subscriptfx

import subscript.language
import subscript.Predef._
import subscript.SubScriptApplication
import subscript.objectalgebra._

import scala.collection.mutable.{Buffer, ListBuffer}

import javafx.{application => jfxa}

import scalafx.application.JFXApp

object SSFXApp {
  def app = JFXApp.ACTIVE_APP.asInstanceOf[SSFXApp]
}

trait SSFXApp extends JFXApp with SSProcess {

  private val subClassInitCode1 = new ListBuffer[() => Unit]

  override def delayedInit(x: => Unit) {
    subClassInitCode1 += (() => x)
  }

  override def main(args: Array[String]) {
    JFXApp.ACTIVE_APP = this
    // arguments = args
    // Put any further non-essential initialization here.
    /* Launch the JFX application.
    */
    jfxa.Application.launch(classOf[AppHelper], args: _*)
  }

  def initialize(): Unit = for (initCode <- subClassInitCode1) initCode()

}

private[subscriptfx] class AppHelper extends javafx.application.Application {
  def start(stage: javafx.stage.Stage) {
    JFXApp.STAGE = stage
    SSFXApp.app.initialize()

    runScript(SSFXApp.app)

    // if (JFXApp.AUTO_SHOW) {
    //   JFXApp.STAGE.show()
    // }
  }

  override def stop() {
    JFXApp.ACTIVE_APP.stopApp()
  }
}
