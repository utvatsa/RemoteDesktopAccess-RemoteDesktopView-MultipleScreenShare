package desktop_vnc;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class MultiCastClient implements ActionListener, WindowListener, KeyListener {

	//public static int DEF_SCALE = 100;

	public JFrame frame;

	public JTextField portField;

	private JButton receiveButton;

	public JSlider sliderScaling;

	private JPanel panelGeneral;

	private JButton pauseContinueButton;

	private JProgressBar pBar;

	private ReceiverThread receiverThread = null;
        

	public MultiCastClient() {
            
                
		
                if (System.getProperty("swing.defaultlaf") == null) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}
		}

//		frame = new JFrame("Multicast Client");
//
//		frame.setResizable(false);
//		frame.addWindowListener(this);
//
//
//		Box boxCon = Box.createHorizontalBox();
//		JLabel labelCon = new JLabel("Multicast Client", JLabel.CENTER);
//		labelCon.setFont(labelCon.getFont().deriveFont(17.0f));
//		labelCon.setBorder(new EmptyBorder(10, 10, 10, 10));
//		boxCon.add(labelCon);
//
//		JPanel panelPort = new JPanel();
//		panelPort.add(new JLabel("Port" + ":"));
//		portField = new JTextField("2753");
//		portField.setColumns(20);
//		panelPort.add(portField);
//
//		// Connect button
//		receiveButton = new JButton("Receive");
//		receiveButton.setActionCommand("receive");
//		receiveButton.addActionListener(this);
//		panelPort.add(receiveButton);
//
//		
//
//		panelGeneral = new JPanel();
//		panelGeneral.setLayout(new BoxLayout(panelGeneral, BoxLayout.Y_AXIS));
//		panelGeneral.add(boxCon);
//		panelGeneral.add(panelPort);
//
//		
//
//		frame.getContentPane().add(panelGeneral);
//
//		
//		frame.pack();
//		frame.setVisible(true);
	}


	public void receive() {
		receiverThread = new ReceiverThread(this);
		receiverThread.start();
	}

	public static void main(String args[]) {
		try {
			new MultiCastClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void windowActivated(WindowEvent windowevent) {
	}

	public void windowClosed(WindowEvent windowevent) {
	}

	public void windowClosing(WindowEvent arg0) {
            //System.out.println("Thread Killing");
            receiverThread.killThread();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

	public void windowDeactivated(WindowEvent windowevent) {
	}

	public void windowDeiconified(WindowEvent windowevent) {
	}

	public void windowIconified(WindowEvent windowevent) {
	}

	public void windowOpened(WindowEvent windowevent) {
	}

	public void actionPerformed(ActionEvent action) {
		
		
		if (action.getActionCommand().equals("receive")) {
			if(!(portField.getText().length() >= 4 && portField.getText().length() <=5 ) || !portField.getText().matches("^[0-9]+$")) {
				JOptionPane.showMessageDialog(frame,
						"Please provide a Port number with 4 or 5 digits.",
						"Info", 1);
				return;
			} else {
				receiveButton.setEnabled(false);
				portField.setEditable(false);

				JPanel panelPbar = new JPanel();
				panelPbar.add(new JLabel("Receiving"));
				pBar = new JProgressBar();
				pBar.setIndeterminate(true);
				panelPbar.add(pBar);
				

				

				panelGeneral.add(panelPbar);

				frame.pack();

				receive();

				return;
			}
		} 
	}

	public void keyPressed(KeyEvent keyevent) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

}

class ReceiverThread extends Thread implements KeyListener {
	private MultiCastClient parent;

	public boolean isPaused = false;

	public Object mutex = new Object();

	boolean fullscreen = false;

	private JFrame frame;

	private JWindow fullscreenWindow = null;

	private JLabel labelImage;

	private JLabel windowImage;

	private BufferedImage image;
        
        public boolean killT = true;

        private ResourceBundle config = ResourceBundle.getBundle("desktop_vnc.config", Locale.getDefault());
        

	public ReceiverThread(MultiCastClient parent) {
		this.parent = parent;
	}

	public void run() {
            
		labelImage = new JLabel();
		windowImage = new JLabel();

		frame = new JFrame("MultiCast Output");
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(labelImage);
		frame.setSize(300, 10);
		frame.setVisible(true);
		frame.addKeyListener(this);


		fullscreenWindow = new JWindow();
		fullscreenWindow.getContentPane().add(windowImage);
		fullscreenWindow.addKeyListener(this);

		try {
			receive();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
        
	public void receive() {

		try {
			while (killT) {
				if (isPaused) {
					synchronized (mutex) {
						mutex.wait();
					}
				}
                                
                               // System.out.println("Thread Running");
                                    
				image = Util.receiveImage(config("defaultIP"), Integer.parseInt(config("defaultPort")));
				if (image == null) {
                                    continue;
				}

				/* Scale image */
				image = Util.shrink(image, Integer.parseInt(config("scaling")) / 100D);
				labelImage.setIcon(new ImageIcon(image));
				//windowImage.setIcon(new ImageIcon(image));

				frame.pack();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

        public void killThread(){
            killT = false;
           // System.out.println("Thread Killed");
        }

	public void keyPressed(KeyEvent keyevent) {	
	}

	public void keyReleased(KeyEvent keyevent) {
	}

	public void keyTyped(KeyEvent keyevent) {
	}

}
