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
 * $Id: FsSyncObject.java,v 1.3 2006/02/20 15:37:52 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.sync;

import org.w3c.dom.Node;


/**
 *	A java bean to hold remote and local node.
 *  @author              Jeetendra Parasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Jeetendra Parasad
 * 	Last Modfied Date:   11-07-2005
 */
public class FsSyncObject {
  private Node remoteNode;

  private Node localNode;


  /**
   * setter for remote node.
   * @param remoteNode node object
   */
  public void setRemoteNode(Node remoteNode) {
    this.remoteNode = remoteNode;
  }


  /**
   * getter for remote node.
   * @return Node object
   */
  public Node getRemoteNode() {
    return remoteNode;
  }


  /**
   * setter for local node.
   * @param localNode Node object
   */
  public void setLocalNode(Node localNode) {
    this.localNode = localNode;
  }


  /**
   * getter for local node.
   * @return local node object
   */
  public Node getLocalNode() {
    return localNode;
  }

}
