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
 * $Id$
 *****************************************************************************
 */

package dbsentry.filesync.server.jxta;

import dbsentry.filesync.server.ServerUtil;

import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;

import net.jxta.socket.JxtaServerSocket;

import org.apache.log4j.Logger;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation: 06-07-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class JxtaSocketServer {

  private Logger logger;

  private PeerGroup secureNetPeerGroup;

  private PipeAdvertisement socketAdv;

  private ServerUtil serverUtil;

  /**
   * @param secureNetPeerGroup
   * @param socketAdv
   * @param logger
   */
  public JxtaSocketServer(PeerGroup secureNetPeerGroup, PipeAdvertisement socketAdv, Logger logger) {
    this.logger = logger;
    this.serverUtil = new ServerUtil(logger);
    this.secureNetPeerGroup = secureNetPeerGroup;
    this.socketAdv = socketAdv;
  }

  /**
   * @return
   */
  public JxtaServerSocket createServerSocket() {
    JxtaServerSocket serverSocket = null;

    //Creates JXTA Server Socket 
    try {
      serverSocket = new JxtaServerSocket(secureNetPeerGroup, socketAdv, 10);
      // block until a connection is available
      serverSocket.setSoTimeout(0);
    } catch (Exception e) {
      logger.error(serverUtil.getStackTrace(e));
      System.exit(-1);
    }

    return serverSocket;
  }

}
