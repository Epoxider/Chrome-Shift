package chromeshift;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
/**
 * The Ball class is an Entity that has a velocity (since it's moving). When
 * the Ball bounces off a surface, it temporarily displays a image with
 * cracks for a nice visual effect.
 * 
 */
 class Bullet extends Entity {

	private Vector velocity;
	float speed;
	int damage;
	int type;
	int lifeTime;

	public Bullet(int direction, int chrome) {
		//addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.BULLET_RSC));
		if (chrome == 3) {
			type = chrome;
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.SUN_BULLET_RSC));
			this.damage = 100;
			if (direction == 0) {
				velocity = new Vector(0f, -0.5f);
			} else if (direction == 1) {
				velocity = new Vector(0.5f, 0f);
			} else if (direction == 2) {
				velocity = new Vector(0f, 0.5f);
			} else if (direction == 3) {
				velocity = new Vector(-0.5f, 0f);
			}
		} else if (chrome == 4)  {
			type = chrome;
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.GREEN_BULLET_RSC));
			lifeTime = 250;
			this.damage = 5;
			if (direction == 0) {
				velocity = new Vector(0f, -1f);
			} else if (direction == 1) {
				velocity = new Vector(1f, 0f);
			} else if (direction == 2) {
				velocity = new Vector(0f, 1f);
			} else if (direction == 3) {
				velocity = new Vector(-1f, 0f);
			}
		} else if (chrome == 1)  {
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.BLUE_BULLET_RSC));
			this.damage = 10;
			if (direction == 0) {
				velocity = new Vector(0f, -1f);
			} else if (direction == 1) {
				velocity = new Vector(1f, 0f);
			} else if (direction == 2) {
				velocity = new Vector(0f, 1f);
			} else if (direction == 3) {
				velocity = new Vector(-1f, 0f);
			}
		} else {
			addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.BULLET_RSC));
			this.damage = 10;
			if (direction == 0) {
				velocity = new Vector(0f, -1f);
			} else if (direction == 1) {
				velocity = new Vector(1f, 0f);
			} else if (direction == 2) {
				velocity = new Vector(0f, 1f);
			} else if (direction == 3) {
				velocity = new Vector(-1f, 0f);
			}
		} 
	}
	

	/**
	 * Update the Bullet based on how much time has passed...
	 * 
	 * @param delta
	 *            the number of milliseconds since the last update
	 */
	public void update(final int delta) {
		translate(velocity.scale(delta));
	}
}