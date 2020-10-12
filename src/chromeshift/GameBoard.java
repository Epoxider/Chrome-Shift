package chromeshift;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

import jig.ResourceManager;

public class GameBoard {
	
	private ArrayList<Tile> tile_array;
	private ArrayList<Tile> blocked_tile_array = new ArrayList<Tile>();
	public ArrayList<Tile> fire_tile_array = new ArrayList<Tile>();
	private float tileWidth;
	private float tileHeight;
	//private float anchorX, anchorY;
	private float width, height;
	private int columns, rows;
	private PriorityQueue<Tile> Q;

	
	public GameBoard(int inAnchorX, int inAnchorY, int inWidth, int inHeight, int inColumns, int inRows, ArrayList<Enemy> inEnemyList) {
		tile_array = new ArrayList<Tile>(columns*rows); //To hold 16 by 12 bricks tile map
		//int numTiles = tile_array.size();
		
		Comparator<Tile> distanceComparator = new Comparator<Tile>() {
            @Override
            public int compare(Tile t1, Tile t2) {
            	float delta = t1.getX() - t2.getX();
            	
            	// We need to reverse the value, since the highest priority
            	// will be the lowest value.
            	return (delta < (float)0.0) ? -1 : (delta > (float)0.0) ? 1 : 0;
            }
        };
        
		Q = new PriorityQueue<Tile>(distanceComparator);

		width = inWidth;
		height = inHeight;
		columns = inColumns;
		rows = inRows;
		tileWidth = (float)width / (float)columns;
		tileHeight = (float)height / (float)rows;
		
		// tile class takes (minX, minY, maxX, maxY, tile id, boolean impassable)
		for (int y = 0; y < rows; y++) {	
			for (int x = 0; x < columns; x ++) {
				Tile tile = new Tile((float)x*tileWidth, (float)y*tileHeight, tileWidth, tileHeight, y*columns+x, false);
				//System.out.println(x * tileWidth + " , " +  y * tileHeight);
				tile_array.add(tile);
			}
		}
		System.out.println("There are this many tiles in array" + tile_array.size());

	}
	
	
	public Tile getTile(float inX, float inY) {
		
		if( (inX < 25) || (inX >= width - 25)) {
			return null;
		}
		if( (inY < 25) || (inY >= height - 25)) {
			return null;
		}
		float col = (inX / width)*columns;
		float row = (inY / height)*rows;
		int tile_index = (int)row*columns + (int)col;
		
        if( tile_index >= tile_array.size() ) {
        	return null;
        }
		return tile_array.get(tile_index);
	}
	
	
	public ArrayList<Tile> getTileArray() {
		return tile_array;
	}
	
	public ArrayList<Tile> getBlockedTileArray() {
		return blocked_tile_array;
	}
	
	
	//Takes in multiple tileID's and sets tiles to block and adds images
	
	//private ArrayList<Tile> blocked_tile_array;
	public void createBarrier(int ...tile_id) {
		for(int i : tile_id) {
			Tile blocked_tile = tile_array.get(i);
			blocked_tile.blocked_path = true;
			blocked_tile_array.add(blocked_tile);
			blocked_tile.addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.WALL1_RSC));
		}
	}
	
	public void createFireArray(int tile_id) {
		Tile f_tile = tile_array.get(tile_id);
		f_tile.fire_tile = true;
		fire_tile_array.add(f_tile);
		f_tile.addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.FIRE_TILE_RSC));
		/*for(int i : tile_id) {
			Tile fire_tile = tile_array.get(i);
			fire_tile_array.add(fire_tile);
			//fire_tile.addImageWithBoundingBox(ResourceManager.getImage(ChromeGame.FIRE_TILE_RSC));
		} */
	}
	
	public void clearFireArray() {
		for (Tile t : fire_tile_array) {
			t.removeImage(ResourceManager.getImage(ChromeGame.FIRE_TILE_RSC));
			t.fire_tile = false;
		}
		fire_tile_array.clear();
	}
	
	
	public float computeTileDistance(Tile t1, Tile t2) {
		float xdist = t1.getX() - t2.getX();
		float ydist = t1.getY() - t2.getY();
		return (float)Math.sqrt(xdist*xdist + ydist*ydist);
	}
	
	
	private void dykstraUpdate(Tile u, Tile v) {
		float alt = u.distance + computeTileDistance(u, v);
		if(alt < v.distance) {
			Q.remove(v);
			v.distance = alt;
			Q.add(v);
			v.prev_tile = u;
		}
	}
	
	
	public ArrayList<Tile> getBestPath(float enemyX, float enemyY, float pX, float pY) {
		// Reset things
		Q.clear();
		
		int numTiles = tile_array.size();
		
		for(int i = 0; i < numTiles; i++) {
			Tile tile = tile_array.get(i);
			tile.distance = (float)1000000.0;
			tile.prev_tile = null;
			Q.add(tile);
		}
		
		// This is the starting tile
		Tile enemy_tile = getTile(enemyX, enemyY);
		
		if (enemy_tile == null) {
			return null;
		}
		
		Q.remove(enemy_tile);
		enemy_tile.distance = (float)0.0;
		Q.add(enemy_tile);
		
		while( Q.size() > 0 ) {
			// Remove the entry with highest priority (lowest dist value)
			Tile u = Q.poll();
			
			// Check neighbors of u
			boolean okLeft = (u.tile_ID % columns) > 0;
			boolean okUp = (u.tile_ID / columns) > 0;
			boolean okRight = ((u.tile_ID+1) % columns) > 0;
			boolean okDown = ((u.tile_ID) / columns) < (rows-1);
			 			
			if( okLeft || u.tile_ID - 1 > 0  ) {
				Tile tile = tile_array.get(u.tile_ID-1);
				if( !tile.blocked_path ) {
					dykstraUpdate(u, tile);
				}
			}
			
			if( okLeft && okUp ) {
				Tile tile = tile_array.get(u.tile_ID-1-columns);
				if( !tile.blocked_path ) {
					dykstraUpdate(u, tile);
				}
			} 
			
			if( okUp ) {
				Tile tile = tile_array.get(u.tile_ID-columns);
				if( !tile.blocked_path ) {
					dykstraUpdate(u, tile);
				}
			}

			if( okRight && okUp ) {
				Tile tile = tile_array.get(u.tile_ID+1-columns);
				if( !tile.blocked_path ) {
					dykstraUpdate(u, tile);
				}
			} 

			if( okRight ) {
				Tile tile = tile_array.get(u.tile_ID+1);
				if( !tile.blocked_path ) {
					dykstraUpdate(u, tile);
				}
			}

			if( okRight && okDown ) {
				Tile tile = tile_array.get(u.tile_ID+1+columns);
				if( !tile.blocked_path ) {
					dykstraUpdate(u, tile);
				}
			}
			
			if( okDown ) {
				Tile tile = tile_array.get(u.tile_ID+columns);
				if( !tile.blocked_path ) {
					dykstraUpdate(u, tile);
				}
			}

			if( okLeft && okDown ) {
				Tile tile = tile_array.get(u.tile_ID-1+columns);
				if( !tile.blocked_path ) {
					dykstraUpdate(u, tile);
				}
			} 
		}
		
		Tile tile = getTile(pX, pY);
		
		if (tile == null) { 
			return null;
		}
		
		ArrayList<Tile> pathToPlayer = new ArrayList<Tile>();
		pathToPlayer.add(0, tile);
		while( tile.prev_tile != null ) {
		    tile = tile.prev_tile;
		    pathToPlayer.add(0, tile);
		}
		
		return pathToPlayer;
		
	}

}
