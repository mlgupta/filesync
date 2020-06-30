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
 * $Id: JxtaServer.java,v 1.53 2006/09/08 04:37:26 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server.jxta;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.jxta.SecurePeerGroupJxta;
import dbsentry.filesync.server.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Stack;

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
import net.jxta.peergroup.PeerGroup;
//import net.jxta.peergroup.PeerGroupFactory;
//import net.jxta.peergroup.NetPeerGroupFactory;
import net.jxta.peergroup.PeerGroupFactory;
//import net.jxta.peergroup.WorldPeerGroupFactory;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaServerPipe;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * @author Jeetendra Prasad
 * @version 1.0
 * @Date of creation: 05-05-2005
 * @Last Modfied by :Saurabh Gupta
 * @Last Modfied Date:
 * @description This class illustrates how to utilize the JxtaBiDiPipe Reads in socket.adv
 * and attempts to bind to a JxtaServerPipe
 */
public class JxtaServer {

  private PeerGroup netPeerGroup = null;

  private PeerGroup secureNetPeerGroup = null;

  private Logger logger;

  private CommonUtil commonUtil;

  private ServerUtil serverUtil;

  /**
   * @param logger
   * @description Constructor For JxtaServer
   */
  public JxtaServer(Logger logger) {
    this.logger = logger;
    this.commonUtil = new CommonUtil(logger);
    this.serverUtil = new ServerUtil(logger);
  }


  /**
   * @param home node jxta home directory
   * @param name node given name (can be hostname)
   * @param groupConfigDocument jxta_config.xml document
   * @description Private method for Creates a PlatformConfig.master with peer name set to name.
   */
  private void createConfig(File home, String name, Document groupConfigDocument) {
    InputStream is = null;
    try {
      is = new FileInputStream("config/PlatformConfig.master");
      home.mkdirs();
      XMLDocument xmlDoc = (XMLDocument)StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, is);
      PlatformConfig platformConfig = (PlatformConfig)AdvertisementFactory.newAdvertisement(xmlDoc);

      platformConfig.setName(name);
      Configurator config = new Configurator(platformConfig);

      ArrayList relayList = getRdvOrRelayList(groupConfigDocument, "jxta-relay");

      if (relayList != null && relayList.size() > 0) {
        config.addRelays(relayList);
      }

      ArrayList rdvList = getRdvOrRelayList(groupConfigDocument, "jxta-rdv");
      if (rdvList != null && rdvList.size() > 0) {
        config.addRendezVous(rdvList);
      }

      PeerID peerID = getPeerID();
      config.setPeerId(peerID);
      config.setSecurity("server", "password");
      config.save();
    } catch (IOException e) {
      logger.error(serverUtil.getStackTrace(e));
      System.exit(1);
    } catch (ConfiguratorException ex) {
      logger.error(serverUtil.getStackTrace(ex));
      System.exit(1);
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
   * @param document jxta_config.xml document
   * @param rdvRelayTag 
   * @return rdvOrRelayList
   * @description Private Method For Fetching  Rdv And Relay List from jxta_config.xml document
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
      logger.error(serverUtil.getStackTrace(ex));
    }
    return rdvOrRelayList;
  }

  /**
   * @return peerID
   * @description Private Method for fetching the PeerID from jxta_config document 
   */
  private PeerID getPeerID() {
    try {
      FileInputStream fis = null;
      File fileJxtaPeerConfig = new File("config/jxta_peer_config.xml");

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder;

      builder = factory.newDocumentBuilder();

      fis = new FileInputStream(fileJxtaPeerConfig);

      Document peerConfigDocument = builder.parse(fis);

      NodeList nodeList = peerConfigDocument.getElementsByTagName("jxta-peer-property");
      int nodeListLength = nodeList.getLength();
      if (nodeListLength != 0) {
        NamedNodeMap namedNodeMap = nodeList.item(0).getAttributes();
        PeerID peerID = (PeerID)IDFactory.fromURI(new URI(namedNodeMap.getNamedItem("peerid").getNodeValue()));
        logger.debug("peer id : " + peerID.toString());
        return peerID;
      } else {
        logger.error("jxta-peer-property tag missing in ");
        System.exit(-1);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
    }
    return null;
  }


  /**
   * @param file file to delete.
   * @return return true is file is deleted successfully otherwise false.
   * @description to delete a file (recursively if it is a folder).
   */
  public boolean deleteItem(File file) {
    //if file is directory then go inside recursively and delete the items first
    Stack deleteFolders = new Stack();

    if (file.isDirectory()) {
      File subItems[] = file.listFiles();
      int itemCount = subItems.length;
      for (int index = 0; index < itemCount; index++) {
        if (subItems[index].isDirectory()) {
          deleteFolders.push(subItems[index]);
          deleteItem(subItems[index]);
        } else {
          if (!subItems[index].delete()) {
            logger.debug("Access denied to" + "\"" + subItems[index].getPath() + "\"");
            return false;
          }
        }
      }
    } else {
      if (!file.delete()) {
        logger.debug("Access denied to" + "\"" + file.getPath() + "\"");
        return false;
      } else {
        return true;
      }
    }
    File temp;
    while (!deleteFolders.isEmpty()) {
      temp = (File)deleteFolders.pop();
      logger.debug("Deleting ? : " + temp + temp.delete());
    }
    file.delete();
    return true;
  }


  /**
   * @return  serverPipe JxtaServerPipe Type
   * @description method for creates the server pipe by using of socket.adv file. 
   */
  public JxtaServerPipe createServerPipe() {
    logger.debug("Reading in pipe.adv");
    FileInputStream fis4pipe = null;
    JxtaServerPipe serverPipe = null;
    try {
      fis4pipe = new FileInputStream("config/pipe.adv");
      XMLDocument xmlDoc =
        (XMLDocument)StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, fis4pipe);
      PipeAdvertisement pipeAdv = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(xmlDoc);
      logger.debug("Pipe Id : " + pipeAdv.getPipeID());

      serverPipe = new JxtaServerPipe(secureNetPeerGroup, pipeAdv);

      // we want to block until a connection is established
      serverPipe.setPipeTimeout(0);

      //      //This will be used for ServetSocket
      //      fis4socket = new FileInputStream("config/socket.adv");
      //      XMLDocument xmlDocSocket = (XMLDocument)StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, fis4socket);
      //      socketAdv = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(xmlDocSocket);

    } catch (Exception e) {
      logger.error("failed to bind to the JxtaServerPipe due to the following exception");
      logger.error(serverUtil.getStackTrace(e));
      System.exit(-1);
    } finally {
      try {
        if (fis4pipe != null) {
          fis4pipe.close();
        }
      } catch (IOException e) {
        ;
      }
      return serverPipe;
    }
  }


  /**
   * @description method for setting the Jxta server configuration, and joins the securePeerGroupJxta.  
   */
  public void startJxta() {
    FileInputStream fis = null;
    boolean isServer = true;
    try {
      System.setProperty("net.jxta.tls.principal", "server");
      System.setProperty("net.jxta.tls.password", "password");
      System.setProperty("JXTA_HOME", "server");
      File home = new File(System.getProperty("JXTA_HOME", "server"));

      if (home.exists()) {
        if (!deleteItem(home)) {
          logger.error("Unable to delete : " + home.getAbsolutePath());
          System.exit(1);
        } else {
          logger.debug(home.getAbsolutePath() + " exists : " + home.exists());
        }
      }
      String localhost =
        "[" + InetAddress.getLocalHost().getHostName() + "]" + "--" + "[" + InetAddress.getLocalHost().getHostAddress() +
        "]";
      String peerName = "FileSyncServer -- " + localhost;

      logger.info("ServerNmae :" + peerName);
      File fileJxtaGroupConfig = new File("config/jxta_group_config.xml");

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder;

      builder = factory.newDocumentBuilder();

      fis = new FileInputStream(fileJxtaGroupConfig);

      Document groupConfigDocument = builder.parse(fis);

      createConfig(home, peerName, groupConfigDocument);

      // create, and Start the default jxta NetPeerGroup
      netPeerGroup = PeerGroupFactory.newNetPeerGroup();

      //SecurePeerGroupServer securePeerGroupServer = new SecurePeerGroupServer(logger, netPeerGroup, document);
      SecurePeerGroupJxta securePeerGroupJxta =
        new SecurePeerGroupJxta(logger, netPeerGroup, groupConfigDocument, isServer);
      secureNetPeerGroup = securePeerGroupJxta.getSecurePeerGroup();

    } catch (PeerGroupException pge) {
      // could not instantiate the group, print the stack and exit
      logger.error("fatal error : group creation failure");
      logger.error(serverUtil.getStackTrace(pge));
      System.exit(1);
    } catch (UnknownHostException uhe) {
      logger.error(serverUtil.getStackTrace(uhe));
      System.exit(1);
    } catch (Exception e) {
      logger.error(serverUtil.getStackTrace(e));
      System.exit(1);
    } finally {
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
   * @return secureNetPeerGroup
   * @description Method for Fetching the secureNetPeerGroup 
   */
  public PeerGroup getSecureNetPeerGroup() {
    return secureNetPeerGroup;
  }

  //  /**
  //   * @return socketAdv
  //   * @description Method for fetching the PipeAdvertisement
  //   */
  //  public PipeAdvertisement getSocketAdv() {
  //    return socketAdv;
  //  }

  //  public PeerGroup getNetPeerGroup() {
  //    return netPeerGroup;
  //  }

  //  }
}
