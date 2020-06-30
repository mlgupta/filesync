package filesync.examples.upload;
import dbsentry.filesync.client.FsFileSystemOperationsRemote;
import dbsentry.filesync.client.jxta.JxtaClient;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.FsUser;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Stack;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;



/**
 * This example demonstrates the use of FileSyncClient.jar and FileSyncCommon.jar by 
 * uploading folder/file to FileSync server. It starts by reading various configuration 
 * files, initializing logger, initializing jxta, starting jxta and using jxta to send 
 * request and receive response. The configuration files used are 
 *    1. jxta_config.xml  :-  Contains the parameter to create secure group. 
 *                            and specify the rdv and relay address
 *    2. PlatformConfig   :-  This is a file which is used to configure jxta silently.
 *    3. socket.adv       :-  this file contains the information to create JxtaBiDiPipe
 *    4. enc_dec_key.txt  :-  contains string to encrypt/decrypt data for various purpose.
 *                            ie. data transfer
 * 
 * The client first establishes a connection with the FileSync server by using a 
 * userid/password. Then it send the upload request with necessary parameter to upload 
 * folder/file. And after completion the upload request/response, the connection is closed.
 */
 
public class UploadFile  {

  public UploadFile.HandleJxtaConfiguration handleJxtaConfiguration;
  
  /**
   * Handles all remote operation
   */
  public FsFileSystemOperationsRemote fsFileSystemOperationsRemote;
  private JxtaClient jxtaClient;
  
  /**
   * A constant to indicate which class will process the response.
   */
  private static final String FOR_THIS_CLASS = "FOR_THIS_CLASS";
  
  /**
   * this object is used to provide some synchronization between the request and response.
   */
  private final static String completeLock = "completeLock";
  private Logger logger;
  
  /**
   * Instantiates the UploadFile class
   */
  public UploadFile() {
    initializeLogger();
    handleJxtaConfiguration = new UploadFile.HandleJxtaConfiguration();
    String userHome = System.getProperty("user.home");

    Logger logger = Logger.getLogger("ClientLogger");

    File socketAdv = new File("config/socket.adv");
    File jxtaConfig = new File("config/jxta_config.xml");
    //File encrDecrPassword = new File("config/enc_dec_key.txt");
    File platformConfig = new File("config/PlatformConfig");
    
    jxtaClient = new JxtaClient(logger,socketAdv,jxtaConfig,null,platformConfig);    
    jxtaClient.addPropertyChangeSupport(handleJxtaConfiguration);
    Thread jxtaThread = new Thread(jxtaClient);
    jxtaThread.start();
    
    try{
      jxtaThread.join();
    }catch(InterruptedException ex){
      ex.printStackTrace();
    }
    
  }

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    int length = args.length;
    
    if(length >= 3){
      UploadFile uploadFile = new UploadFile();
      uploadFile.upload(args);
    }else{
      System.out.println("Invalid number of arguments or format");
      System.out.println("\nUsage Syntax");
      System.out.println("\tjava -jar UploadExample.jar \"file/folder complete path\" src dest");
    }
    //UploadFile uploadFile = new UploadFile();
    //uploadFile.getXml();
  }
  
  /**
   * Establishes connection with the FileSync server, uploads the folder/file and 
   * disconnects from the server .
   * @param args Array of arguments passed to the app.
   */
  public void upload(String[] args){
    Stack fileStack = new Stack();
    int itemCount = args.length - 2;
    for (int index = 0 ; index < itemCount ; index++ ) {
      fileStack.add(args[index]);
    }
    
    System.out.println("Uploading file");
    FsUser fsUser = new FsUser();
    fsUser.setUserId("system");
    fsUser.setUserPassword("system");
    fsFileSystemOperationsRemote.connectUser(fsUser,FOR_THIS_CLASS);
    waitUntilCompleted();
    fsFileSystemOperationsRemote.uploadItem(null,fileStack,args[itemCount],args[itemCount+1],FOR_THIS_CLASS);
    waitUntilCompleted();
    System.out.println("File uploaded");    
    fsFileSystemOperationsRemote.disconnectUser(FOR_THIS_CLASS);
    waitUntilCompleted();
    //System.exit(0);
  }
  
  public void getXml(){
    FsUser fsUser = new FsUser();
    fsUser.setUserId("system");
    fsUser.setUserPassword("system");
    fsFileSystemOperationsRemote.connectUser(fsUser,FOR_THIS_CLASS);
    waitUntilCompleted();
    fsFileSystemOperationsRemote.getFolderContentRecursive("/home/system/test",FOR_THIS_CLASS);
    waitUntilCompleted();
    System.out.println("File uploaded");    
    fsFileSystemOperationsRemote.disconnectUser(FOR_THIS_CLASS);
    waitUntilCompleted();
    System.exit(0);
  }
  /**
   * Initialize logger
   */
  private void initializeLogger(){
    try {
      System.out.println("Initializing Logger...");
      String userHome = System.getProperty("user.home");

      File logFolder = new File(userHome + "/.dbsfs/log");
      if(!logFolder.exists()){
        logFolder.mkdirs();
      }
      
      File file = new File("config/log4j.properties");
      if(file.exists()) {
        PropertyConfigurator.configureAndWatch(file.getAbsolutePath(),2000);
      }else{
        System.out.println("The application was unable to initialize logger properly.");
        System.out.println("log4j-initialization-file : '" + file.getAbsolutePath() + "'");
        System.out.println("The application will exit now!");
        System.exit(1);
      }
      
      logger = Logger.getLogger("ClientLogger");
      logger.info("Logger initialized successfully");

    }catch(Exception ex){
      ex.printStackTrace();
      System.exit(1);
    }
  }  
  

  /**
   * Used to handle jxta configuration notification. and initialize the app for 
   * communication with the FileSync server
   */
  private class HandleJxtaConfiguration implements PropertyChangeListener {
      
      public void propertyChange(PropertyChangeEvent evt){
        Logger logger = Logger.getLogger("ClientLogger");
        jxtaClient = (JxtaClient)evt.getSource();
        Boolean jxtaConfigured = (Boolean)evt.getNewValue();
        if(jxtaConfigured.booleanValue()){
          //File encrDecrPassword = new File("config/enc_dec_key.txt");
          fsFileSystemOperationsRemote = new FsFileSystemOperationsRemote(logger,jxtaClient,null);
          fsFileSystemOperationsRemote.addPropertyChangeSupport(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
            // TODO:  Implement this java.beans.PropertyChangeListener abstract method
            propertyChangeFileSystemOperationRemote(evt);
            }            
          });
        }else{
          System.out.println("Jxta server not found");
        }
      }
  }

  /**
   * Handles notification after each request/response completion
   * @param evt
   */
  public void propertyChangeFileSystemOperationRemote(PropertyChangeEvent evt){
    logger.debug("evt.getSource() : " + evt.getSource());
    FsResponse fsResponse;
    FsExceptionHolder fsExceptionHolder;
    if(evt.getSource().equals(FOR_THIS_CLASS)){
      if(evt.getPropertyName().equals("fsResponse")){
        fsResponse = (FsResponse)evt.getNewValue();
        logger.debug("fsResponse.getResponseCode() : " + fsResponse.getResponseCode());
        handleResponse(fsResponse);
        processCompleted();
      }
    }
  }

  /**
   * The class which actually handles the notification
   * @param fsResponse FsResponse object as parameter
   */
  private void handleResponse(FsResponse fsResponse) {
    FsExceptionHolder fsExceptionHolder;
    String homeFolder;
    
    try{
      int responseCode = fsResponse.getResponseCode();
      switch (responseCode){
      case FsResponse.CONNECT:
        Boolean connectionSuccessFul = (Boolean)fsResponse.getData();
        if(connectionSuccessFul.booleanValue()){
          logger.info("Connected to the server");
        }else{
          fsExceptionHolder = fsResponse.getFsExceptionHolder();
          logger.error(fsExceptionHolder.getErrorMessage());
          logger.info("Invalid userid/password");
        }
        break;
      case FsMessage.DISCONNECT:
        logger.info("User Disconnected");
        break;
      case FsResponse.DOWNLOAD_COMPLETED:
        //JOptionPane.showMessageDialog(this,"File(s) Downloaded successfully");
        logger.info("Download Completed");
        break;
      case FsResponse.DOWNLOAD_FAILURE:
        logger.info("Download failure");
        break;
      case FsResponse.DOWNLOAD_CANCELED:
        logger.info("Download canceled");
        break;
      case FsResponse.UPLOAD_COMPLETED:
        logger.info("Upload Complete");
        break;
      case FsResponse.UPLOAD_FAILURE:
        logger.info("Upload Failure");
        if(fsResponse.getFsExceptionHolder().getErrorCode() == 30002){
          logger.info("Failed to upload, access denied for the specified destination folder.");
        }
        break;
      case FsMessage.UPLOAD_CANCELED:
        logger.info("Upload canceled");
        break;
      case FsMessage.GET_FOLDER_CONTENT_RECURSIVE:
        String xmlString = (String)fsResponse.getData();
        logger.error("xmlString : " + xmlString);
        CommonUtil commonUtil = new CommonUtil(logger);
        Document document = commonUtil.getDocumentFromXMLString(xmlString);        
        File fileLocal = new File("TreeStructure.xml");
        commonUtil.saveXMLDocumentToFile(document,fileLocal);
        break;          
      case FsMessage.FETAL_ERROR:
        logger.error("Fetal Error");
        break;
      case FsMessage.ERROR_MESSAGE:
        fsExceptionHolder = fsResponse.getFsExceptionHolder();
        logger.error(fsExceptionHolder);
        break;
      }
    }catch(Exception ex){
      logger.error(ex.getMessage());
    }
  }
  
  /**
   * Makes the thread to wait until the completion of the request
   */
    private void waitUntilCompleted() {
        try {
            synchronized(completeLock) {
                completeLock.wait();
            }
            System.out.println("Done.");
        } catch (InterruptedException e) {
            System.out.println("Interrupted.");
        }
    }

  
  /**
   * Notifies the waiting thread after completion of the request.
   */
  private void processCompleted(){
    synchronized(completeLock) {
      completeLock.notify();
    }
  }
  
}