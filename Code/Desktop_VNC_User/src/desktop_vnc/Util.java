package desktop_vnc;

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

import javax.imageio.ImageIO;

public class Util {

	/* Flags and sizes */
	public static int HEADER_SIZE = 8;

	public static int MAX_PACKETS = 255;

	public static int SESSION_START = 128;

	public static int SESSION_END = 64;

	public static int DATAGRAM_MAX_SIZE_SENDER = 65507 - HEADER_SIZE;
	public static int DATAGRAM_MAX_SIZE = 65507;

	public static int MAX_SESSION_NUMBER = 255;


	/*
	 * The absolute maximum datagram packet size is 65507, The maximum IP packet
	 * size of 65535 minus 20 bytes for the IP header and 8 bytes for the UDP
	 * header.
	 */
	public static String OUTPUT_FORMAT = "jpg";

	public static int COLOUR_OUTPUT = BufferedImage.TYPE_INT_RGB;

	/**
	 * Takes a screenshot (fullscreen)
	 *
	 * @return Sreenshot
	 * @throws AWTException
	 * @throws IOException
	 */
	public static BufferedImage getScreenshot() throws AWTException, IOException {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		Rectangle screenRect = new Rectangle(screenSize);

		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRect);

		return image;
	}

	/**
	 * Converts BufferedImage to byte array
	 *
	 * @param image
	 *            Image to convert
	 * @param format
	 *            Image format (JPEG, PNG or GIF)
	 * @return Byte Array
	 * @throws IOException
	 */
	public static byte[] bufferedImageToByteArray(BufferedImage image,
			String format) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, format, baos);
		return baos.toByteArray();
	}

	/**
	 * Scales a bufferd image
	 *
	 * @param source
	 *            Image to scale
	 * @param w
	 *            Image widht
	 * @param h
	 *            Image height
	 * @return Scaled image
	 */
	public static BufferedImage scale(BufferedImage source, int w, int h) {
		Image image = source
				.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
		BufferedImage result = new BufferedImage(w, h, COLOUR_OUTPUT);
		Graphics2D g = result.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return result;
	}

	/**
	 * Shrinks a BufferedImage
	 *
	 * @param source
	 *            Image to shrink
	 * @param factor
	 *            Scaling factor
	 * @return Scaled image
	 */
	public static BufferedImage shrink(BufferedImage source, double factor) {
		int w = (int) (source.getWidth() * factor);
		int h = (int) (source.getHeight() * factor);
		return scale(source, w, h);
	}

	/**
	 * Copies a BufferedImage
	 *
	 * @param image
	 *            Image to copy
	 * @return Copied image
	 */
	public static BufferedImage copyBufferedImage(BufferedImage image, String multicastAddress,
			int port) {
		BufferedImage copyOfIm = new BufferedImage(image.getWidth(), image
				.getHeight(), image.getType());
		Graphics2D g = copyOfIm.createGraphics();
		g.drawRenderedImage(image, null);
		g.dispose();
		return copyOfIm;
	}

	public static void processImage(BufferedImage image, int sessionNumber, String multicastAddress,
			int port) throws IOException {
		// Compress byte array
		byte[] imageByteArray = Pack.compressData(Util.bufferedImageToByteArray(image,
				OUTPUT_FORMAT));
		int packets = (int) Math.ceil(imageByteArray.length
				/ (float) DATAGRAM_MAX_SIZE_SENDER);

		/* If image has more than MAX_PACKETS slices -> error */
		if (packets > MAX_PACKETS) {
			System.out.println("Image is too large to be transmitted!");
			return;
		}

		/* Loop through slices */
		for (int i = 0; i <= packets; i++) {
			int flags = 0;
			flags = i == 0 ? flags | SESSION_START : flags;
			flags = (i + 1) * DATAGRAM_MAX_SIZE_SENDER > imageByteArray.length ? flags
					| SESSION_END
					: flags;

			int size = (flags & SESSION_END) != SESSION_END ? DATAGRAM_MAX_SIZE_SENDER
					: imageByteArray.length - i * DATAGRAM_MAX_SIZE_SENDER;

			/* Set additional header */
			byte[] data = new byte[HEADER_SIZE + size];
			data[0] = (byte) flags;
			data[1] = (byte) sessionNumber;
			data[2] = (byte) packets;
			data[3] = (byte) (DATAGRAM_MAX_SIZE_SENDER >> 8);
			data[4] = (byte) DATAGRAM_MAX_SIZE_SENDER;
			data[5] = (byte) i;
			data[6] = (byte) (size >> 8);
			data[7] = (byte) size;

			/* Copy current slice to byte array */
			System.arraycopy(imageByteArray, i * DATAGRAM_MAX_SIZE_SENDER,
					data, HEADER_SIZE, size);
			/* Send multicast packet */
			Util.sendImage(data, multicastAddress, port);

			/* Leave loop if last slice has been sent */
			if ((flags & SESSION_END) == SESSION_END)
				break;
		}
	}

	/*
	 *
	 */
	/**
	 * Sends a byte array via multicast Multicast addresses are IP addresses in
	 * the range of 224.0.0.0 to 239.255.255.255.
	 *
	 * @param imageData
	 *            Byte array
	 * @param multicastAddress
	 *            IP multicast address
	 * @param port
	 *            Port
	 * @return <code>true</code> on success otherwise <code>false</code>
	 */
	public static boolean sendImage(byte[] imageData, String multicastAddress,
			int port) {
		InetAddress ia;

		boolean ret = false;
		int ttl = 2;

		try {
			ia = InetAddress.getByName(multicastAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return ret;
		}

		MulticastSocket ms = null;

		try {
			ms = new MulticastSocket();
			ms.setTimeToLive(ttl);
			DatagramPacket dp = new DatagramPacket(imageData, imageData.length,
					ia, port);
			ms.send(dp);
			ret = true;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if (ms != null) {
				ms.close();
			}
		}

		return ret;
	}

        /*
            Sends the name of the sender to the server
        */
        public static boolean sendName(String name, String multicastAddress,
			int port){
            InetAddress ia;
                byte[] name_byte = name.getBytes();

		boolean ret = false;
		int ttl = 2;

		try {
			ia = InetAddress.getByName(multicastAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return ret;
		}

		MulticastSocket ms = null;

		try {
			ms = new MulticastSocket();
			ms.setTimeToLive(ttl);
			DatagramPacket dp = new DatagramPacket(name_byte, name_byte.length,
					ia, port);
			ms.send(dp);
			ret = true;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if (ms != null) {
				ms.close();
			}
		}

		return ret;
        }
        
	public static BufferedImage receiveImage(String multicastAddress, int port) {
		InetAddress ia = null;
		MulticastSocket ms = null;

		BufferedImage image = null;

		try {
			/* Get address */
			ia = InetAddress.getByName(multicastAddress);

			/* Setup socket and join group */
			ms = new MulticastSocket(port);
			ms.joinGroup(ia);

			int currentSession = -1;
			int slicesStored = 0;
			int[] slicesCol = null;
			byte[] imageData = null;
			boolean sessionAvailable = false;

			/* Setup byte array to store data received */
			byte[] buffer = new byte[DATAGRAM_MAX_SIZE];

			/* Receiving loop */
			while (true) {
				/* Receive a UDP packet */
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				ms.receive(dp);
				byte[] data = dp.getData();

				/* Read header infomation */
				short session = (short) (data[1] & 0xff);
				short slices = (short) (data[2] & 0xff);
				int maxPacketSize = (int) ((data[3] & 0xff) << 8 | (data[4] & 0xff)); // mask
				// the
				// sign
				// bit
				short slice = (short) (data[5] & 0xff);
				int size = (int) ((data[6] & 0xff) << 8 | (data[7] & 0xff)); // mask
				// the
				// sign
				// bit



				/* If SESSION_START falg is set, setup start values */
				if ((data[0] & SESSION_START) == SESSION_START) {
					if (session != currentSession) {
						currentSession = session;
						slicesStored = 0;
						/* Consturct a appropreately sized byte array */
						imageData = new byte[slices * maxPacketSize];
						slicesCol = new int[slices];
						sessionAvailable = true;
					}
				}

				/* If package belongs to current session */
				if (sessionAvailable && session == currentSession) {
					if (slicesCol != null && slicesCol[slice] == 0) {
						slicesCol[slice] = 1;

						System.arraycopy(data, HEADER_SIZE, imageData, slice * maxPacketSize, size);
						slicesStored++;
					}
				}

				/* If image is complete dispay it */
				if (slicesStored == slices) {
					// Uncompress Image and construct input stream
					ByteArrayInputStream bis = new ByteArrayInputStream(
							Pack.deCompressData(imageData));
					image = ImageIO.read(bis);

					break;
				}


			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ms != null) {
				try {
					/* Leave group and close socket */
					ms.leaveGroup(ia);
					ms.close();
				} catch (IOException e) {
				}
			}
		}

		return image;
	}

}
