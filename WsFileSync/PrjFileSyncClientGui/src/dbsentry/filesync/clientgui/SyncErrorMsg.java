package dbsentry.filesync.clientgui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;


public class SyncErrorMsg extends JDialog {
  private JButton butOk = new JButton();

  private JLabel jLabel1 = new JLabel();

  private JToggleButton butDetails = new JToggleButton();

  private JScrollPane jScrollPane1 = new JScrollPane();

  private JTextArea taSyncErrorMsg = new JTextArea();

  public SyncErrorMsg() {
    this((Frame)null, "", false);
  }

  /**
   * 
   * @param modal
   * @param title
   * @param parent
   */
  public SyncErrorMsg(Frame parent, String title, boolean modal) {
    super(parent, title, modal);
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
    /*
    public SyncErrorMsg(JInternalFrame parent, String title, boolean modal) {
      super(parent, title, modal);
      try {
        jbInit();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
    */

  private void jbInit() throws Exception {
    this.setSize(new Dimension(371, 292));
    this.getContentPane().setLayout(null);
    this.setBounds(new Rectangle(10, 10, 371, 160));
    butOk.setText("Ok");
    butOk.setBounds(new Rectangle(275, 95, 75, 25));
    butOk.addActionListener(new ActionListener() {
                              public void actionPerformed(ActionEvent e) {
                                butOk_actionPerformed(e);
                              }
                            }
    );
    jLabel1.setText("Synchronization Complete with Error");
    jLabel1.setBounds(new Rectangle(105, 40, 215, 30));
    butDetails.setText("Details...");
    butDetails.setBounds(new Rectangle(180, 95, 85, 25));
    butDetails.addItemListener(new ItemListener() {
                                 public void itemStateChanged(ItemEvent e) {
                                   jToggleButton1_itemStateChanged(e);
                                 }
                               }
    );
    jScrollPane1.setBounds(new Rectangle(5, 135, 350, 115));
    taSyncErrorMsg.setBackground(this.getBackground());
    taSyncErrorMsg.setToolTipText("null");
    jScrollPane1.getViewport().add(taSyncErrorMsg, null);
    this.getContentPane().add(jScrollPane1, null);
    this.getContentPane().add(butDetails, null);
    this.getContentPane().add(jLabel1, null);
    this.getContentPane().add(butOk, null);
  }

  private void butDetails_actionPerformed(ActionEvent e) {
    this.setBounds(new Rectangle(10, 10, 371, 290));
  }

  public JTextArea getTaSyncErrorMsg() {
    return taSyncErrorMsg;
  }

  private void butOk_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  private void jToggleButton1_itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      butDetails.setText("Hide Details");
      this.setSize(new Dimension(371, 290));
      this.validate();
    } else {
      butDetails.setText("Details...");
      this.setSize(new Dimension(371, 160));
      this.validate();
    }
  }
}
