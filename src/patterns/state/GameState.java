package patterns.state;


public interface GameState {


    void startGame();


    void pause();


    void resume();


    void update();


    void winGame();


    void loseGame(String reason);


    void placeEntity(int x, int y);
}
