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
 
package dbsentry.filesync.clientgui.preference;

import java.io.Serializable;

import java.util.Random;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation:16-05-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class RdvRelay implements Serializable{

  private int rdvRelayId;
  
  private String rdvPeerAddress;
  
  private String relayPeerAddress;
  
  

  public RdvRelay() {
    rdvRelayId = new Random().nextInt();
  }

  public void setRdvPeerAddress(String rdvPeerAddress) {
    this.rdvPeerAddress = rdvPeerAddress;
  }

  public String getRdvPeerAddress() {
    return rdvPeerAddress;
  }

  public void setRelayPeerAddress(String relayPeerAddress) {
    this.relayPeerAddress = relayPeerAddress;
  }

  public String getRelayPeerAddress() {
    return relayPeerAddress;
  }
}
