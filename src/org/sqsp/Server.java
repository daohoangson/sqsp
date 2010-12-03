package org.sqsp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.uonghuyquan.GameServer;


public class Server {
	private static GameServer js;
	public static void main(String[] args) {
		//Run Server
		
		js = new GameServer("Game Server By Quan Uong Huy ...");
		
		//Enable Windows x button
		js.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					exit();
				}
			}
		);
	}
	
	static void exit() {
		try {
			js.finalize();
		}
		catch (Throwable e) {
			System.out.println("Error closing application...\n");
		}
		System.exit(0);
	}
}