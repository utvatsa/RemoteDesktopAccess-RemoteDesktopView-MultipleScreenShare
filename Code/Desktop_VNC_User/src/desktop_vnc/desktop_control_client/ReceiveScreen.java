package desktop_vnc.desktop_control_client;


import java.awt.Graphics;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

class ReceiveScreen extends Thread{
	private ObjectInputStream cObjectInputStream = null;
	private JPanel cPanel = null;
	private boolean continueLoop = true;
	InputStream oin = null;
      
	Image image1 = null;
        CreateFrame caller;


	public ReceiveScreen(InputStream in,JPanel p,CreateFrame Caller){
                //CONSTRUCTOR
		oin = in;
     
		cPanel = p;
                this.caller=Caller;

		start();
	}
        
        public void finalize()
        {
            try {
                oin.close();
                System.out.println("Closing Remote Desktop Iamge Input for Client :"+caller.caller.parent.UIP+" "+caller.caller.parent.Uname);
            } catch (IOException ex) {
                Logger.getLogger(ReceiveScreen.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }

        }
        
        
	public void run(){
		
			//Read screenshots of the client and then draw them
			while(continueLoop){
                           
				byte[] bytes = new byte[1024*1024];
				int count = 0;
				do{
                                     try{
					count+=oin.read(bytes,count,bytes.length-count);
                                     }catch(IOException ex) {
			ex.printStackTrace();
                        break;
		}
				}while(!(count>4 && bytes[count-2]==(byte)-1 && bytes[count-1]==(byte)-39));

                            try {
                                image1 = ImageIO.read(new ByteArrayInputStream(bytes));
                            } catch (IOException ex) {
                                Logger.getLogger(ReceiveScreen.class.getName()).log(Level.SEVERE, null, ex);
                            }
				image1 = image1.getScaledInstance(cPanel.getWidth(),cPanel.getHeight(),Image.SCALE_FAST);
                                
                                
				//Draw the received screenshots

				Graphics graphics = cPanel.getGraphics();
				graphics.drawImage(image1, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);
                                
                                
			

		} 
                        }
	}

