package subscriptfx

import subscript.language
import subscript.Predef._
import subscript.objectalgebra._

import javafx.{application => jfxa, event => jfxe, stage => jfxs}

import scalafx.application.{JFXApp, Platform}
import scalafx.stage.Stage

/** Stage process complements ScalaFX Stage. */
trait StageP extends SSProcess {this: Stage =>

  val closeTrigger = new Trigger

  onCloseRequest = new jfxe.EventHandler[jfxs.WindowEvent] {
    override def handle(e: jfxs.WindowEvent) {
      closeTrigger.trigger
    }
  }

  override script lifecycle =
    SSPlatform.runAndWait: this.show()
    super.lifecycle / closeTrigger
}