/*
 *****************************************************************************
 *                       Confidentiality Information                         *
 *                                                                           *
 * This module is the confidential and proprietary information of            *
 * DBSentry Corp.; it is not to be copied, reproduced, or transmitted in any *
 * form, by any means, in whole or in part, nor is it to be used for any     *
 * purpose other than that for which it is expressly provided without the    *
 * written permission of DBSentry Corp.                                      *
 *                                                                           *       *******From FileSync********
 * Copyright (c) 2004-2005 DBSentry Corp.  All Rights Reserved.              *
 *                                                                           *
 *****************************************************************************
 * $Id: JxtaClient.java,v 1.62 2006/09/07 11:34:22 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client.jxta;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.constants.FsConnectionConstants;
import dbsentry.filesync.common.jxta.SecurePeerGroupJxta;
import dbsentry.filesync.common.listeners.FsConnectionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.ext.config.Configurator;
import net.jxta.id.IDFactory;
import net.jxta.impl.protocol.PlatformConfig;
import net.jxta.peer.PeerID;
//import net.jxta.peergroup.NetPeerGroupFactory;
import net.jxta.peergroup.PeerGroup;
//import net.jxta.peergroup.WorldPeerGroupFactory;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import net.jxta.util.JxtaBiDiPipe;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


//import net.jxta.peergroup.PeerGroupFactory;


/**
 * @author Jeetendra Prasad
 * @version 1.0
 * @Date of creation: 07-05-2005
 * @Last Modfied by : Saurabh Gupta
 * @Last Modfied Date:
 *@description This Class  illustrates how to utilize the JxtaBiDiPipe Reads in socket.adv
 * and attempts to bind to a JxtaServerPipe
 */
public class JxtaClient implements RendezvousListener, Runnable {

  private PeerGroup netPeerGroup = null;

  private PeerGroup secureNetPeerGroup = null;

  private PipeAdvertisement pipeAdv;
  
  private JxtaBiDiPipe bidipipe=null;

  private RendezVousService rendezvous;

  private Logger logger;

  private File platformConfigFile;

  private File jxtaGroupConfig;

  private PropertyChangeSupport jxtaConnectionPropertyChangeSupport;

  private CommonUtil commonUtil;

  private ClientUtil clientUtil;
  
  private PeerID peerId=null;
  
  ArrayList rdvList=null; 
  
  ArrayList relayList=null;
  
  /**
   * @param logger Logger Type
   * @param jxtaGroupConfig File Type
   * @param platformConfigFile File Type
   * @description constructor for JxtaClient
   */
  public JxtaClient(Logger logger,  File jxtaGroupConfig, File platformConfigFile,ArrayList rdvList, ArrayList relayList) {
    this.logger = logger;
    this.jxtaGroupConfig = jxtaGroupConfig;
    this.platformConfigFile = platformConfigFile;
    this.rdvList=rdvList;
    this.relayList=relayList;
    this.commonUtil = new CommonUtil(logger);
    this.clientUtil = new ClientUtil(logger);
    
  }

  /**
     *  void method for Starts jxta
     */
  public void run() {
    FileInputStream fis = null;
    SecurePeerGroupJxta securePeerGroupJxta=null;
    boolean isServer=false;
    try {
      String userHome = System.getProperty("user.home");
      System.setProperty("net.jxta.tls.principal", "client");
      System.setProperty("net.jxta.tls.password", "password");
      System.setProperty("JXTA_HOME", userHome + "/.dbsfs/client");
      File home = new File(System.getProperty("JXTA_HOME", "client"));

      
      //File home = new File(System.getProperty("JXTA_HOME", "client"));

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder;
      builder = factory.newDocumentBuilder();
     
      fis = new FileInputStream(jxtaGroupConfig);

      Document document = builder.parse(fis);

      if (!home.exists()) {
        String localhost =
          InetAddress.getLocalHost().getHostAddress() + "--" + InetAddress.getLocalHost().getHostName();
        String peerName = "FileSyncClient" + localhost;
        createConfig(home, peerName, document);
      }


      // create, and Start the default jxta NetPeerGroup
      logger.debug(home.getAbsolutePath());
      
      netPeerGroup = PeerGroupFactory.newNetPeerGroup();
      jxtaConnectionPropertyChangeSupport
      .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.LOCATING_IN_LAN), null,
                                                                                     Boolean.valueOf(true)));
        
      securePeerGroupJxta = new SecurePeerGroupJxta(logger, netPeerGroup, document ,isServer);
      secureNetPeerGroup = securePeerGroupJxta.getSecurePeerGroup();

      if (secureNetPeerGroup == null) {
        jxtaConnectionPropertyChangeSupport
        .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.EXIST_IN_LAN), null,
                                                                                       Boolean.valueOf(false)));
        jxtaConnectionPropertyChangeSupport
        .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.CONNECTING_TO_INTERNET), null,
                                                                                       Boolean.valueOf(true)));
        rendezvous = netPeerGroup.getRendezVousService();
        rendezvous.addListener(this);
        waitForRendezvousConncection();
        if (rendezvous.isConnectedToRendezVous()) {
          jxtaConnectionPropertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.LOCATING_IN_INTERNET), null,
                                                                                         Boolean.valueOf(true)));
          
          securePeerGroupJxta = new SecurePeerGroupJxta(logger, netPeerGroup, document ,isServer);
                    secureNetPeerGroup = securePeerGroupJxta.getSecurePeerGroup();
          if (secureNetPeerGroup == null) {
            jxtaConnectionPropertyChangeSupport
            .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.EXIST_IN_INTERNET), null,
                                                                                           Boolean.valueOf(false)));
          } else {
            jxtaConnectionPropertyChangeSupport
            .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.EXIST_IN_INTERNET), null,
                                                                                           Boolean.valueOf(true)));
          }
        }
      } else {
        jxtaConnectionPropertyChangeSupport
        .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.EXIST_IN_LAN), null,
                                                                                       Boolean.valueOf(true)));
      }
      if (secureNetPeerGroup != null) {
        secureNetPeerGroup.startApp(null);        
      }

    } catch (PeerGroupException e) {
      // could not instantiate the group, print the stack and exit
      logger.error("fatal error : group creation failure");
      logger.error(clientUtil.getStackTrace(e));
    } catch (UnknownHostException ex) {
      logger.error(clientUtil.getStackTrace(ex));
    } catch (Exception e) {
      logger.error(clientUtil.getStackTrace(e));
    } finally {
      logger.debug("Inside Finally of Run method");
      try {
        if (fis != null) {
          fis.close();
        }
      } catch (IOException e) {
        ;
      }
    }
  }

  /**
     *  Private method for Create a PlatformConfig with peer name set to name
     *
     *@param  home  node jxta home directory
     *@param  name  node given name (can be hostname)
     *@param  document  jxta_config document
     */
  private void createConfig(File home, String name, Document document) {
    InputStream is = null;
    try {
      home.mkdirs();
      is = new FileInputStream(platformConfigFile);
      XMLDocument xmlDoc = (XMLDocument)StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, is);
      PlatformConfig platformConfig = (PlatformConfig)AdvertisementFactory.newAdvertisement(xmlDoc);

      platformConfig.setName(name);

      Configurator config = new Configurator(platformConfig);
      
      ArrayList rdvListLocal=getRdvOrRelayList(document, "jxta-rdv");
      if(rdvListLocal!=null && rdvListLocal.size()>0){
        rdvList.addAll(rdvListLocal);
      }
      
      if (rdvList.size() > 0) {
        config.addRendezVous(rdvList);
      }
      
      ArrayList relayListLocal=getRdvOrRelayList(document, "jxta-relay");
      if(relayListLocal!=null && relayListLocal.size()>0){
        relayList.addAll(relayListLocal);
      }

      if (relayList.size() > 0) {
        config.addRelays(relayList);
      }
      config.setSecurity("client", "password");
      config.save();

    } catch (IOException e) {
      logger.error(clientUtil.getStackTrace(e));
    } catch (ConfiguratorException ex) {
      logger.error(clientUtil.getStackTrace(ex));
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException e) {
        ;
      }
    }

  }

  /**
   * @param document jxta_config document
   * @param rdvRelayTag String rdvrelay Tag
   * @return rdvOrRelayList
   * @description private method for fetching the RdvRelay List from jxta_config document.
   */
  private ArrayList getRdvOrRelayList(Document document, String rdvRelayTag) {
    ArrayList rdvOrRelayList = null;
    try {

      NodeList nodeList = document.getElementsByTagName(rdvRelayTag);

      if (nodeList != null) {
        int nodeListLength = nodeList.getLength();
        if (nodeListLength != 0) {
          rdvOrRelayList = new ArrayList();
          NamedNodeMap namedNodeMap;
          String relayaddress = null;
          for (int index = 0; index < nodeListLength; index++) {
            namedNodeMap = nodeList.item(index).getAttributes();
            relayaddress = namedNodeMap.getNamedItem("address").getNodeValue();
            if (relayaddress.trim().length() != 0) {
              rdvOrRelayList.add(new URI(relayaddress));
            }
          }
          logger.debug(rdvOrRelayList);
        }
      }

    } catch (Exception ex) {
      logger.error(clientUtil.getStackTrace(ex));
    }
    return rdvOrRelayList;
  }

  /**
    
   * @return peerID
   * @description method for Fetching PeerID from fileGroupConfig File.
   */
  public PeerID getPeerId() {
      return peerId;
  }
  
  /**
   * @param event
   * @description 
   */
  public synchronized void rendezvousEvent(RendezvousEvent event) {
    if (event.getType() == event.RDVCONNECT || event.getType() == event.RDVRECONNECT) {
      logger.debug("RDV Peer : " + event.getPeer().toString());
      notify();
    }
  }

  /**
     * private method for awaits a rendezvous connection for specified time.
     */
  private synchronized void waitForRendezvousConncection() {
    if (!rendezvous.isConnectedToRendezVous()) {
      logger.debug("Waiting for Rendezvous Connection");
      try {
        wait(30000);
      } catch (InterruptedException e) {
        ;
        // got our notification
      } finally {
        if (rendezvous.isConnectedToRendezVous()) {
          logger.debug("Connected to Rendezvous");
          jxtaConnectionPropertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.IS_INTERNET_AVAILABLE), null,
                                                                                         Boolean.valueOf(true)));
        } else {
          logger.debug("Unable to Connect to Rendezvous");
          jxtaConnectionPropertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.IS_INTERNET_AVAILABLE), null,
                                                                                         Boolean.valueOf(false)));
        }
      }
    }
  }

  /**
   * @description void method for creates a JxtaBidiPipe.
   */
  public void createJxtaBiDiPipe(String peerIdString,String pipeIdString) {
    boolean success = false;
    
    try {
    
      String pipeAdvString=" <?xml version=\"1.0\"?> ";
      pipeAdvString+=" <!DOCTYPE jxta:PipeAdvertisement> ";
      pipeAdvString+=" <jxta:PipeAdvertisement xmlns:jxta=\"http://jxta.org\"><Id>";
      pipeAdvString+=  pipeIdString;
      pipeAdvString+=" </Id><Type>JxtaUnicast</Type> ";
      pipeAdvString+=" <Name>FileSync</Name> ";
      pipeAdvString+=" </jxta:PipeAdvertisement> ";
    
      jxtaConnectionPropertyChangeSupport
      .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.CONNECTING), null, Boolean.valueOf(true)));
      
      XMLDocument xmlDoc = (XMLDocument)StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, new ByteArrayInputStream(pipeAdvString.getBytes()));
      pipeAdv = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(xmlDoc);
      logger.debug("creating the BiDi pipe");
      bidipipe = new JxtaBiDiPipe();
      bidipipe.setReliable(true);
      logger.debug("Attempting to establish a connection");
      peerId = (PeerID)IDFactory.fromURI(new URI(peerIdString));
      logger.debug("Peer Id : " + peerId.toString());
      logger.debug("Pipe Id : " + pipeAdv.getID());
      bidipipe.connect(secureNetPeerGroup, peerId, pipeAdv, 30000, null);
      logger.info("connected");
      success = true;
    } catch (Exception e) {
      logger.error("failed to bind the JxtaBiDiPipe due to the following exception");
      logger.error(clientUtil.getStackTrace(e));
    } finally {
      jxtaConnectionPropertyChangeSupport
      .firePropertyChange(new PropertyChangeEvent(this, Integer.toString(FsConnectionConstants.CONNECTED), null,
                                                                                     Boolean.valueOf(success)));                                                                                    
    }

  }

  public JxtaBiDiPipe getBidipipe() {
    return bidipipe;
  }
  
  public void closeBidipipe(){
    try {
      if (bidipipe != null && bidipipe.isBound()) {
        bidipipe.close();
        logger.debug("BiDi pipe Closed");
      }
    } catch (Exception ex) {
      logger.error(clientUtil.getStackTrace(ex));
    }
  }
  

  /**
   * void method for stops jxta client.
   */
  public void stopJxta() {
    
    closeBidipipe();
    
    if (secureNetPeerGroup != null) {
      secureNetPeerGroup.stopApp();
      secureNetPeerGroup.unref();
      secureNetPeerGroup = null;
      logger.debug("Unrefering secureNetPeerGroup");
    }
    if (netPeerGroup != null) {
      netPeerGroup.stopApp();
      netPeerGroup.unref();
      netPeerGroup = null;
      logger.debug("Unrefering netPeerGroup");
    }
  }

  /**
   * To add a property change listener for this class.
   * @param connectionListener FsConnectionListener object which will listen for the jxta configured property of this class
   */
  public void addJxtaConnectionListener(FsConnectionListener connectionListener) {
    if (jxtaConnectionPropertyChangeSupport == null) {
      jxtaConnectionPropertyChangeSupport = new PropertyChangeSupport(this);
    }
    jxtaConnectionPropertyChangeSupport.addPropertyChangeListener(connectionListener);
  }

  
  
  

  /**
   * @return secureNetPeerGroup
   * @description method for Fetching secureNetPeergroup.
   */
  public PeerGroup getSecureNetPeerGroup() {
    return secureNetPeerGroup;
  }

  /**
   * @return pipeAdv
   * @description Method for Fetching Pipe Advertisement.
   */
  public PipeAdvertisement getPipeAdv() {
    return pipeAdv;
  }

  
}

