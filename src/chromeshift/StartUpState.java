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
	
	boolean gameplayTut = false;
	boolean controlTut = false;
	boolean shiftTut = false;
	boolean enemyTut = false;
	boolean readyToPlay = false;
	boolean showTitle = true;

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
		
		gameplayTut = false;
		controlTut = false;
		shiftTut = false;
		enemyTut = false;
		showTitle = true;
		
	}


	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
				
		if (gameplayTut) {
			g.clear();
			g.drawImage(ResourceManager.getImage(ChromeGame.GAMEPLAY_TUT_RSC), 0, 0);
		} else if (controlTut) {
			g.clear();
			g.drawImage(ResourceManager.getImage(ChromeGame.CONTROL_TUT_RSC), 0, 0);
		} else if (shiftTut) {
			g.clear();
			g.drawImage(ResourceManager.getImage(ChromeGame.SHIFT_TUT_RSC), 0, 0);
		} else if (enemyTut) {
			g.clear();
			g.drawImage(ResourceManager.getImage(ChromeGame.ENEMY_TUT_RSC), 0, 0);
		} else if (showTitle) {
			//g.drawString("Press space", 550, 200);
			g.drawImage(ResourceManager.getImage(ChromeGame.START_UP_RSC), 0, 0);
		}
		
		//cg.player = new Player(cg.ScreenWidth /2, cg.ScreenHeight /3 * 2);
		//g.drawString("Press space", 550, 200);
		//g.drawImage(ResourceManager.getImage(ChromeGame.START_UP_RSC), 0, 0);
		

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		Input input = container.getInput();
		ChromeGame cg = (ChromeGame)game;

		if (input.isKeyDown(Input.KEY_SPACE)) {
			cg.enterState(ChromeGame.PLAYINGSTATE);
		} else if (input.isKeyDown(Input.KEY_TAB)) {
			gameplayTut = false;
			controlTut = false;
			shiftTut = false;
			enemyTut = false;
			showTitle = true;
		} else if (input.isKeyDown(Input.KEY_1)) {
			gameplayTut = true;
			controlTut = false;
			shiftTut = false;
			enemyTut = false;
			showTitle = false;
		} else if (input.isKeyDown(Input.KEY_2)) {
			gameplayTut = false;
			controlTut = true;
			shiftTut = false;
			enemyTut = false;
			showTitle = false;
		} else if (input.isKeyDown(Input.KEY_3)) {
			gameplayTut = false;
			controlTut = false;
			shiftTut = true;
			enemyTut = false;
			showTitle = false;
		} else if (input.isKeyDown(Input.KEY_4)) {
			gameplayTut = false;
			controlTut = false;
			shiftTut = false;
			enemyTut = true;
			showTitle = false;
		}
		
	}

	@Override
	public int getID() {
		return ChromeGame.STARTUPSTATE;
	}
	
}