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
 * $Id: ClientHandlerHelper.java,v 1.85 2006/09/08 09:13:04 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileFolderPropertyPageRemote;
import dbsentry.filesync.common.FsFilePropertyPageRemote;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsFolderPropertyPageRemote;
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.FsObjectHolder;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsCommandConstants;
import dbsentry.filesync.common.constants.FsDisconnectionConstants;
import dbsentry.filesync.common.enumconstants.EnumSyncOperation;
import dbsentry.filesync.common.listeners.FsRemoteCommandListener;
import dbsentry.filesync.common.listeners.FsSyncCommandListener;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;
import dbsentry.filesync.filesystem.specs.FsTotalInfoFoldersDocs;

import java.util.Arrays;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *	Helps FsClientHandler class in handling the requests from client, by redirecting 
 *  the requests to corresponding classes to handle specific requests.
 *  @author             Jeetendra Prasad
 *  @version            1.0
 * 	Date of creation    7-05-2005
 * 	Last Modfied by     Saurabh Gupta  
 * 	Last Modfied Date   21-03-2006
 */
public class

ClientHandlerHelper {

  private Logger logger;

  private FsResponse fsResponse;

  private FsConnection fsConnection;

  private FolderDocInfoRemote folderDocInfoRemote;

  private FsObjectHolder fsObjectHolders[];

  private CommonUtil commonUtil;

  private ServerUtil serverUtil;

  /**
   * Constructs a new ClientHandlerHelper object with the specified Fsconnection and Logger object.
   * @param fsConnection FsConnection Object.
   * @param logger Logger Objebt.
   */
  public ClientHandlerHelper(FsConnection fsConnection, Logger logger) {
    this.logger = logger;
    this.fsConnection = fsConnection;
    folderDocInfoRemote = new FolderDocInfoRemote(fsConnection);
    this.commonUtil = new CommonUtil(logger);
    this.serverUtil = new ServerUtil(logger);
  }

  /**
   * To get root folders.
   * @param fsRequest FsRequest object requesting root folders.
   * @return fsResponse
   */
  public FsResponse getRootFolders(FsRequest fsRequest) {
    Object[] tempObjects;
    logger.debug("Sending root folders");
    fsResponse = new FsResponse();
    tempObjects = getTreeNode("");
    logger.debug("treeSet : " + tempObjects);
    fsResponse.setDatas(tempObjects);
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsCommandConstants.GET_ROOT_FOLDERS);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To get home folder.
   * @param fsRequest FsRequest object requesting home folder.
   * @return fsResponse FsResponse object containing home folder path.
   * @throws dbsentry.filesync.filesystem.specs.FsException throws FsException if error getting the
   * home folder.
   */
  public FsResponse getHomeFolder(FsRequest fsRequest) throws FsException {

    fsResponse = new FsResponse();
    FsFolder fsFolder = fsConnection.getHomeFolder();
    logger.debug("Sending home folder : " + fsFolder.getPath());
    fsResponse.setData(fsFolder.getPath());
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsCommandConstants.GET_HOME_FOLDER);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To get the root folders of filesytem.
   * @param fsRequest FsRequest object requesting root of specified folder path.
   * @return fsResponse FsResponse object containing root folder.
   * @throws dbsentry.filesync.filesystem.specs.FsException throws if error getting the root of
   * specifed folder path.
   */
  public FsResponse getFolderRoot(FsRequest fsRequest) throws FsException {
    fsResponse = new FsResponse();
    String folderPath = (String)fsRequest.getData();
    FsFolder folderRoot = fsConnection.getFolderRoot(folderPath);
    logger.debug("Sending folder root : " + folderRoot.getPath());
    fsResponse.setData(folderRoot.getPath());
    fsResponse.setData1(fsConnection.getHomeFolder().getPath());
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsCommandConstants.GET_FOLDER_ROOT);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To get sub folders of a folder. 
   * @param fsRequest FsRequest object requesting subfolders of specified folder path.
   * @return fsResponse 
   */
  public FsResponse getSubFolders(FsRequest fsRequest) {
    fsResponse = new FsResponse();
    String absFolderPath = (String)fsRequest.getData();
    logger.debug("absFolderPath : " + absFolderPath);
    logger.debug("Sending content of : " + absFolderPath);
    Object[] treeNodes = getTreeNode(absFolderPath);
    fsResponse.setCurrentTreeNodePath(absFolderPath);
    fsResponse.setSelectTreeNodePath(null);
    fsResponse.setDatas(treeNodes);
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsMessage.GET_SUB_FOLDERS);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    logger.debug("Sending content of next node in the tree");
    return fsResponse;
  }

  /**
   * To handle the GETCONTENTOFFOLDER request by client.  
   * @param fsRequest FsRequest object requesting contents of specified folder .
   * @return fsResponse FsResponse object containing contents of folder.
   * @throws dbsentry.filesync.filesystem.specs.FsException throws if uanble to get the contents of
   * folder.
   */
  public FsResponse getContentOfFolder(FsRequest fsRequest) throws FsException {
    String absFolderPath = (String)fsRequest.getData();

    if (absFolderPath.endsWith(fsConnection.getSeperator())) {
      if (!absFolderPath.equals(fsConnection.getSeperator()))
        absFolderPath = absFolderPath.substring(0, absFolderPath.length() - 1);
    }
    logger.debug("absFolderPath : " + absFolderPath);
    FsFolder fsFolder = (FsFolder)fsConnection.findFsObjectByPath(absFolderPath);
    if (fsFolder != null) {
      if (!fsFolder.getPath().equals("/")) {
        fsObjectHolders = getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        folderDocInfoRemote.addFolderPath(fsFolder.getPath());
        logger.debug("fsFolder.getPath()"+fsFolder.getPath());
        logger.debug("fsFolder.getParent()"+fsFolder.getParent().getName());
        folderDocInfoRemote.addFolderPathParent(fsFolder.getParent().getPath());
        fsResponse.setData1(fsFolder.getParent().getPath());
        fsResponse.setFsFolderDocInfoHolder(folderDocInfoRemote.getFolderDocInfoHolder());
        logger.debug("..............................................." + fsFolder.getParent().getPath());
        fsResponse.setResponseCode(FsCommandConstants.GETCONTENTOFFOLDER);
       }
      
    } else {
      fsResponse.setResponseCode(FsCommandConstants.SEARCH_FAILED);
    }
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To fetch the content of a folder.
   * @param folder FsFolder object whose contents has to be fetched.
   * @return fsObjectHolders array of FsObjectHolder objects representing the folder contents.
   */
  public FsObjectHolder[] getFolderContent(FsFolder folder) {
    try {
      FsObject fsObjects[] = folder.listContentOfFolder();
      if (fsObjects != null) {
        int itemCount = fsObjects.length;
        FsObjectHolder fsObjectHolders[] = new FsObjectHolder[itemCount];
        for (int index = 0; index < itemCount; index++) {
          if (fsObjects[index] instanceof FsFolder) {
            fsObjectHolders[index] = serverUtil.getFsFolderHolder((FsFolder)fsObjects[index]);
          } else {
            fsObjectHolders[index] = serverUtil.getFsFileHolder((FsFile)fsObjects[index]);
          }
        }
        return fsObjectHolders;
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
    }
    return null;
  }

  /**
   * To handle the GET_FLAT_FOLDER_TREE request by client.
   * @param fsRequest FsRequest object requesting flat folder tree starting at startFolderPath till 
   * the endFolderPath.
   * @return fsResponse FsResponse object containing array of Object object, representing flat folder
   * tree.
   * @throws dbsentry.filesync.filesystem.specs.FsException throws FsException if uanble to fetch the 
   * flat folder tree.
   */
  public FsResponse getFlatFolderTree(FsRequest fsRequest) throws FsException {
    Object[] currTreeNode = null;
    String currTreeNodePath;
    Object[] prevTreeNode = null;
    String prevTreeNodePath;
    FsFolder fsFolder;
    fsResponse = new FsResponse();
    FsFolderHolder fsFolderHolder;
    String endFolderPath;
    String startFolderPath = (String)fsRequest.getData1();
    logger.debug("startFolderPath : " + startFolderPath);
    if (fsRequest.getData() != null) {
      endFolderPath = (String)fsRequest.getData();
      if (endFolderPath.endsWith(fsConnection.getSeperator())) {
        endFolderPath = endFolderPath.substring(0, endFolderPath.length() - 1);
      }
      fsResponse.setSelectTreeNodePath(endFolderPath);
      logger.debug("endFolderPath : " + endFolderPath);
    } else {
      endFolderPath = startFolderPath;
      logger.debug("endFolderPath : " + endFolderPath);
    }
    fsResponse.setCurrentTreeNodePath(startFolderPath);
    //this while loop will tarverse the suntree recursively, except the 
    prevTreeNode = new Object[0];
    prevTreeNodePath = "";
    while (!startFolderPath.equals(endFolderPath)) {
      fsFolder = (FsFolder)fsConnection.findFsObjectByPath(endFolderPath);
      currTreeNodePath = fsFolder.getPath();
      currTreeNode = this.getTreeNode(currTreeNodePath);
      logger.debug("currTreeNode : " + currTreeNode);
      if (prevTreeNode != null && prevTreeNode.length != 0) {
        logger.debug("prevTreeNodePath : " + prevTreeNodePath);
        for (int index = 0; index < (int)currTreeNode.length; index++) {
          fsFolderHolder = (FsFolderHolder)currTreeNode[index];
          logger.debug("fsFolderHolder.getPath() : " + fsFolderHolder.getPath());
          if (fsFolderHolder.getPath().equals(prevTreeNodePath)) {
            logger.debug("Adding " + prevTreeNode + " to " + fsFolderHolder.getName());
            fsFolderHolder.setItems(prevTreeNode);
            break;
          }
        }
      }
      prevTreeNode = currTreeNode;
      prevTreeNodePath = currTreeNodePath;
      endFolderPath = fsFolder.getParent().getPath();
    }

    fsFolder = (FsFolder)fsConnection.findFsObjectByPath(startFolderPath);
    currTreeNode = this.getTreeNode(fsFolder.getPath());
    logger.debug("currTreeNode : " + currTreeNode);
    if (prevTreeNode.length != 0) {
      logger.debug("prevTreeNodePath : " + prevTreeNodePath);
      for (int index = 0; index < currTreeNode.length; index++) {
        fsFolderHolder = (FsFolderHolder)currTreeNode[index];
        logger.debug("fsFolderHolder.getPath() : " + fsFolderHolder.getPath());
        if (fsFolderHolder.getPath().equals(prevTreeNodePath)) {
          logger.debug("Adding " + prevTreeNode + " to " + fsFolderHolder.getName());
          fsFolderHolder.setItems(prevTreeNode);
          break;
        }
      }
    }
    displayTreeNode(currTreeNode);
    fsResponse.setDatas(currTreeNode);
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsCommandConstants.GET_FLAT_FOLDER_TREE);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To get the content of parent folder.
   * @param fsRequest FsRequest object requesting contents of parent folder, of specified folder path.
   * @return fsResponse FsResponse object requesting contents of parent folder of current folder.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to find the current folder by 
   * folder path or failed to get the parent folder contents.
   */
  public FsResponse getContentOfParentFolder(FsRequest fsRequest) throws FsException {
    String currFolderPath = folderDocInfoRemote.getCurrentFolderPath();
    FsFolder fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
    FsFolder fsParentFolder = fsFolder.getParent();
    fsRequest.setData(fsParentFolder.getPath());
    fsResponse = getContentOfFolder(fsRequest);
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsCommandConstants.GETCONTENTOFPARENTFOLDER);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To handle the NEWFOLDER request by client.
   * @param fsRequest FsRequest object holding the request to create a new folder at specified 
   * location.
   * @throws dbsentry.filesync.filesystem.specs.FsException if permission denied to create a new 
   * folder at specified location.
   */
  public void createNewFolder(FsRequest fsRequest) throws FsException {
    //create new folder
    String folderPath = (String)fsRequest.getData();
    String folderName = (String)fsRequest.getData1();
    fsConnection.createFolder(folderPath, folderName);
  }

  /**
   * To handle the DELETEITEMS request by client.
   * @param  fsRequest FsRequest object, requesting to delete specified items.
   * @return fsResponse FsResponse object, confirming the delete operation.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to delete specified items.
   */
  public FsResponse deleteItems(FsRequest fsRequest) throws FsException {
    String folderPath = folderDocInfoRemote.getCurrentFolderPath();
    Object itemPaths[] = fsRequest.getDatas();
    fsConnection.deleteItems(itemPaths);
    fsResponse = new FsResponse();
    fsResponse.setData(folderPath);
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsCommandConstants.DELETE);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To handle the SYNC_DELETE_ITEMS request by client.
   * @param  fsRequest FsRequest object requesting to delete specified item.
   * @return fsResponse acknowledgement confirming the sync delete.
   */
  public FsResponse syncDeleteItem(FsRequest fsRequest) {
    try {
      String itemPath = (String)fsRequest.getData();
      FsObject fsObject = fsConnection.findFsObjectByPath(itemPath);
      Document document = commonUtil.getEmptyDocumentObject();
      if (fsObject instanceof FsFolder) {
        serverUtil.addDocumentElement(document, fsObject, EnumSyncOperation.FOLDER_DELETED);
      } else {
        serverUtil.addDocumentElement(document, fsObject, EnumSyncOperation.FILE_DELETED);
      }
      String xmlString = commonUtil.getXMLStringFromDocument(document);
      Object itemPaths[] = { itemPath };
      fsConnection.deleteItems(itemPaths);

      fsResponse = new FsResponse();
      fsResponse.setData(xmlString);
      //data1 contains the index of the item in linkedListOfFsSyncObject
      fsResponse.setDatas(fsRequest.getDatas());
      fsResponse.setOperation(fsRequest.getOperation());
      fsResponse.setResponseCode(FsSyncCommandListener.REMOTE_DELETE);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      return fsResponse;
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      fsResponse = new FsResponse();
      fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
      fsResponse.setOperation(fsRequest.getOperation());
      fsResponse.setResponseCode(FsSyncCommandListener.FAILED);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      return fsResponse;
    }
  }

  /**
   * To navigate one folder back in the navigation history.
   * @param  fsRequest FsRequest object requesting to navigate one folder back in the 
   * navigation history.
   * @return fsResponse FsResponse object containing the folder contents.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to find the previous folder 
   * by folder path.
   */
  public FsResponse navigateBack(FsRequest fsRequest) throws FsException {
    String prevFolderPath = folderDocInfoRemote.getPrevFolderPath();
    FsFolder fsFolder = (FsFolder)fsConnection.findFsObjectByPath(prevFolderPath);
    fsObjectHolders = getFolderContent(fsFolder);
    fsResponse.setDatas(fsObjectHolders);
    fsResponse.setFsFolderDocInfoHolder(folderDocInfoRemote.getFolderDocInfoHolder());
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsRemoteCommandListener.NAVIGATE_BACK);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To navigate to next folder in the navigation history.
   * @param  fsRequest FsRequest object requesting to navigate to next folder in the 
   * navigation history.
   * @return fsResponse FsResponse object containing the folder contents.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to find the next folder 
   * by folder path.
   */
  public FsResponse navigateForward(FsRequest fsRequest) throws FsException {
    String nextFolderPath = folderDocInfoRemote.getNextFolderPath();
    FsFolder fsFolder = (FsFolder)fsConnection.findFsObjectByPath(nextFolderPath);
    fsObjectHolders = getFolderContent(fsFolder);
    fsResponse.setDatas(fsObjectHolders);
    fsResponse.setFsFolderDocInfoHolder(folderDocInfoRemote.getFolderDocInfoHolder());
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsRemoteCommandListener.NAVIGATE_FORWARD);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To disconnect a user from server.
   * @param  fsRequest FsRequest object requesting to disconnect a user form server.
   * @return fsResponse acknowledgement of request to disconnect.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to close the connection.
   */
  public FsResponse disconnectUser(FsRequest fsRequest) throws FsException {
    fsConnection.close();
    fsResponse = new FsResponse();
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsDisconnectionConstants.DISCONNECTED);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To handle the RENAME request by client.
   * @param fsRequest FsRequest object requesting to rename an item.
   * @return fsResponse acknowledgement confirming the rename operation.
   * @throws dbsentry.filesync.filesystem.specs.FsException if permission denied to rename an item.
   */
  public FsResponse rename(FsRequest fsRequest) throws FsException {
    String itemPath = (String)fsRequest.getData();
    String itemName = (String)fsRequest.getData1();
    fsConnection.renameItem(itemPath, itemName);
    fsResponse = new FsResponse();
    fsResponse.setOperation(fsRequest.getOperation());
    fsResponse.setResponseCode(FsRemoteCommandListener.RENAME);
    fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    return fsResponse;
  }

  /**
   * To handle the request for propertypage. 
   * @param  fsRequest FsRequest object requesting properties of specified item(s).
   * @return fsResponse FsResponse object containing the properties.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to fetch the properties.
   */
  public FsResponse showProperties(FsRequest fsRequest) throws FsException {
    FsResponse fsResponse = null;
    String itemPaths[] = null;
    FsObject fsObjects[] = null;

    int folderCount = 0;
    int fileCount = 0;

    try {
      fsResponse = new FsResponse();
      itemPaths = (String[])fsRequest.getData();
      fsObjects = new FsObject[itemPaths.length];

      for (int index = 0; index < itemPaths.length; index++) {
        fsObjects[index] = fsConnection.findFsObjectByPath(itemPaths[index]);
        logger.debug("itemPaths[index]: " + itemPaths[index]);
        logger.debug("fsObjects[index] : " + fsObjects[index].getName());
        if (fsObjects[index] instanceof FsFolder) {
          folderCount++;
        } else {
          fileCount++;
        }
      }
      if (fileCount == 1 && folderCount == 0) {
        logger.debug("only file No folder..........");
        FsFile fsFile = (FsFile)fsObjects[0];
        FsFilePropertyPageRemote fsFilePropertyPageRemote = new FsFilePropertyPageRemote();
        fsFilePropertyPageRemote.setName(fsFile.getName());
        fsFilePropertyPageRemote.setFileType(fsFile.getMimeType());
        fsFilePropertyPageRemote.setLocation(fsFile.getParent().getPath());
        fsFilePropertyPageRemote.setSize(fsFile.getSize());
        fsFilePropertyPageRemote.setCreationDate(fsFile.getCreationDate());
        fsFilePropertyPageRemote.setModifiedDate(fsFile.getModifiedDate());
        fsFilePropertyPageRemote.setFsPermissionHolder(serverUtil.getFsPermissionHolder(fsFile.getPermission()));
        fsResponse.setData(fsFilePropertyPageRemote);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsRemoteCommandListener.FILE_PROPERTYPAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      } else if (folderCount == 1 && fileCount == 0) {
        logger.debug("only Folder No file..........");
        FsFolder fsFolder = (FsFolder)fsObjects[0];
        FsFolderPropertyPageRemote fsFolderPropertyPageRemote = new FsFolderPropertyPageRemote();
        fsFolderPropertyPageRemote.setName(fsFolder.getName());
        fsFolderPropertyPageRemote.setLocation(fsFolder.getParent().getPath());
        FsTotalInfoFoldersDocs fsTotalInfoFoldersDocs = fsConnection.findFolderDocInfo(itemPaths);
        fsFolderPropertyPageRemote.setSize(fsTotalInfoFoldersDocs.getSize());
        fsFolderPropertyPageRemote.setFolderType(fsTotalInfoFoldersDocs.getType());
        fsFolderPropertyPageRemote.setFolderCount(fsTotalInfoFoldersDocs.getFolderCount());
        fsFolderPropertyPageRemote.setFileCount(fsTotalInfoFoldersDocs.getDocumentCount());
        fsFolderPropertyPageRemote.setFsPermissionHolder(serverUtil.getFsPermissionHolder(fsFolder.getPermission()));
        fsFolderPropertyPageRemote.setCreationDate(fsFolder.getCreationDate());
        fsFolderPropertyPageRemote.setModifiedDate(fsFolder.getModifiedDate());

        fsResponse.setData(fsFolderPropertyPageRemote);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsRemoteCommandListener.FOLDER_PROPERTYPAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      } else {
        logger.debug("May be File Or Folder...........");
        FsFileFolderPropertyPageRemote fsFileFolderPropertyPageRemote = new FsFileFolderPropertyPageRemote();

        FsTotalInfoFoldersDocs fsTotalInfoFoldersDocs = fsConnection.findFolderDocInfo(itemPaths);

        fsFileFolderPropertyPageRemote.setNoOfFiles(fsTotalInfoFoldersDocs.getDocumentCount());
        fsFileFolderPropertyPageRemote.setNoOfFolders(fsTotalInfoFoldersDocs.getFolderCount());
        fsFileFolderPropertyPageRemote.setLocation(fsObjects[0].getParent().getPath());
        fsFileFolderPropertyPageRemote.setType(fsTotalInfoFoldersDocs.getType());
        fsFileFolderPropertyPageRemote.setSize(fsTotalInfoFoldersDocs.getSize());

        fsResponse.setData(fsFileFolderPropertyPageRemote);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsRemoteCommandListener.FILE_FOLDER_PROPERTYPAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());

      }
    } catch (Exception e) {
      logger.error(serverUtil.getStackTrace(e));
    } finally {
      return fsResponse;
    }
  }


  /**
   * Handels the GET_FOLDER_CONTENT_RECURSIVE request.
   * @param  fsRequest FsRequest object requesting the contents of specified folder recursively.
   * @return fsResponse FsResponse object containing recursive folder contents.
   * @throws dbsentry.filesync.filesystem.specs.FsException throws if uanble to get the recursive 
   * folder contents.
   */
  public FsResponse getFolderContentRecursive(FsRequest fsRequest) throws FsException {
    fsResponse = new FsResponse();

    try {
      String folderPath = (String)fsRequest.getData();
      logger.debug("startFolderPath : " + folderPath);
      FsFolder fsFolder = (FsFolder)fsConnection.findFsObjectByPath(folderPath);
      if (fsFolder == null) {
        FsExceptionHolder fsExceptionHolder = new FsExceptionHolder();
        fsExceptionHolder.setErrorMessage("Specified remote folder does not exist");
        fsResponse.setFsExceptionHolder(fsExceptionHolder);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsMessage.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        return fsResponse;
      }

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document document = builder.newDocument();
      Element element = document.createElement("FOLDER");
      element.setAttribute("PATH", fsFolder.getPath());
      element.setAttribute("NAME", fsFolder.getName());
      element.setAttribute("ID", fsFolder.getId().toString());

      getFolderContentRecursive(document, element, fsFolder);
      document.appendChild(element);

      fsResponse.setData(commonUtil.getXMLStringFromDocument(document));
      fsResponse.setOperation(fsRequest.getOperation());
      fsResponse.setResponseCode(FsMessage.GET_FOLDER_CONTENT_RECURSIVE);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
    } catch (FsException fex) {
      FsExceptionHolder fsExceptionHolder = new FsExceptionHolder();
      fsExceptionHolder.setErrorCode(fex.getErrorCode());
      fsExceptionHolder.setErrorMessage(fex.getStackTraceString());
      fsResponse.setFsExceptionHolder(fsExceptionHolder);
      fsResponse.setOperation(fsRequest.getOperation());
      fsResponse.setResponseCode(FsMessage.ERROR_MESSAGE);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    }

    return fsResponse;
  }

  /**
   * To get the content of folder recursively.
   * @param document
   * @param element
   * @param fsFolder
  
   */
  private void getFolderContentRecursive(Document document, Element element, FsFolder fsFolder) throws FsException {

    FsObject[] fsObjects = fsFolder.listContentOfFolder();
    Arrays.sort(fsObjects, new FsObjectComparator());
    int itemSize = fsObjects.length;
    for (int index = 0; index < itemSize; index++) {
      if (fsObjects[index] instanceof FsFolder) {
        FsFolder fsFolderTemp = (FsFolder)fsObjects[index];
        Element childElement = serverUtil.getSfuDomElement(document, fsFolderTemp);
        element.appendChild(childElement);
        getFolderContentRecursive(document, childElement, fsFolderTemp);
      }
    }
  }

  /**
   *
   * @param  fsRequest 
   * @return fsResponse 
   * @throws dbsentry.filesync.filesystem.specs.FsException
   */
  public FsResponse getSyncXML(FsRequest fsRequest) throws FsException {
    FsFolder fsFolder;
    fsResponse = new FsResponse();
    try {
      String folderPath = (String)fsRequest.getData();
      String syncXMLString = (String)fsRequest.getData1();
      logger.debug("startFolderPath : " + folderPath);
      fsFolder = (FsFolder)fsConnection.findFsObjectByPath(folderPath);
      if (fsFolder == null) {
        FsExceptionHolder fsExceptionHolder = new FsExceptionHolder();
        fsExceptionHolder.setErrorMessage("\"" + folderPath + "\" does not exist");

        fsResponse.setFsExceptionHolder(fsExceptionHolder);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsMessage.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        return fsResponse;
      }
      //obtain the list of fsObjects in fsFolder folder. 
      Document syncXMLDocument = commonUtil.getDocumentFromXMLString(syncXMLString);
      Node baseNode = syncXMLDocument.getDocumentElement().getFirstChild();

      //prepare document object of the Updated Folder Content
      Document updateSyncXMLDocument = commonUtil.getEmptyDocumentObject();
      Node baseNodeUpdated = serverUtil.getDomElement(updateSyncXMLDocument, fsFolder);
      updateSyncXMLDocument.getDocumentElement().appendChild(baseNodeUpdated);
      Logger fsSynchronizerServerLogger = Logger.getLogger("ServerLogger");
      FsSynchronizerServer fsSynchronizerServer = new FsSynchronizerServer(fsSynchronizerServerLogger);
      fsSynchronizerServer.getSyncXML(updateSyncXMLDocument, baseNodeUpdated, fsFolder, baseNode);

      String updatedSyncXMLString = commonUtil.getXMLStringFromDocument(updateSyncXMLDocument);
      fsResponse.setData(updatedSyncXMLString);
      fsResponse.setOperation(fsRequest.getOperation());
      fsResponse.setResponseCode(FsSyncCommandListener.PREVIEW);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
    } catch (FsException fex) {
      FsExceptionHolder fsExceptionHolder = new FsExceptionHolder();
      fsExceptionHolder.setErrorCode(fex.getErrorCode());
      fsExceptionHolder.setErrorMessage(fex.getStackTraceString());
      fsResponse.setFsExceptionHolder(fsExceptionHolder);
      fsResponse.setOperation(fsRequest.getOperation());
      fsResponse.setResponseCode(FsMessage.ERROR_MESSAGE);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
    }

    return fsResponse;
  }


  /**
   * To get the contents of a folder with specified path in a LinkedList having 
   * FolderHolder objects.
   * @param path
   * @return treeNodeHolder 
   */
  public Object[] getTreeNode(String path) {
    LinkedList treeNodeHolder = new LinkedList();
    FsObject fsObjects[];
    FsFolderHolder fsFolderHolder;
    try {
      int rootCount;
      if (path.equals("")) {
        fsObjects = fsConnection.listRoots();
      } else {
        FsFolder fsFolder = (FsFolder)fsConnection.findFsObjectByPath(path);
        fsObjects = fsFolder.listContentOfFolder();
      }

      rootCount = fsObjects.length;

      for (int index = 0; index < rootCount; index++) {
        if (fsObjects[index] instanceof FsFolder) {
          FsFolder fsFolder = (FsFolder)fsObjects[index];
          fsFolderHolder = serverUtil.getFsFolderHolder(fsFolder);
          treeNodeHolder.add(fsFolderHolder);

        }
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      return new Object[0];
    }

    return treeNodeHolder.toArray();
  }


  private void displayTreeNode(Object[] treeNode) {
    int treeNodeCount = treeNode.length;
    logger.debug("treeNode.length : " + treeNode.length);
    FsFolderHolder fsFolderHolder;
    for (int index = 0; index < treeNodeCount; index++) {
      fsFolderHolder = (FsFolderHolder)treeNode[index];
      logger.debug("nodeName : " + fsFolderHolder);
      Object[] tempObjects = fsFolderHolder.getItems();
      if (tempObjects.length != 0) {
        displayTreeNode(tempObjects);
      }
    }
  }

  /**
   * To handle the CHANGE_PROPERTIES_OF_FILE request by client.
   * @param fsRequest FsRequest object requesting  to change the properties of specified file.
   */
  public void changePropertiesOfFile(FsRequest fsRequest) throws FsException {
    FsFilePropertyPageRemote fsFilePropertyPageRemote = (FsFilePropertyPageRemote)fsRequest.getData();
    String filePath =
      fsFilePropertyPageRemote.getLocation() + fsConnection.getSeperator() + fsFilePropertyPageRemote.getOldName();
    if (fsFilePropertyPageRemote.getName() != null) {
      String newFileName = fsFilePropertyPageRemote.getName();
      fsConnection.renameItem(filePath, newFileName);
    }
  }

  /**
   * To handle the CHANGE_PROPERTIES_OF_FOLDER request by client.
   * @param fsRequest FsRequest object requesting to change the properties of specified folder.
   */
  public void changePropertiesOfFolder(FsRequest fsRequest) throws FsException {
    logger.debug("Inside changePropertiesOfFolder");
    FsFolderPropertyPageRemote fsFolderPropertyPageRemote = (FsFolderPropertyPageRemote)fsRequest.getData();
    String folderPath =
      fsFolderPropertyPageRemote.getLocation() + fsConnection.getSeperator() + fsFolderPropertyPageRemote
      .getOldFolderName();
    if (fsFolderPropertyPageRemote.getName() != null) {
      String newFolderName = fsFolderPropertyPageRemote.getName();
      fsConnection.renameItem(folderPath, newFolderName);
    }
  }

  /**
   * returns String representation of ClientHandlerHelper object.
   * @return String repersentation ClientHandlerHelper object.
   */
  public String toString() {
    return this.toString();
  }

  public FolderDocInfoRemote getFolderDocInfoRemote() {
    return folderDocInfoRemote;
  }
}
