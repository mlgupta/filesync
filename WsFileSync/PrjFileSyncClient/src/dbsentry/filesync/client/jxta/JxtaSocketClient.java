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
 * $Id $
 *****************************************************************************
 */
package dbsentry.filesync.client.jxta;

import dbsentry.filesync.client.ClientUtil;

import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;

import net.jxta.socket.JxtaSocket;

import org.apache.log4j.Logger;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation: 07-07-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class JxtaSocketClient {
  
  private Logger logger;
  
  private PeerGroup  netPeerGroup;
  
  private PeerID peerId=null;

  private PipeAdvertisement  socketAdv;
  
  private ClientUtil clientUtil;

  /**
   * @param netPeerGroup
   * @param socketAdv
   * @param logger
   */
  public JxtaSocketClient(PeerGroup netPeerGroup,PipeAdvertisement socketAdv, PeerID peerId,Logger logger) {
    this.logger=logger;
    this.clientUtil = new ClientUtil(logger);
    this.netPeerGroup=netPeerGroup;
    this.socketAdv=socketAdv;
    this.peerId=peerId;
  }

  /**
   * @return
   */
  public JxtaSocket createClientSocket(){
    JxtaSocket clientSocket=null;
    
    //Creates JXTA Server Socket 
    try {
      logger.debug("inside jxtaSocket.................................................. ");
      clientSocket= new JxtaSocket(netPeerGroup,peerId,socketAdv,180000,true);
      // block until a connection is available
      //clientSocket.setSoTimeout(0);
    }catch (Exception e) {
      logger.error(clientUtil.getStackTrace(e));
      System.exit(-1); 
    }
    
    return clientSocket;
  }
  
  
  
}
