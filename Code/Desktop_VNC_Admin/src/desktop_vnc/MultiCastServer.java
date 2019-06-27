package desktop_vnc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class MultiCastServer
    implements ActionListener, WindowListener
{
	//public static int DEF_SCALE = 100;
	//public static int DEF_INTERVAL = 0;
	public static int MAX_NET_QUAL = 8;

  

    private JFrame frame;
    public JTextField portField;
    private JButton sendButton;
    public JSlider sliderScaling;
    public JSlider sliderInterval;
    public JSlider sliderNetworkQuality;
    public JCheckBox checkboxShowMousePointer;
    private JPanel panelGeneral;
    private JButton pauseContinueButton;
    private JProgressBar pBar;

    private SenderThread senderThread = null;


    public MultiCastServer()
    {
    	
	    	
	    
                if (System.getProperty("swing.defaultlaf") == null) {
                    try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception e) {
                    }
		}
	
	        frame = new JFrame("MultiCast Server");
	        frame.setResizable(false);
	        frame.addWindowListener(this);
	
	        
	
	        Box boxCon = Box.createHorizontalBox();
	        JLabel labelCon = new JLabel("MultiCast Server", JLabel.CENTER);
	        labelCon.setFont(labelCon.getFont().deriveFont(17.0f ));
	        labelCon.setBorder(new EmptyBorder(10,10,10,10));
	        boxCon.add(labelCon);
	
	
//	        JPanel panelPort = new JPanel();
//	        panelPort.add(new JLabel("Port" + ":"));
//	        portField = new JTextField("2753");
//	        portField.setColumns(20);
//	        panelPort.add(portField);
	
	        // Connect button
//	        sendButton = new JButton("Send");
//	        sendButton.setActionCommand("send");
//	        sendButton.addActionListener(this);
//	        panelPort.add(sendButton);
	
	        panelGeneral = new JPanel();
	        panelGeneral.setLayout(new BoxLayout(panelGeneral, BoxLayout.Y_AXIS));
	        panelGeneral.add(boxCon);
                JPanel panelPbar = new JPanel();
                panelPbar.add(new JLabel("Sending"));
                pBar = new JProgressBar();
                pBar.setIndeterminate(true);
                panelPbar.add(pBar);
                pauseContinueButton = new JButton("Pause");
                pauseContinueButton.setActionCommand("pause");
                pauseContinueButton.addActionListener(this);
                panelPbar.add(pauseContinueButton);
                panelGeneral.add(panelPbar);

	
	        
                frame.getContentPane().add(panelGeneral);
	
	
	        frame.pack();
	        frame.setVisible(true);
    	
    }

    
	

    public void send()
    {
        senderThread = new SenderThread(this);
        senderThread.start();
    }
    
    

    public static void main(String args[])
    {
        try
        {
            new MultiCastServer();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    

    public void windowActivated(WindowEvent windowevent)
    {
    }

    public void windowClosed(WindowEvent windowevent)
    {
    }

    public void windowClosing(WindowEvent arg0)
    {
        senderThread.killThread();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
    }

    public void windowDeactivated(WindowEvent windowevent)
    {
    }

    public void windowDeiconified(WindowEvent windowevent)
    {
    }

    public void windowIconified(WindowEvent windowevent)
    {
    }

    public void windowOpened(WindowEvent windowevent)
    {
    }

    public void actionPerformed(ActionEvent action)
    {
//    	if(action.getActionCommand().equals("send")) {
//	        if(!(portField.getText().length() >= 4 && portField.getText().length() <=5 ) || !portField.getText().matches("^[0-9]+$"))
//	        {
//	            JOptionPane.showMessageDialog(frame, "Please provide a Port number with 4 or 5 digits.", "Info", 1);
//	            return;
//	        } else
//	        {
//	            sendButton.setEnabled(false);
//	            portField.setEditable(false);
//
//	            JPanel panelPbar = new JPanel();
//	            panelPbar.add(new JLabel("Sending"));
//	            pBar = new JProgressBar();
//	            pBar.setIndeterminate(true);
//	            panelPbar.add(pBar);
//	            pauseContinueButton = new JButton("Pause");
//	            pauseContinueButton.setActionCommand("pause");
//	            pauseContinueButton.addActionListener(this);
//	            panelPbar.add(pauseContinueButton);
//	            panelGeneral.add(panelPbar);
//
//	            frame.pack();
//
//	            send();
//	            return;
//	        }
//    	}
    	if(action.getActionCommand().equals("pause")) {
    		pauseContinueButton.setText("Continue");
    		pauseContinueButton.setActionCommand("continue");
    		pBar.setIndeterminate(false);
    		senderThread.isPaused = true;
    		frame.pack();
    	}
    	else if(action.getActionCommand().equals("continue")) {
    		pauseContinueButton.setText("Pause");
    		pauseContinueButton.setActionCommand("pause");
    		pBar.setIndeterminate(true);
    		senderThread.isPaused = false;
    		synchronized(senderThread.mutex) {
    			senderThread.mutex.notify();
			}
    		frame.pack();
    	}
    }

}

class SenderThread extends Thread {
	private MultiCastServer parent;
	public boolean isPaused = false;
	public Object mutex = new Object();

	private String ip;
	private int port;
	private int scaling;
	private int interval;
	private int networkQuality;
	private boolean showMouseCursor;
        public boolean killT;
        private ResourceBundle config = ResourceBundle.getBundle("desktop_vnc.config", Locale.getDefault());
        
        
	public SenderThread(MultiCastServer parent) {
            this.parent = parent;
	}
	
	public SenderThread(String ip, int port, int scaling, int interval, int networkQuality, boolean showMouseCursor) {
		this.ip = ip;
		this.port = port;
		this.scaling = scaling;
		this.interval = interval;
		this.networkQuality = networkQuality;
		this.showMouseCursor = showMouseCursor;
	}

        public void killThread(){
            killT=false;
        }
        
        public String config(String key) {
            String value;
            try {
                value = config.getString(key);
            } catch (Exception e) {
                value = "";
            }

            return value;
        }    
        
	public void run() {
		try {
                    killT=true;
                    send();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public  void send() {
		int sessionNumber = 0;

		try {
			while (killT) {
				BufferedImage image;

				if(isPaused) {
					synchronized(mutex) {
						mutex.wait();
					}
				}

				/* Get screenshot */
				image = Util.getScreenshot();
				
				
				if(parent != null) {
					this.ip = config("defaultIP");
					this.port = Integer.parseInt(config("defaultPort"));
					this.scaling = Integer.parseInt(config("scaling"));
					this.interval = Integer.parseInt(config("interval"));
					this.networkQuality = parent.MAX_NET_QUAL;
				}

				

				/* Scale image */
				//image = Util.shrink(image, scaling / 100D);

				// Depending on the network quality setting, send and resend the image
				for(int i=MultiCastServer.MAX_NET_QUAL; i >= networkQuality; i--) {
					Util.processImage(image, sessionNumber, ip, port);
				}

				/* Increase session number */
				//sessionNumber = sessionNumber < Util.MAX_SESSION_NUMBER ? ++sessionNumber
				//		: 0;
				
				Thread.sleep(interval * 10);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
