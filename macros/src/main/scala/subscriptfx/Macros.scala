package subscriptfx

// Scala macros
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

// FX
import javafx.event.EventHandler
import javafx.beans.property.ObjectProperty

// SubScript
import subscript.vm.Script


object Macros {

  implicit def jfxe2sfxe[T](target: ObjectProperty[T]): Script[Any] = macro jfxe2sfxeImpl[T]
  def jfxe2sfxeImpl[T: c.WeakTypeTag](c: Context)(target: c.Expr[ObjectProperty[T]]): c.Expr[Script[Any]] = {
    import c.universe._

    val tpe = weakTypeOf[T]  // typeTag[EventHandler[_ >: X]]; X is what we need to find
    println(tpe)
    val bound = tpe     // EventHandler[_ >: X]
      .asInstanceOf[ExistentialType]
      .quantified.head.typeSignature  // _ >: X
      .asInstanceOf[TypeBounds].lo    // X
    
    c.Expr[Script[Any]] {q"""subscriptfx.op2script[$bound, scalafx.event.Event with scalafx.delegate.SFXDelegate[$bound]](${target.tree})"""}
  }

}