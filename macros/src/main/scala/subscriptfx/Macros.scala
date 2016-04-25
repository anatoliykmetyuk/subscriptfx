package subscriptfx

// Scala macros
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

// FX
import javafx.event.EventHandler
import javafx.beans.property.ObjectProperty

// SubScript
import subscript.vm.Script
import subscript.vm.model.template.TemplateNode

object Macros {

  // This macro is supposed to convert an `ObjectProperty[EventHandler[_ <: E]]` (E = JavaFX event)
  // into a script. The main conversion work is done in `subscriptfx.op2script` script.
  // The macro is needed to capture the lower type bound `E` of `ObjectProperty[EventHandler[_ <: E]]`.
  // So far, I can't see how to do that without a macro.
  // After capturing the type, it is passed to `op2script` and the rest of the work is done there.
  implicit def jfxe2sfxe[T](target: ObjectProperty[T]): TemplateNode.Child = macro jfxe2sfxeImpl[T]
  def jfxe2sfxeImpl[T: c.WeakTypeTag](c: Context)(target: c.Expr[ObjectProperty[T]]): c.Expr[TemplateNode.Child] = {
    import c.universe._

    // Get the type of this ObjectProperty's EventHandler
    val tpe = weakTypeOf[T]  // Either EventHandler[_ >: E] or EventHandler[E]. We need to find `E`.

    val bound: Type = tpe match {
      case t: ExistentialType => t      // `tpe` is EventHandler[_ >: E]
        .quantified.head.typeSignature  // _ >: E
        .asInstanceOf[TypeBounds].lo    // E

      case t => t.typeArgs.head         // `tpe` is EventHandler[E]
    }

    // JavaFX event type `E` is found. Continue the work in `subscriptfx.op2script` while telling it this type.
    // To do this, return the call to `subscriptfx.op2script` from a macro as a tree.
    val callTree = q"subscriptfx.op2script[$bound, scalafx.event.Event with scalafx.delegate.SFXDelegate[$bound]](${target.tree}.asInstanceOf[javafx.beans.property.ObjectProperty[javafx.event.EventHandler[$bound]]])"
    c.Expr[TemplateNode.Child] {q"""subscript.DSL._maybeCall("macro_call", (here: subscript.vm.N_call[Any]) => $callTree)"""}
  }

}