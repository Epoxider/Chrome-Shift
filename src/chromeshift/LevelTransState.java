package chromeshift;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;

/**
 * This state is active prior to the Game starting. In this state, sound is
 * turned off, and the bounce counter shows '?'. The user can only interact with
 * the game by pressing the SPACE key which transitions to the Playing State.
 * Otherwise, all game objects are rendered and updated normally.
 * 
 * Transitions From (Initialization), GameOverState
 * 
 * Transitions To PlayingState
 */
class LevelTransState extends BasicGameState {
	
	int timer = 3000;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
		
		if (!ResourceManager.getSound(ChromeGame.CHROME_SONG_RSC).playing()) {
			ResourceManager.getSound(ChromeGame.CHROME_SONG_RSC).play(1, 0.5f);
		}
		
		timer = 3000;
		
	}


	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		ChromeGame cg = (ChromeGame)game;
				
		if (cg.Get_Level() == 0) {
			g.clear();
			g.drawImage(ResourceManager.getImage(ChromeGame.LEVEL1_RSC), 0, 0);
		} else if (cg.Get_Level() == 1) {
			g.clear();
			g.drawImage(ResourceManager.getImage(ChromeGame.LEVEL2_RSC), 0, 0);
		} else if (cg.Get_Level() == 2) {
			g.clear();
			g.drawImage(ResourceManager.getImage(ChromeGame.LEVEL3_RSC), 0, 0);
		} else if (cg.Get_Level() == 3) {
			g.clear();
			g.drawImage(ResourceManager.getImage(ChromeGame.CHAD_TRANS_RSC), 0, 0);
		} 
		
		//cg.player = new Player(cg.ScreenWidth /2, cg.ScreenHeight /3 * 2);
		//g.drawString("Press space", 550, 200);
		//g.drawImage(ResourceManager.getImage(ChromeGame.START_UP_RSC), 0, 0);
		

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
				
		timer -= delta;
		if (timer <= 0)
			game.enterState(ChromeGame.PLAYINGSTATE, new EmptyTransition(), new HorizontalSplitTransition() );
		
	}

	@Override
	public int getID() {
		return ChromeGame.LEVELTRANSSTATE;
	}
	
}