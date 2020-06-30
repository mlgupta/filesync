/*     THIS CLASS HAS BEEN REMOVED
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
 * $Id: FsConstructResponse.java,v 1.18 2006/02/23 07:32:25 sgupta Exp $
 *****************************************************************************
 */
/*package dbsentry.filesync.common;

import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.PacketCarrier;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;


/**
 *	To reconstruct a response at client side which is divided into packets so that it could be 
 *  accomodated in the jxta buffer.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   06-07-2005
 
public class FsConstructResponse implements PropertyChangeListener {
  private PropertyChangeSupport propertyChangeSupport;

  private Logger logger;

  private String carrierCode;

  private byte byteResponse[];

  private int packetCounter;

  private CommonUtil commonUtil;


  /**
   * Constructs a FsConstructResponse object to reconstruct a response sent by server in packets.
   * @param logger Logger object.
   * @param packetCarrier PacketCarrier object which holds the packet.
   
  public FsConstructResponse(Logger logger, PacketCarrier packetCarrier) {
    this.logger = logger;
    this.carrierCode = packetCarrier.getCarrierCode();
    this.byteResponse = new byte[packetCarrier.getTotalLength()];
    this.commonUtil = new CommonUtil(logger);
  }

  /**
   * Adds a listener that will listen to the property Change event fired by this class.
   * @param propertyChangeListener PropertyChangeListener object
   
  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * To handle the PropertyChangeEvent.
   * @param evt PropertyChangeEvent object.
   
  public synchronized

  void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(carrierCode)) {
      packetCounter++;

      PacketCarrier packetCarrier = (PacketCarrier)evt.getNewValue();
      logger.debug("carrier code:packetNumber : " + carrierCode + " : " + packetCarrier.getPacketNumber());
      ByteArrayInputStream bais = null;

      ObjectInputStream ois = null;
      try {
        byte data[] = packetCarrier.getData();
        int offset = packetCarrier.getOffset();

        for (int index = 0; index < packetCarrier.getLength(); index++) {
          byteResponse[offset++] = data[index];
        }
        if (packetCounter == packetCarrier.getTotalPacket()) {
          bais = new ByteArrayInputStream(byteResponse);
          ois = new ObjectInputStream(bais);
          FsResponse fsResponse = (FsResponse)ois.readObject();

          propertyChangeSupport.firePropertyChange("fsResponse", null, fsResponse);
          propertyChangeSupport.firePropertyChange("removeCarrierCode", null, carrierCode);
          propertyChangeSupport.firePropertyChange("removePropertyChangeSupportForPacketCarrier", null, this);
        }

      } catch (Exception ex) {
        logger.error(commonUtil.getStackTrace(ex));
        propertyChangeSupport.firePropertyChange("removeCarrierCode", null, carrierCode);
        propertyChangeSupport.firePropertyChange("removePropertyChangeSupportForPacketCarrier", null, this);
      } finally {
        try {
          if (ois != null) {
            ois.close();
          }
          if (bais != null) {
            bais.close();
          }
        } catch (IOException e) {
          ;
        }
        return;
      }
    }
  }

  /**
   * gives String representation of FsConstuctResponse object.
   * @return String repersentation of FsConstuctResponse object.
   
  public String toString() {
    return this.toString();
  }

}*/
