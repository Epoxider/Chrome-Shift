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
class TutorialState extends BasicGameState {
	
	boolean gameplayTut = true;
	boolean controlTut = false;
	boolean shiftTut = false;
	boolean enemyTut = false;
	boolean readyToPlay = false;
	
	int timer = 8000;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
		
		gameplayTut = true;
		controlTut = false;
		shiftTut = false;
		enemyTut = false;
		readyToPlay = false;
				
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
		} else if (readyToPlay) {
			g.drawString("PLAY", 550, 450);
		}
		//System.out.println("shift tut is IN RENDER " + shiftTut);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		Input input = container.getInput();
		ChromeGame cg = (ChromeGame)game;
		
		//timer --;
		/* if (timer >= 4000 && timer <= 6000) {
			gameplayTut = false;
			controlTut = true;
		} else if (timer >= 2000 && timer <= 4000) {
			controlTut = false;
			System.out.println("shift tut is IN UPDATE " + shiftTut);
			shiftTut = true;
		} else if (timer > 0 && timer <= 2000) {
			shiftTut = false;
			enemyTut = true;
			timer = 8000;
		} else if (timer < 0) {
			timer = 8000;
		} */
		
		
		//System.out.println(controlTut);
		
		
		/*if (input.isKeyPressed(Input.KEY_SPACE) && controlTut) {
			controlTut = false;
			shiftTut = true;
			System.out.println("got space input");
		} */
		
		//System.out.println("timer is " + timer);
			
			
		if (input.isKeyPressed(Input.KEY_1) && gameplayTut) {
			gameplayTut = false;
			controlTut = true;
			//shiftTut = false;
			//enemyTut = false;
			//readyToPlay = false;
			
			
			System.out.println("going to control tut");
		} else if (input.isKeyPressed(Input.KEY_2) && controlTut) {
			
			//gameplayTut = false;
			controlTut = false;
			shiftTut = true;
			//enemyTut = false;
			//readyToPlay = false;
			System.out.println("RECEIEVED INPUT ON CONTROLTUT IF STATERMENT " + shiftTut);
			
			
		} else if (input.isKeyPressed(Input.KEY_3) && shiftTut) {
			
			//gameplayTut = false;
			//controlTut = false;
			shiftTut = false;
			enemyTut = true;
			//readyToPlay = false;
						
			System.out.println("going to enemy tut");
		} else if (input.isKeyPressed(Input.KEY_4) && enemyTut) {
			
			//gameplayTut = false;
			//controlTut = false;
			//shiftTut = false;
			cg.enterState(ChromeGame.STARTUPSTATE);
			System.out.println("id is " + cg.getCurrentStateID());
						
			System.out.println("going to ready to play");
		}
		
		/*System.out.println("gameplay tut is " + gameplayTut);
		System.out.println("control tut is " + controlTut);
		System.out.println("shift tut is " + shiftTut);
		System.out.println("enemy tut is " + enemyTut);
		System.out.println("play is " + readyToPlay);
		System.out.println(" "); */
	
	}

	@Override
	public int getID() {
		return ChromeGame.TUTORIALSTATE;
	}
	
}