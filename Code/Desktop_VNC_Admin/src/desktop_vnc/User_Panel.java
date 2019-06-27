package desktop_vnc;
// MINE
import desktop_vnc.desktop_control_server.startServer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import javax.imageio.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import jdk.management.resource.ResourceApprover;
import javax.swing.UIManager;


import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;



public class User_Panel implements WindowListener{
    
    private static ResourceBundle confi;
    public Socket client;
    public JFrame frame;
    private static JTextField ipField;
    private JTextField nameField;
    private String Usname;
    private String ServIP;
    private JButton sendButton;
    
    public UserServer us;
    public startServer ControlServer;
    
    ServerSocket remotesocket;
     
    
    public User_Panel(){
        try {
            //CONSTRUCTOR
            if (System.getProperty("swing.defaultlaf") == null) {
                try {
                    UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
                } 
                catch (Exception e) {}
            }

        remotesocket= new ServerSocket(7080);
        } catch (IOException ex) {
            Logger.getLogger(User_Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        confi=ResourceBundle.getBundle("desktop_vnc.config",Locale.getDefault());
        client=new Socket();
        
        // FRAME
        frame = new JFrame("User Panel");
        frame.setLayout(new CardLayout());
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(new Dimension(400, 700));
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    remotesocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(User_Panel.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(sendButton.isEnabled()==false)
                {
                    us.close();
                    disConnect(Usname, ServIP);
                    resetPanel();
                }
                else
                {
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                }
            }
           
        });
        
        // COLOR
        int red = 55;
        int green = 55;
        int blue = 55;
        Color back = new Color(red,green,blue);
        
        red = 200;
        green = 200;
        blue = 200;
        Color white = new Color(red,green,blue);
        
        red = 12;
        green = 122;
        blue = 207;
        Color button = new Color(red,green,blue);
        

        UIManager UI=new UIManager();
        UI.put("OptionPane.background",back);
        UI.put("Panel.background",back);
        
         /* 
        ---------------------------------------------------------------------------------
            SENDER PANEL
        ---------------------------------------------------------------------------------	
        */
            frame.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            frame.getContentPane().setBackground(back);


            JPanel namePane = new JPanel(new BorderLayout());
            
            JLabel l = new JLabel("Please Type your Name in the given Field");
            Font myFont = new Font("Serif",Font.BOLD,18);
            l.setFont(myFont);
            l.setForeground(white);
            l.setBorder(BorderFactory.createEmptyBorder(20, 80, 10, 80));
            namePane.add(l,BorderLayout.NORTH);
            
            nameField = new JTextField("",20);
	    myFont = new Font("Serif",Font.BOLD,16);
	    nameField.setFont(myFont);
            nameField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    namePane.add(nameField,BorderLayout.SOUTH);
            
            c.gridx = 2;
            c.gridy = 0;
            namePane.setBackground(back);
            namePane.setBorder(BorderFactory.createEmptyBorder(10, 80, 15, 80));
            frame.add(namePane,c);
            
            JPanel ipPane = new JPanel(new BorderLayout());
            l = new JLabel("Enter Server IP");
            myFont = new Font("Serif",Font.BOLD,18);
            l.setFont(myFont);
            l.setForeground(white);
            l.setBorder(BorderFactory.createEmptyBorder(20, 80, 10, 80));
            ipPane.add(l,BorderLayout.NORTH);
            
            ipField = new JTextField("",20);
	    myFont = new Font("Serif",Font.BOLD,16);
	    ipField.setFont(myFont);
            ipField.setHorizontalAlignment(JTextField.CENTER);
            ipField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    ipPane.add(ipField,BorderLayout.SOUTH);
	    
            c.gridx = 2;
	    c.gridy = 1;
            ipPane.setBackground(back);
            ipPane.setBorder(BorderFactory.createEmptyBorder(10, 80, 15, 80));
            frame.add(ipPane,c);
            
            frame.pack();

	    JPanel sendPane = new JPanel();
            sendButton= new JButton("SEND");
	    myFont = new Font("Serif",Font.BOLD,20);
	    sendButton.setFont(myFont);
            sendButton.setActionCommand("send");
            sendButton.setBackground(button);
            sendButton.setForeground(white);
            sendButton.setOpaque(true);
            sendButton.setBorderPainted(false);
            
            sendButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if(nameField.getText().length() == 0 && ipField.getText().length() == 0)
                    {
                        System.out.println("Name & IP Empty");
JOptionPane.showMessageDialog(frame,"<html><font color='white'>Please provide your Name & Server IP</font></html>","Warning", 
                                JOptionPane.WARNING_MESSAGE);                        return;
                    } 
                    else if(nameField.getText().length() == 0 && ipField.getText().length() > 0)
                    {
                        System.out.println("Name Empty");
JOptionPane.showMessageDialog(frame,"<html><font color='white'>Please provide your Name</font></html>","Warning", 
                                JOptionPane.WARNING_MESSAGE);                        return;
                    } 
                    else if(nameField.getText().length() > 0 && ipField.getText().length() == 0)
                    {
                        System.out.println("IP Empty");
JOptionPane.showMessageDialog(frame,"<html><font color='white'>Please provide the Server IP</font></html>","Warning", 
                                JOptionPane.WARNING_MESSAGE);                        return;
                    }
                    else
                    {
                        Usname=nameField.getText();
                        ServIP=ipField.getText();
                        int result=connectToAdmin(Usname, ServIP);
                        if(result==-1){
                            //DO SOMETHING
 JOptionPane.showMessageDialog(frame,"<html><font color='white'>Connection Exists</font></html>","IP already in List", 
                                    JOptionPane.ERROR_MESSAGE);
                            nameField.setText("");
                            ipField.setText("");
                        }
                        else if(result==1){
                            sendButton.setEnabled(false);
                            nameField.setEditable(false);
                            ipField.setEditable(false);
                        }
                    }
                   
                }
            });
            
            sendButton.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
            sendPane.add(sendButton);
            c.gridx = 2;
	    c.gridy = 2;
            sendPane.setBorder(BorderFactory.createEmptyBorder(20, 80, 30, 80));
            sendPane.setBackground(back);
            frame.add(sendPane,c);
            
            frame.getRootPane().setDefaultButton(sendButton);
            		            
            frame.pack();
           
    }
    
    public void resetPanel()
    {
        try {
            if(client!=null)
                client.close();
            client=null;
            if(us!=null)
                us.stop();
            us=null;
            if(ControlServer!=null)
                ControlServer=null;
            System.gc();
            //disConnect(Usname, ServIP);
            ipField.setText("");
            
        } catch (IOException ex) {
            Logger.getLogger(User_Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    
    public int connectToAdmin(String name, String serverIP)
    {
        int port =Integer.parseInt(confi.getString("serverPort"));
        int result=0;

        try
        {
            //CREATE CLIENT SOCKET TO MAKE NEW CONNECTION
            client.connect(new InetSocketAddress(serverIP, port), 150);
            DataOutputStream dout = new DataOutputStream(client.getOutputStream());
            DataInputStream din=new DataInputStream(client.getInputStream());

            String ip=client.getLocalSocketAddress().toString();

            dout.writeUTF(ip);
            dout.writeUTF(name);
            dout.writeInt(9);    // +9 denotes connect

            //CHECK IF IP EXISTS 1-NEW 2-EXISTS 3-RECONNECT
            int status=din.readInt();

            if(status==2)  // EXISTS AND ONLINE
            {
                client.close();
                result=-1;
            }
            if(status==3)  //EXISTS AND OFFLINE 
            {
                System.out.println("Reconnecting");
                us=new UserServer(this,7078);
                us.start();     
                MultiCastClient mc = new MultiCastClient();
                mc.receive();
                result=1;
            }
            else
            {
                //STATUS is 1   // NEW CONNECTION
                BufferedImage img=Util.getScreenshot();    
//              JFrame frame1 = new JFrame("IMG");
//              frame1.getContentPane().add(new JLabel(new ImageIcon(img)));
//              frame1.pack();
//              frame1.setVisible(true);


                //STEP 1
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", byteArrayOutputStream);
                byte[] arr=byteArrayOutputStream.toByteArray();                       
                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                dout.write(size);
                dout.write(byteArrayOutputStream.toByteArray());
                        
                //STEP 2
//                ImageIO.write(img, "jpg", client.getOutputStream());
//
//                client.close();
//
//                for(int i=0;i<=99999;i++);
//                client=new Socket(serverIP,port);
//                out = new DataOutputStream(client.getOutputStream());
//                in=new DataInputStream(client.getInputStream());

                //RECEIVING ACKNOWLEDGEMENT OF IMAGE SEND
                int mesg=din.readInt();       
                if(mesg==1){
                    System.out.println("Success");
                    us=new UserServer(this,7078);
                    us.start();     
                    MultiCastClient mc = new MultiCastClient();
                    mc.receive();
                }
                
                client.close();
                result=1;
                      
            }//else closes
            
        }//try closes
        
        catch (IOException ex) {            
            Logger.getLogger(User_Panel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showOptionDialog(frame,"SERVER NOT FOUND! PLEASE CHECK IP","SERVER NOT FOUND", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
            resetPanel();

        } catch (AWTException ex) {
            Logger.getLogger(User_Panel.class.getName()).log(Level.SEVERE, null, ex);
               JOptionPane.showOptionDialog(frame,"ERROR CONNECTING TO SERVER","ERROR CONNECTING TO SERVER! PLEASE TRY AGAIN", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
               resetPanel();
            
        } catch (Exception ex) {
            //System.out.println("lll");
            Logger.getLogger(User_Panel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showOptionDialog(frame,"ERROR CONNECTING TO SERVER","ERROR CONNECTING TO SERVER! PLEASE TRY AGAIN", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
            resetPanel();
        }
        return result;
    }
    
    public void disConnect(String name, String serverIP)
    {
        int port =Integer.parseInt(confi.getString("serverPort"));

        try
        {
            //CREATE CLIENT SOCKET TO MAKE NEW CONNECTION
            client = new Socket(serverIP, port);  
            DataOutputStream dout = new DataOutputStream(client.getOutputStream());
            DataInputStream din=new DataInputStream(client.getInputStream());

            String ip=client.getLocalSocketAddress().toString();
            dout.writeUTF(ip);
            dout.writeUTF(name);
            dout.writeInt(-9);    //-9 denotes disconnect
            
            client.close();
            
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.dispose();
           System.exit(1);
          //  System.exit();
            
            
        } catch (IOException ex) {
            Logger.getLogger(User_Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
   
    public static void main(String[] args){
        // DO NOTHING
       // User_Panel u = new User_Panel();
       // u.build();
       User_Panel user_panel=new User_Panel();
    }
    
    
    public void startRemoteServer(){
        System.out.println("Starting Remote Server");
        ControlServer=null;
        ControlServer=new startServer(remotesocket);
       
    }

    @Override
    public void windowOpened(WindowEvent e) {
        int x=10;
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosing(WindowEvent e) {
        disConnect(Usname, ServIP);
        us.close();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
    }

    @Override
    public void windowClosed(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowIconified(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowActivated(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}




 class UserServer extends Thread
{
  
    User_Panel parent;
    ServerSocket serverSocket;
    Socket server;
    int port;
    int x;
    boolean open;
  
    public UserServer(User_Panel parent,int p) throws IOException, ClassNotFoundException, Exception
    {
        this.parent = parent;
        port=p;
        serverSocket = new ServerSocket(port);
        x=0;         
        serverSocket.setSoTimeout(1800);
        server=new Socket();
        server.setSoTimeout(5000);
        open=true;
    }
    
    public void close(){
            open=false;
           
    }
    
       
    public void run()
    {
        try {  
                while(open)
                {
                    server = serverSocket.accept();
                    DataInputStream din=new DataInputStream(server.getInputStream());
                    DataOutputStream dout=new DataOutputStream(server.getOutputStream());
                     
                   try
                   {
                       //dout.writeInt(9);
                         int remote=din.readInt();  //1 to start
                         System.out.println("Remote:"+remote);
                         if(remote==1)
                         {
                             System.out.println("Hurrah");
                             parent.startRemoteServer();
                             remote=-1;
                         }
                        BufferedImage img=Util.getScreenshot();
//                       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); 
//                    ImageIO.write(img, "jpg", byteArrayOutputStream);
//                    byte[] arr=byteArrayOutputStream.toByteArray();
//                        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
//                        dout.write(size);
//                        dout.write(byteArrayOutputStream.toByteArray());
                        
                        ImageIO.write(img, "jpg",dout);
                        server.close();
                        dout.flush();
//                            //pw.flush();
                           // System.out.println("hi:"+(x++));
                         
//                        }
//                        else 
//                           parent.open=false;
                    }   catch (IOException ex) {
                           JOptionPane.showOptionDialog(parent.frame,"CONNECTION LOST","CONNECTION TO SERVER TERMINATED", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
                           server.close();
                           dout.flush();
                           parent.resetPanel();
                        
                             Logger.getLogger(UserServer.class.getName()).log(Level.SEVERE, null, ex);
                    }   catch (AWTException ex) {
                            Logger.getLogger(UserServer.class.getName()).log(Level.SEVERE, null, ex);
                              JOptionPane.showOptionDialog(parent.frame,"CONNECTION LOST","CONNECTION TO SERVER TERMINATED", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
                           server.close();
                           dout.flush();
                           parent.resetPanel();
                    }
                   
                   
                   
                   
               
//                   else
//                   {
//                       //handle close
//                      // dout.writeInt(-9);
//                       dout.writeInt(-9);
//                       server.close();
//                       break;
//                       
//                       
                   //}
                }
                //server.close();
            }   catch (IOException ex) {
             Logger.getLogger(UserServer.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
}
