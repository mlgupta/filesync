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
 * $Id: FsProfile.java,v 1.11 2006/03/18 11:34:07 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.common.FsUser;

import java.io.Serializable;


/**
 *	A bean which stores sync profile.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   08-07-2005
 */
public class FsProfile implements Serializable {
  private FsUser fsUser;

  private String profileName;

  private String localFolderPath;

  private String remoteFolderPath;

  private boolean includeSubFolder;

  private boolean scheduleThisProfile;

  private boolean syncStatus = false;

  private int scheduleEveryDay;

  private int scheduleEveryDayTime;

  private int scheduleEveryMin;

  private String syncXMLRemote;

  private String syncXMLLocal;

  /**
   * Construct FsProfile object.
   */
  public FsProfile() {
  }

  /**
   * getter for profileName.
   * @return profile name
   */
  public String getProfileName() {
    return profileName;
  }

  /**
   * setter for profileName.
   * @param profileName profile name
   */
  public void setProfileName(String profileName) {
    this.profileName = profileName;
  }

  /**
   * getter for local folder path.
   * @return local folder path
   */
  public String getLocalFolderPath() {
    return localFolderPath;
  }

  /**
   * setter for local folder path.
   * @param localFolderPath local folder path
   */
  public void setLocalFolderPath(String localFolderPath) {
    this.localFolderPath = localFolderPath;
  }

  /**
   * getter for remote folder path.
   * @return remote folder path
   */
  public String getRemoteFolderPath() {
    return remoteFolderPath;
  }

  /**
   * setter for remote folder path.
   * @param remoteFolderPath remote folder path
   */
  public void setRemoteFolderPath(String remoteFolderPath) {
    this.remoteFolderPath = remoteFolderPath;
  }

 

  /**
   * to check if subfolder has to be included for sync operation.
   * @return true if subfolder has to be included
   */
  public boolean isIncludeSubFolder() {
    return includeSubFolder;
  }

  /**
   * indicates if subfolder has to be included.
   * @param includeSubFolder true if subfolder has to be included
   */
  public void setIncludeSubFolder(boolean includeSubFolder) {
    this.includeSubFolder = includeSubFolder;
  }

  /**
   * checks if this profile has to be scheduled.
   * @return true if profile is scheduled
   */
  public boolean isScheduleThisProfile() {
    return scheduleThisProfile;
  }

  /**
   * sets this profile for scheduling.
   * @param scheduleThisProfile true if this profile is to be scheduled
   */
  public void setScheduleThisProfile(boolean scheduleThisProfile) {
    this.scheduleThisProfile = scheduleThisProfile;
  }

  /**
   * gets the interval after which the profile is set to be scheduled.
   * @return the scheduled interval.
   */
  public int getScheduleEveryMin() {
    return scheduleEveryMin;
  }

  /**
   * sets the scheduling interval in min.
   * @param scheduleEveryMin scheduling interval in min
   */
  public void setScheduleEveryMin(int scheduleEveryMin) {
    this.scheduleEveryMin = scheduleEveryMin;
  }

  
  /**
   * gets the day when the profile is scheduled for synchronization.
   * @return scheduled day
   */
  public int getScheduleEveryDay() {
    return scheduleEveryDay;
  }

  /**
   * set the day on which this profile is scheduled for synchronization.
   * @param scheduleEveryDay scheduled day
   */
  public void setScheduleEveryDay(int scheduleEveryDay) {
    this.scheduleEveryDay = scheduleEveryDay;
  }

  /**
   * gets the time of the day when this profile is scheduled for sync.
   * @return time of the day.
   */
  public int getScheduleEveryDayTime() {
    return scheduleEveryDayTime;
  }

  /**
   * sets the time of the day when this profile is scheduled for sync.
   * @param scheduleEveryDayTime time of the day.
   */
  public void setScheduleEveryDayTime(int scheduleEveryDayTime) {
    this.scheduleEveryDayTime = scheduleEveryDayTime;
  }


  /**
   * sets user of this profile.
   * @param fsUser FsUser object
   */
  public void setFsUser(FsUser fsUser) {
    this.fsUser = fsUser;
  }


  /**
   * gets the user of this profile.
   * @return FsUser object.
   */
  public FsUser getFsUser() {
    return fsUser;
  }


  /**
   * set the status of the sync folder on remote system in the form of xml.
   * @param syncXMLRemote status of the sync folder on remote system in the form of xml
   */
  public void setSyncXMLRemote(String syncXMLRemote) {
    this.syncXMLRemote = syncXMLRemote;
  }

  /**
   * gets the status of the sync folder on remote system in the form of xml.
   * @return status of the sync folder on remote system in the form of xml
   */
  public

  String getSyncXMLRemote() {
    return syncXMLRemote;
  }

  /**
   * sets the status of the sync folder on local system in the form of xml.
   * @param syncXMLLocal status of the sync folder on local system in the form of xml
   */
  public void setSyncXMLLocal(String syncXMLLocal) {
    this.syncXMLLocal = syncXMLLocal;
  }

  /**
   * gets the status of the sync folder on local system in the form of xml.
   * @return status of the sync folder on local system in the form of xml
   */
  public String getSyncXMLLocal() {
    return syncXMLLocal;
  }


  /**
   * set sync status to true if this profile has been synchronized once.
   * @param syncStatus true if this profile has been synchronized atleast once
   */
  public void setSyncStatus(boolean syncStatus) {
    this.syncStatus = syncStatus;
  }


  /**
   * check to see the sync status.
   * @return true if this profile has been synchronized atleast once
   */
  public boolean isSyncStatus() {
    return syncStatus;
  }

}
