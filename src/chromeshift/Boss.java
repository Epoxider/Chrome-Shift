package chromeshift;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.newdawn.slick.state.StateBasedGame;


public class Boss extends Entity {
	
	public float speed;
	public int health;
	public int maxHealth = 4000;
	boolean shootReady;
	boolean bossAoEdir = true;
	int shootCD;
	int damage;
	private Vector velocity;
	ArrayList<Tile> pathToPlayer;
	ArrayList<Bullet> bullets_collided = new ArrayList<Bullet>();
	List<Integer> spawnTileList = Arrays.asList(0,11,96,107);
	
	public Boss(final float x, final float y) {
		super(x, y);
		this.shootCD = 1000;
		this.shootReady = true;
		this.health = this.maxHealth;
		this.damage = 80;
		this.speed = 0.05f;		
		addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.BOSS_RSC));
	}
	
	public void Shoot(StateBasedGame game) {
		ChromeGame cg = (ChromeGame)game;
		
		BossBullet bullet = new BossBullet();
		bullet.setPosition(cg.boss.getX(), cg.boss.getY());
		
		Vector target_pos = cg.player.getPosition();
		Vector enemy_pos = new Vector(this.getX(), this.getY());					
		Vector unit_vector = new Vector(target_pos.getX() - enemy_pos.getX(), 
				target_pos.getY() - enemy_pos.getY());
		//scale the velocity of boss's bullets
		bullet.setVelocity(new Vector(unit_vector.getX(), unit_vector.getY()).scale(0.0008f));
		System.out.println("boss bullet speed is " + bullet.getVelocity());
		cg.boss_bullet_array.add(bullet);
	}
	
	public void AoEAttack(int delta, StateBasedGame game) {
		ChromeGame cg = (ChromeGame)game;
		
		cg.gb.clearFireArray();
		
		if (this.bossAoEdir) {
			int col = (int)((float)cg.player.getX()/(float)cg.ScreenWidth * (float)cg.tilesWide);
			for (int row = 0; row < cg.tilesHigh; row++) {
				cg.gb.createFireArray(row * cg.tilesWide + col);
			}
		} else {
			int row = (int)((float)cg.player.getY()/(float)cg.ScreenHeight * (float)cg.tilesHigh);
			for (int col = 0; col < cg.tilesWide; col++) {
				cg.gb.createFireArray(row * cg.tilesWide + col);
			}
		}
		this.bossAoEdir = !this.bossAoEdir;
	}
	
	public void computePathToPlayer(float pX, float pY, GameBoard gb) {
		pathToPlayer = gb.getBestPath(this.getX(), this.getY(), pX, pY);
	}
	
	public boolean takenBulletDamage(Bullet b) {
		boolean damaged = false;
		if (!bullets_collided.contains(b)) {
			bullets_collided.add(b);
			return false;
		} else {
			damaged = true;
			return damaged;
		}
	}

	public void setVelocity(final Vector v) {
		velocity = v;
	} 
	
	public Vector getVelocity() {
		return velocity;
	}
	
	public void update(final int delta) {
		try {
			translate(velocity.scale(delta));
		} catch (Exception e) {
			System.out.println("Caught boss exception " + e + " " + delta + " " + this.velocity);
		} 
		//translate(velocity.scale(delta));
	}
}
