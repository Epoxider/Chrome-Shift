package chromeshift;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
//import org.newdawn.slick.state.StateBasedGame;

/**
 * The Ball class is an Entity that has a velocity (since it's moving). When
 * the Ball bounces off a surface, it temporarily displays a image with
 * cracks for a nice visual effect.
 * 
 */
 class BossBullet extends Entity {

	private Vector velocity;
	int damage;

	public BossBullet() {
		addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.BOSS_BULLET_RSC));
		this.damage = 30;
		
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}
	
	public Vector getVelocity() {
		return velocity;
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