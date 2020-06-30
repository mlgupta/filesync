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
 * $Id: FsFileFolderPropertyPage.java,v 1.26 2006/04/14 13:58:19 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.clientgui.images.FsImage;

import dbsentry.filesync.clientgui.utility.GeneralUtil;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;


/**
 *	To provide an interface to view and manipulate the properties 
 *  of selected files and folder.
 *  @author              Deepali Chitkulwar
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Deepali Chitkulwar
 * 	Last Modfied Date:   08-07-2005 
 */
public class

FsFileFolderPropertyPage extends JDialog {
  private JTabbedPane jTabbedPane1 = new JTabbedPane();

  private JPanel jPanel1 = new JPanel();

  private JLabel jlblNoOfFilesFolders = new JLabel();

  private JLabel jLabel2 = new JLabel();

  private JLabel jLabel3 = new JLabel();

  private JLabel jLabel4 = new JLabel();

  private JLabel jLabel5 = new JLabel();

  private JLabel jlblType = new JLabel();

  private JLabel jlblLocation = new JLabel();

  private JLabel jlblSize = new JLabel();

  private JSeparator jSeparator1 = new JSeparator();

  private JButton btnOk = new JButton();

  private JButton btnCancel = new JButton();

  private String propertyPageFor;

  private int olderReadOnlyState;

  private File itemFiles[];

  private Logger logger;

  private GeneralUtil generalUtil;

  /**
   * To create a dialog box.
   * @param parent the frame which will act as parent of this dialog box
   * @param title title of this dialog box
   * @param modal boolean value indication if this dialog box id modal
   */
  public FsFileFolderPropertyPage(Frame parent, String title, boolean modal) {
    super(parent, title, modal);
    this.logger = Logger.getLogger("ClientLogger");
    this.generalUtil = new GeneralUtil();
    try {
      jbInit();
    } catch (Exception e) {
      logger.error(generalUtil.getStackTrace(e));
    }
    generalUtil.centerForm((JFrame)parent, this);
  }


  private void jbInit() throws Exception {
    this.setSize(new Dimension(280, 300));
    this.getContentPane().setLayout(null);
    this.setBounds(new Rectangle(10, 10, 280, 300));
    jTabbedPane1.setBounds(new Rectangle(5, 10, 255, 220));
    jPanel1.setLayout(null);
    jlblNoOfFilesFolders.setBounds(new Rectangle(80, 25, 170, 15));
    jlblNoOfFilesFolders.setText("0 Files,0 Folders");
    jLabel2.setBounds(new Rectangle(15, 10, 45, 50));
    jLabel2.setIcon(FsImage.imageMultipleItemProperties);
    jLabel3.setText("Type:");
    jLabel3.setBounds(new Rectangle(15, 95, 60, 15));
    jLabel4.setText("Location:");
    jLabel4.setBounds(new Rectangle(15, 125, 60, 15));
    jLabel5.setText("Size:");
    jLabel5.setBounds(new Rectangle(15, 155, 60, 15));
    jlblType.setBounds(new Rectangle(80, 95, 170, 15));
    jlblType.setText("All Types");
    jlblLocation.setBounds(new Rectangle(80, 125, 170, 15));
    jlblLocation.setText("/home/deepali");
    jlblSize.setBounds(new Rectangle(80, 155, 170, 15));
    jlblSize.setText("0 bytes");
    jSeparator1.setBounds(new Rectangle(5, 70, 245, 5));
    btnOk.setText("Ok");
    btnOk.setBounds(new Rectangle(110, 240, 75, 25));
    btnOk.addActionListener(new ActionListener() {
                              public void actionPerformed(ActionEvent e) {
                                btnOk_actionPerformed(e);
                              }
                            }
    );
    btnCancel.setText("Cancel");
    btnCancel.setBounds(new Rectangle(185, 240, 75, 25));
    btnCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    btnCancel_actionPerformed(e);
                                  }
                                }
    );
    jPanel1.add(jSeparator1, null);
    jPanel1.add(jlblSize, null);
    jPanel1.add(jlblLocation, null);
    jPanel1.add(jlblType, null);
    jPanel1.add(jLabel5, null);
    jPanel1.add(jLabel4, null);
    jPanel1.add(jLabel3, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jlblNoOfFilesFolders, null);
    jTabbedPane1.addTab("General", jPanel1);
    this.getContentPane().add(btnCancel, null);
    this.getContentPane().add(btnOk, null);
    this.getContentPane().add(jTabbedPane1, null);
  }

  /**
   * sets the Type of items.
   * @param type Type of items.
   */
  public void setType(String type) {
    jlblType.setText(type);
  }

  /**
   * Sets the Location.
   * @param location absolute path.
   */
  public void setLocation(String location) {
    jlblLocation.setText(location);
  }

  /**
   * Sets the Size.
   * @param size total size of items.
   */
  public void setSize(String size) {
    jlblSize.setText(size);
  }

  /**
   * Sets the No.Of files and folders.
   * @param fileCount no. of files.
   * @param folderCount no. of folders.
   */
  public void setNoOfFilesFolders(int fileCount, int folderCount) {
    jlblNoOfFilesFolders.setText(fileCount + "Files," + folderCount + "Folders");
  }


  public void setOldAttributes(File[] itemFiles, int readOnly) {
    this.itemFiles = itemFiles;
    this.olderReadOnlyState = readOnly;
  }

  private void btnOk_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  private void btnCancel_actionPerformed(ActionEvent e) {
    this.dispose();
  }


  /**
   * a setter to indicated if this property page is for local or remote system file.
   * @param propertyPageFor a constant FsMessage.FOR_LOCAL_FILESYSTEM or FsMessage.FOR_REMOTE_FILESYSTEM
   */
  public

  void setPropertyPageFor(String propertyPageFor) {
    this.propertyPageFor = propertyPageFor;
  }

  /**
   * a getter which indicated if this property page is for local or remote system file.
   * @return a constant FsMessage.FOR_LOCAL_FILESYSTEM or FsMessage.FOR_REMOTE_FILESYSTEM
   */
  public String getPropertyPageFor() {
    return propertyPageFor;
  }

}
