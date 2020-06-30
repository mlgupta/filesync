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
 * $Id: RemoteWillTreeExpansionListener.java,v 1.22 2006/04/10 11:25:35 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.listener;

import dbsentry.filesync.client.FsClient;
import dbsentry.filesync.clientgui.FsClientGui;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsMessage;

import dbsentry.filesync.common.constants.FsRemoteOperationConstants;

import java.awt.Cursor;

import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;


/**
 *	Act as a listener for expansion of remote tree.
 *  @author              Jeetendra Parasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   21-03-2006
 */
public class RemoteWillTreeExpansionListener implements TreeWillExpandListener {
  private Logger logger;

  private String path[];

  private JTree treeRemoteTreeView;

  //private String superResponseCode;
  
  private FsClient fsClient;
  
  private int remoteOperationConstant;

  /**
   *	To create a treeExpansionListener for remote tree.
   *  @param treeRemoteTreeView JTree object
   *  
   */
  public RemoteWillTreeExpansionListener(FsClient fsClient,JTree treeRemoteTreeView,int remoteOperationConstant) {
    logger = Logger.getLogger("ClientLogger");
    this.treeRemoteTreeView = treeRemoteTreeView;
    this.treeRemoteTreeView.addTreeWillExpandListener(this);
    //this.superResponseCode = superResponseCode;
    this.fsClient=fsClient;
    this.remoteOperationConstant=remoteOperationConstant;
  }

  /**
 *	To handle TreeExpansion event.
 *  @param e TreeExpansionEvent
 *  @throws ExpandVetoException  when operation fails.
 */
  public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
    String nodePath = "";
    FsFolderHolder fsFolderHolder = new FsFolderHolder();
    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
    int currentNodeLevel = currentNode.getLevel();
    logger.debug("Level " + currentNodeLevel);
    if (currentNodeLevel > 0) {
      if (currentNode.getChildAt(0).toString().equals("")) {
        fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
        nodePath = fsFolderHolder.getPath();
        fsClient.getFlatFolderTree(nodePath, null, remoteOperationConstant);

        //FsClientGui.fsFileSystemOperationsRemote.getFlatFolderTree(nodePath, null, superResponseCode);
        //if (superResponseCode.equals(FsMessage.FOR_CLIENTGUI)) {
          //FsClientGui.jpRemoteSystem.setCursor(new Cursor(Cursor.WAIT_CURSOR));
          //FsClientGui.cursorCounterRemote++;
        //}

      }
    }
  }

  /**
   * Required by TreeWillExpandListener interface.
   * @param e TreeExpansionEvent object
   */
  public void treeWillCollapse(TreeExpansionEvent e) {
    //saySomething("Tree-collapsed event detected", e);
  }

  /**
 *	To insert treeNodes into currentTreeNodePath.
 *  @param treeNodes array of Objects which holds folderHolder objects.
 *  @param currentTreeNodePath String node path where the nodes will be inserted.
 *  @param selectTreeNodePath String Treenode path to select.
 */
  public void insertTreeNodesRemoteTreeView(Object[] treeNodes, String currentTreeNodePath,
                                            String selectTreeNodePath) {
    treeRemoteTreeView.setRootVisible(false);
    treeRemoteTreeView.setScrollsOnExpand(false);
    DefaultMutableTreeNode currentNodeRemote;
    DefaultMutableTreeNode node;
    DefaultMutableTreeNode childNode;
    DefaultTreeModel treeModel;
    FsFolderHolder fsFolderHolder;
    String nodeName;

    Object treeNodeObject[] = treeNodes;
    Arrays.sort(treeNodeObject);

    logger.debug("currentTreeNodePath : " + currentTreeNodePath);
    if (currentTreeNodePath == null) {
      currentNodeRemote = new DefaultMutableTreeNode();
      fsFolderHolder = new FsFolderHolder();
      fsFolderHolder.setName("My Computer : ");
      fsFolderHolder.setPath("");
      currentNodeRemote.setUserObject(fsFolderHolder);

      for (int index = 0; index < treeNodeObject.length; index++) {
        fsFolderHolder = (FsFolderHolder)treeNodeObject[index];
        nodeName = fsFolderHolder.getName();
        logger.debug("nodeName : " + nodeName);
        node = new DefaultMutableTreeNode();
        node.setUserObject(fsFolderHolder);
        currentNodeRemote.add(node);

        if (fsFolderHolder.hasSubDirectory()) {
          childNode = new DefaultMutableTreeNode();
          fsFolderHolder = new FsFolderHolder();
          fsFolderHolder.setName("");
          fsFolderHolder.setPath("");
          childNode.setUserObject(fsFolderHolder);
          node.add(childNode);
        }
      }
      DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
      renderer.setLeafIcon(FsImage.imgFolderOpen);
      renderer.setClosedIcon(FsImage.imgFolderClosed);
      renderer.setOpenIcon(FsImage.imgFolderOpen);

      treeRemoteTreeView.setCellRenderer(renderer);
      treeModel = new DefaultTreeModel(currentNodeRemote);
      treeRemoteTreeView.setModel(treeModel);
      treeRemoteTreeView.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      treeModel.reload();
    } else {
      logger.debug("treeNodes : " + treeNodes);
      DefaultMutableTreeNode currentNodeRootRemote =
        (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot();
      logger.debug("currentNodeRemote : " + currentNodeRootRemote.toString());
      currentNodeRemote =
        dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(currentNodeRootRemote, currentTreeNodePath);
      if (currentNodeRemote != null) {
        currentNodeRemote.removeAllChildren();

        for (int index = 0; index < treeNodeObject.length; index++) {
          fsFolderHolder = (FsFolderHolder)treeNodeObject[index];
          nodeName = fsFolderHolder.getName();
          logger.debug("nodeName : " + nodeName);
          node = new DefaultMutableTreeNode();
          node.setUserObject(fsFolderHolder);
          currentNodeRemote.add(node);

          Object[] tempObjects = fsFolderHolder.getItems();
          logger.debug("tempObjects : " + tempObjects);
          if (tempObjects.length != 0) {
            insertTreeNodesRemoteTreeView(tempObjects, fsFolderHolder.getPath(), null);
          } else {
            if (fsFolderHolder.hasSubDirectory()) {
              childNode = new DefaultMutableTreeNode();
              fsFolderHolder = new FsFolderHolder();
              fsFolderHolder.setName("");
              fsFolderHolder.setPath("");
              childNode.setUserObject(fsFolderHolder);
              node.add(childNode);
            }
          }
        }
      }
    }

    ((DefaultTreeModel)treeRemoteTreeView.getModel()).reload(currentNodeRemote);
    logger.debug(" selectTreeNodePath : " + selectTreeNodePath);
    if (selectTreeNodePath != null) {
      DefaultMutableTreeNode root =
        (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot();

      TreeNode treeNode[] = currentNodeRemote.getPath();
      TreePath treePath = new TreePath(treeNode);
      treeRemoteTreeView.expandPath(treePath);

      DefaultMutableTreeNode selectTreeNode =
        dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root, selectTreeNodePath);
      logger.debug(" selectTreeNode : " + selectTreeNode);
      treeNode = selectTreeNode.getPath();
      treePath = new TreePath(treeNode);
      treeRemoteTreeView.setSelectionPath(treePath);
      treeRemoteTreeView.requestFocus();
      ((DefaultTreeModel)treeRemoteTreeView.getModel()).reload(selectTreeNode);
      treeRemoteTreeView.scrollPathToVisible(treePath);
    }
  }

}
