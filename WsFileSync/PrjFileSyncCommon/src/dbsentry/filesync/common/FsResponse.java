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
 * $Id: FsResponse.java,v 1.9 2006/02/20 15:36:19 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;


/**
 *	To create a serializable FsResponse class whose object holds data
 *  sent by server in response to a request and which can pass through stream.
 *  @author            Jeetendra Prasad
 *  @version           1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class FsResponse extends FsMessage {
  private int responseCode;

  private String superResponseCode;

  private String currentTreeNodePath;

  private String selectTreeNodePath;

  private FsFolderDocInfoHolder fsFolderDocInfoHolder;

  /**
   * To get responseCode.
   * @return responseCode stored integer responseCode.
   */
  public int getResponseCode() {
    return responseCode;
  }

  /**
   * To get responseCode.
   * @param responseCode integer to be set as responseCode.
   */
  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  /**
   * To get fsFolderDocInfoHolder.
   * @return fsFolderDocInfoHolder stored FsFolderDocInfoHolder object.
   */
  public FsFolderDocInfoHolder getFsFolderDocInfoHolder() {
    return fsFolderDocInfoHolder;
  }

  /**
   * To set fsFolderDocInfoHolder.
   * @param fsFolderDocInfoHolder FsFolderDocInfoHolder object.
   */
  public void setFsFolderDocInfoHolder(FsFolderDocInfoHolder fsFolderDocInfoHolder) {
    this.fsFolderDocInfoHolder = fsFolderDocInfoHolder;
  }

  /**
   * To set currentTreeNodePath(folderpath).
   * @param currentTreeNodePath absolute path String to be set as currentTreeNodePath.
   */
  public void setCurrentTreeNodePath(String currentTreeNodePath) {
    this.currentTreeNodePath = currentTreeNodePath;
  }

  /**
   * To get currentTreeNodePath(folderpath).
   * @return currentTreeNodePath stored currentTreeNodePath.
   */
  public String getCurrentTreeNodePath() {
    return currentTreeNodePath;
  }

  /**
   * To set selectTreeNodePath(folderpath).
   * @param selectTreeNodePath absolute path String to be set as selectTreeNodePath.
   */
  public void setSelectTreeNodePath(String selectTreeNodePath) {
    this.selectTreeNodePath = selectTreeNodePath;
  }


  /**
   * To get selectTreeNodePath. 
   * @return selectTreeNodePat stored selectTreeNodePathh
   */
  public String getSelectTreeNodePath() {
    return selectTreeNodePath;
  }

  /**
   * To set superResponseCode.
   * @param superResponseCode integer to be set as superResponseCode.
   */
  public void setSuperResponseCode(String superResponseCode) {
    this.superResponseCode = superResponseCode;
  }

  /**
   * To get superResponseCode.
   * @return superResponseCode stored superResponseCode
   */
  public String getSuperResponseCode() {
    return superResponseCode;
  }

  /**
   * gives String representation of FsResponse object.
   * @return String repersentation of FsResponse object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\t responseCode : " + responseCode;
    strTemp += "\n\t superResponseCode : " + superResponseCode;
    strTemp += "\n\t currentTreeNodePath : " + currentTreeNodePath;
    strTemp += "\n\t selectTreeNodePath : " + selectTreeNodePath;
    strTemp += "\n\t Carrier Code : " + fsFolderDocInfoHolder;

    return strTemp;
  }


}
