package chromeshift;

import java.util.ArrayList;

import jig.Entity;

import jig.ResourceManager;
import jig.Vector;

public class Enemy extends Entity {
	
	ArrayList<Tile> pathToPlayer;
	ArrayList<Bullet> bullets_collided = new ArrayList<Bullet>();
	int health;
	float speed;
	int damage;
	private Vector velocity;
	
	public Enemy(final float x, final float y, int enemy_type) {
		super(x, y);		
		pathToPlayer = null;
		
		if (enemy_type == 0) {
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.ENEMY1_RSC));
			speed = 0.2f;
			this.damage = 10;
			this.health = 30;
			
		} else if (enemy_type == 1) {
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.ENEMY2_RSC));
			speed = 0.1f;
			this.damage = 20;
			this.health = 60;
			
		} else if (enemy_type == 2) {
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.ENEMY3_RSC));
			speed = 0.2f;
			this.damage = 40;
			this.health = 100;
			
		} 
	}
	
	public void computePathToPlayer(float pX, float pY, GameBoard gb) {
		pathToPlayer = gb.getBestPath(this.getX(), this.getY(), pX, pY);
	}

	//could be used for changing enemy speed later
	public void setVelocity(final Vector v) {
		velocity = v;
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
	
	//could be useful for changing enemy speed later
	public Vector getVelocity() {
		return velocity;
	}
	
	public void update(final int delta) {
		if (velocity != null) {
			translate(velocity.scale(delta));
		}
		/*try {
			translate(velocity.scale(delta));
		} catch (Exception e) {
			System.out.println("caught exception with enemy updating " + e);
		}*/
	}
}