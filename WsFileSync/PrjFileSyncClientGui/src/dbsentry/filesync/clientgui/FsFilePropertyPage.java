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
  * $Id: FsFilePropertyPage.java,v 1.13 2005/05/29 07:05:05 jeet Exp 
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.FsFilePropertyPageRemote;
import dbsentry.filesync.common.FsMessage;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;

import java.util.Date;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

/**
 *	To provide an interface to view and manipulate the properties 
 *  of selected files.
 *  @author              Deepali Chitkulwar
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Deepali Chitkulwar
 * 	Last Modfied Date:   08-07-2005 
 */
public class FsFilePropertyPage extends JDialog {
  private JTabbedPane jTabbedPane1 = new JTabbedPane();

  private JPanel jPanel1 = new JPanel();

  private JLabel jlblFileIcon = new JLabel();

  private JTextField jtfFileName = new JTextField();

  private JSeparator jSeparator1 = new JSeparator();

  private JLabel jLabel1 = new JLabel();

  private JSeparator jSeparator2 = new JSeparator();

  private JLabel jLabel3 = new JLabel();

  private JLabel jLabel4 = new JLabel();

  private JSeparator jSeparator3 = new JSeparator();

  private JSeparator jSeparator4 = new JSeparator();

  private JLabel jLabel6 = new JLabel();

  private JLabel jLabel7 = new JLabel();

  private JButton btnOk = new JButton();

  private JButton btnCancel = new JButton();

  private JLabel jLabel9 = new JLabel();

  private JLabel jlblTypeOfFile = new JLabel();

  private JLabel jlblLocation = new JLabel();

  private JLabel jlblSize = new JLabel();

  private JLabel jlblCreated = new JLabel();

  private JLabel jlblModified = new JLabel();

  private PropertyChangeSupport propertyChangeSupport;

  private JLabel jlblPermissions = new JLabel();

  private String propertyPageFor;

  private String oldFileName;

  private Logger logger;

  private GeneralUtil generalUtil;

  /**
   * Construct FsFilePropertyPage object.
   * @param parent the frame which whill act as parent of this dialog box
   * @param title title of this dialog box
   * @param modal boolean value which indicates if dialog box is modal
   */
  public FsFilePropertyPage(Frame parent, String title, boolean modal) {
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
    this.setSize(new Dimension(340, 380));
    this.getContentPane().setLayout(null);
    jTabbedPane1.setBounds(new Rectangle(5, 5, 315, 310));
    jPanel1.setLayout(null);
    jPanel1.setOpaque(false);
    jtfFileName.setText("File name");
    jtfFileName.setBounds(new Rectangle(95, 15, 200, 25));

    jSeparator1.setBounds(new Rectangle(0, 55, 310, 5));
    jLabel1.setText("Type of File:");
    jLabel1.setBounds(new Rectangle(10, 65, 90, 15));
    jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    jSeparator2.setBounds(new Rectangle(0, 95, 310, 5));
    jLabel3.setText("Location:");
    jLabel3.setBounds(new Rectangle(10, 110, 90, 15));
    jLabel4.setText("Size:");
    jLabel4.setBounds(new Rectangle(10, 135, 90, 15));
    jSeparator3.setBounds(new Rectangle(0, 165, 315, 2));
    jSeparator4.setBounds(new Rectangle(0, 235, 330, 5));
    jLabel6.setText("Created:");
    jLabel6.setBounds(new Rectangle(10, 180, 90, 15));
    jLabel7.setText("Modified:");
    jLabel7.setBounds(new Rectangle(10, 205, 90, 15));
    btnOk.setText("Ok");
    btnOk.setBounds(new Rectangle(165, 320, 75, 25));
    btnOk.addActionListener(new ActionListener() {
                              public void actionPerformed(ActionEvent e) {
                                btnOk_actionPerformed(e);
                              }
                            }
    );
    btnCancel.setText("Cancel");
    btnCancel.setBounds(new Rectangle(245, 320, 75, 25));
    btnCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    btnCancel_actionPerformed(e);
                                  }
                                }
    );
    jLabel9.setText("Attributes:");
    jLabel9.setBounds(new Rectangle(10, 250, 90, 15));
    jlblTypeOfFile.setBounds(new Rectangle(100, 65, 195, 15));
    jlblTypeOfFile.setText("Generic File");
    jlblLocation.setBounds(new Rectangle(100, 110, 190, 15));
    jlblLocation.setText("/home/deepali");
    jlblSize.setBounds(new Rectangle(100, 135, 190, 15));
    jlblSize.setText("0 bytes");
    jlblCreated.setBounds(new Rectangle(100, 180, 195, 15));
    jlblCreated.setText("Friday ,March 11,2005");
    jlblModified.setBounds(new Rectangle(100, 205, 195, 15));
    jlblModified.setText("Friday ,March 11,2005");
    jlblPermissions.setBounds(new Rectangle(100, 250, 190, 15));
    jlblFileIcon.setBounds(new Rectangle(15, 10, 45, 35));
    jPanel1.add(jlblPermissions, null);
    jPanel1.add(jlblFileIcon, null);
    jPanel1.add(jlblModified, null);
    jPanel1.add(jlblCreated, null);
    jPanel1.add(jlblSize, null);
    jPanel1.add(jlblLocation, null);
    jPanel1.add(jlblTypeOfFile, null);
    jPanel1.add(jLabel9, null);
    jPanel1.add(jLabel7, null);
    jPanel1.add(jLabel6, null);
    jPanel1.add(jSeparator4, null);
    jPanel1.add(jSeparator3, null);
    jPanel1.add(jLabel4, null);
    jPanel1.add(jLabel3, null);
    jPanel1.add(jSeparator2, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jSeparator1, null);
    jPanel1.add(jtfFileName, null);
    this.getContentPane().add(btnCancel, null);
    this.getContentPane().add(btnOk, null);
    jTabbedPane1.addTab("General", jPanel1);
    this.getContentPane().add(jTabbedPane1, null);
  }

  /**
   * adds property change support for properties in this class.
   * @param propertyChangeListener PropertyChangeListener object
   */
  public void addPropertyChangeSupport(PropertyChangeListener propertyChangeListener) {
    propertyChangeSupport = new PropertyChangeSupport(this);
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * setter for type of file.
   * @param fileType type of file.
   */
  public void setTypeOfFile(String fileType) {
    jlblTypeOfFile.setText(fileType);
  }

  /**
   * setter for the location of this file.
   * @param location absolute path.
   */
  public void setLocation(String location) {
    jlblLocation.setText(location);
  }

  /**
   * setter for the size of this file.
   * @param size size of file.
   */
  public void setSize(String size) {
    jlblSize.setText(size);
  }

  /**
   * setter for the creation date of this file.
   * @param date Date object containing creation date.
   */
  public void setCreationDate(Date date) {
    jlblCreated.setText(date.toString());
    ;
  }

  /**
   * setter for modified date of this file.
   * @param date modification date.
   */
  public void setModifiedDate(String date) {
    jlblModified.setText(date);
  }

  /**
   * setter for name of this file.
   * @param fileName name of file.
   */
  public void setFileName(String fileName) {
    jtfFileName.setText(fileName);
  }

  private void btnOk_actionPerformed(ActionEvent e) {
    String newFileName = jtfFileName.getText();
    if (propertyPageFor.equals(FsMessage.FOR_LOCAL_FILESYSTEM)) {
      if (oldFileName.equals(newFileName)) {
        this.dispose();
        return;
      } else {
        logger.debug("oldFileName : " + oldFileName);
        File oldFile = new File(jlblLocation.getText() + File.separator + oldFileName);
        File newFile = new File(jlblLocation.getText() + File.separator + newFileName);
        oldFile.renameTo(newFile);
        String refreshFolderPath = newFile.getParent();
        propertyChangeSupport.firePropertyChange("refreshFolderPath", null, refreshFolderPath);
      }
    } else {
      FsFilePropertyPageRemote fsFilePropertyPageRemote = new FsFilePropertyPageRemote();
      fsFilePropertyPageRemote.setOldName(oldFileName);
      fsFilePropertyPageRemote.setLocation(jlblLocation.getText());
      if (!newFileName.equals(oldFileName)) {
        fsFilePropertyPageRemote.setName(newFileName);
        propertyChangeSupport
        .firePropertyChange(new PropertyChangeEvent(propertyPageFor, "fsFilePropertyPageRemote", null,
                                                                         fsFilePropertyPageRemote));
      }
    }
    this.dispose();
  }

  private void btnCancel_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  /**
   * setter for the old file name.
   * @param oldFileName old file name
   */
  public void setOldFileInfo(String oldFileName) {
    this.oldFileName = oldFileName;
  }

  /**
   * a getter for the file icon.
   * @return jlblFileIcon a label which contains file icon 
   */
  public void setJlblFileIcon(Icon fileIcon) {
    jlblFileIcon.setIcon(fileIcon);
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
  public

  String getPropertyPageFor() {
    return propertyPageFor;
  }


  /**
   * a getter for the file permission.
   * @param permissions String containing permissions of  file.
   */
  public void setPermissions(String permissions) {
    jlblPermissions.setText(permissions);
  }

}
