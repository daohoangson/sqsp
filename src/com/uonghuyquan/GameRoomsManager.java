package com.uonghuyquan;

import java.util.ArrayList;

public class GameRoomsManager {
	ArrayList<GameRoom> Rooms;	//Rooms array.
	int roomCnt;			//Room Counter.
	int maxRooms;			//Maximum rooms.
	
	//Constructor:
	public GameRoomsManager(int maxRooms) {
		this.maxRooms = maxRooms;
		Rooms = new ArrayList<GameRoom>();
	}
	
	//Gets room counter
	public int getRoomCnt() {
		return roomCnt;
	}
	
	//Member leaving logic:
	public void memberLeave(String username, int roomId) {
		
		if (roomId >= 0) {
			Rooms.get(roomId).removeMember(username);
			if ((Rooms.get(roomId).membersCount() == 0) && (roomId > 0)) {
				removeRoom(roomId);
			}
		}
	}
	
	//Removes a room:
	public void removeRoom(int roomId) {
		if (roomId >= 0) {
			Rooms.remove(roomId);
			roomCnt--;
		}
	}
	
	//Add a room:
	public boolean addRoom(int roomId, int size) {
		if (roomCnt < maxRooms) {
			GameRoom r = new GameRoom(roomId,size);
			Rooms.add(r);
			r.setOffset(roomCnt);
			roomCnt++;
			
			return true;
		}
		else {
			return false;
		}
	}
	
	//returns room element for general use:
	public GameRoom getRoomByOffset(int roomOffset) {
		
		if (roomOffset >= 0) {
			return Rooms.get(roomOffset);
		}
		//Room does not exist:
		return null;
	}
	
	public GameRoom getRoomByName(String username) {
		for(int i=0; i<Rooms.size();i++){
			for(int j=0;j<Rooms.get(i).getMembers().size();j++){
				if(Rooms.get(i).getMembers().elementAt(j)==username)
					return Rooms.get(i);
			}
		}
		return null;
	}
	public GameRoom getRoomById(int roomId) {
		for(int i=0; i<Rooms.size();i++){
				if(Rooms.get(i).getId()==roomId)
					return Rooms.get(i);
			}
		return null;
	}
	//Set permissions for member:
	public boolean setMemberPermissions(int roomId, String username, int permissions) {

		if (roomId >= 0) {
			return Rooms.get(roomId).setPermissions(username, String.valueOf(permissions));
		}
		
		return false;
	}
	
	//Check if a member is in room:
	public boolean isMemberInRoom(String username, int roomId) {

		if (roomId >= 0) {
			return Rooms.get(roomId).isMember(username);
		}
		return false;
	}
	public boolean roomIsExist(int roomId){
		for(int i=0;i<roomCnt;i++){
			if(Rooms.get(i).getId()==roomId)
				return true;
		}
		return false;
	}
	public GameRoom getLatestRoom(){
		return Rooms.get(roomCnt-1);
	}
}
/* EOF */