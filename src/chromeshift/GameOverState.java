package chromeshift;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;


/**
 * This state is active when the Game is over. In this state, the ball is
 * neither drawn nor updated; and a gameover banner is displayed. A timer
 * automatically transitions back to the StartUp State.
 * 
 * Transitions From PlayingState
 * 
 * Transitions To StartUpState
 */
class GameOverState extends BasicGameState {
	

	private int timer;
	//private int kill_score; // the user's score, to be displayed, but not updated.
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		timer = 3000;
		container.setSoundOn(true);
		
		ChromeGame cg = (ChromeGame)game;
		cg.Remove_Barriers();
		cg.Remove_Enemies();
		cg.Remove_Bullets();
		cg.gb.clearFireArray();
		cg.player.setPosition(cg.ScreenWidth/2, cg.ScreenHeight/3 * 2);
		cg.player.health = 100;	
		cg.player.SetChrome(1);
		cg.Set_Current_Level(0);
		cg.Create_Enemies(0); 
		
	}

	public void setUserScore(int kills) {
		//kill_score = kills;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {


	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		timer -= delta;
		if (timer <= 0)
			game.enterState(ChromeGame.STARTUPSTATE, new EmptyTransition(), new HorizontalSplitTransition() );

		

	}

	@Override
	public int getID() {
		return ChromeGame.GAMEOVERSTATE;
	}
	
}