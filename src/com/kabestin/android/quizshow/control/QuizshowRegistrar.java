package com.kabestin.android.quizshow.control;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.kabestin.android.quizshow.utilities.Utilities;

public class QuizshowRegistrar {
	
	QuizshowRegistrationListener listener;

	public QuizshowRegistrar() {
		listener = null;
	}
	
	public void setRegistrationListener(QuizshowRegistrationListener qrl) {
		listener = qrl;
	}
	
	public void startRegistration() {
		
		final int PORT = 7210;
		DatagramSocket socket;
		
		try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
			return;
		}
		
		// Phase 1: Discovery
		try {
            byte[] buf = new byte[10];
            String dString = "register";
            buf = dString.getBytes();
            
            // Get our address and subnet mask
            //InetAddress localHost = Inet4Address.getLocalHost();
            InetAddress localHost = Utilities.getLocalHostLANAddress();
            String subnetMask = "255.255.240.0";  // PrefManager.getString("registrationSubnetMask", "255.255.255.0");

            subnetMask += ".";
            long networkIDmask = 0;
            for (int i=0; i<4; i++) {
            	String octet = subnetMask.substring(0, subnetMask.indexOf('.'));
            	networkIDmask = networkIDmask << 8 | Integer.valueOf(octet);
            	subnetMask = subnetMask.substring(subnetMask.indexOf('.')+1);
            }
            byte[] addressb = localHost.getAddress();
            long address = 0;
            for (int i=0; i<4; i++) address = address << 8 | addressb[i]; 
            long networkID = networkIDmask & address;
            long numberOfHosts = networkIDmask ^ 0xFFFFFFFFL;
            
//            Log.i("QuizshowRegistrar/StartRegistration", "Pinging "+numberOfHosts+" hosts");
//            
//            for (int h=0; h<numberOfHosts; h++) {
//            	address = networkID | h;
//            	String addr =  ((address >> 24) & 0xFF) + "." 
//            						+ ((address >> 16) & 0xFF) + "." 
//            						+ ((address >> 8) & 0xFF) + "." 
//            						+ (address & 0xFF);
//            	InetAddress iAddr = InetAddress.getByName(addr);
//        		Log.i("QuizshowRegistrar/StartRegistration", "Checking "+addr);           	
//            	if (iAddr.isReachable(1000)) {
//            		// machine is turned on and can be pinged
//            		Log.i("QuizshowRegistrar/StartRegistration", "Sending a ping to "+addr);
//                    DatagramPacket packet;
//                    packet = new DatagramPacket(buf, buf.length, iAddr, PORT);
//                    socket.send(packet);
//            	}
//            }
//           
//            try {
//                Thread.sleep((long)Math.random() * 300);
//            } catch (InterruptedException e) { 
//            	e.printStackTrace();
//            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		socket.close();
		
		// Phase 2: Registration
		// Spin off a server thread to receive replies 
		RegistrationServer server;
		try {
			server = new RegistrationServer(7120);
			server.start();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public class RegistrationServer extends Thread {
		private boolean finished = false;

		private ServerSocket serverSock = null;
		private DataInputStream input = null;
		private PrintStream output = null;

		public RegistrationServer(int port) throws IOException {
			serverSock = new ServerSocket();
			serverSock.setReuseAddress(true);
			serverSock.bind(new InetSocketAddress(port), 25);
		}

		public void stopAccepting() {
			finished = true;
			try {
				serverSock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run() {
			Socket remoteSock = null;

			while (!finished) {
				StringBuilder msgSb = new StringBuilder();

				boolean messageToSend = false;
				
				try {	
					remoteSock = serverSock.accept();


					try {
						input = new DataInputStream(remoteSock.getInputStream());
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
					try {
						output = new PrintStream(remoteSock.getOutputStream());
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
					//System.out.println("Got a connection from " + remoteSock.getInetAddress().getHostName());

					String line = "";
					try {
						line = input.readLine();
						if (line != null) {
							//System.out.println(line);
							String[] parts = line.split("/");
							listener.registerPlayer(parts[0], parts[1]);
							output.println("OK");
						} 
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						remoteSock.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}


}
