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
 * $Id: FsSynchronizerClient.java,v 1.8 2006/02/20 15:37:52 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.sync;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.client.utility.FolderFileComparatorLocal;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.enumconstants.EnumSyncOperation;

import java.io.File;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *	This class provides functions to find the changes made in the files in a 
 *  given local folder. and also the function to compare the changes made in 
 *  the files in remote folder with the changes made in the files in the local folder.
 *  @author              Jeetendra Parasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Jeetendra Parasad
 * 	Last Modfied Date:   11-07-2005
 */
public class FsSynchronizerClient {
  private Logger logger;

  private Document remoteSyncXMLDocument;

  private Document localSyncXMLDocument;

  private CommonUtil commonUtil;

  private ClientUtil clientUtil;

  /**
   * Constructs FsSynchronizerClient object.
   * @param remoteSyncXMLDocument remote sync xml document, holding the status of the 
   * files in a remote folder
   * @param localSyncXMLDocument local sync xml document, holding the status of the 
   * files in a local folder
   */
  public FsSynchronizerClient(Document remoteSyncXMLDocument, Document localSyncXMLDocument) {
    logger = Logger.getLogger("ClientLogger");
    this.remoteSyncXMLDocument = remoteSyncXMLDocument;
    this.localSyncXMLDocument = localSyncXMLDocument;
    this.commonUtil = new CommonUtil(logger);
    this.clientUtil = new ClientUtil(logger);
  }

  /**
   * To generate list of item that has be changed added or deleted.
   * @param linkedListOfFsSyncObject list of item that has be changed added or deleted 
   * @param baseNodeRemote base node local
   * @param baseNodeLocal base node local
   * @return it will return true if something has be changed added or deleted, else it will return false 
   */
  public boolean generateSyncPreview(LinkedList linkedListOfFsSyncObject, Node baseNodeRemote, Node baseNodeLocal) {
    boolean diffExist = false;
    int resultOfComparision;
    Node remoteNode;
    Node localNode;
    String localNodeValue;
    String remoteNodeValue;
    FsSyncObject fsSyncObject;
    NodeList remoteTreeNodeList;
    NodeList localTreeNodeList;

    int remoteItemsLength = 0;
    remoteTreeNodeList = baseNodeRemote.getChildNodes();
    if (remoteTreeNodeList != null) {
      remoteItemsLength = remoteTreeNodeList.getLength();
    }

    int localItemsLength = 0;
    localTreeNodeList = baseNodeLocal.getChildNodes();
    if (localTreeNodeList != null) {
      localItemsLength = localTreeNodeList.getLength();
    }

    int remoteItemsPointer = 0;
    int localItemsPointer = 0;
    if (remoteItemsLength > 0 && localItemsLength > 0) {
      while (remoteItemsPointer < remoteItemsLength && localItemsPointer < localItemsLength) {
        fsSyncObject = new FsSyncObject();
        remoteNode = remoteTreeNodeList.item(remoteItemsPointer);
        logger.debug("remoteItems : " + remoteNode.getAttributes().getNamedItem("PATH").getNodeValue());
        localNode = localTreeNodeList.item(localItemsPointer);
        logger.debug("localItems : " + localNode.getAttributes().getNamedItem("PATH").getNodeValue());
        resultOfComparision = commonUtil.compareTwoNode(remoteNode, localNode);
        logger.debug("resultOfComparision : " + resultOfComparision);
        if (resultOfComparision > 0) {
          localNodeValue = localNode.getNodeName();
          if (localNodeValue.equals("FOLDER")) {
            //add this folder to the list
            fsSyncObject.setLocalNode(localNode);
            linkedListOfFsSyncObject.add(fsSyncObject);
          } else {
            //add this file to the list
            fsSyncObject.setLocalNode(localNode);
            linkedListOfFsSyncObject.add(fsSyncObject);
          }
          diffExist = true;
          localItemsPointer++;
        } else if (resultOfComparision < 0) {
          remoteNode = remoteTreeNodeList.item(remoteItemsPointer);
          remoteNodeValue = remoteNode.getNodeName();
          if (remoteNodeValue.equals("FOLDER")) {
            //add this folder to the list
            fsSyncObject.setRemoteNode(remoteNode);
            linkedListOfFsSyncObject.add(fsSyncObject);
          } else {
            //add this file to the list
            fsSyncObject.setRemoteNode(remoteNode);
            linkedListOfFsSyncObject.add(fsSyncObject);
          }
          diffExist = true;
          remoteItemsPointer++;
        } else {
          //if they are equal then either they are file or they are folder
          //if they are folder
          remoteNode = remoteTreeNodeList.item(remoteItemsPointer);
          localNode = localTreeNodeList.item(localItemsPointer);
          remoteNodeValue = remoteNode.getNodeName();
          if (remoteNodeValue.equals("FOLDER")) {
            remoteNodeValue = remoteNode.getAttributes().getNamedItem("STATUS").getNodeValue();
            localNodeValue = localNode.getAttributes().getNamedItem("STATUS").getNodeValue();
            if ((remoteNodeValue.equals(EnumSyncOperation.FOLDER_DELETED) &&
                 localNodeValue.equals(EnumSyncOperation.FOLDER_DELETED))) {
              //if both the node has been deleted then remove them from the local and remote xml
              commonUtil.removeNodeFromDocument(localSyncXMLDocument, localNode);
              commonUtil.removeNodeFromDocument(remoteSyncXMLDocument, remoteNode);
            } else {
              if (remoteNodeValue.equals(EnumSyncOperation.FOLDER_DELETED) ||
                  localNodeValue.equals(EnumSyncOperation.FOLDER_DELETED)) {
                //add this folder to the list
                fsSyncObject.setRemoteNode(remoteNode);
                fsSyncObject.setLocalNode(localNode);
                linkedListOfFsSyncObject.add(fsSyncObject);
              } else {
                //both the node are equal add them to the local and remote xml
                commonUtil.addNodeToDocument(localSyncXMLDocument, localNode, false);
                commonUtil.addNodeToDocument(remoteSyncXMLDocument, remoteNode, false);

                //add content of this folder to the list
                diffExist = generateSyncPreview(linkedListOfFsSyncObject, remoteNode, localNode);
              }
            }
          } else {
            remoteNodeValue = remoteNode.getAttributes().getNamedItem("STATUS").getNodeValue();
            localNodeValue = localNode.getAttributes().getNamedItem("STATUS").getNodeValue();
            if (remoteNodeValue.equals(EnumSyncOperation.NEW_FILE) &&
                localNodeValue.equals(EnumSyncOperation.NEW_FILE)) {
              //if the files are new then check for md5sum
              remoteNodeValue = remoteNode.getAttributes().getNamedItem("MD5SUM").getNodeValue();
              localNodeValue = localNode.getAttributes().getNamedItem("MD5SUM").getNodeValue();
              if (!remoteNodeValue.equals(localNodeValue)) {
                //if md5sum are not equal then add them to the list and ask user to decide, what has to be done
                //add this file to the list
                fsSyncObject.setRemoteNode(remoteNode);
                //add this file to the list
                fsSyncObject.setLocalNode(localNode);
                diffExist = true;
                linkedListOfFsSyncObject.add(fsSyncObject);
              } else {
                //if both the node are equal then add them to the local and remote xml
                commonUtil.addNodeToDocument(localSyncXMLDocument, localNode, false);
                commonUtil.addNodeToDocument(remoteSyncXMLDocument, remoteNode, false);
              }
            } else if ((remoteNodeValue.equals(EnumSyncOperation.FILE_CHANGED) &&
                        localNodeValue.equals(EnumSyncOperation.FILE_CHANGED)) ||
                       (remoteNodeValue.equals(EnumSyncOperation.FILE_CHANGED) &&
                                                                                   localNodeValue.equals(EnumSyncOperation
                                                                                                        .FILE_UNCHANGED)) ||
                       (remoteNodeValue.equals(EnumSyncOperation.FILE_CHANGED) &&
                                                                                                                              localNodeValue.equals(EnumSyncOperation
                                                                                                        .FILE_DELETED)) ||
                       (remoteNodeValue.equals(EnumSyncOperation.FILE_DELETED) &&
                                                                                                                            localNodeValue.equals(EnumSyncOperation
                                                                                                        .FILE_CHANGED)) ||
                       (remoteNodeValue.equals(EnumSyncOperation.FILE_DELETED) &&
                                                                                                                            localNodeValue.equals(EnumSyncOperation
                                                                                                        .FILE_UNCHANGED)) ||
                       (remoteNodeValue.equals(EnumSyncOperation.FILE_UNCHANGED) &&
                                                                                                                              localNodeValue.equals(EnumSyncOperation
                                                                                                          .FILE_CHANGED)) ||
                       (remoteNodeValue.equals(EnumSyncOperation.FILE_UNCHANGED) &&
                                                                                                                              localNodeValue.equals(EnumSyncOperation
                                                                                                          .FILE_DELETED))) {

              fsSyncObject.setRemoteNode(remoteNode);
              fsSyncObject.setLocalNode(localNode);
              diffExist = true;
              linkedListOfFsSyncObject.add(fsSyncObject);
            } else if (remoteNodeValue.equals(EnumSyncOperation.FILE_DELETED) &&
                       localNodeValue.equals(EnumSyncOperation.FILE_DELETED)) {
              //if both the node has been deleted then remove them from the local and remote xml
              commonUtil.removeNodeFromDocument(localSyncXMLDocument, localNode);
              commonUtil.removeNodeFromDocument(remoteSyncXMLDocument, remoteNode);
            }
          }

          remoteItemsPointer++;
          localItemsPointer++;
        }
      }
    }

    //if remoteItems has element then mark all of them for upload
    if (remoteItemsLength != 0 && remoteItemsPointer < remoteItemsLength) {
      //if the local item length is zero then mark all the remote items for upload      
      while (remoteItemsPointer < remoteItemsLength) {
        fsSyncObject = new FsSyncObject();
        remoteNode = remoteTreeNodeList.item(remoteItemsPointer);

        //        logger.debug("remoteItems : " + remoteNode.getAttributes().getNamedItem("PATH").getNodeValue());
        remoteNodeValue = remoteNode.getNodeName();
        if (remoteNodeValue.equals("FOLDER")) {
          //add this folder to the list
          fsSyncObject.setRemoteNode(remoteNode);
          linkedListOfFsSyncObject.add(fsSyncObject);
        } else {
          //add this file to the list
          fsSyncObject.setRemoteNode(remoteNode);
          linkedListOfFsSyncObject.add(fsSyncObject);
        }
        remoteItemsPointer++;
      }
      return diffExist;
    }

    //if localItems has element then mark all of them for upload
    if (localItemsLength != 0 && localItemsPointer < localItemsLength) {
      while (localItemsPointer < localItemsLength) {
        fsSyncObject = new FsSyncObject();
        localNode = localTreeNodeList.item(localItemsPointer);
        localNodeValue = localNode.getNodeName();
        //        logger.debug("localItems : " + localNode.getAttributes().getNamedItem("PATH").getNodeValue());
        if (localNodeValue.equals("FOLDER")) {
          //add this folder to the list
          fsSyncObject.setLocalNode(localNode);
          linkedListOfFsSyncObject.add(fsSyncObject);
        } else {
          //add this file to the list
          fsSyncObject.setLocalNode(localNode);
          linkedListOfFsSyncObject.add(fsSyncObject);
        }
        localItemsPointer++;
      }
      return diffExist;
    }

    return diffExist;
  }


  /**
   * Compares file object with node object. The comparision is based on name of 
   * folder or file. The folder is always greater than the file.
   * @param file file object
   * @param oldNode node object
   * @return the comparision result.
   */
  public int compareFileWithNode(File file, Node oldNode) {
    String oldNodeValue = oldNode.getNodeName();
    if (file.isDirectory() && oldNodeValue.equals("FOLDER")) {
      oldNodeValue = oldNode.getAttributes().getNamedItem("NAME").getNodeValue();
      return file.getName().compareToIgnoreCase(oldNodeValue);
    } else if (file.isFile() && oldNodeValue.equals("FILE")) {
      oldNodeValue = oldNode.getAttributes().getNamedItem("NAME").getNodeValue();
      return file.getName().compareToIgnoreCase(oldNodeValue);
    } else if (file.isDirectory() && oldNodeValue.equals("FILE")) {
      return -1;
    } else {
      return 1;
    }
  }


  /**
   * To generate document object of files that has been added changed or deleted.
   * @param document an empty document object which will contain the changes, addition or deletion
   * @param node the current node where changes, addition or deletion is being made.
   * @param baseFile current base file object
   * @param baseNode current base node object
   */
  public void getSyncXML(Document document, Node node, File baseFile, Node baseNode) {
    int resultOfComparision;
    File file;
    File files[];
    Node oldNode;
    NodeList oldNodeList = null;
    String oldNodeValue;
    int oldNodeListLength = 0;

    if (baseNode != null) {
      oldNodeList = baseNode.getChildNodes();
      oldNodeListLength = oldNodeList.getLength();
    }

    files = baseFile.listFiles();
    Arrays.sort(files, new FolderFileComparatorLocal());
    int filesLength = files.length;

    int filesPointer = 0;
    int oldNodesPointer = 0;
    if (filesLength > 0 && oldNodeListLength > 0) {
      while (filesPointer < filesLength && oldNodesPointer < oldNodeListLength) {
        file = files[filesPointer];
        logger.debug("localItems : " + file.getPath());
        oldNode = oldNodeList.item(oldNodesPointer);
        logger.debug("localItems old : " + oldNode.getAttributes().getNamedItem("PATH").getNodeValue());
        resultOfComparision = compareFileWithNode(file, oldNode);
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
          file = files[filesPointer];
          if (file.isDirectory()) {
            //add this folder to the list
            Element childElement = clientUtil.getDomElement(document, file);
            childElement.setAttribute("STATUS", EnumSyncOperation.NEW_FOLDER);
            node.appendChild(childElement);

            //add content of this folder to the document
            getSyncXML(document, childElement, file, null);
          } else {
            //add this file to the document
            Element childElement = clientUtil.getDomElement(document, file);
            childElement.setAttribute("STATUS", EnumSyncOperation.NEW_FILE);
            node.appendChild(childElement);
          }
          filesPointer++;
        } else {
          //if they are equal then either they are file or they are folder
          //if they are folder
          file = files[filesPointer];
          oldNode = oldNodeList.item(oldNodesPointer);
          if (file.isDirectory()) {
            //add this folder to the document
            Element childElement = commonUtil.getDomElement(document, oldNode);
            childElement.setAttribute("STATUS", EnumSyncOperation.FOLDER_UNCHANGED);
            node.appendChild(childElement);

            //add content of this folder to the list
            getSyncXML(document, childElement, file, oldNode);
          } else {
            long oldTime = Long.parseLong(oldNode.getAttributes().getNamedItem("MODIFIED_DATE").getNodeValue());
            long newTime = file.lastModified();
            Date oldDate = new Date(oldTime);
            Date newDate = new Date(newTime);
            resultOfComparision = (newDate.compareTo(oldDate));
            if (resultOfComparision == 0) {
              Element childElement = commonUtil.getDomElement(document, oldNode);
              childElement.setAttribute("STATUS", EnumSyncOperation.FILE_UNCHANGED);
              node.appendChild(childElement);
            } else {
              //it is assumed that file date is greater
              Element childElement = clientUtil.getDomElement(document, file);
              childElement.setAttribute("STATUS", EnumSyncOperation.FILE_CHANGED);
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
          filesPointer++;
          oldNodesPointer++;
        }
      }
    }

    //if remoteItems has element then mark all of them for upload
    if (filesLength != 0 && filesPointer < filesLength) {
      //if the local item length is zero then mark all the remote items for upload      
      while (filesPointer < filesLength) {
        file = files[filesPointer];
        logger.debug("localItems : " + file.getPath());
        if (file.isDirectory()) {
          //add this folder to the document
          //add this folder to the document
          Element childElement = clientUtil.getDomElement(document, file);
          childElement.setAttribute("STATUS", EnumSyncOperation.NEW_FOLDER);
          node.appendChild(childElement);

          //add content of this folder to the list
          getSyncXML(document, childElement, file, null);
        } else {
          //add this file to the document
          Element childElement = clientUtil.getDomElement(document, file);
          childElement.setAttribute("STATUS", EnumSyncOperation.NEW_FILE);
          node.appendChild(childElement);
        }
        filesPointer++;
      }
    }

    //if localItems has element then mark all of them for upload
    if (oldNodeListLength != 0 && oldNodesPointer < oldNodeListLength) {
      while (oldNodesPointer < oldNodeListLength) {
        oldNode = oldNodeList.item(oldNodesPointer);
        oldNodeValue = oldNode.getNodeName();
        logger.debug("remoteItems : " + oldNode.getAttributes().getNamedItem("PATH").getNodeValue());
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
