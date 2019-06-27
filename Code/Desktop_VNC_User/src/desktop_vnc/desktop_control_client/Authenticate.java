package desktop_vnc.desktop_control_client;



import desktop_vnc.User;
import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

import java.io.DataInputStream;

import java.io.DataOutputStream;

import java.io.IOException;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class Authenticate extends JFrame implements ActionListener{
			private Socket cSocket = null;
			DataOutputStream psswrchk = null;
			DataInputStream verification = null;
			String verify ="";
			JButton SUBMIT;
			JPanel panel;
			JLabel label, label1;
			String width="",height="";
			final JTextField text1;
                       User parent;
                       int port;
                       StartClient caller;
                       CreateFrame abc;

Authenticate(User user,int port,StartClient cl ){
                                this.parent=user;
                                this.port=port;
				label1=new JLabel();
				label1.setText("Password");
				text1 = new JTextField(15);
                                caller=cl;
				
		
				label=new JLabel();
				label.setText("");
				this.setLayout(new BorderLayout());
		
				SUBMIT = new JButton("SUBMIT");

				panel=new JPanel(new GridLayout(2,1));
				panel.add(label1);
				panel.add(text1);
				panel.add(label);
				panel.add(SUBMIT);
				add(panel,BorderLayout.CENTER);
				SUBMIT.addActionListener(this);
				setTitle("LOGIN FORM");
				}


public void actionPerformed(ActionEvent ae){


				String value1=text1.getText();
				
				try{
                                 cSocket=new Socket(parent.UIP,port);
				psswrchk= new DataOutputStream(cSocket.getOutputStream());
				verification= new DataInputStream(cSocket.getInputStream());
//				psswrchk.writeUTF(value1);
//				verify=verification.readUTF();
	
				}catch (IOException e){
				e.printStackTrace();
				}

//				if(verify.equals("valid")){
				try{
				width = verification.readUTF();
				height = verification.readUTF();
                              
		
				}catch (IOException e){
				e.printStackTrace();		
				}
			//	abc= new CreateFrame(cSocket,width,height,parent,this);
				//dispose();
				}
				

				

public void close()
{
                            try {
                                cSocket.close();
                                System.out.println("Closing Sockets and connection for: "+parent.Uname+" "+parent.UIP);
                                abc=null;
                                caller.freeChild();
                            } catch (IOException ex) {
                                Logger.getLogger(Authenticate.class.getName()).log(Level.SEVERE, null, ex);
                            }
}
			
	}

