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
 * $Id: FsRdv.java,v 1.3 2005/08/01 07:57:49 jeet Exp $
 *****************************************************************************
 */
package dbsentry.filesync.rdv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.URI;

import java.util.ArrayList;
import java.util.List;

import net.jxta.ext.config.Address;
import net.jxta.ext.config.Configurator;
import net.jxta.ext.config.HttpTransport;
import net.jxta.ext.config.MulticastAddress;
import net.jxta.ext.config.TcpTransport;
import net.jxta.ext.config.TcpTransportAddress;
import net.jxta.ext.config.Transport;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *	Jxta client to provide connection with the jxta server.
 *  @author            Jeetendra Prasad
 *  @version            1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   06-07-2005
 */
public class FsRdv {
  private int httpPort = 9720;
  private int tcpPort = 9721;
  private static PeerGroup peerGroup;
  private Logger logger;
  /**
   * A constructor to initialize jxta client.
   * @param logger a logger object
   * @param configFile rdv configuration file
   */
  public FsRdv(Logger logger) {
    this.logger = logger;
    configureJxta();
  }

  /**
   * starts jxta client.
   * @throws java.lang.Exception raises exception if not able to start jxta client property
   */
  private void startJXTA() throws Exception {
    try {
      // create, and Start the default jxta peerGroup
      configureConfigurator(httpPort,tcpPort);
      if(peerGroup == null){
        peerGroup = PeerGroupFactory.newNetPeerGroup();
      }
    } catch (Exception ex) {
      logger.error(getStackTrace(ex));
      throw ex;
    }
  }

  /**
   * configures rdv to start at a perticula port.
   * @param jxtaHttpPort http port
   * @param jxtaTcpPort tcp port
   */
  private void configureConfigurator(int jxtaHttpPort,int jxtaTcpPort){
    Configurator config = null;
    try {
      File jxtaHome = new File(".jxta");
      if(jxtaHome.exists()){
        logger.debug("Deleting : " + jxtaHome.getAbsolutePath());
        if(!deleteItem(jxtaHome)){
          logger.error("Unable to delete : " + jxtaHome.getAbsolutePath());
          System.exit(1);
        }
      }
      jxtaHome.mkdirs();
      System.setProperty("JXTA_HOME",jxtaHome.getAbsolutePath());
      // create, and Start the default jxta peerGroup
      URI uri; 
      List list;
      Address address;
      Transport transport;
      List listAddress; 
      String localhost = InetAddress.getLocalHost().getHostAddress();
      String peerName = "DBSFileSyncRdv" + localhost;
      config = new Configurator(peerName,"FileSyncRdv","FileSyncRdv","FileSyncRdv");

      //configure relays      
      uri = new URI("http://rdv.jxtahosts.net/cgi-bin/relays.cgi?2");
      config.addRelay(uri);
      config.setRelay(true);
      config.setRelayDiscovery(true);
      config.setRelayIncoming(true);
      config.setRelayOutgoing(true);
      
      //configure Rendzevous      
      uri = new URI("http://rdv.jxtahosts.net/cgi-bin/rendezvous.cgi?2");
      config.addRendezVous(uri);
      config.setRendezVous(true);
      config.setRendezVousDiscovery(true);
      
      //configure Transport
      list = config.getTransports();
      for(int index = 0; index < list.size(); index++){
        transport = (Transport)list.get(index);
        if(transport instanceof TcpTransport){
          listAddress = transport.getAddresses();
          address = (Address)listAddress.get(0);
          uri = new URI("tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + jxtaTcpPort );
          address.setAddress(uri);
        }else if(transport instanceof HttpTransport){
          transport.setEnabled(true);
          transport.setIncoming(true);
          listAddress = transport.getAddresses();
          address = (Address)listAddress.get(0);
          uri = new URI("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + jxtaHttpPort );
          address.setAddress(uri);
        }
      }
     
      config.save();   
      printConfiguratorDetail(config);
    } catch (Exception ex) {
      ex.printStackTrace();  
    }
  }


  /**
   * configures jxta client
   */
  private void configureJxta(){
    logger.debug("configuring jxta");
    
    try{
      startJXTA();
      if(peerGroup != null){
        logger.debug("jxta configured");
      }
    }catch(Exception ex){
      logger.error(getStackTrace(ex));
    }
  }

  /**
   * stops jxta client.
   */
  public void stopJxta(){
    peerGroup.unref();
    peerGroup = null;
  }
  
 /**
   * to delete a file (recursively if it is a folder).
   * @param file file to delete.
   * @return return true is file is deleted successfully otherwise flase.
   */
   public boolean deleteItem(File file){
    //if file is directory then go inside recursively and delete the items first
    if(file.isDirectory()){
      File subItems[] = file.listFiles();
      int itemCount = subItems.length;
      if(itemCount > 0){
        for(int index = 0 ; index < itemCount; index++){
          if(subItems[index].isDirectory()){
            return deleteItem(subItems[index]);
          }else{
            if(!subItems[index].delete()){
              logger.error("Access denied to" + "\"" + subItems[index].getPath() + "\"");      
              return false;
            }
          }
        }
      }else{
        if(!file.delete()){
          logger.error("Access denied to" + "\"" + file.getPath() + "\"");      
          return false;
        }
      }
    }else{
      if(!file.delete()){
        logger.error("Access denied to" + "\"" + file.getPath() + "\"");      
        return false;
      }
    }
    
    return true;
  }  



  /**
   *	To get string representation of stack trace of an exception.
   *  @param  ex exception object whose stack trace has to be converted to string
   *  @return stack trace as string
   */

   public String getStackTrace(Exception ex){
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos);
    ex.printStackTrace(pw);
    pw.flush();
    pw.close();
    return baos.toString();

  }

  private void printConfiguratorDetail(Configurator config){
    List list,listAddress,listMulticastAddress;
    Transport transport;
    TcpTransportAddress tcpTransportAddress;
    MulticastAddress multicastAddress;
    Address address;
    URI uri;
    int counter;
    
    try{
      logger.debug("Config file path : " +  config.getHome().getAbsolutePath());      
      logger.debug("LocalHost : " +  InetAddress.getLocalHost().getHostAddress());
      logger.debug("isProxy : " +  config.isProxy());
      logger.debug("PeerProxyAddress : " +  config.getPeerProxyAddress());
  

      list = config.getRendezVous();
      for(int index = 0; index < list.size(); index++){
        logger.debug("RendezVous Address : " +  list.get(index));
      }
      logger.debug("isRendezVous : " +  config.isRendezVous());
      logger.debug("isRendezVousDiscovery : " +  config.isRendezVousDiscovery());

      list = config.getRelays();
      for(int index = 0; index < list.size(); index++){
        logger.debug("Relay Address : " +  list.get(index));
      }
 
      logger.debug("isRelay : " +  config.isRelay());
      logger.debug("isRelaysDiscovery : " +  config.isRelaysDiscovery());
      logger.debug("isRelayIncoming : " +  config.isRelayIncoming());
      logger.debug("isRelayOutgoing : " +  config.isRelayOutgoing());
    
      list = config.getTransports();
      for(int index = 0; index < list.size(); index++){
        transport = (Transport)list.get(index);
        logger.debug("Transport Scheme : " +  transport.getScheme());
        logger.debug(transport.getScheme() + " Enabled : " +  transport.isEnabled());
        logger.debug(transport.getScheme() + " Incoming : " +  transport.isIncoming());
        logger.debug(transport.getScheme() + " Outgoing : " +  transport.isOutgoing());
        
        listAddress = transport.getAddresses();
        for(int i = 0; i < listAddress.size(); i++){
          address = (Address)listAddress.get(i);
          if(address instanceof TcpTransportAddress){
            tcpTransportAddress = (TcpTransportAddress)address;
            listMulticastAddress = tcpTransportAddress.getMulticastAddresses();
            for(counter = 0; counter < listMulticastAddress.size(); counter++){
              multicastAddress = (MulticastAddress)listMulticastAddress.get(counter);
              logger.debug(transport.getScheme() + " MulticastAddress : " +  multicastAddress.getAddress());
            }
          }
          logger.debug(transport.getScheme() + " PortRange : " +  address.getPortRange());
          uri = address.getAddress();
          logger.debug(transport.getScheme() + " Address : " +  uri);
        }
        
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }  

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
