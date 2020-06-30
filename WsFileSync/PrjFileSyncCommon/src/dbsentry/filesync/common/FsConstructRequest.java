         //THIS HAS BEEN REMOVED 
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
 * $Id: FsConstructRequest.java,v 1.16 2006/02/23 07:32:25 sgupta Exp $
 *****************************************************************************
 */
/*package dbsentry.filesync.common;

import dbsentry.filesync.common.PacketCarrier;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.Date;

import org.apache.log4j.Logger;


/**
 *	To reconstruct a request at server side which is divided into packets so that it could be 
 *  accomodated in the jxta buffer.
 *  @author             Jeetendra Prasad
 *  @version            1.0
 * 	Date of creation:   07-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 
public class FsConstructRequest implements PropertyChangeListener {
  private PropertyChangeSupport propertyChangeSupport;

  private Logger logger;

  private String carrierCode;

  private byte byteRequest[];

  private int packetCounter;

  private CommonUtil commonUtil;


  /**
   * Constructs a FsConstructRequest object to reconstruct a request sent by client.
   * @param logger Logger object.
   * @param packetCarrier PacketCarrier object which holds the packet.
   
  public FsConstructRequest(Logger logger, PacketCarrier packetCarrier) {
    this.logger = logger;
    this.carrierCode = packetCarrier.getCarrierCode();
    this.byteRequest = new byte[packetCarrier.getTotalLength()];
    commonUtil = new CommonUtil(logger);

  }

  /**
   * Adds a listener that will listen to the property Change event fired by this class.
   * @param propertyChangeListener PropertyChangeListener  object
   
  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * To handle the PropertyChangeEvent.
   * @param evt PropertyChangeEvent object.
   
  public synchronized void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(carrierCode)) {
      packetCounter++;
      PacketCarrier packetCarrier = (PacketCarrier)evt.getNewValue();
      logger.debug("carrier code:packetNumber : " + carrierCode + " : " + packetCarrier.getPacketNumber());
      Date recdDate = new Date();
      logger.debug("Recived At " + recdDate);
      logger.debug("Sent At " + packetCarrier.getSentDate());
      logger.debug("Difference in Milliseconds " + (recdDate.getTime() - packetCarrier.getSentDate().getTime()));
      ByteArrayInputStream bais = null;
      ObjectInputStream ois = null;
      try {
        byte data[] = packetCarrier.getData();
        int offset = packetCarrier.getOffset();

        for (int index = 0; index < packetCarrier.getLength(); index++) {
          byteRequest[offset++] = data[index];
        }
        if (packetCounter == packetCarrier.getTotalPacket()) {
          bais = new ByteArrayInputStream(byteRequest);
          ois = new ObjectInputStream(bais);
          FsRequest fsRequest = (FsRequest)ois.readObject();
          propertyChangeSupport.firePropertyChange("fsRequest", null, fsRequest);
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
   * gives String representation of FsConstuctRequest object.
   * @return String repersentation of FsConstuctRequest object.
   
  public String toString() {
    return this.toString();
  }

}*/
