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
 * $Id: FsSynchronizerServer.java,v 1.24 2006/02/20 15:36:35 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.enumconstants.EnumSyncOperation;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;

import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class FsSynchronizerServer {
  private Logger logger;

  private CommonUtil commonUtil;

  private ServerUtil serverUtil;

  public FsSynchronizerServer(Logger logger) {
    this.logger = logger;
    this.commonUtil = new CommonUtil(logger);
    this.serverUtil = new ServerUtil(logger);
  }

  private int compare(FsObject fsObject, Node oldNode) {
    String oldNodeValue = oldNode.getNodeName();
    if (fsObject instanceof FsFolder && oldNodeValue.equals("FOLDER")) {
      oldNodeValue = oldNode.getAttributes().getNamedItem("NAME").getNodeValue();
      return fsObject.getName().compareToIgnoreCase(oldNodeValue);
    } else if (fsObject instanceof FsFile && oldNodeValue.equals("FILE")) {
      oldNodeValue = oldNode.getAttributes().getNamedItem("NAME").getNodeValue();
      return fsObject.getName().compareToIgnoreCase(oldNodeValue);
    } else if (fsObject instanceof FsFolder && oldNodeValue.equals("FILE")) {
      return -1;
    } else {
      return 1;
    }
  }

  public void getSyncXML(Document document, Node node, FsFolder fsFolderArg, Node oldNodeArg) throws FsException {
    int resultOfComparision;
    FsObject fsObject;
    FsFolder fsFolder;
    FsFile fsFile;
    Node oldNode;
    String oldNodeValue;
    NodeList oldNodeList = null;

    int oldNodeListLength = 0;
    //get child Nodes of the Node
    if (oldNodeArg != null) {
      oldNodeList = oldNodeArg.getChildNodes();
      oldNodeListLength = oldNodeList.getLength();
    }
    //get content of the fsfolder
    FsObject fsObjects[] = fsFolderArg.listContentOfFolder();
    Arrays.sort(fsObjects, new FsObjectComparator());
    int fsObjectsLength = fsObjects.length;

    int fsObjectsPointer = 0;
    int oldNodesPointer = 0;
    if (fsObjectsLength > 0 && oldNodeListLength > 0) {
      while (fsObjectsPointer < fsObjectsLength && oldNodesPointer < oldNodeListLength) {
        fsObject = fsObjects[fsObjectsPointer];
        logger.debug("remoteItems : " + fsObject.getPath());
        oldNode = oldNodeList.item(oldNodesPointer);
        logger.debug("old remoteItems : " + oldNode.getAttributes().getNamedItem("PATH").getNodeValue());
        resultOfComparision = compare(fsObject, oldNode);
        logger.debug("resultOfComparision : " + resultOfComparision);
        if (resultOfComparision > 0) {
          oldNodeValue = oldNode.getNodeName();
          if (oldNodeValue.equals("FOLDER")) {
            //add this folder to the document
            Element childElement = commonUtil.getDomElement(document, oldNode);
            childElement.setAttribute("STATUS", EnumSyncOperation.FOLDER_DELETED);
            node.appendChild(childElement);

          } else {
            //add this node to the document
            Element childElement = commonUtil.getDomElement(document, oldNode);
            childElement.setAttribute("STATUS", EnumSyncOperation.FILE_DELETED);
            node.appendChild(childElement);
          }
          oldNodesPointer++;
        } else if (resultOfComparision < 0) {
          fsObject = fsObjects[fsObjectsPointer];
          if (fsObject instanceof FsFolder) {
            //add this folder to the list
            fsFolder = (FsFolder)fsObject;
            Element childElement = serverUtil.getDomElement(document, fsFolder);
            childElement.setAttribute("STATUS", EnumSyncOperation.NEW_FOLDER);
            node.appendChild(childElement);

            //add content of this folder to the document
            getSyncXML(document, childElement, fsFolder, null);
          } else {
            //add this file to the document
            fsFile = (FsFile)fsObject;
            Element childElement = serverUtil.getDomElement(document, fsFile);
            childElement.setAttribute("STATUS", EnumSyncOperation.NEW_FILE);
            node.appendChild(childElement);
          }
          fsObjectsPointer++;
        } else {
          //if they are equal then either they are file or they are folder
          //if they are folder
          fsObject = fsObjects[fsObjectsPointer];
          oldNode = oldNodeList.item(oldNodesPointer);
          if (fsObject instanceof FsFolder) {
            fsFolder = (FsFolder)fsObject;
            //add this folder to the document
            Element childElement = commonUtil.getDomElement(document, oldNode);
            childElement.setAttribute("STATUS", EnumSyncOperation.FOLDER_UNCHANGED);
            node.appendChild(childElement);

            //add content of this folder to the list
            getSyncXML(document, childElement, fsFolder, oldNode);
          } else {
            fsFile = (FsFile)fsObject;
            String oldDate = oldNode.getAttributes().getNamedItem("MODIFIED_DATE").getNodeValue();
            resultOfComparision = (fsFile.getModifiedDate()).compareTo(new Date(Long.parseLong(oldDate)));
            if (resultOfComparision == 0) {
              Element childElement = commonUtil.getDomElement(document, oldNode);
              childElement.setAttribute("STATUS", EnumSyncOperation.FILE_UNCHANGED);
              node.appendChild(childElement);
            } else {
              //it is assumed that file date is greater
              Element childElement = serverUtil.getDomElement(document, fsFile);
              String oldMD5Sum = oldNode.getAttributes().getNamedItem("MD5SUM").getNodeValue();
              String newMD5Sum = childElement.getAttributes().getNamedItem("MD5SUM").getNodeValue();
              if (oldMD5Sum.equals(newMD5Sum)) {
                childElement.setAttribute("STATUS", EnumSyncOperation.FILE_UNCHANGED);
              } else {
                childElement.setAttribute("STATUS", EnumSyncOperation.FILE_CHANGED);
              }
              node.appendChild(childElement);
            }
          }
          fsObjectsPointer++;
          oldNodesPointer++;
        }
      }
    }

    //if remoteItems has element then mark all of them for upload
    if (fsObjectsLength != 0 && fsObjectsPointer < fsObjectsLength) {
      //if the local item length is zero then mark all the remote items for upload      
      while (fsObjectsPointer < fsObjectsLength) {
        fsObject = fsObjects[fsObjectsPointer];
        logger.debug("remoteItems : " + fsObject.getPath());
        if (fsObject instanceof FsFolder) {
          //add this folder to the document
          fsFolder = (FsFolder)fsObject;
          //add this folder to the document
          Element childElement = serverUtil.getDomElement(document, fsFolder);
          childElement.setAttribute("STATUS", EnumSyncOperation.NEW_FOLDER);
          node.appendChild(childElement);

          //add content of this folder to the list
          getSyncXML(document, childElement, fsFolder, null);
        } else {
          fsFile = (FsFile)fsObject;
          //add this file to the document
          Element childElement = serverUtil.getDomElement(document, fsFile);
          childElement.setAttribute("STATUS", EnumSyncOperation.NEW_FILE);
          node.appendChild(childElement);
        }
        fsObjectsPointer++;
      }
    }

    //if localItems has element then mark all of them for upload
    if (oldNodeListLength != 0 && oldNodesPointer < oldNodeListLength) {
      while (oldNodesPointer < oldNodeListLength) {
        oldNode = oldNodeList.item(oldNodesPointer);
        oldNodeValue = oldNode.getNodeName();
        logger.debug("localItems : " + oldNode.getAttributes().getNamedItem("PATH").getNodeValue());
        if (oldNodeValue.equals("FOLDER")) {
          //add this folder to the document
          Element childElement = commonUtil.getDomElement(document, oldNode);
          childElement.setAttribute("STATUS", EnumSyncOperation.FOLDER_DELETED);
          node.appendChild(childElement);

        } else {
          //add this file to the document
          Element childElement = commonUtil.getDomElement(document, oldNode);
          childElement.setAttribute("STATUS", EnumSyncOperation.FILE_DELETED);
          node.appendChild(childElement);
        }
        oldNodesPointer++;
      }
    }
  }

}
