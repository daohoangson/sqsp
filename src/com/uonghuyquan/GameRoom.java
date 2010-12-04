package com.uonghuyquan;

import java.util.Random;
import java.util.Vector;

import com.daohoangson.GameMessage;

public class GameRoom {
	int id;								//Room Id.
	int offset;
	int size;							//room size
	int conCnt;							//Number of connections.
	int stt;							//1 - playing,0 - waiting
	final static int maxInRoom = 4;		//Maximum members.
	int code[];							//Code of result
	int notcheat[];						
    private Vector<String> members;		//Administrators.
    private Vector<String> permissions;	//Permissions of chat room. (0- host, 1- player).
    private Vector<Boolean> memstt;		//status of menbers
    private Vector<Integer> scores;		//score
    int turn;							// 0,1,2,3 <=> offset
    boolean finished;					//true or false
    
    //Constructor
	public GameRoom(int id, int size) {
		this.offset = -1;
		this.id = id;
		this.size = size;
		this.turn = 0;
		finished = false;
		conCnt = 0;
		members = new Vector<String>(maxInRoom);
		permissions = new Vector<String>(maxInRoom);
		scores = new Vector<Integer>(maxInRoom);
		for(int i=0;i<maxInRoom;i++)
			scores.add(0);
		memstt = new Vector<Boolean>(40);
		stt = 0;
		
		notcheat = new int[size];
		int cardTypes = size / 10;
		if(size <= 10)cardTypes=size/2;
		code = new int[size];
		Random rand = new Random();
		for(int i=0;i<size;i++){
			code[i] = -1;
			notcheat[i] = 0;
		}
		for(int i = 0; i < cardTypes;i++) {
			for(int j = 0; j < size/cardTypes; j++) {
				int k;
				do {
					k = rand.nextInt(size);
				} while (code[k] != -1);
				code[k] = i;
			}
		}
	}
	public void setNotcheat(int index) {
		this.notcheat[index] = 1;
	}
	public int getNotcheat(int index) {
		return notcheat[index];
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	//Adds a member to room:
	public boolean addMember(String username) {
		if(conCnt >= maxInRoom)return false;
		//Check if first member, if so he is the host
		if (conCnt == 0) {
			members.add(username);
			permissions.add("0");
			memstt.add(false);
		}
		else {
			members.add(username);
			permissions.add("1");
			memstt.add(false);
		}
		conCnt++;
		return true;
	}
	
	//Removes a member from room:
	public void removeMember(String username) {
		int i;
		
		for (i=0 ; i<members.size() ; i++) {
			if (members.elementAt(i).equals(username)) {
				permissions.removeElementAt(i);
				members.removeElementAt(i);
				memstt.removeElementAt(i);
				conCnt--;
				return;
			}
		}
	}
	
	//Get member permissions:
	public int getPermissions(String username) {
		int i;
		
		for (i=0 ; i<members.size() ; i++) {
			if (members.elementAt(i).equals(username)) {
				return Integer.parseInt((String)permissions.elementAt(i));
			}
		}
		
		return -1;
	}
	
	//Set member permissions:
	public boolean setPermissions(String username, String permissions) {
		int i;
		
		for (i=0 ; i<members.size() ; i++) {
			if (members.elementAt(i).equals(username)) {
				this.permissions.set(i, permissions);
				return true;
			}
		}
		return false;
	}
	

	//Check if member is in room:
	public boolean isMember(String memberName) {
		int i;
		
		for (i=0 ; i<members.size() ; i++) {
			if (members.elementAt(i).equals(memberName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isHost(String memberName) {
		if(members.elementAt(0).equals(memberName))
			return true;
		return false;
	}
	
	public boolean setReady(String username){
		for (int i=0 ; i<members.size() ; i++) {
			if (members.elementAt(i).equals(username)) {
				this.memstt.set(i, true);
				return true;
			}
		}
		return false;
	}
	
	public boolean setNotReady(String username){
		for (int i=0 ; i<members.size() ; i++) {
			if (members.elementAt(i).equals(username)) {
				this.memstt.set(i, false);
				return true;
			}
		}
		return false;
	}
	
	public boolean getMemStt(int index){
		return memstt.elementAt(index);
	}

	//Count members in room;
	public int membersCount() {
		return conCnt;
	}
	public int getId() {
		return id;
	}
	public int getSize() {
		return size;
	}
	public String getNameByOffset(int index){
		return members.elementAt(index);
	}
	public int getUsers(){
		return members.size();
	}
	public int getStt() {
		return stt;
	}
	public void setStt(int stt) {
		this.stt = stt;
	}
	//
	public void buildRoomInfoMessage(GameMessage m) {
		m.addParam("RoomID", getId());
		m.addParam("Room-Size", getSize());
		m.addParam("Host", getNameByOffset(0));
		m.addParam("Users", getUsers());
		for (int i = 0; i < getUsers(); i++) {
				m.addParam("User" + i, getNameByOffset(i));
				m.addParam("Ready" + i, getMemStt(i) ? 1 : 0);
		}
		m.addParam("Status", getStt());
	}
	public boolean readyToPlay(){
		for(int i=0; i< memstt.size();i++){
			if(memstt.elementAt(i)==false)
				return false;
		}
		return true;
	}
	public Vector<String> getMembers() {
		return members;
	}
	public void setTurn(int turn) {
		this.turn = turn;
	}
	public int getTurn() {
		return turn;
	}
	public int getCode(int index) {
		return code[index];
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public boolean getFinished(){
		return this.finished;
	}
	public void setScore(int score, int offset) {
		this.scores.setElementAt(offset, score);
	}
	public int getScore(int offset) {
		return scores.elementAt(offset);
	}
}

/* EOF */

