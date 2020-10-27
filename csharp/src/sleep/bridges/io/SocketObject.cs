/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
 using System;
 using java = biz.ritter.javapi;

using sleep.runtime;
using sleep.bridges;

namespace sleep.bridges.io{

public class SocketObject : IOObject
{
   protected java.net.Socket socket;

   /** returns the socket used for this connection */
   public Object getSource()
   {
      return socket;
   }

   public void open(SocketHandler paramsJ, ScriptEnvironment env)
   {
      try
      {
         socket = new java.net.Socket();
         
         if (paramsJ.laddr != null)
         {
            socket.bind(new InetSocketAddress(paramsJ.laddr, paramsJ.lport));
         }

         socket.connect(new InetSocketAddress(paramsJ.host, paramsJ.port), paramsJ.timeout);

         socket.setSoLinger(true, paramsJ.linger);

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

   private static java.util.Map<Object,Object> servers;

   private static java.net.ServerSocket getServerSocket(int port, SocketHandler paramsJ) //throws Exception
   {
      String key = port + "";

      if (servers == null)
      {
         servers = java.util.Collections.synchronizedMap(new java.util.HashMap<Object,Object>
         ());
      }

      java.net.ServerSocket server = null;

      if (servers.containsKey(key))
      {
         server = (ServerSocket)servers.get(key);
      }
      else
      {
         server = new ServerSocket(port, paramsJ.backlog, paramsJ.laddr != null ? InetAddress.getByName(paramsJ.laddr) : null);
         servers.put(key, server);
      }

      return server;
   }
 
   public void listen(SocketHandler paramsJ, ScriptEnvironment env)
   {
      ServerSocket server = null;

      try
      {
         server = getServerSocket(paramsJ.port, paramsJ);
         server.setSoTimeout(paramsJ.timeout);
        
         socket = server.accept();
         socket.setSoLinger(true, paramsJ.linger);

         paramsJ.callback.setValue(SleepUtils.getScalar(socket.getInetAddress().getHostAddress()));

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

    public static readonly int LISTEN_FUNCTION  = 1;
    public static readonly int CONNECT_FUNCTION = 2;

    public class SocketHandler : java.lang.Runnable
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
}