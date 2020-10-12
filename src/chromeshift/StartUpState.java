package chromeshift;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

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
class StartUpState extends BasicGameState {

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
		
		container.setSoundOn(true);
		
	}


	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		ChromeGame cg = (ChromeGame)game;
		
		cg.player.render(g);
		//cg.player = new Player(cg.ScreenWidth /2, cg.ScreenHeight /3 * 2);
		g.drawString("Press space", 550, 200);
		g.drawImage(ResourceManager.getImage(ChromeGame.START_UP_RSC), 0, 0);
		

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		Input input = container.getInput();
		ChromeGame cg = (ChromeGame)game;

		if (input.isKeyDown(Input.KEY_SPACE)) {
			cg.enterState(ChromeGame.PLAYINGSTATE);
		} else if (input.isKeyDown(Input.KEY_TAB)) {
			cg.enterState(ChromeGame.TUTORIALSTATE);
		}
		
		
	}

	@Override
	public int getID() {
		return ChromeGame.STARTUPSTATE;
	}
	
}