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
 * $Id: FsMessageSender.java,v 1.22 2006/03/21 12:03:52 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import dbsentry.filesync.common.PacketCarrier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.Date;
import java.util.Random;

import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.util.JxtaBiDiPipe;

import org.apache.log4j.Logger;


/**
 *	
 *  @author            Jeetendra Prasad
 *  @version           1.0
 * 	Date of creation:  13-07-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class FsMessageSender {


  private static final String SenderMessage = "FileSync";

  private JxtaBiDiPipe jxtaBiDiPipe;

  private Logger logger;

  private CommonUtil commonUtil;


  public FsMessageSender(JxtaBiDiPipe jxtaBiDiPipe, Logger logger) {
    this.logger = logger;
    this.jxtaBiDiPipe = jxtaBiDiPipe;
    commonUtil = new CommonUtil(logger);

  }

  public synchronized void send(FsMessage fsMessage) {

    Message msg = null;
    int fsMessageByteLength;
    int bufferPointer = 0;
    ByteArrayMessageElement bame;
    StringMessageElement sme = null;
    PacketCarrier packetCarrier = null;
    byte packetCarrierData[];
    int packetCarrierLength;
    byte fsMessageByte[] = null;
    int packetCounter = 0;

    try {
      msg = new Message();
      ByteArrayOutputStream baos = null;
      ObjectOutputStream oos = null;

      try {
        baos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(baos);
        oos.writeObject(fsMessage);
        oos.flush();
        baos.flush();
        fsMessageByte = baos.toByteArray();
      } catch (Exception e) {
        ;
      } finally {
        try {
          if (oos != null) {
            oos.close();
          }
          if (baos != null) {
            baos.close();
          }
        } catch (IOException e) {
          ;
        }
      }


      if (fsMessageByte != null) {
        fsMessageByteLength = fsMessageByte.length;
        logger.debug("fsRequestByte.length : " + fsMessageByte.length / 1024);

        int totalPacket = (int)Math.ceil((double)fsMessageByteLength / (double)CommonUtil.JXTA_BUFFER_SIZE);
        String carrierCode = String.valueOf((new Random()).nextInt());
        while (bufferPointer < fsMessageByteLength) {
          packetCounter++;
          packetCarrier = new PacketCarrier();
          packetCarrier.setTotalPacket(totalPacket);
          packetCarrier.setTotalLength(fsMessageByteLength);
          packetCarrier.setCarrierCode(carrierCode);
          packetCarrier.setSentDate(new Date());
          packetCarrier.setPacketNumber(packetCounter);
          if (fsMessageByteLength - bufferPointer > commonUtil.JXTA_BUFFER_SIZE) {
            packetCarrierData =
              constructPacketCarrier(packetCarrier, fsMessageByte, bufferPointer, commonUtil.JXTA_BUFFER_SIZE);
            packetCarrierLength = packetCarrierData.length;
            bufferPointer = bufferPointer + commonUtil.JXTA_BUFFER_SIZE;
          } else {
            packetCarrierData =
              constructPacketCarrier(packetCarrier, fsMessageByte, bufferPointer, fsMessageByteLength - bufferPointer);
            packetCarrierLength = packetCarrierData.length;
            bufferPointer = fsMessageByteLength;
          }
          sme = new StringMessageElement(SenderMessage, String.valueOf(packetCarrierLength), null);
          bame = new ByteArrayMessageElement(SenderMessage, null, packetCarrierData, null);

          msg = new Message();
          msg.addMessageElement(null, sme);
          //while remove it`s creating problems....
          msg.addMessageElement(null, bame);
          
          jxtaBiDiPipe.sendMessage(msg);
          
        }


      }
      //    logger.debug("packetCounter : " + packetCounter);
    } catch (IOException ioe) {
      logger.debug("Cause of exception : " + ioe.getLocalizedMessage());
      logger.error(commonUtil.getStackTrace(ioe));
    } catch (Exception ex) {
      logger.error(commonUtil.getStackTrace(ex));
    }
  }

  private byte[] constructPacketCarrier(PacketCarrier packetCarrier, byte[] fsRequestByte, int offset, int length) {

    byte[] packetCarrierBytes = null;
    byte data[];
    int dataLength;
    int startIndex;
    int endIndex;
    ByteArrayOutputStream baos = null;
    ObjectOutputStream oos = null;
    try {
      data = new byte[length];
      dataLength = length;
      startIndex = offset;
      endIndex = startIndex + length;
      int i = 0;
      for (int index = startIndex; index < endIndex; index++) {
        data[i++] = fsRequestByte[index];
      }
      packetCarrier.setData(data);
      packetCarrier.setOffset(startIndex);
      packetCarrier.setLength(length);

      baos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(baos);
      oos.writeObject(packetCarrier);
      oos.flush();
      baos.flush();
      packetCarrierBytes = baos.toByteArray();


    } catch (Exception ex) {
      logger.error(commonUtil.getStackTrace(ex));
    } finally {

      try {
        if (oos != null) {
          oos.close();
        }
        if (baos != null) {
          baos.close();
        }
      } catch (IOException e) {
        ;
      }

      return packetCarrierBytes;
    }
  }
}
