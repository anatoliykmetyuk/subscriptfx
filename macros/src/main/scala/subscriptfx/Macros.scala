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

  implicit def jfxe2sfxe[T](target: ObjectProperty[T]): subscript.vm.model.template.TemplateNode.Child = macro jfxe2sfxeImpl[T]
  def jfxe2sfxeImpl[T: c.WeakTypeTag](c: Context)(target: c.Expr[ObjectProperty[T]]): c.Expr[subscript.vm.model.template.TemplateNode.Child] = {
    import c.universe._

    val tpe = weakTypeOf[T]  // typeTag[EventHandler[_ >: X]]; X is what we need to find
    println(tpe)

    val bound: Type = tpe match {
      case t: ExistentialType => t
        .quantified.head.typeSignature  // _ >: X
        .asInstanceOf[TypeBounds].lo    // X

      case t => t.typeArgs.head
    }

    val callTree = q"subscriptfx.op2script[$bound, scalafx.event.Event with scalafx.delegate.SFXDelegate[$bound]](${target.tree}.asInstanceOf[javafx.beans.property.ObjectProperty[javafx.event.EventHandler[_ >: $bound]]])"
    // subscript.DSL._maybeCall(${calleeName.tree}, $exprTree)
    c.Expr[subscript.vm.model.template.TemplateNode.Child] {q"""subscript.DSL._maybeCall("macro_call", (here: subscript.vm.N_call[Any]) => $callTree)"""}
  }

}