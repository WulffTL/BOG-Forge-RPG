package renderEngine;

/**
 * Created by Wulff on 10/17/2016.
 */
public interface IGameLogic {

    void init() throws Exception;

    void input(Window window);

    void update(float interval);

    void render(Window window);

}