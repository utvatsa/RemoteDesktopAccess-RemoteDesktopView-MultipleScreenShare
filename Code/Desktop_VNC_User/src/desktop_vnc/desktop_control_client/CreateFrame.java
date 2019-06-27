package desktop_vnc.desktop_control_client;



import desktop_vnc.User;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.util.zip.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

class CreateFrame extends Thread {
	public String width="";
        public String height="";
	public JFrame frame ;
        private JDesktopPane desktop;
	private Socket cSocket;
	private JInternalFrame interFrame;
	private JPanel cPanel;
        
        ReceiveScreen rs;
        SendEvents se;
        StartClient caller;
	//JDesktopPane represents the main container that will contain all connected clients' screens

	 
	public CreateFrame(Socket csocket, String width, String height, StartClient Caller) {
            //CONSTRUCTOR
            this.caller=Caller;    
            
            frame=new JFrame(caller.parent.Uname+" "+caller.parent.UIP);
        
            desktop = new JDesktopPane();
            interFrame = new JInternalFrame("Server Screen", true, true, true);
            cPanel = new JPanel();
       

            this.width = width;
            this.height = height;
            this.cSocket = csocket;
            
            start();
                
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    
                        try {
                            cSocket.close();
                          
                            
                            rs.finalize();
                            se.finalize();
                            se=null;
                            rs=null;
                            
                            System.gc();
                            
                        } catch (IOException ex) {
                            Logger.getLogger(CreateFrame.class.getName()).log(Level.SEVERE, null, ex);
                            ex.printStackTrace();
                        }    
                    
                    
                    
                    System.gc();
                    //System.out.println("Closing Remote Control Client Socket for:"+caller.parent.UIP+" "+caller.parent.Uname);
                    
                    caller.freeChild();         
                }
            });
            
	}//CONSTRUCTOR END
	
	//Draw GUI per each connected client

	public void drawGUI() {
		frame.add(desktop, BorderLayout.CENTER);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Show thr frame in maximized state
	
		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);		//CHECK THIS LINE
		frame.setVisible(true);
		interFrame.setLayout(new BorderLayout());
		interFrame.getContentPane().add(cPanel, BorderLayout.CENTER);
		interFrame.setSize(100,100);
		desktop.add(interFrame);

		try {
			//Initially show the internal frame maximized
			interFrame.setMaximum(true);
			}catch (PropertyVetoException ex) { 
				ex.printStackTrace();
		}

		//This allows to handle KeyListener events
		cPanel.setFocusable(true);
		interFrame.setVisible(true);
		
	}

	public void run() { 
		//Used to read screenshots
		InputStream in = null;
                DataOutputStream out=null;
		//start drawing GUI
		drawGUI();

		try{
			in = cSocket.getInputStream();
         
			}catch (IOException ex){
			ex.printStackTrace();
		}

		//Start receiving screenshots
		rs=new ReceiveScreen(in,cPanel,this);
		//Start sending events to the client
                se=new SendEvents(cSocket,cPanel,width,height);
	}
}
