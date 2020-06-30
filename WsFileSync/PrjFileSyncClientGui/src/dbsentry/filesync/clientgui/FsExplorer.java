
package dbsentry.filesync.clientgui;

import dbsentry.filesync.clientgui.preference.FsPreferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.apache.log4j.Logger;


/* Used by FileSyncClientMDI.java. */
/**
  *  @author              Saurabh Kumar
  *  @version             1.0
  *  Date of creation:    12-03-2006
  *  Last Modfied Date:   26-04-2006 
  */
public class FsExplorer extends JInternalFrame {
  
  private Logger logger;

  //Splitpane for Complete  Brower
  private JSplitPane splpFileBrowser = new JSplitPane();
  
  //Panel for Complete browser 
  private JDesktopPane jpFileBrowser = new JDesktopPane();
  

  //Panel for Remote/Local Toolbar
  private JPanel jpToolBarRemote = new JPanel();
  private JPanel jpToolBarLocal = new JPanel();

  //Panel for Remote/Local Browser
  public static JPanel jpLocalSystem = new JPanel();

  
  //Cursor

  private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

  private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);


  private int dividerLocationRemote = 250;

  private int dividerLocationLocal = 250;

  public static int cursorCounterRemote = 0;

  public static int cursorCounterLocal = 0;

  private FileSyncClientMDI mdiParent;

  private FsRemoteView fsRemoteView=null;

  private FsLocalView fsLocalView=null;
  
  private String userCache = System.getProperty("user.home") + "/.dbsfs";

  //public CursorManage cursorManage;

  public FsExplorer(Logger logger, FileSyncClientMDI mdiParent) {
    super("File Sync Explorer", true, true, true, true);
    try {
      this.logger = logger;
      
      this.mdiParent = mdiParent;
      
      //this.cursorManage=new CursorManage(logger,mdiParent);

      jbInit();
      
      applyPreferences();
      
      launchLocalView();

      //          BasicInternalFrameUI ui = (BasicInternalFrameUI)this.getUI();
      //          ui.setNorthPane(null);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    //Split Panel For Total File Browser
    splpFileBrowser.setDividerSize(6);
    splpFileBrowser.setBorder(BorderFactory.createEmptyBorder());
    splpFileBrowser.setOrientation(JSplitPane.VERTICAL_SPLIT);

    //Panel for Total File Browser
    jpFileBrowser.setLayout(new BorderLayout());
    jpFileBrowser.setBorder(BorderFactory.createEmptyBorder());
    jpFileBrowser.setBackground(Color.white);
    jpFileBrowser.setBounds(new Rectangle(0, 30, 890, 530));
    jpFileBrowser.add(splpFileBrowser, BorderLayout.CENTER);
    
    this.setBorder(BorderFactory.createEmptyBorder());
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(jpFileBrowser, BorderLayout.CENTER);
    this.setSize(new Dimension(593, 465));
    
  }

  private void launchLocalView(){
  
    fsLocalView = new FsLocalView(logger, mdiParent);
    splpFileBrowser.add(fsLocalView, JSplitPane.RIGHT);
    fsLocalView.initialVisualSettings();
    splpFileBrowser.setDividerLocation(265);
    
    fsLocalView.addInternalFrameListener(new InternalFrameAdapter() {
                                        public void internalFrameActivated(InternalFrameEvent e) {
                                          logger.debug("i m in activated of local explorer");
                                          mdiParent.menuEditUpload.setVisible(true);
                                          mdiParent.menuEditUpload.setEnabled(true);
                                          mdiParent.menuEditDownload.setVisible(false);
                                          mdiParent.menuEditDownload.setEnabled(false);
                                        }

                                        public void internalFrameDeactivated(InternalFrameEvent e) {
                                         logger.debug("i m in deactivated of local explorer");
                                          mdiParent.menuEditUpload.setVisible(false);
                                          mdiParent.menuEditUpload.setEnabled(false);
                                          mdiParent.menuEditDownload.setVisible(true);
                                          mdiParent.menuEditDownload.setEnabled(true);
                                          
                                        }
                                      });
  }
  
  public void launchRemoteView(){
    fsRemoteView = new FsRemoteView(logger, mdiParent);
    splpFileBrowser.add(fsRemoteView, JSplitPane.LEFT);
    fsRemoteView.initialVisualSettings();
    fsRemoteView.setVisible(true);
    splpFileBrowser.setDividerLocation(265);
    
    fsRemoteView.addInternalFrameListener(new InternalFrameAdapter() {
                                        public void internalFrameActivated(InternalFrameEvent e) {
                                          logger.debug("i m in activated of remote explorer");
                                          mdiParent.menuEditUpload.setVisible(false);
                                          mdiParent.menuEditUpload.setEnabled(false);
                                          mdiParent.menuEditDownload.setVisible(true);
                                          mdiParent.menuEditDownload.setEnabled(true);
                                        }

                                        public void internalFrameDeactivated(InternalFrameEvent e) {
                                         logger.debug("i m in deactivated of remote explorer");
                                          mdiParent.menuEditUpload.setVisible(true);
                                          mdiParent.menuEditUpload.setEnabled(true);
                                          mdiParent.menuEditDownload.setVisible(false);
                                          mdiParent.menuEditDownload.setEnabled(false);
                                          
                                        }
                                      });
  }
  
  public void disposeRemoteView(){
    fsRemoteView.dispose();
    fsRemoteView=null;
  }
  


  private void applyPreferences(){
    FsPreferences fsPreferences = readPreferencesFromDisk();
    if (!fsPreferences.isBrowserLocalVisible()) {
      splpFileBrowser.remove(splpFileBrowser.getRightComponent());
    }
    
    if (!fsPreferences.isBrowserRemoteVisible()) {
      splpFileBrowser.remove(splpFileBrowser.getLeftComponent());
    }
    
    if (!fsPreferences.isTreeLocalVisible()) {
      fsLocalView.splpLocalSystem.remove(fsLocalView.splpLocalSystem.getLeftComponent());
    }
    if (!fsPreferences.isTreeRemoteVisible()) {
      fsRemoteView.splpRemoteSystem.remove(fsRemoteView.splpRemoteSystem.getLeftComponent());
    }
    
    tileHorizontally(fsPreferences);
    tileVertically(fsPreferences);
    
  }

  private void tileHorizontally(FsPreferences fsPreferences) {
    
    if (fsPreferences.isTileHorizontally()) {
      splpFileBrowser.setOrientation(JSplitPane.VERTICAL_SPLIT);
      splpFileBrowser.setDividerLocation(jpFileBrowser.getHeight() / 2);
      jpToolBarLocal.setPreferredSize(new Dimension(jpToolBarLocal.getWidth(), 34));
      jpToolBarRemote.setPreferredSize(new Dimension(jpToolBarRemote.getWidth(), 34));
    }
  }

  private void tileVertically(FsPreferences fsPreferences) {
    if (fsPreferences.isTileVertically()) {
      splpFileBrowser.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
      splpFileBrowser.setDividerLocation(jpFileBrowser.getWidth() / 2);
      jpToolBarLocal.setPreferredSize(new Dimension(jpToolBarLocal.getWidth(), 60));
      jpToolBarRemote.setPreferredSize(new Dimension(jpToolBarRemote.getWidth(), 60));
    }
  }


  
  public void setDefaultCursorForLocalBrowser() {
    cursorCounterLocal = cursorCounterLocal - 1;
    logger.debug(" cursorCounterLocal : " + cursorCounterLocal);
    if (cursorCounterLocal <= 0) {
      jpLocalSystem.setCursor(defaultCursor);
    }
  }

  private void setWaitCursorForLocalBrowser() {
    cursorCounterLocal++;
    jpLocalSystem.setCursor(waitCursor);
  }


  
  private void button_mouseEnteredNoFX(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
  }

  private void button_mouseExitedNoFX(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
  }
  
  public void menuUploadDownloadOperation(){
    if (fsLocalView.isSelected()) {
      fsLocalView.localUploadOperation();
    } else {
      if (fsRemoteView.isSelected()) {
        fsRemoteView.remoteDownloadOperation();
      }
    }
  }
  
  public void cutOperation() {
    if (fsLocalView.isSelected()) {
      fsLocalView.localCutOperation();
    } else {
      if (fsRemoteView.isSelected()) {
        fsRemoteView.remoteCutOperation();
      }
    }

  }

  public void copyOperation() {
    if (fsLocalView.isSelected()) {
      fsLocalView.localCopyOperation();
    } else {
      if (fsRemoteView.isSelected()) {
        fsRemoteView.remoteCopyOperation();
      }
    }
  }

  public void pasteOperation() {
    if (fsLocalView.isSelected()) {
      fsLocalView.localPasteOperation();
    } else {
      if (fsRemoteView.isSelected()) {
        fsRemoteView.remotePasteOperation();
      }
    }
  }

  public void deleteOperation() {
    if (fsLocalView.isSelected()) {
      fsLocalView.localDeleteOperation();
    } else {
      if (fsRemoteView.isSelected()) {
        fsRemoteView.remoteDeleteOperation();
      }
    }
  }

  public void renameOperation() {
    if (fsLocalView.isSelected()) {
      fsLocalView.localRenameOperation();
    } else {
      if (fsRemoteView.isSelected()) {
        fsRemoteView.remoteRenameOperation();
      }
    }
  }

  public void refreshOperation() {
    if (fsLocalView.isSelected()) {
      fsLocalView.localRefreshOperation();
    } else {
      if (fsRemoteView.isSelected()) {
        fsRemoteView.remoteRefreshOperation();
      }
    }

  }

  public void newFolderOperation() {
    if (fsLocalView.isSelected()) {
      fsLocalView.localNewFolderOperation();
    } else {
      if ((fsRemoteView.isSelected()) && (mdiParent.getButDisconnect().isEnabled())) {
        fsRemoteView.remoteNewFolderOperation();
      }
    }

  }

  public void propertyOperation() {
    if (fsLocalView.isSelected()) {
      fsLocalView.localPropertyOperation();
    } else {
      if (fsRemoteView.isSelected()) {
        fsRemoteView.remotePropertyOperation();
      }
    }

  }

  public void menuTileHorizontally_actionPerformed() {
    
    FsPreferences fsPreferences = readPreferencesFromDisk();    
    fsPreferences.setTileHorizontally(true);
    fsPreferences.setTileVertically(false);
    tileHorizontally(fsPreferences);
    writePreferencesToDisk(fsPreferences);
    
  }

  public void menuTileVertically_actionPerformed() {
    FsPreferences fsPreferences = readPreferencesFromDisk();
    fsPreferences.setTileHorizontally(false);
    fsPreferences.setTileVertically(true);
    tileVertically(fsPreferences);
    writePreferencesToDisk(fsPreferences);
    
  }

  public void menuRemoteBrowser_actionPerformed() {
    FsPreferences fsPreferences = readPreferencesFromDisk();
    if (fsPreferences.isBrowserRemoteVisible()) {
      splpFileBrowser.remove(splpFileBrowser.getLeftComponent());
    } else {
      splpFileBrowser.setDividerLocation(265);
      splpFileBrowser.add(fsRemoteView, JSplitPane.LEFT);
    }
    fsPreferences.setBrowserRemoteVisible(!fsPreferences.isBrowserRemoteVisible());
    writePreferencesToDisk(fsPreferences);
  }

  public void menuLocalBrowser_actionPerformed() {
    FsPreferences fsPreferences = readPreferencesFromDisk();
    if (fsPreferences.isBrowserLocalVisible()) {
      splpFileBrowser.remove(splpFileBrowser.getRightComponent());
    } else {
      splpFileBrowser.setDividerLocation(265);
      splpFileBrowser.add(fsLocalView, JSplitPane.RIGHT);
    }
    fsPreferences.setBrowserLocalVisible(!fsPreferences.isBrowserLocalVisible());
    writePreferencesToDisk(fsPreferences);
  }

  public void menuRemoteTree_actionPerformed() {
    FsPreferences fsPreferences = readPreferencesFromDisk();
    if (fsPreferences.isTreeRemoteVisible()) {
      dividerLocationRemote = fsRemoteView.splpRemoteSystem.getDividerLocation();
      fsRemoteView.splpRemoteSystem.remove(fsRemoteView.splpRemoteSystem.getLeftComponent());
    } else {
      fsRemoteView.splpRemoteSystem.add(fsRemoteView.getJpRemoteTreeView(), JSplitPane.LEFT);
      fsRemoteView.splpRemoteSystem.setDividerLocation(dividerLocationRemote);
    }
    fsPreferences.setTreeRemoteVisible(!fsPreferences.isTreeRemoteVisible());
    writePreferencesToDisk(fsPreferences);
  }


  public void menuLocalTree_actionPerformed() {
    FsPreferences fsPreferences = readPreferencesFromDisk();
    if (fsPreferences.isTreeLocalVisible()) {
      dividerLocationLocal = fsLocalView.splpLocalSystem.getDividerLocation();
      fsLocalView.splpLocalSystem.remove(fsLocalView.splpLocalSystem.getLeftComponent());
    } else {
      fsLocalView.splpLocalSystem.add(fsLocalView.getJpLocalTreeView(), JSplitPane.LEFT);
      fsLocalView.splpLocalSystem.setDividerLocation(dividerLocationLocal);
    }
    fsPreferences.setTreeLocalVisible(!fsPreferences.isTreeLocalVisible());
    writePreferencesToDisk(fsPreferences);
  }
  
  private FsPreferences readPreferencesFromDisk(){
   
   FsPreferences fsPreferences=null;
   File preferencesFile = new File(userCache + File.separator + "preferences" + File.separator + "preferences.fs");
   
   logger.debug("preferences File : " + preferencesFile.toString());
   
   if(!preferencesFile.exists()){
     fsPreferences = new FsPreferences();
     
   }else{
     
     FileInputStream fis = null;
     ObjectInputStream ois = null;
     
     try {
       fis = new FileInputStream(preferencesFile);
       ois = new ObjectInputStream(fis);
       fsPreferences = (FsPreferences)ois.readObject();
     }
     catch (FileNotFoundException e) {
       ;
     }
     catch (IOException e) {
       ;
     }
     catch (ClassNotFoundException e) {
       ;
     }
     finally {
       try {
         if (fis != null) {
           fis.close();
         }
         if (ois != null) {
           ois.close();
         }
       }
       catch (IOException e) {
         ;
       }
     }
   }
   
   return fsPreferences;
  
  }
  
  private void writePreferencesToDisk(FsPreferences fsPreferences){
   
   FileOutputStream fos = null;
   ObjectOutputStream oos = null;
   
   File preferencesfile = new File(userCache + File.separator + "preferences" + File.separator + "preferences.fs");
   if(preferencesfile.exists()){
     preferencesfile.delete();
   }
   try {
     fos = new FileOutputStream(preferencesfile);
     oos = new ObjectOutputStream(fos);
     oos.writeObject(fsPreferences);
     oos.flush();
   }
   catch (FileNotFoundException e) {
     ;
   }
   catch (IOException e) {
     ;
   }
   finally {
     try {
       if (fos != null) {
         fos.close();
       }
       if (oos != null) {
         oos.close();
       }
     }
     catch (IOException e) {
       ;
     }
   }
  
  }

  public FileSyncClientMDI getMdiParent() {
    return mdiParent;
  }

  public FsRemoteView getFsRemoteView() {
    return fsRemoteView;
  }

  public FsLocalView getFsLocalView() {
    return fsLocalView;
  }

}
