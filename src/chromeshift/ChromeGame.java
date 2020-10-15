package chromeshift;

import java.util.ArrayList;
import java.util.Iterator;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * A Simple Game of Bounce.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 * 
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 * 
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 * 
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 * 
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 * 
 * 
 * @author wallaces
 * 
 */
public class ChromeGame extends StateBasedGame {
	
	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int GAMEOVERSTATE = 2;
	public static final int GAMEWINSTATE = 3;
	public static final int LEVELTRANSSTATE = 4;
	
	public static final int MAXLEVEL = 4;
	
	public static final String RED_CHROME_RSC = "chromeshift/resource/red_chrome.png";
	public static final String BLUE_CHROME_RSC = "chromeshift/resource/blue_chrome.png";
	public static final String YELLOW_CHROME_RSC = "chromeshift/resource/yellow_chrome.png";
	public static final String GREEN_CHROME_RSC = "chromeshift/resource/green_chrome.png";
	public static final String ALL_CHROME_RSC = "chromeshift/resource/all_chrome.png";
	public static final String BULLET_RSC = "chromeshift/resource/bullet.png";
	public static final String GREEN_BULLET_RSC = "chromeshift/resource/green_bullet.png";
	public static final String BLUE_BULLET_RSC = "chromeshift/resource/blue_bullet.png";
	public static final String SUN_BULLET_RSC = "chromeshift/resource/sun_bullet.png";
	public static final String ENEMY1_RSC = "chromeshift/resource/enemy1.png";
	public static final String ENEMY2_RSC = "chromeshift/resource/enemy2.png";
	public static final String ENEMY3_RSC = "chromeshift/resource/enemy3.png";
	public static final String BOSS_RSC = "chromeshift/resource/big_boss.png";
	public static final String BOSS_BULLET_RSC = "chromeshift/resource/boss_bullet.png";
	//public static final String TILE_TEST_RSC = "chromeshift/resource/tile_test.png";
	public static final String WALL1_RSC = "chromeshift/resource/wall1.png";
	public static final String FIRE_TILE_RSC = "chromeshift/resource/fire_tile.png";
	
	public static final String START_UP_RSC = "chromeshift/resource/StartUp.png";
	public static final String VICTORY_RSC = "chromeshift/resource/victory.png";
	public static final String YOU_DIED_RSC = "chromeshift/resource/you_died.png";
	public static final String GAMEPLAY_TUT_RSC = "chromeshift/resource/gameplay_tut.png";
	public static final String SHIFT_TUT_RSC = "chromeshift/resource/shift_tut.png";
	public static final String CONTROL_TUT_RSC = "chromeshift/resource/controls_tut.png";
	public static final String ENEMY_TUT_RSC = "chromeshift/resource/enemy_tut.png";
	public static final String LEVEL1_RSC = "chromeshift/resource/level1.png";
	public static final String LEVEL2_RSC = "chromeshift/resource/level2.png";
	public static final String LEVEL3_RSC = "chromeshift/resource/level3.png";
	public static final String CHAD_TRANS_RSC = "chromeshift/resource/ChadTrans.png";
	
	
	//SOUNDS
	public static final String PEW_RSC = "chromeshift/resource/bit_lazer.wav";
	public static final String SUN_PEW_RSC = "chromeshift/resource/SunShot.wav";
	public static final String CHROME_SONG_RSC = "chromeshift/resource/ChromeSong.wav";
	public static final String ENEMY_DYING_RSC = "chromeshift/resource/enemyDying.wav";
	
	

	public static int MaxScreenWidth = 1200;
	public static int MaxScreenHeight = 1000;
	public static int BotMargin = 100;
	public int ScreenWidth = MaxScreenWidth;
	public int ScreenHeight = MaxScreenHeight;
	public int BoardHeight = ScreenHeight - BotMargin;
	int current_level;
	int tilesWide = 12;
	int tilesHigh = 9;
	
	Player player;
	Boss boss;
	ArrayList<Enemy> enemy_array;
	ArrayList<Bullet> bullet_array;
	ArrayList<BossBullet> boss_bullet_array;
	GameBoard gb;

	/**
	 * Create the ChromeGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public ChromeGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;
		
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);

				
	}


	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingState());
		addState(new GameWinState());
		addState(new LevelTransState());
		
		current_level = 0;
		enemy_array = new ArrayList<Enemy>();
		bullet_array = new ArrayList<Bullet>();
		boss_bullet_array = new ArrayList<BossBullet>();
		gb = new GameBoard(0, BotMargin, ScreenWidth, ScreenHeight, tilesWide, tilesHigh, enemy_array);

		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		
		ResourceManager.loadSound(PEW_RSC);
		ResourceManager.loadSound(SUN_PEW_RSC);
		ResourceManager.loadSound(CHROME_SONG_RSC);
		ResourceManager.loadSound(ENEMY_DYING_RSC);
		

		// preload all the resources to avoid warnings & minimize latency...
		
		ResourceManager.loadImage(RED_CHROME_RSC);
		ResourceManager.loadImage(BLUE_CHROME_RSC);
		ResourceManager.loadImage(YELLOW_CHROME_RSC);
		ResourceManager.loadImage(GREEN_CHROME_RSC);
		ResourceManager.loadImage(ALL_CHROME_RSC);
		ResourceManager.loadImage(BULLET_RSC);
		ResourceManager.loadImage(GREEN_BULLET_RSC);
		ResourceManager.loadImage(BLUE_BULLET_RSC);
		ResourceManager.loadImage(SUN_BULLET_RSC);
		ResourceManager.loadImage(ENEMY1_RSC);
		ResourceManager.loadImage(ENEMY2_RSC);
		ResourceManager.loadImage(ENEMY3_RSC);
		ResourceManager.loadImage(BOSS_RSC);
		ResourceManager.loadImage(BOSS_BULLET_RSC);
		ResourceManager.loadImage(WALL1_RSC);
		ResourceManager.loadImage(FIRE_TILE_RSC);
		ResourceManager.loadImage(START_UP_RSC);
		ResourceManager.loadImage(VICTORY_RSC);
		ResourceManager.loadImage(YOU_DIED_RSC);
		ResourceManager.loadImage(GAMEPLAY_TUT_RSC);
		ResourceManager.loadImage(SHIFT_TUT_RSC);
		ResourceManager.loadImage(CONTROL_TUT_RSC);
		ResourceManager.loadImage(ENEMY_TUT_RSC);
		ResourceManager.loadImage(LEVEL1_RSC);
		ResourceManager.loadImage(LEVEL2_RSC);
		ResourceManager.loadImage(LEVEL3_RSC);
		ResourceManager.loadImage(CHAD_TRANS_RSC);
		
		player = new Player(ScreenWidth / 2, ScreenHeight / 3 * 2);
		Create_Enemies(current_level);
		
	}
	
	
	//Spawns an enemy on a given tile, if player is on that tile, spawn on 1st tile
	public void Set_Enemy(int type, ArrayList<Tile>tile_array, int ...tiles) {
		for(int i : tiles) {
			Tile e_tile = tile_array.get(i);
			Tile safe_tile = tile_array.get(6);
			if (e_tile != gb.getTile(player.getX(), player.getY())) {
				Enemy enemy = new Enemy(e_tile.getX(), e_tile.getY(), type);
				enemy_array.add(enemy);
			} else {			
				Enemy enemy = new Enemy(safe_tile.getX(), safe_tile.getY(), type);
				enemy_array.add(enemy);
			}
		}
	}
	
	//Creates levels by setting barriers and enemies per level
	public void Create_Enemies(int level) {
		if (level == 0) {
			gb.createBarrier(13,14,21,22,25,26,33,34,73,74,81,82,85,86,93,94);
			Set_Enemy(0, gb.getTileArray(),0,11,96,107);
			Set_Enemy(1, gb.getTileArray(),5,48,59,102);
		} else if (level == 1) {
			System.out.println("SWITCHING LEVELS");
			gb.createBarrier(13,14,15,16,17,18,19,20,21,22,25,34,37,46,61,70,73,82,85,
					86,87,88,89,90,91,92,93,94);
			for (int j=0; j<=11; j++) {
				Set_Enemy(0, gb.getTileArray(),j);
			}
			for (int j=96; j<=107; j++) {
				Set_Enemy(0, gb.getTileArray(),j);
			}
		} else if (level == 2) {
			for (int j=37; j<=38; j++) {
				gb.createBarrier(j);
			}
			for (int j=45; j<=46; j++) {
				gb.createBarrier(j);
			}
			for (int j=61; j<=62; j++) {
				gb.createBarrier(j);
			}
			for (int j=69; j<=70; j++) {
				gb.createBarrier(j);
			}
			gb.createBarrier(16,28,19,31,76,88,79,91,13,22,85,94);
			Set_Enemy(0, gb.getTileArray(),6,7,47,49,58,60,101,103);
			Set_Enemy(1, gb.getTileArray(),5,48,59,102);
			Set_Enemy(2, gb.getTileArray(),0,11,96,107);
			
		} else if (level == 3) {
			boss = new Boss(gb.getTileArray().get(18).getX(), gb.getTileArray().get(18).getY());
		}
	}	
	
	public void Set_Current_Level(int i) {
		current_level = i;
	}
	
	public int Get_Level() {
		return current_level;
	}
	
	public void Remove_Barriers() {
		for (Iterator<Tile> tIter = gb.getTileArray().iterator(); tIter.hasNext();) {
			Tile t = tIter.next();
			t.blocked_path = false;
			t.removeImage(ResourceManager.getImage(ChromeGame.WALL1_RSC));
		}
		gb.getBlockedTileArray().clear();

	}
	
	public void Remove_Enemies() {
		enemy_array.clear();

	}
	
	public void Remove_Bullets() {
		bullet_array.clear();
		boss_bullet_array.clear();
	}
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new ChromeGame("Chrome Shift!", MaxScreenWidth, MaxScreenHeight));
			app.setDisplayMode(MaxScreenWidth, MaxScreenHeight, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
