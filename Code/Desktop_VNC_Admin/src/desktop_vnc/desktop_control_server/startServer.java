
package desktop_vnc.desktop_control_server;

import java.net.ServerSocket;









public class startServer{
    
      InitConnection ins;
      ServerSocket socket=null;
      
	public startServer(ServerSocket socket){
            this.socket=socket;
            
            System.out.println("Remote Desktop Server Started");
//		SetPassword frame1= new SetPassword();
//	    	frame1.setSize(300,80); 				
//	    	frame1.setLocation(500,300);
//		frame1.setVisible(true);	

          ins=new InitConnection(7080,this,socket);

	}
        
        public void close()
        {
            ins=null;
        }
}
