package desktop_vnc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;

public class BACKUP_User_Panel {
    
    
    
    public BACKUP_User_Panel(){
        if (System.getProperty("swing.defaultlaf") == null) {
            try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } 
            catch (Exception e) {}
	}
        
        // FRAME
        JFrame frame = new JFrame("User Panel");
        frame.setLayout(new CardLayout());
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(400, 300);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
         // COLOR
        int red = 49;
        int green = 49;
        int blue = 49;
        Color back = new Color(red,green,blue);
        red = 200;
        green = 200;
        blue = 200;
        Color white = new Color(red,green,blue);
        
        
         /* 
            ---------------------------------------------------------------------------------
		SENDER PANEL
            ---------------------------------------------------------------------------------	
            */
            frame.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            frame.getContentPane().setBackground(back);


            JLabel l = new JLabel("Please Type your Name in the given Field");
            Font myFont = new Font("Serif",Font.BOLD,18);
            l.setFont(myFont);
            l.setForeground(white);
            c.gridx = 2;
            c.gridy = 0;
            frame.add(l,c);

	    JTextField nameField = new JTextField("",20);
	    myFont = new Font("Serif",Font.BOLD,16);
	    nameField.setFont(myFont);
	    c.gridx = 2;
	    c.gridy = 1;
	    frame.add(nameField,c);

	    JButton sendButton = new JButton("Send");
	    myFont = new Font("Serif",Font.BOLD,16);
	    sendButton.setFont(myFont);
            sendButton.setActionCommand("send");
            sendButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if(nameField.getText().length() == 0)
                    {
                        System.out.println("Name Empty");
                        JOptionPane.showMessageDialog(frame, "Please provide your Name", "Info", 1);
                        return;
                    } else
                    {
                        sendButton.setEnabled(false);
                        nameField.setEditable(false);

                        JPanel panelPbar = new JPanel();
                        JProgressBar pBar = new JProgressBar();
                        pBar.setIndeterminate(true);
                        panelPbar.add(pBar);
                        panelPbar.setBackground(back);
                        c.gridx = 2;
                        c.gridy = 4;
                        frame.add(panelPbar,c);

                        frame.pack();

                    }
                    
                    // CHANGE
                    MultiCastClient mc = new MultiCastClient();
                    mc.receive();
                }
            });
             c.gridx = 2;
	    c.gridy = 3;
            frame.add(sendButton,c);
            
           
	   


	    /* 
            ---------------------------------------------------------------------------------
            END OF SENDER
            ---------------------------------------------------------------------------------	
            */
    }
    
    
    
    public static void main(String[] args){
        // DO NOTHING
       // BACKUP_User_Panel u = new User_Panel();
        //u.build();
    }
}
