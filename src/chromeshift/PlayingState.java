package chromeshift;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jig.Vector;
import jig.Collision;
import jig.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This state is active when the Game is being played. 
 * The user can also control the character using the WAS & D keys.
 * 
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	
	Color redColor = new Color(255, 0, 0, 255);
	Color orangeColor = new Color(255, 120, 0, 255);
	//GameBoard gb;
	boolean displayPath = false;
	
	int scoreTimer = 0;
	int enemyKills = 0;
	//int score = enemyKills / scoreTimer;
	
	int cooldownTimer = 120;
	boolean cooldownReady = true;
	
	int totalEnemies = 20;
	int spawnTimer = 1000;
	//boolean noEnemies = false;
	
	int bulletDirection;
	
	int YellowShootCD = 240;
	boolean YellowCDReady = true;

	int bossShootTimer = 1000;
	boolean bossShootCD = true;
	
	int bossAoETimer = 5000;
	int timePlayerInFire = 0;
	boolean bossAoECD = false;
	
	int bossSpawnTimer = 10000;
	
	List<Integer> spawnTileList = Arrays.asList(0,11,96,107);
	
	List<Integer> spawnTypeList1 = Arrays.asList(0,1);
	List<Integer> spawnTypeList2 = Arrays.asList(0,1,2);
	
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		
	}
	

	private void Reset_Everything(int level, StateBasedGame game) {
		ChromeGame cg = (ChromeGame)game;
		cg.Remove_Barriers();
		cg.Remove_Enemies();
		cg.Remove_Bullets();
		cg.gb.clearFireArray();
		cg.player.setPosition(cg.ScreenWidth/2, cg.BoardHeight/3*2);
		cg.player.health = cg.player.maxHealth;
		cg.player.SetChrome(1);
		cg.Set_Current_Level(level);
		cg.Create_Enemies(level);
		totalEnemies = 20;
		//System.out.println("Setting level to " + cg.current_level);
	}

	public void render_health_bar(Graphics g, StateBasedGame game, Color hcolor, int left, int top, int width, int height, int phealthblocks, float healthPercent) {
		ChromeGame cg = (ChromeGame)game;
		float margin = 2f;
		
		if (cg.player.godMode) {
			hcolor = new Color(255, 255, 0, 255);
		}
		
		Color blackColor = new Color(0, 0, 0, 255);
		g.setColor(blackColor);
		g.fillRect(left, top, width,  height);
		g.setColor(hcolor);
		g.drawRect((float)left+margin, (float)top+margin, (float)width-2*margin, (float)height-2*margin);
		
		
		float block_width = ((float)width - 4*margin - (phealthblocks-1)*margin)/phealthblocks;
		for( int x = 0; x < phealthblocks; x++) {
			//float maxPlayerHealth = (float)cg.player.maxHealth;
			
			if( x >= healthPercent * phealthblocks ) {
				break;
			}
			float leftx = (float)left + 2*margin + (float)x * (block_width+margin);
			g.fillRect(leftx, (float)top + 2*margin, block_width, (float)height-4*margin);
		}
		
	}
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);

	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		ChromeGame cg = (ChromeGame)game;
		
		g.setAntiAlias(false);
		
		Color greyColor = new Color(112, 128, 144, 255);
		g.setColor(greyColor);
		g.fillRect(0, cg.BoardHeight, cg.ScreenWidth,  100);
		
		Color pathColor = new Color(0, 255, 255, 255);
		Color tileColor = new Color(255, 0, 0, 255);
		Color oldColor = g.getColor();
		g.setColor(pathColor);
		
		for (Tile t : cg.gb.getTileArray()) {
			t.render(g);
			//g.drawString(""+t.tile_ID, t.getX(), t.getY());
			if (displayPath) {
				g.setColor(tileColor);
				g.drawRect(t.getCoarseGrainedMinX(), t.getCoarseGrainedMinY(), 
						t.getCoarseGrainedWidth(), t.getCoarseGrainedHeight()); 
				g.setColor(oldColor);
			}	
			g.setColor(oldColor);
		}
		
		cg.player.render(g);
		
		for( Enemy enemy : cg.enemy_array) {
			enemy.render(g);
			if (displayPath) {
				if (enemy.pathToPlayer == null) {
					continue;
				}	
				g.setColor(pathColor);
				float lastX = (float)-1.0;
				float lastY = (float)-1.0;
				for(Tile tile : enemy.pathToPlayer ) {
					if(lastX >= 0.0) {
						g.drawLine(lastX,  lastY,  tile.getX(), tile.getY());
					}
					lastX = tile.getX();
					lastY = tile.getY();
				}
				g.setColor(oldColor);
			}
		}
		
		for (Bullet b : cg.bullet_array) {
			b.render(g);
		}
		
		for (BossBullet b : cg.boss_bullet_array) {
			b.render(g);
		}
		
		
		if (cg.Get_Level() == 3) {
			cg.boss.render(g);
			if (displayPath) {	
				g.setColor(pathColor);
				float lastX = (float)-1.0;
				float lastY = (float)-1.0;
				for(Tile tile : cg.boss.pathToPlayer ) {
					if(lastX >= 0.0) {
						g.drawLine(lastX,  lastY,  tile.getX(), tile.getY());
					}
					lastX = tile.getX();
					lastY = tile.getY();
				}
				g.setColor(oldColor);
			}
			g.drawString("Boss Health: " + cg.boss.health, 450, container.getHeight() - 50);
		}
		
		float playerHealthPercent = (float)cg.player.health / (float)cg.player.maxHealth;
		render_health_bar(g, cg, redColor, 100, container.getHeight() - 55, 300, 50, 20, playerHealthPercent);
		g.drawString("Health: " + cg.player.health, 100, container.getHeight() - 75);
		
		if( cg.Get_Level() == 3 ) {
			float bossHealthPercent = (float)cg.boss.health / (float)cg.boss.maxHealth;
			render_health_bar(g, cg, orangeColor, 800, container.getHeight() - 55, 300, 50, 20, bossHealthPercent);
			g.drawString("Boss Health " + cg.boss.health, 800, container.getHeight() - 75);
		}
		
		if (cg.player.godMode) {
			g.drawString("GODMODE ON", 305, 925);
		}
		
		
		
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		ChromeGame cg = (ChromeGame)game;
		Vector new_velocity;
		float speed = cg.player.speed;
		
		if (input.isKeyPressed(Input.KEY_G)) {
			cg.player.godMode = !cg.player.godMode;
		}
		
				
		cooldownTimer -= delta;
		if (cooldownTimer <= 0) {
			cooldownReady = true;
			cooldownTimer = 120;
		}
		
		YellowShootCD -= delta;
		if (YellowShootCD <= 0) {
			YellowCDReady = true;
			YellowShootCD = 480;
		}
		
		//Spawns enemies every second at random corners
		spawnTimer -= delta;
		if (spawnTimer <= 0) {
			if (cg.Get_Level() == 0) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(spawnTypeList1.get(rand.nextInt(spawnTypeList1.size())), cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					System.out.println("enemy type is " + spawnTypeList1.get(rand.nextInt(spawnTypeList1.size()))); 
					totalEnemies--;
				}
				spawnTimer = 1000;
			} else if (cg.Get_Level() == 1) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(spawnTypeList1.get(rand.nextInt(spawnTypeList1.size())), cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					totalEnemies--;
				}
			} else if (cg.Get_Level() == 2) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(spawnTypeList2.get(rand.nextInt(spawnTypeList2.size())), cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					totalEnemies--;
				}
			} /*else if (cg.Get_Level() == 3) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(spawnTypeList2.get(rand.nextInt(spawnTypeList2.size())), cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					totalEnemies--;
				
			} */
			
			spawnTimer = 1000;
		}
		
		if (cg.Get_Level() == 3) {
			bossSpawnTimer -= delta;
			if (bossSpawnTimer <= 0) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(spawnTypeList2.get(rand.nextInt(spawnTypeList2.size())), cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					totalEnemies--;
				}
				bossSpawnTimer = 5000;
			}
		}
			
		//Code to vizualize pathing
		if (input.isKeyPressed(Input.KEY_P)) {
			displayPath = !displayPath;
		}
		
		
		//Cheat to switch level
		if( (input.isKeyDown(Input.KEY_8))) {
			Reset_Everything(0,cg);
			cg.enterState(ChromeGame.LEVELTRANSSTATE);
		} else if( (input.isKeyDown(Input.KEY_9))) {
			Reset_Everything(1,cg);
			cg.enterState(ChromeGame.LEVELTRANSSTATE);
		} else if( (input.isKeyDown(Input.KEY_0))) {
			Reset_Everything(2,cg);
			System.out.println("level is " + cg.Get_Level());
			cg.enterState(ChromeGame.LEVELTRANSSTATE);
		} else if( (input.isKeyDown(Input.KEY_7))) {
			cg.Remove_Barriers();
			cg.Remove_Enemies();
			cg.Remove_Bullets();
			cg.player.setPosition(cg.ScreenWidth/2, cg.BoardHeight/3*2);
			cg.player.health = cg.player.maxHealth;
			cg.player.SetChrome(1);
			cg.Set_Current_Level(3);
			cg.Create_Enemies(3);
			totalEnemies = 20;
			System.out.println("level is " + cg.Get_Level());
			cg.enterState(ChromeGame.LEVELTRANSSTATE);
		}
		
		
		//Code to move player
		if(input.isKeyDown(Input.KEY_W)) {			
			new_velocity = new Vector(0f, -0.5f*speed);	
		} else if (input.isKeyDown(Input.KEY_A)) {		
			new_velocity = new Vector(-0.5f*speed, 0f);
		} else if (input.isKeyDown(Input.KEY_S)) {			
			new_velocity = new Vector(0f, 0.5f*speed);
		} else if (input.isKeyDown(Input.KEY_D)){			
			new_velocity = new Vector(0.5f*speed, 0f);
		} else {
			new_velocity = new Vector(0f,0f);
		}
		
		if (input.isKeyDown(Input.KEY_A) && input.isKeyDown(Input.KEY_W)) {		
			new_velocity = new Vector(-0.5f*speed, -0.5f);
		} 		
		if (input.isKeyDown(Input.KEY_D) && input.isKeyDown(Input.KEY_W)) {			
			new_velocity = new Vector(0.5f, -0.5f*speed);
		} 		
		if (input.isKeyDown(Input.KEY_A) && input.isKeyDown(Input.KEY_S)){			
			new_velocity = new Vector(-0.5f*speed, 0.5f);
		} 		
		if (input.isKeyDown(Input.KEY_D) && input.isKeyDown(Input.KEY_S)){	
			System.out.println("registering s key was pressed during d down");
			new_velocity = new Vector(0.5f*speed, 0.5f);
		} 
		cg.player.setVelocity(new_velocity);
		
		//Code to switch Chromes
		if( (input.isKeyDown(Input.KEY_1))) {			
			cg.player.SetChrome(1);
		} else if ((input.isKeyDown(Input.KEY_2))) {		
			cg.player.SetChrome(2);
		} else if ((input.isKeyDown(Input.KEY_3))) {			
			cg.player.SetChrome(3);
		} else if ((input.isKeyDown(Input.KEY_4))) {			
			cg.player.SetChrome(4);
		}
		
		//Code to shoot		
		/*if(input.isKeyPressed(Input.KEY_I) && cooldownReady) {	
			cg.player.Shoot(0, cg.player.chromeColor, cg);
			cooldownReady = false;
		} else if (input.isKeyPressed(Input.KEY_J) && cooldownReady) {		
			cg.player.Shoot(3, cg.player.chromeColor, cg);
			cooldownReady = false;
		} else if (input.isKeyPressed(Input.KEY_K) && cooldownReady) {			
			cg.player.Shoot(2, cg.player.chromeColor, cg);	
			cooldownReady = false;
		} else if (input.isKeyPressed(Input.KEY_L) && cooldownReady){			
			cg.player.Shoot(1, cg.player.chromeColor, cg);
			cooldownReady = false;
		} */
		
		//USED TO TEST INCREASED YELLOW CHROME SHOOTING CD
		if (cg.player.chromeColor == 4) {
			if(input.isKeyPressed(Input.KEY_I) && YellowCDReady) {	
				cg.player.Shoot(0, cg.player.chromeColor, cg);
				ResourceManager.getSound(ChromeGame.SUN_PEW_RSC).play(0.5f,1);
				YellowCDReady = false;
			} else if (input.isKeyPressed(Input.KEY_J) && YellowCDReady) {		
				cg.player.Shoot(3, cg.player.chromeColor, cg);
				ResourceManager.getSound(ChromeGame.SUN_PEW_RSC).play(0.5f,1);
				YellowCDReady = false;
			} else if (input.isKeyPressed(Input.KEY_K) && YellowCDReady) {			
				cg.player.Shoot(2, cg.player.chromeColor, cg);	
				ResourceManager.getSound(ChromeGame.SUN_PEW_RSC).play(0.5f,1);
				YellowCDReady = false;
			} else if (input.isKeyPressed(Input.KEY_L) && YellowCDReady){			
				cg.player.Shoot(1, cg.player.chromeColor, cg);
				ResourceManager.getSound(ChromeGame.SUN_PEW_RSC).play(0.5f,1);
				YellowCDReady = false;
			}
		} else {
			if(input.isKeyPressed(Input.KEY_I) && cooldownReady) {	
				cg.player.Shoot(0, cg.player.chromeColor, cg);
				ResourceManager.getSound(ChromeGame.PEW_RSC).play(1, 1);
				cooldownReady = false;
			} else if (input.isKeyPressed(Input.KEY_J) && cooldownReady) {		
				cg.player.Shoot(3, cg.player.chromeColor, cg);
				ResourceManager.getSound(ChromeGame.PEW_RSC).play(1, 1);
				cooldownReady = false;
			} else if (input.isKeyPressed(Input.KEY_K) && cooldownReady) {			
				cg.player.Shoot(2, cg.player.chromeColor, cg);
				ResourceManager.getSound(ChromeGame.PEW_RSC).play(1, 1);
				cooldownReady = false;
			} else if (input.isKeyPressed(Input.KEY_L) && cooldownReady){			
				cg.player.Shoot(1, cg.player.chromeColor, cg);
				ResourceManager.getSound(ChromeGame.PEW_RSC).play(1, 1);
				cooldownReady = false;
			}
		}
		
		//Constrains player to only moving inside screen by setting position
		if (cg.player.getX() < 25) {			
			cg.player.setPosition(25, cg.player.getY());			
		} else if (cg.player.getX() > cg.ScreenWidth - 25 - 1) {			
			cg.player.setPosition(cg.ScreenWidth - 25 - 1, cg.player.getY());			
		} else if (cg.player.getY() < 25) {			
			cg.player.setPosition(cg.player.getX(), 25);			
		} else if (cg.player.getY() > cg.BoardHeight - 25 - 1) {			
			cg.player.setPosition(cg.player.getX(), cg.BoardHeight - 25 - 1);			
		}
		
		//Stops players from entering blocked tiles
		for (Tile t : cg.gb.getBlockedTileArray()) {
			Collision c = cg.player.collides(t);
			if (c != null) {
				
				float x_vector = c.getMinPenetration().getX();
				float y_vector = c.getMinPenetration().getY();
				
				if (x_vector != 0.0) {					
					if (x_vector > 0.0) {					
						cg.player.setPosition(t.getCoarseGrainedMaxX() + 26, cg.player.getY());						
					} else if (x_vector < 0.0) {						
						cg.player.setPosition(t.getCoarseGrainedMinX() - 26, cg.player.getY());
					}					
				} else if (y_vector != 0.0) {					
					if (y_vector > 0.0) {						
						cg.player.setPosition(cg.player.getX(), t.getCoarseGrainedMaxY() + 26);
					} else if (y_vector < 0.0) {						
						cg.player.setPosition(cg.player.getX(), t.getCoarseGrainedMinY() - 26);					
					}
				}
			}
		}
		
		
		//ENEMY CODE: Checks Wall collisions, barrier collisions, health
		for (Iterator<Enemy> enemyIter = cg.enemy_array.iterator(); enemyIter.hasNext();) {	
			
			Enemy e = enemyIter.next();
			e.computePathToPlayer(cg.player.getX(), cg.player.getY(), cg.gb);
			
			//checks if enemy is not on player tile
			if (e.pathToPlayer != null) {	
				if (e.pathToPlayer.size() > 1) {
					Vector target_pos = e.pathToPlayer.get(1).getPosition();
					Vector enemy_pos = new Vector(e.getX(), e.getY());					
					Vector unit_vector = new Vector(target_pos.getX() - enemy_pos.getX(), 
							target_pos.getY() - enemy_pos.getY());;			
					float magnitude = (float)Math.sqrt(unit_vector.getX()*unit_vector.getX() 
							+ unit_vector.getY()*unit_vector.getY());				
					e.setVelocity(new Vector(unit_vector.getX()/magnitude * e.speed, 
							unit_vector.getY()/magnitude * e.speed ));								
				}
			}
			
			//pushes enemy back into screen if it tries to leave
			if (e.getX() < 25 - 1) {				
				e.setPosition(30, e.getY());				
			} else if (e.getX() > cg.ScreenWidth - 25 - 1) {				
				e.setPosition(cg.ScreenWidth - 30, e.getY());				
			} else if (e.getY() < 25 - 1) {				
				e.setPosition(e.getX(), 30);				
			} else if (e.getY() > cg.BoardHeight - 26) {				
				e.setPosition(e.getX(), cg.BoardHeight - 30);				
			}
			
			//Code if enemy collides with barrier
			for(Enemy enem : cg.enemy_array) {
				Collision ec = e.collides(enem);
				if (ec != null) {					
					float x_vector = ec.getMinPenetration().getX();
					float y_vector = ec.getMinPenetration().getY();					
					//enemy collides into tile from bottom
					e.translate(x_vector*0.2f, y_vector*0.2f);
				}
			}
			
			for(Tile t : cg.gb.getBlockedTileArray()) {
				Collision tc = e.collides(t);
				if (tc != null) {					
					float x_vector = tc.getMinPenetration().getX();
					float y_vector = tc.getMinPenetration().getY();					
					//enemy collides into tile from bottom
					e.translate(x_vector*3f, y_vector*3f);
				}
			}
			
			//Removes enemy if health drops to 0
			if(e.health <= 0) {
				ResourceManager.getSound(ChromeGame.ENEMY_DYING_RSC).play(1,0.2f);
				if (cg.enemy_array.size() != 0) {
					
					enemyIter.remove();
				}
			}
			
			//Player looses health when enemy hits player
			Collision player_collision = e.collides(cg.player);
			if (player_collision != null) {
				//Player takes double damage in blue chrome
				ResourceManager.getSound(ChromeGame.ENEMY_DYING_RSC).play(1,1);
				if (cg.player.chromeColor == 1) {
					cg.player.health -= e.damage *2;
				} else {
					cg.player.health -= e.damage;
				}
	
				try {
					enemyIter.remove();
				} catch (Exception except) {
					System.out.println("caught exception in enemy moving " + except);
				}
				//enemyIter.remove();
				System.out.println(cg.player.health);
			}
			
			e.update(delta);
		}
		
		
		//BULLET CODE
		for (Iterator<Bullet> bulletIter = cg.bullet_array.iterator(); bulletIter.hasNext();) {
			Bullet b = bulletIter.next();
			//bullet collision code
			
			if (cg.player.godMode == true) {
				b.damage = 300;
			}
			//Code if bullet hits barrier
			for (Tile t : cg.gb.getBlockedTileArray()) {
				Collision c = b.collides(t);
				if (c != null ) {
					b.setPosition(cg.player.getX(), cg.player.getY());
					if (cg.bullet_array.size() != 0) {
						bulletIter.remove();
					}
				}
			}
			
			//Code if bullet hits enemy
			for (Enemy e : cg.enemy_array) {
				Collision c = b.collides(e);
				//boolean enemy_hit = false;				
				if (c != null) {					
					//if red chrome add health
					/*if (cg.player.chromeColor == 2) {
						cg.player.AddHealth();
					} */					
					
					//if enemy has already been damaged by bullet "b" and 
					if (b.type != 3) {
						e.health -= b.damage;
						if (cg.player.chromeColor == 2) {
							cg.player.AddHealth();
						}
						if (cg.bullet_array.size() != 0) {
							b.setPosition(cg.player.getX(), cg.player.getY());
							try {
								bulletIter.remove();
							} catch (Exception except) {
								System.out.println("caught exception with bullet hitting enemy error " + except);
							}
						}
					} else if (!e.takenBulletDamage(b)) {
						e.health -= b.damage;
					}
				}
			}
			
			//Player bullets hit Boss
			if (cg.Get_Level() == 3) {
				Collision c = b.collides(cg.boss);
				if (c != null) {										
					//if enemy has already been damaged by bullet "b" and 
					if (b.type != 3) {
						cg.boss.health -= b.damage;
						if (cg.player.chromeColor == 2) {
							cg.player.AddHealth();
						}
						if (cg.bullet_array.size() != 0) {
							b.setPosition(cg.player.getX(), cg.player.getY());
							try {
								bulletIter.remove();
							} catch (Exception except) {
								System.out.println("caught exception with bullet hitting enemy error " + except);
							}
						}
					} else if (!cg.boss.takenBulletDamage(b)) {
						cg.boss.health -= b.damage;
					}
				}
			}
			
			//Code if bullet hits wall
			if (b.getX() < 25 - 1) {				
				if (cg.bullet_array.size() != 0) {
					bulletIter.remove();
				}				
			} else if (b.getX() > cg.ScreenWidth - 25 - 1) {				
				if (cg.bullet_array.size() != 0) {
					bulletIter.remove();
				}					
			} else if (b.getY() < 25 - 1) {				
				if (cg.bullet_array.size() != 0) {
					bulletIter.remove();
				}					
			} else if (b.getY() > cg.BoardHeight - 26) {				
				if (cg.bullet_array.size() != 0) {
					bulletIter.remove();
				}				
			}
			b.update(delta);		
		} 
		
		//BOSS CODE
		if (cg.Get_Level() == 3) {		
			cg.boss.computePathToPlayer(cg.player.getX(), cg.player.getY(), cg.gb);			
			//Move boss towards player
			if (cg.boss.pathToPlayer != null) {	
				if (cg.boss.pathToPlayer.size() > 1) {
					Vector target_pos = cg.boss.pathToPlayer.get(1).getPosition();
					Vector boss_pos = new Vector(cg.boss.getX(), cg.boss.getY());					
					Vector unit_vector = new Vector(target_pos.getX() - boss_pos.getX(), 
							target_pos.getY() - boss_pos.getY());;			
					float magnitude = (float)Math.sqrt(unit_vector.getX()*unit_vector.getX() 
							+ unit_vector.getY()*unit_vector.getY());				
					cg.boss.setVelocity(new Vector(unit_vector.getX()/magnitude * cg.boss.speed, 
							unit_vector.getY()/magnitude * cg.boss.speed ));								
				}
			}
			
			Collision bc = cg.player.collides(cg.boss);
			if (bc != null) {
				cg.player.health -= cg.boss.damage;
				float x_vector = bc.getMinPenetration().getX();
				float y_vector = bc.getMinPenetration().getY();					
				//enemy collides into tile from bottom
				if (x_vector != 0.0) {					
					if (x_vector > 0.0) {					
						cg.player.setPosition(cg.boss.getCoarseGrainedMaxX() + 40, cg.player.getY());						
					} else if (x_vector < 0.0) {						
						cg.player.setPosition(cg.boss.getCoarseGrainedMinX() - 40, cg.player.getY());
					}					
				} else if (y_vector != 0.0) {					
					if (y_vector > 0.0) {						
						cg.player.setPosition(cg.player.getX(), cg.boss.getCoarseGrainedMaxY() + 40);
					} else if (y_vector < 0.0) {						
						cg.player.setPosition(cg.player.getX(), cg.boss.getCoarseGrainedMinY() - 40);					
					}
				}
				
			}
			
			//BOSS AOE ATTACK
			
			bossAoETimer -= delta;
			if (bossAoETimer <= 0) {
				bossAoECD = true;
				bossAoETimer = 5000;
			}
			if (bossAoECD) {
				cg.boss.AoEAttack(delta, cg);
				bossAoECD = false;
			}
			boolean playerInFire = false;
			for (Tile t : cg.gb.fire_tile_array) {
				Collision c = cg.player.collides(t);
				if (c != null) {
					playerInFire = true;
				}
			}			
			if (playerInFire) {
				timePlayerInFire += delta;
			} else {
				timePlayerInFire = 0;
			}
			if (timePlayerInFire > 100) {
				cg.player.health -= 2;
				timePlayerInFire = 0;
			}
			
			//BOSS BULLETS
			for (Iterator<BossBullet> boss_bulletIter = cg.boss_bullet_array.iterator(); boss_bulletIter.hasNext();) {
				BossBullet bb = boss_bulletIter.next();
				//bullet collision code
				
				Collision c = bb.collides(cg.player);
				if (c != null) {
					//boss collides with player
					if (cg.player.chromeColor == 1) {
						cg.player.health -= bb.damage * 2;
					} else {
						cg.player.health -= bb.damage;
					}
					if (cg.boss_bullet_array.size() != 0) {
						bb.setPosition(cg.boss.getX(), cg.boss.getY());
						try {
							boss_bulletIter.remove();
						} catch (Exception except) {
							System.out.println("caught exception with bullet hitting enemy error " + except);
						}
					}
				}
				
				
				//Code if bullet hits wall
				if (bb.getX() < 25 - 1) {				
					if (cg.boss_bullet_array.size() != 0) {
						boss_bulletIter.remove();
					}				
				} else if (bb.getX() > cg.ScreenWidth - 25 - 1) {				
					if (cg.boss_bullet_array.size() != 0) {
						boss_bulletIter.remove();
					}					
				} else if (bb.getY() < 25 - 1) {				
					if (cg.boss_bullet_array.size() != 0) {
						boss_bulletIter.remove();
					}					
				} else if (bb.getY() > cg.BoardHeight - 26) {				
					if (cg.boss_bullet_array.size() != 0) {
						boss_bulletIter.remove();
					}				
				}
				bb.update(delta);
			}
			
			bossShootTimer -= delta;
			if (bossShootTimer <= 0) {
				//bossShootCD = true;
				cg.boss.Shoot(cg);
				//bossShootCD = false;
				bossShootTimer = 5000;
			}
			
			
			if (cg.boss.getX() < 25 - 1) {				
				cg.boss.setPosition(30, cg.boss.getY());				
			} else if (cg.boss.getX() > cg.ScreenWidth - 25 - 1) {				
				cg.boss.setPosition(cg.ScreenWidth - 30, cg.boss.getY());				
			} else if (cg.boss.getY() < 25 - 1) {				
				cg.boss.setPosition(cg.boss.getX(), 30);				
			} else if (cg.boss.getY() > cg.BoardHeight - 26) {				
				cg.boss.setPosition(cg.boss.getX(), cg.BoardHeight - 30);				
			}
			
			if (cg.Get_Level() >= 3 && cg.boss.health <= 0) {				
				cg.enterState(ChromeGame.GAMEWINSTATE);
			}		
			cg.boss.update(delta);
		}
		
		
		//If player dies reset game
		if (cg.player.health <= 0) {
			Reset_Everything(0,cg);
			cg.enterState(ChromeGame.GAMEOVERSTATE);
		}
		
		//if no more enemies, go next level
		if(cg.enemy_array.size() == 0 && cg.Get_Level() <= 2) {		
			Reset_Everything(cg.Get_Level()+1,cg);
			cg.enterState(ChromeGame.LEVELTRANSSTATE);
		}
		
		if (cg.player.godMode) {
			cg.player.health = 2000;			
		} else {
			cg.player.maxHealth = 100;
			if (cg.player.health >= cg.player.maxHealth) {
				cg.player.health = cg.player.maxHealth;
			}
		}
		
		cg.player.update(delta);		
	}

	@Override
	public int getID() {
		return ChromeGame.PLAYINGSTATE;
	}
	
}