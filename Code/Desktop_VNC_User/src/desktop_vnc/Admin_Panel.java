package desktop_vnc;

import java.awt.Toolkit;
import java.util.*;
import java.awt.image.BufferedImage;

import java.awt.*;
import javax.swing.*;
import java.applet.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Admin_Panel
 implements ActionListener, WindowListener{
    

    protected ArrayList<User> Users;
    private AdminServer adminServer;
    public JButton castButton;
    public JFrame frame;
    public JPanel grid;
    public JLabel cs;
    public static int connSize;
    
    public java.util.Date dt;
    public SimpleDateFormat sdf;
    
    public Admin_Panel(){
        //CONSTRUCTOR
        if (System.getProperty("swing.defaultlaf") == null) {
            try {
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
            } 
            catch (Exception e) {}
	}
       
        Users=new ArrayList<User>();
        dt=new java.util.Date();
        sdf=new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        connSize = 0;
          
        
        // FRAME
        frame = new JFrame("Admin Panel");
        //frame.setLayout(new CardLayout());
        //Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //frame.setSize(dim);
        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int) tk.getScreenSize().getWidth());
        int ySize = ((int) tk.getScreenSize().getHeight());
        frame.setSize(xSize,ySize);
        //frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        
        frame.getContentPane().setBackground(back);
        /* 
        ---------------------------------------------------------------------------------
            RECEIVER (ADMIN) PANEL
        ---------------------------------------------------------------------------------	
        */
        frame.setLayout(new BorderLayout());
        grid = new JPanel(new GridLayout(0,4));
        grid.setBackground(new Color(63,63,63));
        JScrollPane screenPane = new JScrollPane(grid);
        screenPane.setSize(860,1000);
        screenPane.setVisible(true);
        screenPane.setBorder(null);
        frame.add(screenPane,BorderLayout.CENTER);

            JPanel buttonPane = new JPanel();
            //buttonPane.setBackground(white);
           // buttonPane.setSize(860,800);
            //buttonPane.setVisible(true);
            frame.add(buttonPane,BorderLayout.SOUTH);  
            
            JPanel castPanel = new JPanel();
            castButton = new JButton("Cast Screen");
            castButton.setFont(new Font("Verdana",1,22));
            castButton.setForeground(white);
            castButton.setBackground(button);
            castButton.setOpaque(true);
            castButton.setBorderPainted(false);
            castButton.setActionCommand("cast");
            castButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    MultiCastServer ms=new MultiCastServer();
                    ms.send();
                }
            });
            castButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            castButton.setFocusPainted(false);
            castPanel.add(castButton);
            castPanel.setBorder(BorderFactory.createEmptyBorder(10, 70, 50, 70));
            castPanel.setBackground(back);
            buttonPane.setBackground(back);
            buttonPane.add(castPanel);
          //  frame.pack();

            JPanel connPane = new JPanel();
            JLabel tempL = new JLabel("Connected Clients : ");
            cs = new JLabel();
            connPane.add(tempL);
            connPane.add(cs);
            cs.setText(""+connSize);
            connPane.setBackground(back);
            cs.setForeground(white);
            tempL.setForeground(white);
            buttonPane.add(connPane);
		
            // STARTS THREAD TO RECIEVE INFO FROM CLIENTS
            receive();

    }
     
    public void receive() {
        try{
            adminServer = new AdminServer(this);
            adminServer.start();
        } catch (SQLException ex) {
            Logger.getLogger(Admin_Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Admin_Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Admin_Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void createConn(String IP, String name, BufferedImage img){
        try {
                connSize++;
                cs.setText(""+connSize);
                User newuser =new User(this,IP,name,img,connSize);
                Users.add(newuser);
            
                System.out.println(" Name: "+ name+" IP: "+IP+" Connected at: "+ sdf.format(dt));
                
        } catch (Exception ex) {
            Logger.getLogger(Admin_Panel.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void refreshConn(User user,String newname)
    {
        System.out.println(" Name: "+ user.Uname +" IP: "+user.UIP+" ReConnected at: "+ sdf.format(dt) + " with new Name: "+newname);
        connSize++;
        cs.setText(""+connSize);
        //RESUMING THREAD FOR USER AND SENDING NEW NAME
        user.resumeThread(newname);  
    }
    
    public void closeConn(String IP, String Name)
    {
        for(User user:Users)
        {
            if(IP.equalsIgnoreCase(user.UIP)==true)
            {
                user.pauseThread();
                connSize--;
                cs.setText(""+connSize);
                System.out.println(" Name: "+ Name+" IP: "+IP+" disconnected at: "+ sdf.format(dt));
                break;
            }   
        }
    }
    
    public static void main(String[] args){
        Admin_Panel admin_Panel=new Admin_Panel();
    }
        
     
    public void windowActivated(WindowEvent windowevent){
    }

    public void windowClosed(WindowEvent windowevent){
    }

    public void windowClosing(WindowEvent arg){
        //frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //DO SOMETHING
    }

    public void windowDeactivated(WindowEvent windowevent){
    }

    public void windowDeiconified(WindowEvent windowevent){
    }

    public void windowIconified(WindowEvent windowevent){
    }

    public void windowOpened(WindowEvent windowevent){
    }   
    
    public void actionPerformed(ActionEvent action){
    }
        
}


class AdminServer extends Thread
{
    Admin_Panel parent;
    private ServerSocket serverSocket;
    private Socket server;
    private BufferedImage image;

   public AdminServer(Admin_Panel parent) throws IOException, SQLException, ClassNotFoundException, Exception
   {
        this.parent = parent;
        serverSocket = new ServerSocket(7077);
    }

   public void run()
   {
       while(true)
        {
           try
           {
                server = serverSocket.accept();
                DataInputStream din=new DataInputStream(server.getInputStream());
                DataOutputStream dout=new DataOutputStream(server.getOutputStream());

                // IDENTIFY CONNECT AND DISCONNECT
                
                String IP = din.readUTF();
                IP=IP.substring(0, (IP.indexOf(":")));
                IP=IP.substring(1);
                String name = din.readUTF();
                
                int dowhat=din.readInt();   //+9 to connect -9 to disconnect
                
                if(dowhat==9)   
                {
                    //CONNECT
                    int flag=1;
                    for(User user:parent.Users)
                    {

                            if(IP.equalsIgnoreCase(user.UIP)==true) // IP EXISTS
                            {
                                if(user.online==true)   //IP EXISTS AND ONLINE
                                {
                                    flag=2;
                                    break;
                                }
                                else        // IP EXISTS BUT OFFLINE  
                                {
                                    dout.writeInt(3);    //writing 3 to indicate re-connection
                                    parent.refreshConn(user,name); // refreshing connection sending newname in name
                                    flag=3;
                                    break;      //RESUMING 
                                }
                            }

                    }

                    if(flag==2)         //IP ALREADY EXISTS AND ONLINE
                    {   
                        dout.writeInt(2);   // writing 2 to indicate already connected
                                Thread.sleep(30);
                        server.close();
                    }   
                    if(flag==1)
                    {                       //CREATE NEW CONNECTION
                        dout.writeInt(1);   //writing 1 to indiacte new connection
                       // Thread.sleep(20);
                        //STEP 1                           
                        byte[] sizeAr = new byte[4];
                        din.read(sizeAr);
                        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
                        byte[] imageAr = new byte[size];
                        din.read(imageAr);
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageAr));  

                        //STEP 2    
//                            BufferedImage img=(BufferedImage)ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));                        
                           // server.close();

                        for(int i=0;i<=99999;i++);

                        // CHECK IMAGE
    //                        JFrame frame1 = new JFrame("NEW");
    //                        frame1.getContentPane().add(new JLabel(new ImageIcon(img)));
    //                        frame1.pack();
    //                        frame1.setVisible(true);   

                          //STEP 2
    //                        server = serverSocket.accept();
    //                        din=new DataInputStream(server.getInputStream());
    //                        dout=new DataOutputStream(server.getOutputStream());

                        //Sending acknwledgement for image received
                        dout.writeInt(1);

                        parent.createConn(IP,name,img);                   //CREATE CONENCTION
                        server.close();                      

                    }//if flag==1 ends here
                }
                else
                {
                    //DISCONNECT
                       parent.closeConn(IP, name);
                       server.close();
                }
            }//try ends here
            catch(SocketTimeoutException st)
            {
                System.out.println("Admin Server Socket timed out!");
                // break;
            }
            catch(IOException e)
            {
                e.printStackTrace();
                //break;
            }
            catch(Exception ex)
            {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }//while loop ends
   }
}

