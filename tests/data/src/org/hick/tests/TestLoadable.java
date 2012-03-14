package org.hick.tests;

import sleep.interfaces.*;
import sleep.runtime.*;

public class TestLoadable implements Loadable
{
   public void scriptLoaded(ScriptInstance si)
   {
      si.getScriptEnvironment().getEnvironment().put("&foo", new FooFunction());
   }

   public void scriptUnloaded(ScriptInstance si)
   {
   }
}
