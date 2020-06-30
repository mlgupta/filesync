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
  * Copyright (c) 1804-1805 DBSentry Corp.  All Rights Reserved.              *
  *                                                                           *
  *****************************************************************************
  * $Id$
  *****************************************************************************
  */
  package dbsentry.filesync.clientgui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation: 30-08-1806
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class FsStatusBar extends JPanel{
  private GridBagLayout grdBgLayout = new GridBagLayout();

  private JLabel lblSetMessage = new JLabel();
  
  private JLabel lblSetSesionName = new JLabel();
  
  private JLabel lblSetUserName = new JLabel();
  
  private Logger logger;

  /**
   * @param logger
   */
  public FsStatusBar(Logger logger) {
    try {
      this.logger=logger;
      jbInit();
    }
    catch (Exception e) {
      logger.error(e);
    }
  }

  private void jbInit() {
    this.setLayout(grdBgLayout);
    this.setBorder(BorderFactory.createEmptyBorder());

    //lblSetMessage.setText("Message");
    lblSetMessage.setBorder(BorderFactory.createEtchedBorder());
    lblSetMessage.setHorizontalAlignment(SwingConstants.LEFT);
    lblSetMessage.setToolTipText("Messages");
    lblSetMessage.setFont(new FsFont());
    
    //lblSetSesionName.setText("Session");
    lblSetSesionName.setBorder(BorderFactory.createEtchedBorder());
    lblSetSesionName.setHorizontalAlignment(SwingConstants.CENTER);
    lblSetSesionName.setToolTipText("Current Session");
    lblSetSesionName.setFont(new FsFont());
    
    //lblSetUserName.setText("User");
    lblSetUserName.setBorder(BorderFactory.createEtchedBorder());
    lblSetUserName.setHorizontalAlignment(SwingConstants.CENTER);
    lblSetUserName.setToolTipText("Connected User");
    lblSetUserName.setFont(new FsFont());

    this.add(lblSetMessage, new GridBagConstraints(0, 0, 1, 1, 40.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints
                                                   .BOTH, new Insets(0, 0, 0, 0), 0, 0));
    this.add(lblSetSesionName, new GridBagConstraints(1, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints
                                                      .BOTH, new Insets(0, 0, 0, 0), 0, 0));
    this.add(lblSetUserName, new GridBagConstraints(2, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints
                                                    .BOTH, new Insets(0, 0, 0, 0), 0, 0));
  }


  /**
   * @param setMessage
   */
  public void setLblSetMessage(String setMessage) {
    this.lblSetMessage.setText(setMessage);
  }

  /**
   * @param setSessionName
   */
  public void setLblSetSesionName(String setSessionName) {
    this.lblSetSesionName.setText(setSessionName);
  }

  /**
   * @param setUserName
   */
  public void setLblSetUserName(String setUserName) {
    this.lblSetUserName.setText(setUserName);
  }
}
