package chromeshift;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import org.newdawn.slick.state.StateBasedGame;


public class Player extends Entity {
	
	public float speed;
	public int health;
	public int chromeColor;
	private Vector velocity;
	
	public Player(final float x, final float y) {
		super(x, y);
		health = 100;
		speed = 1f;
		this.SetChrome(1);
	}
	
	//1=blue,2=red,3=yellow,4=green
	public void SetChrome(int i) {
		if (i == 1) {
			this.chromeColor = i;
			this.speed = 1.5f;
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.BLUE_CHROME_RSC));
		} else if (i == 2) {
			this.speed = 0.5f;
			this.chromeColor = i;
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.RED_CHROME_RSC));		
		} else if (i == 3) {
			this.chromeColor = i;
			this.speed = 1f;
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.YELLOW_CHROME_RSC));
		} else if (i == 4) {
			this.chromeColor = i;
			this.speed = 1;
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.GREEN_CHROME_RSC));
		}
	}
	
	public void Shoot(int direction, int chrome, StateBasedGame game) {
		ChromeGame cg = (ChromeGame)game;
		if (this.chromeColor == 3) {
			this.health -= 20;
		}
		
		if(direction == 0) {			
			Bullet bullet = new Bullet(0, this.chromeColor);
			bullet.setPosition(cg.player.getX(), cg.player.getY());
			cg.bullet_array.add(bullet);
		} else if (direction == 3) {		
			Bullet bullet = new Bullet(3, this.chromeColor);
			bullet.setPosition(cg.player.getX(), cg.player.getY());
			cg.bullet_array.add(bullet);
		} else if (direction == 2) {			
			Bullet bullet = new Bullet(2, this.chromeColor);
			bullet.setPosition(cg.player.getX(), cg.player.getY());
			cg.bullet_array.add(bullet);	
		} else if (direction == 1){			
			Bullet bullet = new Bullet(1, this.chromeColor);
			bullet.setPosition(cg.player.getX(), cg.player.getY());
			cg.bullet_array.add(bullet);
		}
	}
	
	//for red chrome
	public void AddHealth() {
		this.health++;
	}
	
	//for yellow chrome
	public void SpendHealth() {
		this.health -= 20;
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
			System.out.println("csaught exception " + e);
		} 
	}
}
