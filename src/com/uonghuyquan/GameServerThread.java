package com.uonghuyquan;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;


public class GameServerThread extends Thread {
	//Data members:
	public Socket socket;			//Client socket
	public GameServer server;			//Main server class
	private GameIO io;				//Input/Output
	private String name;			//User Name
	//Constructor
	public GameServerThread(GameServer server, Socket socket) throws IOException {
		this.socket = socket;
		this.server = server;
		io = new GameIO(socket);
	}
	
	//Here every thread is assigned to a user, created in JServer.
	public void run() {
		
		try {		
			//Check if server is full:
			if (server.isServerFull()) {
				io.writeError(GameMessage.ERROR);
				return;
			}
			
			//main loop to deal with client
			while (socket.isConnected()) {
				
				//hello
				GameMessage m = io.read();
				if(m.is(GameMessage.HELLO)){
					io.writeOK();
				}
				//login
				login();
				
				//join room
				joinRoom();
				
				//room state
				roomState();
				
				//room
				room();
			}
			this.stop();
		} 	
		catch (IOException e) {
			// TODO Auto-generated catch block
			server.output(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void login() throws IOException {
	    while (name == null) {
	        GameMessage m = io.read();
	        if (m.is(GameMessage.LOGIN)) {
	            String username = m.getParam("Username");
	            String password = m.getParam("Password");
	
	            if (username.length() == 0) {
	                io.writeError(GameMessage.E_INVALID_USERNAME);
	                continue;
	            }
	
	            if (password.length() == 0) {
	                io.writeError(GameMessage.E_INVALID_PASSWORD);
	                continue;
	            }
	
	            if (server.isNameTaken(username)) {
	                io.writeError(GameMessage.E_LOGGEDIN_USERNAME);
	                continue;
	            }
	
	            // TODO: check password?
	
	            this.name = username;
	            server.addClient(socket, username);
	            io.writeOK();
	        } else {
	            io.writeError(GameMessage.E_INVALID);
	        }
	    }
	}

	private void joinRoom() throws IOException{
		while (server.getRoomId(socket) == -1) {
			GameIO.debug("Waiting in loop", 4);
			GameMessage m = io.read();
			GameMessage response = null;

			switch (m.getCode()) {
			case GameMessage.ROOMS:
				response = new GameMessage(GameMessage.OK);
				response.addParam("Rooms",server.getRoomMgr().getRoomCnt());
				io.write(response);
				break;
			case GameMessage.ROOM_MAKE:
				Random rand = new Random();
				GameRoomsManager roomsMgr = server.getRoomMgr();
				int newRoomId;
				do{
					newRoomId = rand.nextInt(100);
				}while(server.getRoomMgr().roomIsExist(newRoomId));
				roomsMgr.addRoom(newRoomId, 400);

				GameRoom gameRoom = roomsMgr.getLatestRoom();
				boolean flag = gameRoom.addMember(name);
				if(!flag){
					io.writeError(GameMessage.E_INVALID);
					break;
				}
				server.setClientRoom(socket, newRoomId);
				response = new GameMessage(GameMessage.OK);
				gameRoom.buildRoomInfoMessage(response);
				//System.err.println(response);
				io.write(response);
				break;
			case GameMessage.ROOM_INFO:
			case GameMessage.ROOM_JOIN:
				GameRoom roomInfo;
				if (m.getCode() == GameMessage.ROOM_INFO) {
					int roomOffset = m.getParamAsInt("Room-Offset");
					roomInfo = server.getRoomMgr().getRoomByOffset(roomOffset);
				} else {
					int roomId = m.getParamAsInt("RoomID");
					roomInfo = server.getRoomMgr().getRoomById(roomId);
					if (roomInfo != null) {
						flag = roomInfo.addMember(name);
						if(!flag){
							io.writeError(GameMessage.E_INVALID);
							break;
						}
						server.setClientRoom(socket, roomId);
					}
				}

				if (roomInfo != null) {
					response = new GameMessage(GameMessage.OK);
					roomInfo.buildRoomInfoMessage(response);
					io.write(response);
				} else {
					io.writeError(GameMessage.E_ROOM_NOT_FOUND);
				}
				break;
			}
			System.out.println("this room id:"+server.getRoomId(socket));
		}
	}
	private void roomState() throws IOException, InterruptedException{
		GameRoom thisRoom = server.getRoomMgr().getRoomByName(name);
		while(thisRoom.getStt() == 0){
			Thread.sleep(1000);
			GameMessage m = new GameMessage(GameMessage.ROOM_STATE);
			thisRoom.buildRoomInfoMessage(m);
			io.write(m);
			
			////////////////////////////////////////////////
			m = io.read();
			if(m.getParamAsInt("Ready") == 1){
				thisRoom.setReady(name);
			}
			if(m.getParamAsInt("Ready") == 0){
				thisRoom.setNotReady(name);
			}
			if(thisRoom.readyToPlay() && thisRoom.getUsers()>1){
				thisRoom.setStt(1);
				m = new GameMessage(GameMessage.ROOM_STATE);
				thisRoom.buildRoomInfoMessage(m);
				io.write(m);
			}
			System.out.println("------this room stt:"+thisRoom.getStt());
		}
	}
	private void room() throws IOException, InterruptedException{
		GameRoom thisRoom = server.getRoomMgr().getRoomByName(name);
		if(!thisRoom.isHost(name))return;
		else System.out.println("host:"+name);
		GameMessage m = null;
		int turn = 0;
		while(!thisRoom.getFinished()){
			int code[] = new int[2];
			String playing = thisRoom.getNameByOffset(turn);
			System.out.println("turn:"+playing);
			int i;
			for(i=0;i<2;i++){
				System.out.println("loop");
				m = new GameMessage(GameMessage.TURN);
				m.addParam("Turn",playing);
				for(int j = 0;j<server.getConTot();j++){
					if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId()){
						io = new GameIO(server.getCliSocks().elementAt(j));
						io.write(m);
					}
				}
				for(int j = 0;j<server.getConTot();j++){
					if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId() && server.getCliNames().elementAt(j).equals(playing)){
						io = new GameIO(server.getCliSocks().elementAt(j));
						m = io.read();
					}
				}
				if(m.getCode() != GameMessage.GO)
					for(int j = 0;j<server.getConTot();j++){
						if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId() && server.getCliNames().elementAt(j).equals(playing)){
							io = new GameIO(server.getCliSocks().elementAt(j));
							m = io.read();
						}
					}
				else{
					int location = m.getParamAsInt("Location");
					System.out.println(location);
					if(location<thisRoom.getSize() && location>-1 && thisRoom.getNotcheat(i) == 0){
						code[i] = thisRoom.getCode(location);
						thisRoom.setNotcheat(i);
						m =  new GameMessage(GameMessage.GO_MOVED);
						m.addParam("Username",playing);
						m.addParam("Location", location);
						m.addParam("Code", thisRoom.getCode(i));
						for(int j = 0;j<server.getConTot();j++){
							if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId()){
								io = new GameIO(server.getCliSocks().elementAt(j));
								io.write(m);
							}
						}
						i++;
						System.out.println("send"+i);
					}else{
						io.writeError(GameMessage.ERROR);
					}
				}
			}
			m = new GameMessage(GameMessage.GO_DONE);
			m.addParam("Username", playing);
			for(int j = 0;j<server.getConTot();j++){
				if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId()){
					io = new GameIO(server.getCliSocks().elementAt(j));
					io.write(m);
				}
			}
			// if true
			if(code[0] == code[1]){
				System.out.println("hello");
				int thisOffset = turn;
				int thisScore = thisRoom.getScore(thisOffset);
				thisScore++;
				thisRoom.setScore(thisScore, thisOffset);
				m = new GameMessage(GameMessage.SCORED);
				for(int j=0;j<thisRoom.conCnt;j++){
					m.addParam("User"+j,thisRoom.getNameByOffset(j));
					m.addParam("Score"+j,thisRoom.getScore(j));
				}
				for(int j = 0;j<server.getConTot();j++){
					if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId()){
						io = new GameIO(server.getCliSocks().elementAt(j));
						io.write(m);
					}
				}
				//if win
				if(thisRoom.getScore(thisOffset) >= thisRoom.getSize()/4){
					m = new GameMessage(GameMessage.WON);
					for(int j=0;j<thisRoom.conCnt;j++){
						m.addParam("User"+j,thisRoom.getNameByOffset(j));
						m.addParam("Score"+j,thisRoom.getScore(j));
					}
					for(int j = 0;j<server.getConTot();j++){
						if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId()){
							io = new GameIO(server.getCliSocks().elementAt(j));
							io.write(m);
						}
					}
					thisRoom.setFinished(true);
					for(int j = 0;j<server.getConTot();j++){
						if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId()){
							server.getRoomMgr().memberLeave(server.getCliNames().elementAt(j), thisRoom.getId());
						}
					}
					server.getRoomMgr().removeRoom(thisRoom.getId());
					Thread.sleep(10000);
				}
			}
			turn++;
			turn = turn%thisRoom.getUsers();
		}
	}
	private void chat(GameMessage m, String content) throws IOException {
		GameRoom thisRoom = server.getRoomMgr().getRoomByName(name);
		m = new GameMessage(GameMessage.CHAT);
		m.addParam("Content",content);
		m.addParam("User", name);
		for(int j = 0;j<server.getConTot();j++){
			if(server.getRoomId(server.getCliSocks().elementAt(j)) == thisRoom.getId()){
				io = new GameIO(server.getCliSocks().elementAt(j));
				io.write(m);
			}
		}
	}
}
/* EOF */