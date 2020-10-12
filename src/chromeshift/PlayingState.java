package chromeshift;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jig.Vector;
import jig.Collision;

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
	
	//GameBoard gb;
	boolean displayPath = false;
	
	int cooldownTimer = 120;
	boolean cooldownReady = true;
	
	int totalEnemies = 20;
	int spawnTimer = 1000;
	boolean noEnemies = false;
	
	int bulletDirection;
	
	int GreencooldownTimer = 240;
	boolean GreencooldownReady = true;

	int bossShootTimer = 1000;
	boolean bossShootCD = true;
	
	int bossAoETimer = 5000;
	int timePlayerInFire = 0;
	boolean bossAoECD = false;
	
	List<Integer> spawnTileList = Arrays.asList(0,11,96,107);
	List<Integer> spawnTypeList2 = Arrays.asList(0,1);
	List<Integer> spawnTypeList3 = Arrays.asList(0,1,2);
	
	
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
		cg.player.setPosition(cg.ScreenWidth/2, cg.ScreenHeight/3*2);
		cg.player.health = 100;
		cg.player.SetChrome(1);
		cg.Set_Current_Level(level);
		cg.Create_Enemies(level);
		totalEnemies = 20;
		//System.out.println("Setting level to " + cg.current_level);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);

	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		ChromeGame cg = (ChromeGame)game;
		
		/*for (Tile t : cg.gb.fire_tile_array) {
			t.render(g);
		} */
		
		//cg.player.render(g);
		
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
			g.drawString("Boss Health: " + cg.boss.health, 400, container.getHeight() - 50);
		}
		
		g.drawString("Health: " + cg.player.health, 100, container.getHeight() - 50);
		g.drawString("Enemies Left: " + cg.enemy_array.size(), 950, container.getHeight() - 50);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		ChromeGame cg = (ChromeGame)game;
		Vector new_velocity;
		float speed = cg.player.speed;
		
				
		cooldownTimer -= delta;
		if (cooldownTimer <= 0) {
			cooldownReady = true;
			cooldownTimer = 120;
		}
		
		GreencooldownTimer -= delta;
		if (GreencooldownTimer <= 0) {
			GreencooldownReady = true;
			GreencooldownTimer = 240;
		}
		
		//Spawns enemies every second at random corners
		spawnTimer -= delta;
		if (spawnTimer <= 0) {
			if (cg.Get_Level() == 0) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(0, cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					totalEnemies--;
				}
				spawnTimer = 1000;
			} else if (cg.Get_Level() == 1) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(spawnTypeList2.get(rand.nextInt(spawnTypeList2.size())), cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					totalEnemies--;
				}
			} else if (cg.Get_Level() == 2) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(spawnTypeList3.get(rand.nextInt(spawnTypeList3.size())), cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					totalEnemies--;
				}
			} /*else if (cg.Get_Level() == 3) {
				if (totalEnemies > 0) {
					Random rand = new Random();
					cg.Set_Enemy(spawnTypeList3.get(rand.nextInt(spawnTypeList3.size())), cg.gb.getTileArray(), 
							spawnTileList.get(rand.nextInt(spawnTileList.size())));
					totalEnemies--;
				}
			} */
			
			spawnTimer = 1000;
		}
			
		//Code to vizualize pathing
		if (input.isKeyPressed(Input.KEY_P)) {
			displayPath = !displayPath;
		}
		
		
		//Cheat to switch level
		if( (input.isKeyDown(Input.KEY_8))) {
			Reset_Everything(0,cg);
			cg.enterState(ChromeGame.STARTUPSTATE);
		} else if( (input.isKeyDown(Input.KEY_9))) {
			Reset_Everything(1,cg);
			cg.enterState(ChromeGame.STARTUPSTATE);
		} else if( (input.isKeyDown(Input.KEY_0))) {
			Reset_Everything(2,cg);
			System.out.println("level is " + cg.Get_Level());
			cg.enterState(ChromeGame.STARTUPSTATE);
		} else if( (input.isKeyDown(Input.KEY_7))) {
			cg.Remove_Barriers();
			cg.Remove_Enemies();
			cg.Remove_Bullets();
			cg.player.setPosition(cg.ScreenWidth/2, cg.ScreenHeight/3*2);
			cg.player.health = 100;
			cg.player.SetChrome(1);
			cg.Set_Current_Level(3);
			cg.Create_Enemies(3);
			totalEnemies = 20;
			System.out.println("level is " + cg.Get_Level());
			cg.enterState(ChromeGame.STARTUPSTATE);
		}
		
		
		//Code to move player
		if( (input.isKeyDown(Input.KEY_W)) && (cg.player.getY() > 25) ) {			
			new_velocity = new Vector(0f, -0.5f*speed);	
		} else if ((input.isKeyDown(Input.KEY_A)) && 
			       (cg.player.getX() > 25) ) {		
			new_velocity = new Vector(-0.5f*speed, 0f);
		} else if ((input.isKeyDown(Input.KEY_S)) && 
				   (cg.player.getY() < cg.ScreenHeight - 25 - 1)) {			
			new_velocity = new Vector(0f, 0.5f*speed);
		} else if ((input.isKeyDown(Input.KEY_D)) &&
				   (cg.player.getX() < cg.ScreenWidth - 25 - 1)){			
			new_velocity = new Vector(0.5f*speed, 0f);
		} else {
			new_velocity = new Vector(0f,0f);
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
		if (cg.player.chromeColor == 4) {
			if(input.isKeyPressed(Input.KEY_I) && GreencooldownReady) {	
				cg.player.Shoot(0, cg.player.chromeColor, cg);
				GreencooldownReady = false;
			} else if (input.isKeyPressed(Input.KEY_J) && GreencooldownReady) {		
				cg.player.Shoot(3, cg.player.chromeColor, cg);
				GreencooldownReady = false;
			} else if (input.isKeyPressed(Input.KEY_K) && GreencooldownReady) {			
				cg.player.Shoot(2, cg.player.chromeColor, cg);	
				GreencooldownReady = false;
			} else if (input.isKeyPressed(Input.KEY_L) && GreencooldownReady){			
				cg.player.Shoot(1, cg.player.chromeColor, cg);
				GreencooldownReady = false;
			}
		} else {
			if(input.isKeyPressed(Input.KEY_I) && cooldownReady) {	
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
			}
		}
		
		//Constrains player to only moving inside screen by setting position
		if (cg.player.getX() < 25) {			
			cg.player.setPosition(25, cg.player.getY());			
		} else if (cg.player.getX() > cg.ScreenWidth - 25 - 1) {			
			cg.player.setPosition(cg.ScreenWidth - 25 - 1, cg.player.getY());			
		} else if (cg.player.getY() < 25) {			
			cg.player.setPosition(cg.player.getX(), 25);			
		} else if (cg.player.getY() > cg.ScreenHeight - 25 - 1) {			
			cg.player.setPosition(cg.player.getX(), cg.ScreenHeight - 25 - 1);			
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
			} else if (e.getY() > cg.ScreenHeight - 26) {				
				e.setPosition(e.getX(), cg.ScreenHeight - 30);				
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
					e.translate(x_vector, y_vector);
				}
			}
			
			//Removes enemy if health drops to 0
			if(e.health <= 0) {
				if (cg.enemy_array.size() != 0) {
					enemyIter.remove();
				}
			}
			
			//Player looses health when enemy hits player
			Collision player_collision = e.collides(cg.player);
			if (player_collision != null) {
				//Player takes double damage in blue chrome
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
			
			/*if (b.type == 4) {					
				//removes green bullet after delta amount of time
				b.lifeTime -= delta;
				if (b.lifeTime <= 0) {
					b.setPosition(cg.player.getX(), cg.player.getY());
					try {
						bulletIter.remove();
					} catch (Exception except) {
						System.out.println("caught exception with bullet hitting enemy error " + except);
					}
				}
			} */
			
			//Code if bullet hits enemy
			for (Enemy e : cg.enemy_array) {
				Collision c = b.collides(e);
				//boolean enemy_hit = false;				
				if (c != null) {					
					//if red chrome add health
					if (cg.player.chromeColor == 2) {
						cg.player.AddHealth();
					}					
					//green bullets only last a second					
					//if enemy has already been damaged by bullet "b" and 
					if (b.type != 4) {
						e.health -= b.damage;
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
			
			if (cg.Get_Level() == 3) {
				Collision c = b.collides(cg.boss);
				if (c != null) {					
					if (cg.player.chromeColor == 2) {
						cg.player.AddHealth();
					}					
					//green bullets only last a second					
					//if enemy has already been damaged by bullet "b" and 
					if (b.type != 4) {
						cg.boss.health -= b.damage;
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
			} else if (b.getY() > cg.ScreenHeight - 26) {				
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
			
			/*
			 * int bossAoETimer = 5000;
				int timePlayerInFire = 0;
				boolean bossAoECD = false;
			 */
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
						cg.player.health -= cg.boss.damage * 2;
					} else {
						cg.player.health -= cg.boss.damage;
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
				} else if (bb.getY() > cg.ScreenHeight - 26) {				
					if (cg.boss_bullet_array.size() != 0) {
						boss_bulletIter.remove();
					}				
				}
				bb.update(delta);
			}
			
			bossShootTimer -= delta;
			if (bossShootTimer <= 0) {
				bossShootCD = true;
				System.out.println("boss bullet array size is " + cg.boss_bullet_array.size());
				cg.boss.Shoot(cg);
				//System.out.println("boss after shot");
				bossShootCD = false;
				bossShootTimer = 5000;
			}
			
			
			if (cg.boss.getX() < 25 - 1) {				
				cg.boss.setPosition(30, cg.boss.getY());				
			} else if (cg.boss.getX() > cg.ScreenWidth - 25 - 1) {				
				cg.boss.setPosition(cg.ScreenWidth - 30, cg.boss.getY());				
			} else if (cg.boss.getY() < 25 - 1) {				
				cg.boss.setPosition(cg.boss.getX(), 30);				
			} else if (cg.boss.getY() > cg.ScreenHeight - 26) {				
				cg.boss.setPosition(cg.boss.getX(), cg.ScreenHeight - 30);				
			}
			
			if (cg.Get_Level() >= 3 && cg.boss.health <= 0) {				
				cg.enterState(ChromeGame.GAMEOVERSTATE);
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
			cg.enterState(ChromeGame.STARTUPSTATE);
		}
		
		if (cg.player.health >= 100) {
			cg.player.health = 100;
		}
		cg.player.update(delta);		
	}

	@Override
	public int getID() {
		return ChromeGame.PLAYINGSTATE;
	}
	
}