package desktop_vnc.desktop_control_client;

import desktop_vnc.User;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JOptionPane;

public class StartClient{
    static String port ;
    static String IP;
    public User parent;
    public CreateFrame CF;
    public DataOutputStream outstream;
    public DataInputStream instream;
    public String width;
    public String height;
  
    private Socket cSocket;
        
    public StartClient(User user){
        //CONSTRUCTOR
        port="7080";
        this.parent=user;
        this.IP=user.UIP;
    
        Start();
            
    }

    public  void Start(){
        System.out.println("Remote Desktop Client Started for Client:"+parent.UIP+" "+parent.Uname);
        initialize(Integer.parseInt(port));
    }

    public void initialize(int port){				
        System.out.println("Connecting to the Server of Client:"+parent.UIP+" "+parent.Uname);
//Authenticate class is responsible for security purposes
//				frame1= new Authenticate(parent, port,this );
//	
//				frame1.setSize(300,80);
//				frame1.setLocation(500,300);
//				frame1.setVisible(true);

        try{
            cSocket=new Socket(parent.UIP,port);
            outstream= new DataOutputStream(cSocket.getOutputStream());
            instream= new DataInputStream(cSocket.getInputStream());
//				outstream.writeUTF(value1);
//				verify=instream.readUTF();

            }catch (IOException e){
                e.printStackTrace();
        }
			//if(verify.equals("valid")){
        try{
            width = instream.readUTF();
            height = instream.readUTF();
            } catch (Exception ex){
                ex.printStackTrace();
        }
                     
        CF= new CreateFrame(cSocket,width,height,this);
    }
                        
    public void freeChild()
    {
        CF.frame.dispose();
        CF=null;
        System.out.println("Closing Remote Desktop Client for :"+parent.UIP+" "+parent.Uname);
    }

}



