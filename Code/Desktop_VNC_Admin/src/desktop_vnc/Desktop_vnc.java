package desktop_vnc;
// MINE

import java.awt.*;
import javax.swing.*;
import java.applet.*;
import java.awt.event.*;

public class Desktop_vnc extends Applet {

    public static void main(String[] args) {
        // FRAME
        JFrame frame = new JFrame("Desktop View & Control");
        frame.setLayout(new GridLayout(1,2));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(800, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //port=7078;
        
        // ADMIN
        JPanel admin = new JPanel();
        admin.setPreferredSize(new Dimension(420, 600));
        admin.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel alabel = new JLabel("ADMIN");
        alabel.setFont(new Font("Verdana",1,52));
        admin.add(alabel);
	admin.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent ev){
               Admin_Panel admin_panel = new Admin_Panel();
               //admin_panel.build();
            }
        });
        frame.add(admin);
        
        
        // USER
        
        
        
        JPanel user = new JPanel();
        user.setPreferredSize(new Dimension(420, 600));
        user.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel ulabel = new JLabel("USER");
        ulabel.setFont(new Font("Verdana",1,52));
        user.add(ulabel);
         
        user.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent ev){
               User_Panel user_panel = new User_Panel();
             
               //user_panel.build();
            }
        });
	frame.add(user);
    }
    // END
}
