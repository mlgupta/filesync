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

import dbsentry.filesync.common.constants.FsSocketConstants;

import java.io.Serializable;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation: 06-07-2006
 * @Last Modfied by : Saurabh Gupta   
 * @Last Modfied Date:21-07-2006
 */
public class FsSocketPacketCarrier implements Serializable, FsSocketConstants {
  
  private byte[]  data;
  
  private byte[] data1;
  
  private Object data2;
  
  private int operation;
  
  private int superOperation;
  
  private long count;
  
  private long bufferSize;
  
  private long totalDataSize;
  
  private String absolutePath;
  
  private FsExceptionHolder fsExceptionHolder4Socket;
  
  public void setData(byte[] data) {
    this.data = data;
  }

  public byte[] getData() {
    return data;
  }

  public void setOperation(int operation) {
    this.operation = operation;
  }

  public int getOperation() {
    return operation;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public long getCount() {
    return count;
  }

  public void setBufferSize(long bufferSize) {
    this.bufferSize = bufferSize;
  }

  public long getBufferSize() {
    return bufferSize;
  }

  public void setTotalDataSize(long totalDataSize) {
    this.totalDataSize = totalDataSize;
  }

  public long getTotalDataSize() {
    return totalDataSize;
  }


  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public void setData1(byte[] data1) {
    this.data1 = data1;
  }

  public byte[] getData1() {
    return data1;
  }

  public void setData2(Object data2) {
    this.data2 = data2;
  }

  public Object getData2() {
    return data2;
  }

  public void setFsExceptionHolder4Socket(FsExceptionHolder fsExceptionHolder4Socket) {
    this.fsExceptionHolder4Socket = fsExceptionHolder4Socket;
  }

  public FsExceptionHolder getFsExceptionHolder4Socket() {
    return fsExceptionHolder4Socket;
  }

  public void setSuperOperation(int superOperation) {
    this.superOperation = superOperation;
  }

  public int getSuperOperation() {
    return superOperation;
  }
}
