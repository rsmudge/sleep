/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package sleep.bridges.io;

import java.io.*;
import java.net.*;
import sleep.runtime.*;
import sleep.bridges.SleepClosure;

import java.util.*;

public class SocketObject extends IOObject
{
   protected Socket socket;

   /** returns the socket used for this connection */
   public Object getSource()
   {
      return socket;
   }

   public void open(SocketHandler params, ScriptEnvironment env)
   {
      try
      {
         socket = new Socket();
         
         if (params.laddr != null)
         {
            socket.bind(new InetSocketAddress(params.laddr, params.lport));
         }

         socket.connect(new InetSocketAddress(params.host, params.port), params.timeout);

         socket.setSoLinger(true, params.linger);

         openRead(socket.getInputStream());
         openWrite(socket.getOutputStream());
      }
      catch (Exception ex)
      {
         env.flagError(ex);
      }
   }

   /** releases the socket binding for the specified port */
   public static void release(int port)
   {
      String key = port + "";
      
      ServerSocket temp = null;
      if (servers != null && servers.containsKey(key))
      {
         temp = (ServerSocket)servers.get(key);
         servers.remove(key);
 
         try
         {
            temp.close();
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
   }

   private static Map servers;

   private static ServerSocket getServerSocket(int port, SocketHandler params) throws Exception
   {
      String key = port + "";

      if (servers == null)
      {
         servers = Collections.synchronizedMap(new HashMap());
      }

      ServerSocket server = null;

      if (servers.containsKey(key))
      {
         server = (ServerSocket)servers.get(key);
      }
      else
      {
         server = new ServerSocket(port, params.backlog, params.laddr != null ? InetAddress.getByName(params.laddr) : null);
         servers.put(key, server);
      }

      return server;
   }
 
   public void listen(SocketHandler params, ScriptEnvironment env)
   {
      ServerSocket server = null;

      try
      {
         server = getServerSocket(params.port, params);
         server.setSoTimeout(params.timeout);
        
         socket = server.accept();
         socket.setSoLinger(true, params.linger);

         params.callback.setValue(SleepUtils.getScalar(socket.getInetAddress().getHostAddress()));

         openRead(socket.getInputStream());
         openWrite(socket.getOutputStream());

         return;
      }
      catch (Exception ex)
      {
         env.flagError(ex);
      }
   }

   public void close()
   {
      try
      {
         socket.close();
      }
      catch (Exception ex) { }

      super.close();
   }

    public static final int LISTEN_FUNCTION  = 1;
    public static final int CONNECT_FUNCTION = 2;

    public static class SocketHandler implements Runnable
    {
       public ScriptInstance script;
       public SleepClosure   function;
       public SocketObject   socket;

       public int            port;
       public int            timeout;
       public String         host;
       public Scalar         callback;

       public int            type;
       public String         laddr;
       public int            lport;
       public int            linger;
       public int            backlog;

       public void start()
       {
          if (function != null)
          {
             socket.setThread(new Thread(this));
             socket.getThread().start();
          }
          else
          {
             run();
          }
       }

       public void run()
       {
          if (type == LISTEN_FUNCTION)
          {
             socket.listen(this, script.getScriptEnvironment());
          }
          else
          {
             socket.open(this, script.getScriptEnvironment());
          }

          if (function != null)
          {
             Stack  args  = new Stack();
             args.push(SleepUtils.getScalar(socket));
             function.callClosure("&callback", script, args);
          }
       }
    }
}
