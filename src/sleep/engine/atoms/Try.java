package sleep.engine.atoms;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.*;
import sleep.runtime.*;

public class Try extends Step
{
   Block owner, handler;
   String var;

   public Try (Block _owner, Block _handler, String _var)
   {
      owner   = _owner;
      handler = _handler;
      var     = _var;
   }

   public String toString(String prefix)
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append(prefix);
      buffer.append("[Try]\n");
      buffer.append(owner.toString(prefix + "   "));
      buffer.append(prefix);
      buffer.append("[Catch]: " + var + "\n");
      buffer.append(handler.toString(prefix + "   "));
      return buffer.toString();
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      int mark = e.markFrame();
      e.installExceptionHandler(owner, handler, var);
      Scalar o = owner.evaluate(e);
      e.cleanFrame(mark);
      return o;
   }
}



