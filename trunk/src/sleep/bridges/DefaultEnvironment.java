package sleep.bridges;
 
import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

public class DefaultEnvironment implements Loadable, Environment
{
    public void scriptUnloaded (ScriptInstance si)
    {
    }

    public void scriptLoaded (ScriptInstance si)
    {
        Hashtable env = si.getScriptEnvironment().getEnvironment(); 
        env.put("sub",    this);
        env.put("inline", this);
    }

    public void bindFunction(ScriptInstance si, String type, String name, Block code)
    {
        Hashtable env = si.getScriptEnvironment().getEnvironment(); 

        if (type.equals("sub"))
        {
           env.put("&"+name, new SleepClosure(si, code));
        }
        else if (type.equals("inline"))
        {
           env.put("^&"+name, code); /* add an inline function, very harmless */
        }
    }
}
