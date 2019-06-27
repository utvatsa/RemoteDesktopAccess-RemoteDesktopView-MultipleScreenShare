package desktop_vnc.desktop_control_server;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


class SendScreen extends Thread{

	Socket socket=null;
	Robot robot=null;
	Rectangle rectangle=null;
	boolean continueLoop=true;
        InitConnection caller;
	
	OutputStream oos=null;
        DataInputStream dins=null;
	public SendScreen(Socket socket,Robot robot,Rectangle rect,InitConnection Caller) {
	this.socket=socket;
        this.caller=Caller;
	this.robot=robot;
	rectangle=rect;
	start();
	}
        
        public void finalize()
        {
            try {
                this.socket.close();
           System.out.println("Closing Remote Desktop Server SEnd Screen Thread");
           this.stop();
            } catch (IOException ex) {
                Logger.getLogger(SendScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

	public void run(){
	
		try{
	oos=socket.getOutputStream();
        dins=new DataInputStream(socket.getInputStream());
	
	}catch(IOException ex){
		ex.printStackTrace();
	}

	while(continueLoop){
            
        
            BufferedImage image=robot.createScreenCapture(rectangle);
            
            try{
            	ImageIO.write(image,"jpg",oos);
            }catch(IOException ex){
                caller.endConn();
                
                ex.printStackTrace();
                break;
            }
	
	
            try{
                    Thread.sleep(300);
            }catch(InterruptedException e){
                    e.printStackTrace();
            }

       
        
	
	}
}
}

