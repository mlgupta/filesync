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
 * $Id: ServerUtil.java,v 1.50 2006/02/20 15:36:35 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsPermissionHolder;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;
import dbsentry.filesync.filesystem.specs.FsPermission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.Arrays;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * provides utilities at server side.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation:   04-07-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class ServerUtil {

  private CommonUtil commonUtil;

  private Logger logger;

  /**
   * Constructs a ServerUtil object with a Logger object .
   * @param logger Logger object used to log the appropriate messages to the user.
   */
  public ServerUtil(Logger logger) {
    this.logger = logger;
    this.commonUtil = new CommonUtil(logger);
  }


  /**
   * Gets the stackTraceString of the FsException object.
   * @param ex FsException object whose stack trace is to be extracted.
   * @return the stackTraceString.
   */
  public String getStackTrace(FsException ex) {
    return ex.getStackTraceString();
  }

  /**
   * Gets the stackTrace of the Exception object. 
   * @param ex Exception object whose stack trace is to be extracted.
   * @return string containing stackTrace.
   */
  public String getStackTrace(Exception ex) {
    ByteArrayOutputStream baos = null;
    PrintWriter pw = null;
    String getStack = null;

    try {
      baos = new ByteArrayOutputStream();
      pw = new PrintWriter(baos);
      ex.printStackTrace(pw);
      pw.flush();
      getStack = baos.toString();
      baos.flush();
    } catch (Exception e) {
      ;
    } finally {
      try {
        if (baos != null) {
          baos.close();
        }
        if (pw != null) {
          pw.close();
        }
      } catch (IOException e) {
        ;
      }
    }
    return getStack;
  }

  public Element getSfuDomElement(Document document, FsObject fsObject) {
    Element element = document.createElement("FOLDER");
    element.setAttribute("PATH", fsObject.getPath());
    element.setAttribute("NAME", fsObject.getName());
    element.setAttribute("ID", fsObject.getId().toString());
    return element;
  }

  public Element getDomElement(Document document, FsObject fsObject) {
    if (fsObject instanceof FsFile) {
      FsFile fsFile = (FsFile)fsObject;
      Element element = document.createElement("FILE");
      element.setAttribute("PATH", fsObject.getPath());
      element.setAttribute("NAME", fsObject.getName());

      element.setAttribute("SIZE", String.valueOf(fsFile.getSize()));
      try {
        InputStream is = fsFile.getInputStream();
        element.setAttribute("MD5SUM", commonUtil.generateMD5Sum(is));
        is.close();
      } catch (FsException fex) {
        logger.error(getStackTrace(fex));
      } catch (Exception ex) {
        logger.error(commonUtil.getStackTrace(ex));
      }
      element.setAttribute("MODIFIED_DATE", String.valueOf(fsFile.getModifiedDate().getTime()));
      element.setAttribute("ID", commonUtil.generateMD5Sum(fsObject.getPath()));
      return element;
    } else {
      Element element = document.createElement("FOLDER");
      element.setAttribute("PATH", fsObject.getPath());
      element.setAttribute("NAME", fsObject.getName());
      element.setAttribute("ID", commonUtil.generateMD5Sum(fsObject.getPath()));
      return element;
    }
  }

  public Element constructNodeTree(Document document, FsObject fsObject) throws FsException {
    Element element = null;
    try {
      if (fsObject instanceof FsFile) {
        FsFile fsFile = (FsFile)fsObject;
        // element = (Element)document.createElement("FILE");                                     saurabh_remove
        element.setAttribute("PATH", fsFile.getPath());
        element.setAttribute("NAME", fsFile.getName());
        element.setAttribute("SIZE", String.valueOf(fsFile.getSize()));
        element.setAttribute("MD5SUM", commonUtil.generateMD5Sum(fsFile.getInputStream()));
        element.setAttribute("MODIFIED_DATE", String.valueOf(fsFile.getModifiedDate().getTime()));
        element.setAttribute("ID", commonUtil.generateMD5Sum(fsFile.getPath()));
      } else {
        FsFolder fsFolder = (FsFolder)fsObject;
        //  element = (Element)document.createElement("FOLDER");                                    saurabh_remove
        element.setAttribute("PATH", fsFolder.getPath());
        element.setAttribute("NAME", fsFolder.getName());
        element.setAttribute("ID", commonUtil.generateMD5Sum(fsFolder.getPath()));
        FsObject fsObjects[] = fsFolder.listContentOfFolder();
        Arrays.sort(fsObjects, new FsObjectComparator());
        int filesLength = fsObjects.length;
        for (int index = 0; index < filesLength; index++) {
          Element elementTemp = constructNodeTree(document, fsObjects[index]);
          element.appendChild(elementTemp);
        }
      }
    } catch (Exception ex) {
      logger.error(commonUtil.getStackTrace(ex));
    }
    return element;
  }

  public Document addDocumentElement(Document document, FsObject fsObject, String syncOperation) {
    try {
      FsObject fsObjectParent = fsObject.getParent();
      String fsObjectParentPath = fsObjectParent.getPath();
      String md5Sum = commonUtil.generateMD5Sum(fsObjectParentPath);
      Node root = document.getDocumentElement().getFirstChild();
      Node fsObjectElement = commonUtil.getElementByID(md5Sum, root);
      if (fsObjectElement == null) {
        Element element = getDomElement(document, fsObjectParent);
        if (syncOperation != null) {
          element.setAttribute("STATUS", syncOperation);
        }
        fsObjectElement = document.getDocumentElement();
        fsObjectElement.appendChild(element);
        fsObjectElement = element;
      }

      //add document element corresponding to the fsObject to the document
      Element element = getDomElement(document, fsObject);
      fsObjectElement.appendChild(element);
      return document;
    } catch (FsException fex) {
      logger.error(getStackTrace(fex));
    } catch (Exception ex) {
      logger.error(commonUtil.getStackTrace(ex));
    }
    return null;
  }


  /**
   * Constructs a FsExceptionHolder object out of a FsException object, which could pass through a stream.
   * @param fsException FsException object whose holder is to constructed so that it could 
   * pass through a stream.
   * @return the constructed exception holder.
   */
  public FsExceptionHolder getFsExceptionHolder(FsException fsException) {
    FsExceptionHolder fsExceptionHolder = new FsExceptionHolder();
    fsExceptionHolder.setErrorCode(fsException.getErrorCode());
    fsExceptionHolder.setErrorMessage(fsException.getMessage());
    return fsExceptionHolder;
  }

  /**
   * Overloaded function which takes generic Exception object, to construct a holder which could 
   * pass through a stream.
   * @param exception Generic exception object whose holder is to be constructed so that it could 
   * pass through stream.
   * @return the constructed exception holder.
   */
  public FsExceptionHolder getFsExceptionHolder(Exception exception) {
    FsExceptionHolder fsExceptionHolder = new FsExceptionHolder();
    fsExceptionHolder.setErrorMessage(exception.getMessage());
    return fsExceptionHolder;
  }

  /**
   * Constructs a FsFileHolder object out of a FsFile object, which could pass through a stream.
   * @param fsFile FsFile object whose holder is to be constructed so that it could 
   * pass through a stream.
   * @return the constructed file holder.
   */
  public FsFileHolder getFsFileHolder(FsFile fsFile) {
    FsFileHolder fsFileHolder = new FsFileHolder();
    fsFileHolder.setName(fsFile.getName());
    fsFileHolder.setPath(fsFile.getPath());
    fsFileHolder.setOwner(fsFile.getOwner());
    fsFileHolder.setCreationDate(fsFile.getCreationDate());
    fsFileHolder.setModifiedDate(fsFile.getModifiedDate());
    fsFileHolder.setDescription(fsFile.getDescription());
    fsFileHolder.setMimeType(fsFile.getMimeType());
    fsFileHolder.setSize(fsFile.getSize());
    return fsFileHolder;
  }

  /**
   * Constructs a FsFolderHolder object out of a FsFolder object, which could pass through a stream.
   * @param fsFolder FsFolder object whose holder is to constructed so that it could 
   * pass through a stream.
   * @return constructed folder holder.
   */
  public FsFolderHolder getFsFolderHolder(FsFolder fsFolder) {
    FsFolderHolder fsFolderHolder = new FsFolderHolder();
    try {
      fsFolderHolder.setName(fsFolder.getName());
      fsFolderHolder.setPath(fsFolder.getPath());
      fsFolderHolder.setOwner(fsFolder.getOwner());
      fsFolderHolder.setCreationDate(fsFolder.getCreationDate());
      fsFolderHolder.setModifiedDate(fsFolder.getModifiedDate());
      fsFolderHolder.setDescription(fsFolder.getDescription());
      fsFolderHolder.setHasSubDirectory(fsFolder.hasSubfolders());
    } catch (FsException fex) {
      logger.error(getStackTrace(fex));
    }
    return fsFolderHolder;
  }


  /**
   * Constructs a FsPermissionHolder object out of a FsPermission object, which could pass through a stream.
   * @param fsPermission FsPermission object whose holder is to constructed so that it could 
   * pass through a stream.
   * @return constructed permission holder.
   */
  public FsPermissionHolder getFsPermissionHolder(FsPermission fsPermission) {
    FsPermissionHolder fsPermissionHolder = new FsPermissionHolder();
    fsPermissionHolder.setPermissions(fsPermission.getPermissions());
    return fsPermissionHolder;
  }

  /**
   * Forms the path string , out of an array which hold path elements.
   * @param fsConnection FsConnection object.
   * @param folders an Object array which holds path elements.
   * @return constructed path string.
   */
  public String getRelativePath(FsConnection fsConnection, Object[] folders) {
    String filePathRelative = "";
    if (folders != null) {
      int folderCount = folders.length;

      for (int index = 0; index < folderCount - 1; index++) {
        filePathRelative += folders[index] + fsConnection.getSeperator();
      }
      filePathRelative += folders[folderCount - 1];
    }
    return filePathRelative;
  }

  /**
   * gives String representation of ServerUtil object.
   * @return String repersentation of ServerUtil object.
   */
  public String toString() {
    return this.toString();
  }

}
