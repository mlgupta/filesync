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
 * $Id: LocalWillTreeExpansionListener.java,v 1.23 2006/04/18 07:14:12 skumar Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.listener;

import dbsentry.filesync.client.utility.FolderFileComparatorLocal;
import dbsentry.filesync.clientgui.FsClientGui;
import dbsentry.filesync.clientgui.FileSyncClientMDI;
import dbsentry.filesync.clientgui.FsExplorer;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.common.FsFolderHolder;

import java.awt.Cursor;

import java.io.File;

import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;


/**
 *	Purpose: Act as a listener for expansion of local tree.
 *  @author              Jeetendra Parasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   21-03-2006
 */
public class LocalWillTreeExpansionListener implements TreeWillExpandListener {
  private Logger logger;

  private JTree treeLocalTreeView;

  /**
   *	A constructor to create LocalWillTreeExpansionListener object.
   *  @param treeLocalTreeView JTree object
   */
  public

  LocalWillTreeExpansionListener(JTree treeLocalTreeView) {
    this.logger = Logger.getLogger("ClientLogger");
    this.treeLocalTreeView = treeLocalTreeView;
    this.treeLocalTreeView.addTreeWillExpandListener(this);
  }

  /**
   *	To handle TreeExpansion event.
   *  @param e TreeExpansionEvent
   *  @throws ExpandVetoException when operation fails.
   */
  public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
    // Required by TreeWillExpandListener interface.
    File file;
    DefaultMutableTreeNode childNode;
    DefaultMutableTreeNode subChildNode;
    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
    int currentNodeLevel = currentNode.getLevel();
  // FsClientGui.jpLocalSystem.setCursor(new Cursor(Cursor.WAIT_CURSOR));  // TODO
    FsExplorer.jpLocalSystem.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (currentNodeLevel != 0 && currentNode.getFirstChild().toString().equals("")) {
      FsFolderHolder fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
      File currentFile = new File(fsFolderHolder.getPath());
      File childFiles[] = currentFile.listFiles();
      File subChildFiles[];
      int childFilesLength;
      int subChildFilesLength;
      if (childFiles != null) {
        childFilesLength = childFiles.length;
        currentNode.removeAllChildren();

        Arrays.sort(childFiles, new FolderFileComparatorLocal());
        for (int index = 0; index < childFilesLength; index++) {

          file = childFiles[index];
          if (file.isDirectory()) {
            childNode = new DefaultMutableTreeNode(file.getName());
            fsFolderHolder = new FsFolderHolder();
            fsFolderHolder.setName(file.getName());
            fsFolderHolder.setPath(file.getAbsolutePath());
            childNode.setUserObject(fsFolderHolder);

            currentNode.add(childNode);
            subChildFiles = file.listFiles();
            if (subChildFiles != null) {
              subChildFilesLength = subChildFiles.length;
              for (int counter = 0; counter < subChildFilesLength; counter++) {
                if (subChildFiles[counter].isDirectory()) {
                  subChildNode = new DefaultMutableTreeNode("");
                  fsFolderHolder = new FsFolderHolder();
                  fsFolderHolder.setName("");
                  fsFolderHolder.setPath("");
                  subChildNode.setUserObject(fsFolderHolder);
                  childNode.add(subChildNode);
                  break;
                }
              }
            }
          }
        }
      }
      //((DefaultTreeModel)treeLocalTreeView.getModel()).reload(currentNode);;
    }
   // FsClientGui.jpLocalSystem.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));                 TODO
    FsExplorer.jpLocalSystem.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * Required by TreeWillExpandListener interface.
   * @param e TreeExpansionEvent object
   */
  public void treeWillCollapse(TreeExpansionEvent e) {
    //saySomething("Tree-collapsed event detected", e);
  }

  /**
   *	To initialize the local tree to visualize home folder.
   */
  public void initializeLocalTreeView() {
    FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    treeLocalTreeView.setRootVisible(true);
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("My Computer");
    FsFolderHolder fsFolderHolder = new FsFolderHolder();
    fsFolderHolder.setName("My Computer");
    fsFolderHolder.setPath("");
    rootNode.setUserObject(fsFolderHolder);

    DefaultMutableTreeNode node, childNode, subChildNode;
    File fileRoots[] = File.listRoots();
    int fileRootsLength = fileRoots.length;
    for (int index = 0; index < fileRootsLength; index++) {
      if (!fileSystemView.isFloppyDrive(fileRoots[index])) {
        String nodeName = fileRoots[index].getAbsolutePath();
        logger.debug(" Root " + index + " : " + nodeName);
        node = new DefaultMutableTreeNode(nodeName);
        fsFolderHolder = new FsFolderHolder();
        fsFolderHolder.setName(nodeName);
        fsFolderHolder.setPath(fileRoots[index].getAbsolutePath());
        node.setUserObject(fsFolderHolder);
        rootNode.add(node);
        File files[] = fileRoots[index].listFiles();
        if (files != null) {
          childNode = new DefaultMutableTreeNode("");
          fsFolderHolder = new FsFolderHolder();
          fsFolderHolder.setName("");
          fsFolderHolder.setPath("");
          childNode.setUserObject(fsFolderHolder);
          node.add(childNode);
        }
      }
    }

    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(FsImage.imgFolderOpen);
    renderer.setClosedIcon(FsImage.imgFolderClosed);
    renderer.setOpenIcon(FsImage.imgFolderOpen);
    treeLocalTreeView.setCellRenderer(renderer);
    DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    treeLocalTreeView.setModel(treeModel);
    treeLocalTreeView.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    treeModel.reload();

    File fileHome = new File(System.getProperty("user.home"));
    String homeFolderPath = fileHome.getPath();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill =
      dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root, homeFolderPath);
    TreeNode treeNode[];
    TreePath treePath;
    logger.debug("Root Of Home Folder : " + nodeToFill);
    fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
    File fileHomeRoot = new File(fsFolderHolder.getPath());
    DefaultMutableTreeNode subTreeRoot = new DefaultMutableTreeNode();
    DefaultMutableTreeNode nodeToHighlight = new DefaultMutableTreeNode();
    constructSubTree(fileHome, fileHomeRoot, subTreeRoot, nodeToHighlight);
    logger.debug("Sub Tree Root : " + subTreeRoot);
    logger.debug("Node To Highlight : " + nodeToHighlight);
    logger.debug("nodeToExpand.getRoot() : " + nodeToHighlight.getRoot());

    File listFiles[] = fileHomeRoot.listFiles();

    int listFilesLength = listFiles.length;
    logger.debug("Current Node Which is Filling : " + nodeToFill);
    nodeToFill.removeAllChildren();

    Arrays.sort(listFiles, new FolderFileComparatorLocal());

    logger.debug("Current Node Which is Filling : " + nodeToFill);
    nodeToFill.removeAllChildren();
    for (int index = 0; index < listFilesLength; index++) {
      File file = listFiles[index];
      if (file.isDirectory()) {
        if (file.getAbsolutePath().equals(((FsFolderHolder)subTreeRoot.getUserObject()).getPath())) {
          nodeToFill.add(subTreeRoot);
          logger.debug("Node Matched : " + subTreeRoot);
          treeNode = subTreeRoot.getPath();
          treePath = new TreePath(treeNode);
        } else {
          logger.debug("Current Node Which is adding : " + file);
          childNode = new DefaultMutableTreeNode();
          fsFolderHolder = new FsFolderHolder();
          fsFolderHolder.setName(file.getName());
          fsFolderHolder.setPath(file.getAbsolutePath());
          childNode.setUserObject(fsFolderHolder);

          File subChildFiles[];
          int subChildFilesLength;
          subChildFiles = file.listFiles();
          if (subChildFiles != null) {
            subChildFilesLength = subChildFiles.length;
            logger.debug("subChildFilesLength : " + subChildFilesLength);
            for (int counter = 0; counter < subChildFilesLength; counter++) {
              if (subChildFiles[counter].isDirectory()) {
                subChildNode = new DefaultMutableTreeNode("");
                fsFolderHolder = new FsFolderHolder();
                fsFolderHolder.setName("");
                fsFolderHolder.setPath("");
                subChildNode.setUserObject(fsFolderHolder);
                childNode.add(subChildNode);
                break;
              }
            }
          }
          nodeToFill.add(childNode);
        }
      }
    }
    logger.debug(" Row Count For Tree : " + treeLocalTreeView.getRowCount());
    treeNode = ((DefaultMutableTreeNode)nodeToHighlight.getParent()).getPath();
    treePath = new TreePath(treeNode);
    treeLocalTreeView.expandPath(treePath);
    ((DefaultTreeModel)treeLocalTreeView.getModel()).reload(nodeToHighlight.getParent());
    treeLocalTreeView.setSelectionPath(new TreePath(nodeToHighlight.getPath()));
    treeLocalTreeView.requestFocus();
    treeLocalTreeView.scrollPathToVisible(new TreePath(nodeToHighlight.getPath()));
  }

  /**
   *	To construct a subtree.
   *	@param fileAddress File object of which subtree to be constructed
   *	@param filePathToFill File object to which the constructed subtree will be added.
   *	@param subTreeRoot an object of DefaultMutableTreeNode representing a sub node
   *	@param nodeToHighlight DefaultMutableTreeNode which gets selected.
   */
  public void constructSubTree(File fileAddress, File filePathToFill, DefaultMutableTreeNode subTreeRoot,
                               DefaultMutableTreeNode nodeToHighlight) {
    DefaultMutableTreeNode currNode, childNode, subChildNode;
    DefaultMutableTreeNode prevNode = null;
    String prevNodePath = "";
    FsFolderHolder fsFolderHolder;
    boolean flag = true;
    boolean isDirectory = true;

    while (!fileAddress.getAbsolutePath().equals(filePathToFill.getAbsolutePath())) {

      fsFolderHolder = new FsFolderHolder();
      fsFolderHolder.setName(fileAddress.getName());
      fsFolderHolder.setPath(fileAddress.getAbsolutePath());
      if (flag) {
        nodeToHighlight.setUserObject(fsFolderHolder);
        logger.debug("Node To Highlight : " + nodeToHighlight);
        currNode = nodeToHighlight;
        File[] subChildFiles = fileAddress.listFiles();
        int subChildFilesLength = subChildFiles.length;
        if (subChildFiles != null) {
          subChildFilesLength = subChildFiles.length;
          logger.debug("subChildFilesLength : " + subChildFilesLength);
          for (int counter = 0; counter < subChildFilesLength; counter++) {
            if (subChildFiles[counter].isDirectory()) {
              subChildNode = new DefaultMutableTreeNode("");
              fsFolderHolder = new FsFolderHolder();
              fsFolderHolder.setName("");
              fsFolderHolder.setPath("");
              subChildNode.setUserObject(fsFolderHolder);
              nodeToHighlight.add(subChildNode);
              break;
            }
          }
        }
        flag = false;
      } else {
        if (fileAddress.getParentFile().getAbsolutePath().equals(filePathToFill.getAbsolutePath())) {
          subTreeRoot.setUserObject(fsFolderHolder);
          logger.debug("Sub Tree Root : " + subTreeRoot);
          currNode = subTreeRoot;
        } else {
          currNode = new DefaultMutableTreeNode();
          currNode.setUserObject(fsFolderHolder);
        }
      }
      if (!currNode.equals(nodeToHighlight)) {
        logger.debug("Current Node Which is Filling : " + fileAddress.getName());
        File listFiles[] = fileAddress.listFiles();
        int listFilesLength = listFiles.length;

        Arrays.sort(listFiles, new FolderFileComparatorLocal());
        logger.debug("subTreeRoot Node : " + subTreeRoot);
        for (int index = 0; index < listFilesLength; index++) {
          File file = listFiles[index];
          if (file.isDirectory()) {
            logger.debug(" Node which is adding in current Node : " + file);
            if (prevNodePath.equals(file.getAbsolutePath())) {
              currNode.add(prevNode);
              logger.debug(" Node Matched : " + prevNode);
            } else {
              childNode = new DefaultMutableTreeNode();
              fsFolderHolder = new FsFolderHolder();
              fsFolderHolder.setName(file.getName());
              fsFolderHolder.setPath(file.getAbsolutePath());
              childNode.setUserObject(fsFolderHolder);
              File fileSubChildren[] = file.listFiles();
              if (fileSubChildren != null) {
                for (int i = 0; i < fileSubChildren.length; i++) {
                  if (fileSubChildren[i].isDirectory()) {
                    isDirectory = false;
                    break;
                  }
                }
                if (!isDirectory) {
                  subChildNode = new DefaultMutableTreeNode();
                  fsFolderHolder = new FsFolderHolder();
                  fsFolderHolder.setName("");
                  fsFolderHolder.setPath("");
                  subChildNode.setUserObject(fsFolderHolder);
                  childNode.add(subChildNode);
                  isDirectory = true;
                }
              }
              currNode.add(childNode);
            }
          }
        }
      }
      prevNode = currNode;
      prevNodePath = fileAddress.getAbsolutePath();
      logger.debug("prev Node : " + prevNode);
      logger.debug("prevNodePath : " + prevNodePath);
      fileAddress = fileAddress.getParentFile();
    }
    logger.debug("prev Node : " + prevNode);
    logger.debug("prevNodePath : " + prevNodePath);
    logger.debug("Sub Tree Root : " + subTreeRoot);
    logger.debug("Node To Highlight : " + nodeToHighlight);
  }


}
