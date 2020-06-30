package dbsentry.filesync.clientgui;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;


public class ButtonMouseAdapter extends MouseAdapter{
  public static Border normalButtonBorder=BorderFactory.createEmptyBorder();
  public void mouseEntered(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    if(button.isEnabled()){
      button.setBorder(BorderFactory.createEtchedBorder());
    }
  }

  public void mouseExited(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(normalButtonBorder);
  }

  public void mousePressed(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    
  }

  public void mouseReleased(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(normalButtonBorder);
  }
  
  
}
