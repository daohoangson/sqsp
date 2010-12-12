package com.uonghuyquan.old;

import java.util.ArrayList;

public class GameRoomsManager {
	ArrayList<GameRoom> GameRooms;	//Rooms array.
	int roomCnt;			//Room Counter.
	int maxRooms;			//Maximum rooms.
	
	//Constructor:
	public GameRoomsManager(int maxRooms) {
		this.maxRooms = maxRooms;
		GameRooms = new ArrayList<GameRoom>();
	}
	
	//Gets room counter
	public int getRoomCnt() {
		return roomCnt;
	}
	
	//Member leaving logic:
	public void memberLeave(String username, int roomId) {
		
		if (roomId >= 0) {
			GameRooms.get(roomId).removeMember(username);
			if ((GameRooms.get(roomId).membersCount() == 0) && (roomId > 0)) {
				removeRoom(roomId);
			}
		}
	}
	
	//Removes a room:
	public void removeRoom(int roomId) {
		if (roomId >= 0) {
			GameRooms.remove(roomId);
			roomCnt--;
		}
	}
	
	//Add a room:
	public boolean addRoom(int roomId, int size) {
		if (roomCnt < maxRooms) {
			GameRoom r = new GameRoom(roomId,size);
			GameRooms.add(r);
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
			return GameRooms.get(roomOffset);
		}
		//Room does not exist:
		return null;
	}
	
	public GameRoom getRoomByName(String username) {
		for(int i=0; i<GameRooms.size();i++){
			for(int j=0;j<GameRooms.get(i).getMembers().size();j++){
				if(GameRooms.get(i).getMembers().elementAt(j)==username)
					return GameRooms.get(i);
			}
		}
		return null;
	}
	public GameRoom getRoomById(int roomId) {
		for(int i=0; i<GameRooms.size();i++){
				if(GameRooms.get(i).getId()==roomId)
					return GameRooms.get(i);
			}
		return null;
	}
	//Set permissions for member:
	public boolean setMemberPermissions(int roomId, String username, int permissions) {

		if (roomId >= 0) {
			return GameRooms.get(roomId).setPermissions(username, String.valueOf(permissions));
		}
		
		return false;
	}
	
	//Check if a member is in room:
	public boolean isMemberInRoom(String username, int roomId) {

		if (roomId >= 0) {
			return GameRooms.get(roomId).isMember(username);
		}
		return false;
	}
	public boolean roomIsExist(int roomId){
		for(int i=0;i<roomCnt;i++){
			if(GameRooms.get(i).getId()==roomId)
				return true;
		}
		return false;
	}
	public GameRoom getLatestRoom(){
		return GameRooms.get(roomCnt-1);
	}
}
/* EOF */