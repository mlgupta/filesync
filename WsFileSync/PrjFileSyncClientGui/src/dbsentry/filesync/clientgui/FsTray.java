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
 * $Id: FsTray.java,v 1.48 2006/06/29 12:47:03 sgupta Exp $
 *****************************************************************************
*/
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.FsClient;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description
 * @Date of creation: 13-03-2006
 * @Last Modfied by : Saurabh Gupta
 * @Last Modfied Date: 13-04-2006
 */
public class FsTray {
  private Logger logger;
  private boolean trayLaunched=false;
  
     
  public static void main(String[] args) {
   
    try {
      // For Native Look and Feel    
      try {
        //System.out.println("Look and Feel Class Name :  " + UIManager.getCrossPlatformLookAndFeelClassName());

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      FsTray fsTray=new FsTray();
      fsTray.initializeLogger();
      fsTray.launchApplication();
    }
    catch (Exception e) {
      e.printStackTrace();
      e.printStackTrace();
      System.exit(1);
    }

  }

  private FileSyncClientMDI  createMDIWindow(){
    FileSyncClientMDI window = new FileSyncClientMDI(logger);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension windowSize = window.getSize();

    if (windowSize.height > screenSize.height) {
      windowSize.height = screenSize.height;
    }

    if (windowSize.width > screenSize.width) {
      windowSize.width = screenSize.width;
    }
    window.setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
    window.setVisible(false);
    
    return window;
    
  }

  public void launchApplication() {
     try {
      
      logger.debug("Creating MDI Window");
      
      //Instantiating and Lauching Fs Prefrerence Window 
      FsPreferenceUI fsPreferenceUI= new FsPreferenceUI(logger,this,createMDIWindow());
      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      
      Dimension frameSize = fsPreferenceUI.getSize();

      if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
      }

      if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
      }
      
      fsPreferenceUI.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
      fsPreferenceUI.setVisible(true);
      
    }
    catch (Exception e) {
      logger.debug(e.getMessage());
      e.printStackTrace();
    }
  }

  private void initializeLogger() {
    try {
      System.out.println("Initializing Logger...");
      
      String userHome = System.getProperty("user.home");
      
      File mimeTypeFolder = new File(userHome + "/.dbsfs/mimetype");
      if (!mimeTypeFolder.exists()) {
        mimeTypeFolder.mkdirs();
      }

      File logFolder = new File(userHome + "/.dbsfs/log");
      if (!logFolder.exists()) {
        logFolder.mkdirs();
      }
      
      File preferencesFolder = new File(userHome + "/.dbsfs/preferences/");
      if (!preferencesFolder.exists()) {
        preferencesFolder.mkdirs();
      }
      
      
      
      File syncProfileFolder = new File(userHome + "/.dbsfs/syncprofile/"); 
      if (!syncProfileFolder.exists()) {
        syncProfileFolder.mkdirs();
      }
      
      File miscFolder = new File(userHome + "/.dbsfs/misc");

      if (!miscFolder.exists()) {
        miscFolder.mkdirs();
      }
      
      
      File abcFile = new File(userHome + "/.dbsfs/mimetype/abc");
      if (!abcFile.exists()) {
        abcFile.createNewFile();
      }

      File log4jFile = new File("config/log4j.properties");
      
      if (log4jFile.exists()) {
        PropertyConfigurator.configureAndWatch(log4jFile.getAbsolutePath(), 2000);
      }
      else {
        System.out.println("The application was unable to initialize logger properly.");
        System.out.println("log4j-initialization-file : '" + log4jFile.getAbsolutePath() + "'");
        System.out.println("The application will exit now!");
        System.exit(1);
      }

      logger = Logger.getLogger("ClientLogger");
      
      logger.info("Logger initialized successfully");

    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }
  
  public void prepareTray(FileSyncClientMDI inmdiWindow) {
    
    final SystemTray tray = SystemTray.getDefaultSystemTray();

    final FileSyncClientMDI mdiWindow = inmdiWindow;

    JPopupMenu menu;
    JMenuItem menuItem;

    System.setProperty("javax.swing.adjustPopupLocationToFit", "false");
    menu = new JPopupMenu("A Menu");

  
    final TrayIcon ti =
      new TrayIcon(new ImageIcon(FsTray.class.getResource("images/FileSyncTray.gif")), "DBSentry File Synchronizer",
                                     menu);
    ti.setIconAutoSize(true);
    ti.addActionListener(new ActionListener() {
                           public void actionPerformed(ActionEvent e) {
                             mdiWindow.setVisible(true);
                           }
                         }
    );
    tray.addTrayIcon(ti);


    menuItem = new JMenuItem("Open DBSentry File Synchronizer");
    menuItem.setMnemonic(KeyEvent.VK_O);
    menuItem.setIcon(new ImageIcon(FsTray.class.getResource("images/FileSyncTray.gif")));
    menuItem.addActionListener(new ActionListener() {
                                 public void actionPerformed(ActionEvent e) {
                                     mdiWindow.setVisible(true);
                                 }
                               }
    );
    menu.add(menuItem);
    menu.addSeparator();


    menuItem = new JMenuItem("View Log");
    menuItem.setMnemonic(KeyEvent.VK_L);
    menuItem.setIcon(new ImageIcon(FsTray.class.getResource("images/menu_index.gif")));
    menuItem.addActionListener(new ActionListener() {
                                 public void actionPerformed(ActionEvent e) {
                                   try {
                                     File logFile =
                                       new File(System.getProperty("user.home") + "/.dbsfs/log/filesync_client.log");
                                     Desktop.open(logFile);
                                   }
                                   catch (Exception ex) {
                                     ex.printStackTrace();
                                   }
                                 }
                               }
    );
    menu.add(menuItem);
    menu.addSeparator();
    
    
    menuItem = new JMenuItem("Exit");
    menuItem.setMnemonic(KeyEvent.VK_E);
    menuItem.setIcon(new ImageIcon(FsTray.class.getResource("images/menu_exit.gif")));
    menuItem.addActionListener(new ActionListener() {
                                 public void actionPerformed(ActionEvent e) {
                                   tray.removeTrayIcon(ti);
                                   System.exit(0);
                                 }
                               }
    );
    menu.add(menuItem);


  }


  public void setTrayLaunched(boolean trayLaunched) {
    this.trayLaunched = trayLaunched;
  }

  public boolean isTrayLaunched() {
    return trayLaunched;
  }
}
