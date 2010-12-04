package Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Button;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.Vector;

import shared.GameIO;
import shared.GameMessage;


public class GameServer extends Frame implements ActionListener, Runnable {
	//Data members:
	private static final long serialVersionUID = 1L;
	private Panel inPanel, outPanel;					//Panels
	private TextArea output;							//Output messages
	private int outCnt, maxCon,conTot;					//Server properties: maxCon- Maximum connections, conTot- Number of clients.
	private Button btnStart, btnExit;					//Buttons
    private TextField status, port;						//Editable text fields
    private Label lblPort;								//Port label
	private ServerSocket inSock;						//Server main socket mgr
	private Socket socket;								//Temp Socket
    private boolean running;							//Run flag
    private Thread thListen;							//Listening thread
    private Vector<Socket> cliSocks;					//Clients sockets list 
    private Vector<String> cliNames;					//Clients names list
    private Vector<Integer> cliRoom;					//Clients rooms list
    private GameRoomsManager roomMgr;						//Class Room Manager
  
    //Constructor
	public GameServer(String caption) {
		super(caption);
		setSize(new Dimension(500,500));
		setLayout(new BorderLayout());
		
		//Set Starting values:
		maxCon = 40;
		conTot = 0;
		inSock = null;
		outCnt = 1;
		running = false;
		cliSocks = new Vector<Socket>(maxCon);
		cliNames = new Vector<String>(maxCon);
		cliRoom = new Vector<Integer>((maxCon/GameRoom.maxInRoom));
		roomMgr = new GameRoomsManager(10);
		
		//Create the panels and buttons:
		outPanel = new Panel(new BorderLayout());
		inPanel = new Panel(new FlowLayout());
		
		inPanel.setBackground(Color.WHITE);
		outPanel.setBackground(Color.WHITE);
		
		output = new TextArea();
		//output.setEnabled(false);
		output.setEditable(false);
		output.setBackground(Color.WHITE);
		outPanel.add(output, BorderLayout.CENTER);

		
		status = new TextField(" - To start server press 'Start Server' button... - ");
		status.setBackground(Color.PINK);
		status.setEditable(false);
		outPanel.add(status, BorderLayout.NORTH);
		
		btnStart = new Button("Start Server");
		btnStart.addActionListener(this);
		btnStart.setBackground(Color.WHITE);
		inPanel.add(btnStart);
		
		btnExit = new Button("Exit");
		btnExit.addActionListener(this);
		btnExit.setBackground(Color.BLACK);
		btnExit.setForeground(Color.WHITE);
		inPanel.add(btnExit);
		
		lblPort = new Label("Port:");
		inPanel.add(lblPort);
		
		port = new TextField(GameIO.DEFAULT_PORT+"");
		inPanel.add(port);
		
		add(outPanel,BorderLayout.CENTER);
		add(inPanel, BorderLayout.SOUTH);
		setVisible(true);
	}
	
	//Buttons buttons and more buttons:
	public void actionPerformed(ActionEvent e) {
		//Start server button:
		if (e.getSource() == btnStart) {
			if (e.getActionCommand() == "Start Server") {
				btnStart.setLabel("Stop Server");
				startServer();
			}
			else {
				btnStart.setLabel("Start Server");
				stopServer();
			}
		}
		
		//Exit button:
		if (e.getSource() == btnExit) {
			stopServer();
			System.exit(0);
		}
	}

	
	//Start server method:
	public void startServer() {
		//Tell admin we are starting.
		status("Starting Server please wait...\n", Color.YELLOW);
		
		//Set the listening socket:
		if (inSock == null) {
			try {
				inSock = new ServerSocket(Integer.parseInt(port.getText()));
			}
			catch (IOException e) {
				output("Error opening Socket: " + e.getMessage() + "\n");
				stopServer();
			}
			try {
				//We wait a bit here (for the flow)...
				Thread.sleep(1000);
			}
			catch (Exception e){
				output(e.getMessage());
			}
			output("Listening to port:" + port.getText() + "\n");
		}
		else {
			output("Server is already running...\n");
		}
		
		//We got here (phew) all is good so far...
		running = true;
		
		//redirect the main to the listening thread
		thListen = new Thread(this);
		thListen.start();
		
		//show it and be proud:
		status("Server is Running...\n", Color.GREEN);
	}
	
	public void run() {
		//run forever(or until you die muhahaha....):
		while (running){
			
			//Accept any in bound connection request (to the limit):
        	try {
        		if (inSock != null) {
        			socket = inSock.accept();
        			new GameServerThread(this, socket).start();
        			
        			//document connected peers:
        			output("connected local port=" + socket.getLocalPort() + " remote port=" + socket.getPort() + "\n");
        		}
        	}
        	catch (IOException e) {
        		output("Closing Sockets:\n");
        		output(e.getMessage() + "\n");
        		break;
        	}
        }
		
		//kill listening thread: 
		stopServer();
		thListen.stop();
		thListen.destroy();
	}
	
	//Name check method (only 1 unique name for each user):
	synchronized boolean isNameTaken(String name) {
		int i;
		
		//check for names:
		for (i=0 ; i<cliNames.size() ; i++) {
			if (cliNames.elementAt(i).equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	//Add a client to client list:
	synchronized void addClient(Socket socket, String name) {
		cliSocks.add(socket);
		cliNames.add(name);
		cliRoom.add(-1);
		output("User:" + name + " in Socket:" + socket.toString() + "Connected...\n");
		conTot++;
	}
	
	//Remove a client from clients list:
	synchronized void removeClient(Socket socket, String name) {
		int i;
		
		for (i=0 ; i<cliNames.size() ; i++) {
			if ((cliNames.get(i) == name) && (cliSocks.get(i) == socket)) {
				roomMgr.memberLeave(name, getRoomId(socket));
				cliRoom.remove(i);
				break;
			}
		}
		cliNames.remove(name);
		cliSocks.remove(socket);
		output("User:" + name + " in Socket:" + socket.toString() + " Disconnected...\n");
		conTot--;
	}
	
	//Clients room indexes
	synchronized void setClientRoom(Socket socket, int roomId) {
		int i;
		
		for (i=0 ; i<cliNames.size() ; i++) {
			if (cliSocks.elementAt(i).equals(socket)) {
				cliRoom.set(i, roomId);
				return;
			}
		}
	}
	
	//get the room name associated with socket
	public int getRoomId(Socket socket) {
		int i;
		
		for (i=0 ; i<cliNames.size() ; i++) {
			if (cliSocks.get(i) == socket) {
				return cliRoom.get(i);
			}
		}
		return 0;
	}
	
	//Stop server logic:
	public void stopServer() {
		status("Stopping Server please wait...\n", Color.YELLOW);
		if (inSock != null) {
			try {
				//Tell everyone server is going down:
				GameMessage m = new GameMessage(GameMessage.LOST);
				GameIO io;
				for(int i=0 ; i<cliSocks.size() ; i++) {
					Socket sockTmp = cliSocks.elementAt(i);
		    		io = new GameIO(sockTmp);
					io.write(m);
					sockTmp.close();
				}
				cliNames.removeAllElements();
				cliRoom.removeAllElements();
				cliSocks.removeAllElements();
				roomMgr = null;
				roomMgr = new GameRoomsManager(10);
				conTot = 0;
				inSock.close();
			}
			catch (IOException e) {
				output("Error Closing Socket: " + e.getMessage() + "\n");
				return;
			}
			inSock = null;
			try {
				//We wait a bit here...
				Thread.sleep(1200);
			}
			catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		
		//Set status to idle
		running = false;
		status("Server stopped...\n", Color.RED);
	}

	//This method will check available user:
	synchronized boolean isClientExists(String clientName) throws IOException {
		int i;
		
    	for(i=0 ; i<cliSocks.size() ; i++) {
    		if (cliNames.elementAt(i).equals(clientName)) {
				return true;
    		}
    	}
    	return false;
	}
	
	//Test if server is full:
	public boolean isServerFull() {
		if (conTot < maxCon) {
			return false;
		}
		else {
			return true;
		}
	}

	//Output method will append to log:
	public void output(String msg) {
		output.append("[" + outCnt + "] " + msg);
		outCnt++;
	}
	
	//Status will set the current server status:
	public void status(String msg, Color c) {
		status.setText(msg);
		status.setForeground(Color.BLACK);
		status.setBackground(c);
		output.append("[" + outCnt + "] " + msg);
		outCnt++;
	}

	//Finalize, if all goes as planned exit nicely...
	public void finalize() throws Throwable {
		if (running) {
			stopServer();
		}
		super.finalize();
	}
	//gets room manager
	public GameRoomsManager getRoomMgr() {
		return roomMgr;
	}
	public Vector<Integer> getCliRoom() {
		return cliRoom;
	}
	public Vector<String> getCliNames() {
		return cliNames;
	}
	public Vector<Socket> getCliSocks() {
		return cliSocks;
	}
	public int getConTot() {
		return conTot;
	}
}

/* EOF */

