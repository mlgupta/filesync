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
 * $Id: FsImage.java,v 1.33 2006/09/11 11:20:19 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.images;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;

/**
 *	An class which defines list of constants of imageicon used in this application.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   11-07-2005
 */

public class FsImage  {
  public static Image imageTitle = (new ImageIcon(FsImage.class.getResource("FileSync16x16.gif"))).getImage();      
  public static ImageIcon imageNavigateBack = new ImageIcon(FsImage.class.getResource("butt_back.gif"));
  public static ImageIcon imageNavigateForward = new ImageIcon(FsImage.class.getResource("butt_forward.gif"));
  public static ImageIcon imageNavigateUp = new ImageIcon(FsImage.class.getResource("butt_go_up.gif"));
  public static ImageIcon imageUpload = new ImageIcon(FsImage.class.getResource("butt_upload.gif"));
  public static ImageIcon imageDownload = new ImageIcon(FsImage.class.getResource("butt_dwnld.gif"));
  public static ImageIcon imageNewFolder = new ImageIcon(FsImage.class.getResource("butt_new_folder.gif"));
  public static ImageIcon imageDelete = new ImageIcon(FsImage.class.getResource("butt_delete_folder.gif"));
  public static ImageIcon imageRefresh = new ImageIcon(FsImage.class.getResource("butt_refresh.gif"));
  public static ImageIcon imageProperty = new ImageIcon(FsImage.class.getResource("butt_property.gif"));
  public static ImageIcon imageRename = new ImageIcon(FsImage.class.getResource("butt_rename.gif"));
  public static ImageIcon imageConnect = new ImageIcon(FsImage.class.getResource("butt_connect.gif"));
  public static ImageIcon imageDisconnect = new ImageIcon(FsImage.class.getResource("butt_disconnect.gif"));
  public static ImageIcon imageGo = new ImageIcon(FsImage.class.getResource("butt_go.gif"));
  public static ImageIcon imageFileSync = new ImageIcon(FsImage.class.getResource("butt_filesync.gif"));
  public static ImageIcon imageCut = new ImageIcon(FsImage.class.getResource("butt_cut.gif"));
  public static ImageIcon imageCopy = new ImageIcon(FsImage.class.getResource("butt_copy.gif"));
  public static ImageIcon imagePaste = new ImageIcon(FsImage.class.getResource("butt_paste.gif"));
  public static ImageIcon imageBack = new ImageIcon(FsImage.class.getResource("butt_back.gif"));
  public static ImageIcon imageForward = new ImageIcon(FsImage.class.getResource("butt_forward.gif"));
  public static ImageIcon imageMultipleItemProperties = new ImageIcon(FsImage.class.getResource("multiple_item_properties.gif"));
  
  public static ImageIcon iconNew = new ImageIcon(FsImage.class.getResource("butt_new.gif"));
  public static  ImageIcon iconEdit = new ImageIcon(FsImage.class.getResource("butt_edit.gif"));  
  public static  ImageIcon iconDelete = new ImageIcon(FsImage.class.getResource("butt_delete.gif"));
  public static  ImageIcon iconSave = new ImageIcon(FsImage.class.getResource("butt_save.gif"));  
  public static  ImageIcon iconCancel = new ImageIcon(FsImage.class.getResource("butt_cancel.gif"));
  public static  ImageIcon iconPreview = new ImageIcon(FsImage.class.getResource("butt_view.gif"));  
  public static  ImageIcon iconExit = new ImageIcon(FsImage.class.getResource("butt_exit.gif"));    
  public static  ImageIcon iconStart = new ImageIcon(FsImage.class.getResource("butt_start.gif"));  
  public static ImageIcon iconWarning = new ImageIcon(FsImage.class.getResource("warning.gif"));
  
  public static  ImageIcon imgSyncQuestion = new ImageIcon(FsImage.class.getResource("sync_question.gif"));    
  public static  ImageIcon imgSyncBack = new ImageIcon(FsImage.class.getResource("sync_back.gif"));    
  public static  ImageIcon imgSyncForward = new ImageIcon(FsImage.class.getResource("sync_forward.gif"));    
  public static  ImageIcon imgSyncCancel = new ImageIcon(FsImage.class.getResource("sync_cancel.gif"));    

  public static  ImageIcon imgSyncRefresh = new ImageIcon(FsImage.class.getResource("sync_refresh.gif"));    
  public static  ImageIcon imgSyncRun = new ImageIcon(FsImage.class.getResource("sync_run.gif"));    
  public static  ImageIcon imgSyncStop = new ImageIcon(FsImage.class.getResource("sync_stop.gif"));    
  public static  ImageIcon imgSyncClose = new ImageIcon(FsImage.class.getResource("sync_close.gif"));    

  public static ImageIcon arrow_up = new ImageIcon(FsImage.class.getResource("arrow_up.gif"));
  public static ImageIcon arrow_down = new ImageIcon(FsImage.class.getResource("arrow_down.gif"));
  public static ImageIcon imgFolderClosed = new ImageIcon(FsImage.class.getResource("folder_closed.gif"));
  public static ImageIcon imgFolderOpen = new ImageIcon(FsImage.class.getResource("folder_open.gif"));
  
  public static ImageIcon about_screen_composed = new ImageIcon(FsImage.class.getResource("about-screen_composed.gif"));
  public static ImageIcon about_screen_logo = new ImageIcon(FsImage.class.getResource("about-screen_logo.gif"));  
  
  public static ImageIcon imageUnknownFile = new ImageIcon(FsImage.class.getResource("unknown_file.gif")); 
  public static ImageIcon imageFileReplace = new ImageIcon(FsImage.class.getResource("file_replace.gif")); 
  public static ImageIcon imageFolderReplace = new ImageIcon(FsImage.class.getResource("folder_replace.gif"));
  
  //public static ImageIcon imageFileExplorer=new ImageIcon(FsImage.class.getResource("file_explorer.gif"));
  public static ImageIcon imageFileExplorer=new ImageIcon(FsImage.class.getResource("explorer_22x19.gif"));
  public static ImageIcon imageFileExplorer16x16=new ImageIcon(FsImage.class.getResource("explorer_22x19.gif"));
  
  public static ImageIcon imageFileSync32x32 = new ImageIcon(FsImage.class.getResource("FileSync_22x20.gif"));
  public static ImageIcon imageFileSync16x16 = new ImageIcon(FsImage.class.getResource("FileSync_22x20.gif"));
  
  public static ImageIcon imageUploadDownloadManager16x16=new ImageIcon(FsImage.class.getResource("upload_download_manager_22x19.gif"));
  public static ImageIcon imageUploadDownloadManager=new ImageIcon(FsImage.class.getResource("upload_download_manager_22x19.gif"));
  
  public static ImageIcon imageProcessDelete=new ImageIcon(FsImage.class.getResource("process_delete.gif"));
  public static ImageIcon imageProcessCancel=new ImageIcon(FsImage.class.getResource("process_cancel.gif"));
  public static ImageIcon imageProcessHide=new ImageIcon(FsImage.class.getResource("process_hide.gif"));
  public static ImageIcon imageProcessShow=new ImageIcon(FsImage.class.getResource("process_show.gif"));
  
  
  public static ImageIcon imageHome=new ImageIcon(FsImage.class.getResource("butt_home.gif"));
  
  public static ImageIcon imageMenuCut=new ImageIcon(FsImage.class.getResource("menu_cut.gif"));
  public static ImageIcon imageMenuCopy=new ImageIcon(FsImage.class.getResource("menu_copy.gif"));
  public static ImageIcon imageMenuPaste=new ImageIcon(FsImage.class.getResource("menu_paste.gif"));
  public static ImageIcon imageMenuDelete=new ImageIcon(FsImage.class.getResource("menu_delete.gif"));
  public static ImageIcon imageMenuRename=new ImageIcon(FsImage.class.getResource("menu_rename.gif"));
  public static ImageIcon imageMenuNewFolder=new ImageIcon(FsImage.class.getResource("menu_new_folder.gif"));
  public static ImageIcon imageMenuRefresh=new ImageIcon(FsImage.class.getResource("menu_refresh.gif"));
  public static ImageIcon imageMenuProperty=new ImageIcon(FsImage.class.getResource("menu_property.gif"));
  public static ImageIcon imageMenuConnect=new ImageIcon(FsImage.class.getResource("menu_connect.gif"));
  public static ImageIcon imageMenuDisconnect=new ImageIcon(FsImage.class.getResource("menu_disconnect.gif"));
  public static ImageIcon imageMenuManager=new ImageIcon(FsImage.class.getResource("menu_up_down.gif"));
  public static ImageIcon imageMenuPreferences=new ImageIcon(FsImage.class.getResource("menu_preference.gif"));
  public static ImageIcon imageMenuExplorer=new ImageIcon(FsImage.class.getResource("menu_explorer.gif"));
  public static ImageIcon imageMenuSync=new ImageIcon(FsImage.class.getResource("menu_sync.gif"));
  public static ImageIcon imageMenuCascade=new ImageIcon(FsImage.class.getResource("menu_cascade.gif"));
  public static ImageIcon imageMenuTile=new ImageIcon(FsImage.class.getResource("menu_tile.gif"));
  public static ImageIcon imageMenuIndex=new ImageIcon(FsImage.class.getResource("menu_index.gif"));
  public static ImageIcon imageMenuContent=new ImageIcon(FsImage.class.getResource("menu_content.gif"));
  public static ImageIcon imageMenuAbout=new ImageIcon(FsImage.class.getResource("menu_about.gif"));
  public static ImageIcon imageMenuSearch=new ImageIcon(FsImage.class.getResource("menu_search.gif"));
  public static ImageIcon imageMenuExit=new ImageIcon(FsImage.class.getResource("menu_exit.gif"));
  public static ImageIcon imageMenuClose=new ImageIcon(FsImage.class.getResource("menu_close.gif"));
  public static ImageIcon imageMenuUpload=new ImageIcon(FsImage.class.getResource("menu_upload.gif"));
  public static ImageIcon imageMenuDownload=new ImageIcon(FsImage.class.getResource("menu_download.gif"));
  
  
}
