package chromeshift;
import jig.Entity;
import jig.ResourceManager;
public class Tile extends Entity {
	
	Tile prev_tile;
	float distance;
	int tile_ID;
	int tile_damage;
	
	boolean fire_tile;
	boolean blocked_path;
	
	
	public Tile(final float minX, final float minY, final float width, final float height, int Tile_ID, boolean impassable) {
		
		this.setPosition(minX + width/2, minY + height/2);
		this.setCoarseGrainedMinX(-width/2);
		this.setCoarseGrainedMinY(-height/2);
		this.setCoarseGrainedMaxX(width/2);
		this.setCoarseGrainedMaxY(height/2);
		this.setScale(1f);
		
		this.antiAliasing = false;

		
		this.tile_ID = Tile_ID;
		
		if (this.fire_tile) {
			this.addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.FIRE_TILE_RSC));
		}
		
		if (blocked_path) {
			this.blocked_path = true;
			this.addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.WALL1_RSC));
		} else {
			this.blocked_path = false;
			//this.removeImage(ResourceManager.getImage(ChromeGame.WALL1_RSC));
		}
		//addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.TILE_TEST_RSC));
		
	}
}
