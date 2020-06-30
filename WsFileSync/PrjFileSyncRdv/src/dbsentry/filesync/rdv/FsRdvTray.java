package dbsentry.filesync.rdv;

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
 * $Id: FsRdvTray.java,v 1.2 2005/08/01 07:57:49 jeet Exp $
 *****************************************************************************
 */
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.tray.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *	This class starts the application and puts it under the system tray.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   11-07-2005
 */ 
public class FsRdvTray{

  SystemTray tray = SystemTray.getDefaultSystemTray();
  TrayIcon ti;
  FsRdv fsRdv;

  /**
   * Construct FsTray object.
   * @param p_fsClientGui the application main class
   */
  public FsRdvTray(FsRdv p_fsRdv) {
    this.fsRdv = p_fsRdv;
    JPopupMenu menu;
    JMenuItem menuItem;

    System.setProperty("javax.swing.adjustPopupLocationToFit", "false");
    menu = new JPopupMenu("A Menu");

    menuItem = new JMenuItem("Configure logger");
    menuItem.setMnemonic(KeyEvent.VK_C);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try{
          Desktop.open(new File("config/log4j.properties"));
        }catch(Exception ex){
          ex.printStackTrace();
        }
      }
    });
    menu.add(menuItem);
    
    menuItem = new JMenuItem("View Log");
    menuItem.setMnemonic(KeyEvent.VK_L);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try{
          File logFile = new File("log/filesync_rdv.log");
          Desktop.open(logFile);
        }catch(Exception ex){
          ex.printStackTrace();
        }
      }
    });
    menu.add(menuItem);
    menu.addSeparator();

//    ImageIcon icon = new ImageIcon(FsTray.class.getResource("images/butt_exit.gif"));
    menuItem = new JMenuItem("Exit");
    menuItem.setMnemonic(KeyEvent.VK_E);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tray.removeTrayIcon(ti);
        System.exit(0);
      }
    });
    menu.add(menuItem);

    // ImageIcon i = new ImageIcon("duke.gif");
    ImageIcon i = new ImageIcon(FsRdvTray.class.getResource("images/tray.gif"));
    ti = new TrayIcon(i, "DBSentry File Synchronizer", menu);
    ti.setIconAutoSize(true);
    tray.addTrayIcon(ti);
  }


  /**
   * The main function to start rdv application.
   * @param args command line arguments
   */
  public static void main(String[] args){
    try{
        System.out.println("Initializing Logger...");
        File logFolder = new File("log");
        if(!logFolder.exists()){
          logFolder.mkdir();
        }
        File file = new File("config/log4j.properties");
        if(file.exists()) {
          PropertyConfigurator.configureAndWatch(file.getAbsolutePath(),2000);
        }else{
          System.out.println("Unable to find log4j initialization file : " + file.getAbsolutePath() );
          System.exit(1);
        }
        Logger logger = Logger.getLogger("RdvLogger");
        logger.info("Logger initialized successfully");
        
        FsRdv fsRdv = new FsRdv(logger);
        new FsRdvTray(fsRdv);
        logger.info("jxta running...");
      }catch(Exception ex){
        ex.printStackTrace();
        System.exit(1);
      }    
  }
}
