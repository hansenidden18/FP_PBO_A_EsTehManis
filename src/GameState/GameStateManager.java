package GameState;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class GameStateManager {
	
	private ArrayList<GameState> gameState;
	private int currentState;
	
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	
	public GameStateManager() {
		
		gameState = new ArrayList<GameState>();
		
		currentState = MENUSTATE;
		gameState.add(new MenuState(this));
		gameState.add(new Level1State(this));
		
	}
	
	public void setState(int state) {
		currentState = state;
		gameState.get(currentState).init();
	}
	
	public void update() {
		gameState.get(currentState).update();
	}
	
	public void draw(Graphics2D g) {
		gameState.get(currentState).draw(g);
	}
	
	public void keyPressed(int k) {
		gameState.get(currentState).keyPressed(k);
	}
	
	public void keyReleased(int k) {
		gameState.get(currentState).keyReleased(k);
	}
}
