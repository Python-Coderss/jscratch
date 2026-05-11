package com.burrbox.dndiablo;

import com.jscratch.Costume;
import com.jscratch.Sprite;
import com.jscratch.Stage;

public class DnDiablo {
	private static int gamestate = 0;
	public static final int MENU = 0;
	public static final int PLAYERSELECTION = 0;
	public static final int GAME = 0;
	
	
    public static void main(String[] args) {
    	Stage.run(() -> {
    		Stage stage = Stage.getInstance();
    		createTitle(stage);
    		createPlayButton(stage);
    		createCharacterCreationButton(stage);
    	});
    }
    
    private static void createTitle(Stage stage) {
    	stage.addSprite(new Sprite("title") {
			public Sprite init() {
				addCostume(new Costume("title", "dndiablo/title.png", 0, 0)); // no rotation so I do not care
				this.nextCostume();
				this.setSize(200);
				this.goTo(-220, 160);
				this.whenIReceive("menuoff", () -> {
					this.hide();
				});
				this.whenScaleChanged((old, newscale) -> {
					double scfa = newscale / (old+0.0);
					this.goTo(this.getX() * scfa, this.getY() * scfa);
					this.setScaleX(this.getScaleX() * scfa);
					this.setScaleY(this.getScaleY() * scfa);
					
				});
				return this;
			}
		}.init());
    }
    
    private static void createPlayButton(Stage stage) {
    	stage.addSprite(new Sprite("play") {
			public Sprite init() {
				addCostume(new Costume("play", "dndiablo/play.png", 0, 0)); // no rotation so I do not care
				this.nextCostume();
				this.setSize(200);
				this.goTo(-160, 80);
				this.whenIReceive("menuoff", () -> {
					this.hide();
				});
				this.whenClicked(() -> {
					if (isVisible()) {
					stage.broadcast("begin");
					stage.broadcast("menuoff");
					}
				});
				this.whenScaleChanged((old, newscale) -> {
					double scfa = newscale / (old+0.0);
					this.goTo(this.getX() * scfa, this.getY() * scfa);
					this.setScaleX(this.getScaleX() * scfa);
					this.setScaleY(this.getScaleY() * scfa);
					
				});
				return this;
			}
		}.init());
    }
    
    private static void createCharacterCreationButton(Stage stage) {
    	stage.addSprite(new Sprite("characterCreation") {
			public Sprite init() {
				addCostume(new Costume("play", "dndiablo/charactercreation.png", 0, 0)); // no rotation so I do not care
				this.nextCostume();
				this.setSize(200);
				this.goTo(-160, 40);
				this.whenIReceive("menuoff", () -> {
					
					this.hide();
				});
				this.whenClicked(() -> {
					if (isVisible()) {
					stage.broadcast("makecharacter");
					stage.broadcast("menuoff");
					}
				});
				this.whenScaleChanged((old, newscale) -> {
					double scfa = newscale / (old+0.0);
					this.goTo(this.getX() * scfa, this.getY() * scfa);
					this.setScaleX(this.getScaleX() * scfa);
					this.setScaleY(this.getScaleY() * scfa);
					
				});
				return this;
			}
		}.init());
    }
}
