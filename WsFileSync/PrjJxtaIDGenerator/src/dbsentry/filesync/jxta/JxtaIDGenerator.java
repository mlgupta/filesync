package dbsentry.filesync.jxta;

//import java.net.URI;
//import java.net.URISyntaxException;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.WindowConstants;
//import java.awt.BorderLayout;
//import javax.swing.JPanel;
import javax.swing.JLabel;
//import java.awt.FlowLayout;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import net.jxta.id.IDFactory;
import java.awt.Font;
import net.jxta.peergroup.PeerGroupID;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import net.jxta.platform.ModuleClassID;

public class JxtaIDGenerator extends JFrame  {


  private JButton butGroupID = new JButton();
  private JButton butPipeID = new JButton();
  private JTextField txtID = new JTextField();
  private JLabel lblID = new JLabel();
  private JButton butPeerID = new JButton();
  private JRadioButton radRandomID = new JRadioButton();
  private JRadioButton radIDFromString = new JRadioButton();
  private JTextField txtIDString = new JTextField();
  private ButtonGroup buttonGroup1 = new ButtonGroup();
  private JButton butModuleClassID = new JButton();
  private JButton butModuleSpecID = new JButton();

  public JxtaIDGenerator() {
    try {
      jbInit();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    butPipeID.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butPipeID_actionPerformed(e);
        }
      });
    this.setSize(new Dimension(717, 187));
    this.setTitle("Jxta ID Generator");
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.getContentPane().setLayout(null);
    butGroupID.setText("Group ID");
    butGroupID.setBounds(new Rectangle(15, 15, 95, 25));
    butGroupID.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butGroupID_actionPerformed(e);
        }
      });
    butPipeID.setText("Pipe ID");
    butPipeID.setBounds(new Rectangle(245, 15, 95, 25));
    txtID.setBounds(new Rectangle(40, 105, 660, 25));
    txtID.setFont(new Font("Monospaced", 1, 14));
    lblID.setText("ID : ");
    lblID.setBounds(new Rectangle(15, 105, 30, 25));
    lblID.setToolTipText("null");
    butPeerID.setText("Peer ID");
    butPeerID.setBounds(new Rectangle(137, 14, 81, 26));
    butPeerID.setToolTipText("null");
    butPeerID.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butPeerID_actionPerformed(e);
        }
      });
    radRandomID.setSelected(true);  
    radRandomID.setText("Random ID");
    radRandomID.setBounds(new Rectangle(15, 65, 104, 24));
    radRandomID.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          radRandomID_actionPerformed(e);
        }
      });
    radIDFromString.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          radIDFromString_actionPerformed(e);
        }
      });
    txtIDString.setEnabled(false);
    butModuleClassID.setText("Module Class ID");
    butModuleClassID.setBounds(new Rectangle(365, 15, 135, 25));
    butModuleClassID.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butModuleClassID_actionPerformed(e);
        }
      });
    butModuleSpecID.setText("Module Spec ID");
    butModuleSpecID.setBounds(new Rectangle(525, 15, 145, 25));
    butModuleSpecID.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butModuleSpecID_actionPerformed(e);
        }
      });
    buttonGroup1.add(radRandomID);
    radIDFromString.setText("ID From String");
    radIDFromString.setBounds(new Rectangle(150, 65, 115, 25));
    buttonGroup1.add(radIDFromString);
    txtIDString.setBounds(new Rectangle(270, 65, 430, 25));
    this.getContentPane().add(butModuleSpecID, null);
    this.getContentPane().add(butModuleClassID, null);
    this.getContentPane().add(txtIDString, null);
    this.getContentPane().add(radIDFromString, null);
    this.getContentPane().add(radRandomID, null);
    this.getContentPane().add(butPeerID, null);
    this.getContentPane().add(lblID, null);
    this.getContentPane().add(txtID, null);
    this.getContentPane().add(butPipeID, null);
    this.getContentPane().add(butGroupID, null);
  }
  
  public static void main(String args[]){
    JxtaIDGenerator idGenerator = new JxtaIDGenerator();
    idGenerator.setVisible(true);
  }

  private void butGroupID_actionPerformed(ActionEvent e) {
    if(radRandomID.isSelected()){
      txtID.setText(IDFactory.newPeerGroupID().toString());
    }else{
      byte seedByte[] = txtIDString.getText().getBytes();
      PeerGroupID peerGroupID = IDFactory.newPeerGroupID(seedByte);
      txtID.setText(peerGroupID.toString());
    }
  }


  private void butPeerID_actionPerformed(ActionEvent e) {
    
    if(radRandomID.isSelected()){
      txtID.setText(IDFactory.newPeerID(IDFactory.newPeerGroupID()).toString());
    }else{
      byte seedByte[] = txtIDString.getText().getBytes();
      PeerGroupID peerGroupID = IDFactory.newPeerGroupID(seedByte);
      txtID.setText((IDFactory.newPeerID(peerGroupID,seedByte).toString()));
    }
    
  }

  private void butPipeID_actionPerformed(ActionEvent e) {
    if(radRandomID.isSelected()){
      txtID.setText(IDFactory.newPipeID(IDFactory.newPeerGroupID()).toString());  
    }else{
      byte seedByte[] = txtIDString.getText().getBytes();
      PeerGroupID peerGroupID = IDFactory.newPeerGroupID(seedByte);
      txtID.setText((IDFactory.newPipeID(peerGroupID,seedByte).toString()));
    }

    
  }

  private void radIDFromString_actionPerformed(ActionEvent e) {
    txtIDString.setEnabled(true);
  }

  private void radRandomID_actionPerformed(ActionEvent e) {
    txtIDString.setEnabled(false);
  }

  private void butModuleClassID_actionPerformed(ActionEvent e) {
    txtID.setText(IDFactory.newModuleClassID().toString());
  }

  private void butModuleSpecID_actionPerformed(ActionEvent e) {
    ModuleClassID moduleClassID = IDFactory.newModuleClassID();
    txtID.setText(IDFactory.newModuleSpecID(moduleClassID).toString());
  }

}