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
package dbsentry.filesync.common;

import dbsentry.filesync.common.PacketCarrier;

import dbsentry.filesync.common.listeners.FsMessageListener;

import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;                                         saurabh_remove
import java.beans.PropertyChangeSupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.Date;

import org.apache.log4j.Logger;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description
 * @Date of creation: 08-02-2006
 * @Last Modfied by : Saurabh Gupta
 * @Last Modfied Date: 13-03-2006
 */
public class FsConstructMessage implements FsMessageListener {

  private PropertyChangeSupport propertyChangeSupport4FsMessage;

  private Logger logger;

  private String carrierCode;

  private byte byteRequest[];

  private int packetCounter;

  private CommonUtil commonUtil;
  
  public FsConstructMessage(Logger logger, String carrierCode ,int carrierTotalLength) {
    this.logger = logger;
    this.carrierCode = carrierCode;
    this.byteRequest = new byte[carrierTotalLength];
    commonUtil = new CommonUtil(logger);
  }
  
  /*public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {              saurabh_remove
    if (propertyChangeSupport4FsMessage == null) {
      propertyChangeSupport4FsMessage = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4FsMessage.addPropertyChangeListener(propertyChangeListener);
  }*/
  
  public void addFsMessageListener(FsMessageListener fsMessageListener) {
   if (propertyChangeSupport4FsMessage == null) {
     propertyChangeSupport4FsMessage = new PropertyChangeSupport(this);
   }
   propertyChangeSupport4FsMessage.addPropertyChangeListener(fsMessageListener);
 }
 
  public synchronized void propertyChange(PropertyChangeEvent evt) {
    logger.debug("Inside Property change of FsConstruct Message :" + evt.getSource().toString());
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
          FsMessage fsMessage = (FsMessage)ois.readObject();
          propertyChangeSupport4FsMessage.firePropertyChange(Integer.toString(FSMESSAGE) , null, fsMessage);
          propertyChangeSupport4FsMessage.firePropertyChange(Integer.toString(REMOVE_CARRIER_CODE), null, carrierCode);
          propertyChangeSupport4FsMessage.firePropertyChange(Integer.toString(REMOVE_PROPERTY_CHANGE_LISTENER), null, this);
        }
      } catch (Exception ex) {
        logger.error(commonUtil.getStackTrace(ex));
        propertyChangeSupport4FsMessage.firePropertyChange(Integer.toString(REMOVE_CARRIER_CODE), null, carrierCode);
        propertyChangeSupport4FsMessage.firePropertyChange(Integer.toString(REMOVE_PROPERTY_CHANGE_LISTENER), null, this);
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
  
  public String toString() {
    return this.toString();
  }
  
}
