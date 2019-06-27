package desktop_vnc.desktop_control_server;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class InitConnection{
		
	ServerSocket socket = null;
        Socket sc=null;
	DataInputStream instream = null;
	DataOutputStream outstream = null;
	String width="";
	String height="";
        startServer parent;
        SendScreen senscre;
        ReceiveEvents re;
			
	InitConnection(int port, startServer Caller,ServerSocket ss){
                this.parent=Caller;
		Robot robot = null;
		Rectangle rectangle = null;
		try{
			System.out.println("Awaiting Connection from Client");
			socket=ss;
			
			GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gDev = gEnv.getDefaultScreenDevice();
	
			Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
			String width=""+dim.getWidth();
			String height=""+dim.getHeight();
			rectangle=new Rectangle(dim);
			robot=new Robot(gDev);

			drawGUI();

			//while(true){
				 sc=socket.accept();
				instream=new DataInputStream(sc.getInputStream());
				outstream=new DataOutputStream(sc.getOutputStream());
				//String username=password.readUTF();
				//String pssword=password.readUTF();
				
//				if(pssword.equals(value1)){
//					verify.writeUTF("valid");
                                        System.out.println("Hi");
					outstream.writeUTF(width);
					outstream.writeUTF(height);
					senscre=new SendScreen(sc,robot,rectangle,this);
					re=new ReceiveEvents(sc,robot);
                                        
                                        
//                        }
//				else{
//					verify.writeUTF("Invalid");
//                                    if(sc.getRemoteSocketAddress()==null)
//                                    { 
//                                        senscre.stop();
//                                        re.stop();
//                                        re=null;
//                                        senscre=null;
//                                        System.out.println("Closing Remote Desktop Server ");
//                                        System.gc();
//                                        parent.close();
//                                        break;
//                                    }
//				}
                        //}
		}catch (Exception ex){
			ex.printStackTrace();
		}
                
                
	}
			
	private void drawGUI(){
	}
        
        public void closeINIT()
        {
            try {
                sc.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(InitConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void endConn(){
            
            re.finalize();
            re=null;
            System.gc();
            
            senscre.finalize();
            System.gc();
            closeINIT();
            
            System.out.println("Closing Remote Desktop Server ");
            parent.close();

        }

                

}
