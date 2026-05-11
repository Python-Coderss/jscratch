package com.burrbox.dndiablo;

import com.jscratch.Sprite;


/*
 * the base enemy class
 */
public class DndEntity extends Sprite {
	public DndCharacterStats stats;
	public int worldX;
	public int worldY;
	
	
	public DndEntity(String name) {
		super(name);
	}

}
