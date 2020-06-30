/*
 *****************************************************************************
 *                       Confidentiality Information                         *
 *                                                                           *
 * This module is the confidential and proprietary information of            *
 * DBSentry Corp.; it is not to be copied, reproduced, or transmitted in any *
 * form, by any means, in whole or in part, nor is it to be used for any     *
 * purpose other than that for which it is expressly provided without the    *
 * written permission of DBSentry Corp.                                      *
 *                                                                           *
 * Copyright (c) 2004-2005 DBSentry Corp.  All Rights Reserved.              *
 *                                                                           *
 *****************************************************************************
 * $Id: FsServer.java,v 1.51 2006/09/12 06:13:58 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.filesystem.specs.FsDataSource;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.server.jxta.JxtaServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import java.net.InetAddress;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.JxtaServerPipe;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author 
 * @version 1.0
 * @description
 * @Date of creation: 
 * @Last Modfied by :Saurabh Gupta
 * @Last Modfied Date:21-03-2006
 */
public class FsServer {

  private Hashtable connectedUsers = null;

  private JxtaServerPipe jxtaServerPipe;

  private Logger logger;

  private ServerUtil serverUtil;

  private FsSocketServer socketServer;

  public static int SERVER_PORT;

  public FsServer() {

    try {
      File logFolder = new File("log");
      if (!logFolder.exists()) {
        logFolder.mkdir();
      }
      File file = new File("config/log4j.properties");
      if (file.exists()) {
        PropertyConfigurator.configureAndWatch(file.getAbsolutePath(), 2000);
      } else {
        System.exit(1);
      }

      this.logger = Logger.getLogger("ServerLogger");

      logger.debug("Instantiating FileSyncServer");

      logger.debug("Logger initialized successfully");

      JxtaServer jxtaServer = new JxtaServer(logger);
      jxtaServer.startJxta();

      FsServerControlRoom fsServerControlRoom = new FsServerControlRoom(this, logger);
      Thread fsServerControlRoomThread = new Thread(fsServerControlRoom);
      fsServerControlRoomThread.start();

      this.serverUtil = new ServerUtil(logger);
      this.connectedUsers = new Hashtable(10);
      this.jxtaServerPipe = jxtaServer.createServerPipe();

      this.socketServer = new FsSocketServer(jxtaServer.getSecureNetPeerGroup(), this.connectedUsers, logger);
      Thread socketServerThread = new Thread(socketServer);
      socketServerThread.start();

      startFsServer();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void startFsServer() {
    try {
      logger.info("Connecting to the file system");
      FsDataSource fsDataSource = getFsDataSource();

      if (fsDataSource != null) {
        logger.info("Connected to the file system");
        System.out.println("FileSync Server started successfully");
      } else {
        logger.info("Unable To connect the DataSource........");
        System.exit(1);
      }
      while (true) {
        try {
          logger.info("Calling accept");
          JxtaBiDiPipe jxtaBiDiPipe = jxtaServerPipe.accept();
          if (jxtaBiDiPipe != null) {
            logger.debug("JxtaBiDiPipe Created");
            jxtaBiDiPipe.setMessageListener(new FsClientHandler(logger, this, jxtaBiDiPipe, fsDataSource));

          }
        } catch (Exception ex) {
          logger.error(serverUtil.getStackTrace(ex));
        }
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
    }
    System.exit(0);
  }

  public static void main(String[] args) {
    String usageMsg =
      "Run server with valid arguments : \n" + "\tstatus \t: to check status of the server\n" + "\tstart \t: to start the server\n" +
      "\tstop \t: to stop the server\n" + "\tlistusers \t: to list users connected the server\n"+"\tPort No (optional) \t: to run server on some specific port no";
    if (args.length == 0) {
      System.out.println(usageMsg);
      System.exit(1);
    }if(args.length>2){
      System.out.println("Invaliod Command argument......");
      System.out.println("Please specify only two argument 1. Command 2. Port No..");
      System.exit(1);
    }
    
    if (args[0].equals("start")) {
      try {
        System.out.println("DBSentry FileSync Server Version : 1.3.0");
        System.out.println("Starting DBSentry FileSync Server..................");
        
        InputStream is = null;
        OutputStream os = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        Socket socket = null;
        try {
          if(args.length==2){
            SERVER_PORT = Integer.parseInt(args[1]);
            System.out.println("Server is running on custom port no....."+ SERVER_PORT);
          }else{
            SERVER_PORT = 6455;
          }
          
          socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
          os = socket.getOutputStream();
          dos = new DataOutputStream(os);
          dos.writeUTF("status");
          dos.flush();
          is = socket.getInputStream();
          dis = new DataInputStream(is);
          String response = dis.readUTF();
          if (response.equals("1")) {
            System.out.println("FileSync Server Is AlreadyRunning on this Port "+SERVER_PORT);
            System.out.println("If u want to run another FileSync Server please specify port no other than "+SERVER_PORT);
            System.exit(0);
          }
        } catch (Exception e) {
          ;
        } finally {
          try {
            if (dis != null) {
              dis.close();
            }
            if (is != null) {
              is.close();
            }
            if (dos != null) {
              dos.close();
            }
            if (os != null) {
              os.close();
            }
            if (socket != null) {
              socket.close();
            }
          } catch (IOException e) {
            ;
          }
        }
        new FsServer();
      } catch (Exception ex) {
        ex.printStackTrace();
        System.exit(1);
      }
    } else if (args[0].equals("stop")) {
      if(args.length==2){
        SERVER_PORT = Integer.parseInt(args[1]);
      }
      if(args.length==1){
        SERVER_PORT=6455;
      }
      System.out.println("Stopping FileSync Server ......"+ "on port no "+SERVER_PORT);
      OutputStream os = null;
      DataOutputStream dos = null;
      Socket socket = null;
      try {
        socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);

        os = socket.getOutputStream();
        dos = new DataOutputStream(os);
        dos.writeUTF(args[0]);
        dos.flush();
        System.out.println("FileSync Server stopped");
      } catch (Exception ex) {
        System.out.println("FileSync Server is not running on this port no :"+SERVER_PORT);
      } finally {
        try {

          if (dos != null) {
            dos.close();
          }
          if (os != null) {
            os.close();
          }
          if (socket != null) {
            socket.close();
          }
        } catch (IOException e) {
          ;
        }

      }
    } else if (args[0].equals("status")) {
      InputStream is = null;
      OutputStream os = null;
      DataOutputStream dos = null;
      DataInputStream dis = null;
      Socket socket = null;
      try {
        if(args.length==2){
          SERVER_PORT = Integer.parseInt(args[1]);
          System.out.println("FileSync Server status on port no....."+ SERVER_PORT);
        }
        if(args.length==1){
          SERVER_PORT=6455;
        }
        socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
        os = socket.getOutputStream();
        dos = new DataOutputStream(os);
        dos.writeUTF(args[0]);
        dos.flush();
        is = socket.getInputStream();
        dis = new DataInputStream(is);
        String response = dis.readUTF();
        if (response.equals("1")) {
          System.out.println("FileSync Server Is Running"+ "on port no" +SERVER_PORT);
        }
      } catch (Exception ex) {
        System.out.println("FileSync Server is not running");
      } finally {
        try {
          if (dis != null) {
            dis.close();
          }
          if (is != null) {
            is.close();
          }
          if (dos != null) {
            dos.close();
          }
          if (os != null) {
            os.close();
          }
          if (socket != null) {
            socket.close();
          }
        } catch (IOException e) {
          ;
        }
      }
    } else if (args[0].equals("listusers")) {
      InputStream is = null;
      OutputStream os = null;
      DataOutputStream dos = null;
      ObjectInputStream ois = null;
      Socket socket = null;
      try {
        if(args.length==2){
          SERVER_PORT = Integer.parseInt(args[1]);
          System.out.println("FileSync Server listusers on port no....."+ SERVER_PORT);
        }else{
          SERVER_PORT=6455;
        }
        socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
        os = socket.getOutputStream();
        dos = new DataOutputStream(os);
        dos.writeUTF(args[0]);
        dos.flush();

        is = socket.getInputStream();
        ois = new ObjectInputStream(is);
        Hashtable connectedUsers = (Hashtable)ois.readObject();
        Iterator iterator = connectedUsers.values().iterator();
        FsUserInfo fsUserInfo;
        if (!iterator.hasNext()) {
          System.out.println("No user are connected ");
        }
        while (iterator.hasNext()) {
          fsUserInfo = (FsUserInfo)iterator.next();
          System.out.println(fsUserInfo.getUserid() + "\t\t" + fsUserInfo.getLogginTime());
        }
      } catch (Exception ex) {
        System.out.println("FileSync Server is not running on this port no :"+SERVER_PORT);
      } finally {
        try {
          if (dos != null) {
            dos.close();
          }
          if (os != null) {
            os.close();
          }
          if (is != null) {
            is.close();
          }
          if (ois != null) {
            ois.close();
          }
          if (socket != null) {
            socket.close();
          }
        } catch (IOException e) {
          ;
        }
      }
    } else {
      System.out.println("Invalid arguments");
      System.out.println(usageMsg);
    }
  }

  private FsDataSource getFsDataSource() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    Element documentElement = null;
    NodeList nodeList = null;
    NamedNodeMap namedNodeMap = null;
    Node fsNode = null;
    Node paramNode = null;
    String plugin = null;
    Class pluginClass = null;
    FsDataSource fsDataSource = null;

    try {
      builder = factory.newDocumentBuilder();
      File configFile = new File("config/fsConfig.xml");
      FileInputStream fis = new FileInputStream(configFile);
      Document document = builder.parse(fis);
      fis.close();

      documentElement = document.getDocumentElement();
      nodeList = documentElement.getElementsByTagName("FS");
      fsNode = nodeList.item(0);

      ArrayList cargArrayList = new ArrayList();
      ArrayList argArrayList = new ArrayList();

      namedNodeMap = fsNode.getAttributes();
      plugin = namedNodeMap.getNamedItem("plugin").getNodeValue();
      nodeList = ((Element)fsNode).getElementsByTagName("PARAM");

      for (int index = 0; index < nodeList.getLength(); index++) {
        paramNode = nodeList.item(index);
        namedNodeMap = paramNode.getAttributes();
        argArrayList.add(namedNodeMap.getNamedItem("value").getNodeValue());
        cargArrayList.add(String.class);
      }

      Class[] cargs = new Class[cargArrayList.size()];
      cargArrayList.toArray(cargs);
      Object[] args = argArrayList.toArray();


      for (int index = 0; index < cargs.length; index++) {
        logger.debug("Parameter Type " + index + " : " + cargs[index]);
        logger.debug("Parameter " + index + " : " + args[index]);
      }

      pluginClass = Class.forName(plugin);
      fsDataSource = (FsDataSource)pluginClass.getMethod("getDataSource", cargs).invoke(pluginClass, args);

    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
    }
    return fsDataSource;
  }

  public void setJxtaServerPipe(JxtaServerPipe jxtaServerPipe) {
    this.jxtaServerPipe = jxtaServerPipe;
  }


  public JxtaServerPipe getJxtaServerPipe() {
    return jxtaServerPipe;
  }

  public Hashtable getConnectedUsers() {
    return connectedUsers;
  }


}
