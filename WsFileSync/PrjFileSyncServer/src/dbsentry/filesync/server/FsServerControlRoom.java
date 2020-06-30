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
 * $Id: FsServerControlRoom.java,v 1.10 2006/03/16 14:00:30 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;


public class FsServerControlRoom implements Runnable {

  private FsServer fsServer;

  private Logger logger;

  public FsServerControlRoom(FsServer fsServer, Logger logger) {
    this.fsServer = fsServer;
    this.logger = logger;
  }

  public void run() {
    try {
      logger.debug("In the run of FsServerControlRoom class");
      ServerSocket serverSocket = new ServerSocket(FsServer.SERVER_PORT);
      while (true) {
        OutputStream os = null;
        DataOutputStream dos = null;
        ObjectOutputStream oos = null;
        InputStream is = null;
        DataInputStream dis = null;
        Socket socket = null;
        try {
          socket = serverSocket.accept();
          is = socket.getInputStream();
          dis = new DataInputStream(is);
          String command = dis.readUTF();
          if (command.equals("stop")) {
            logger.info("server stopped");
            if (dis != null) {
              dis.close();
            }
            if (is != null) {
              is.close();
            }
            if (socket != null) {
              socket.close();
            }
            System.exit(0);
          } else if (command.equals("status")) {
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            dos.writeUTF("1");
            dos.flush();
          } else if (command.equals("listusers")) {
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(fsServer.getConnectedUsers());
            oos.flush();
          }
        } catch (Exception ex) {
          logger.debug(ex);
        } finally {
          if (dis != null) {
            dis.close();
          }
          if (is != null) {
            is.close();
          }
          if (dos != null) {
            dos.close();
          }
          if (oos != null) {
            oos.close();
          }
          if (os != null) {
            os.close();
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
