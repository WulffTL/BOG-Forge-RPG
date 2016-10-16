package textures;

import renderEngine.Loader;

/**
 * Created by Travis on 1/12/2016.
 *
 */
public class TerrainTexturePack {

    private TerrainTexture backgroundTexture;
    private TerrainTexture rTexture;
    private TerrainTexture gTexture;
    private TerrainTexture bTexture;

    public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture rTexture, TerrainTexture gTexture,
                              TerrainTexture bTexture) {
        super();
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public TerrainTexturePack(Loader loader, String backgroundTexture, String rTexture, String gTexture, String bTexture) {
        super();
        this.backgroundTexture = new TerrainTexture(loader.loadTexture(backgroundTexture));
        this.rTexture = new TerrainTexture(loader.loadTexture(rTexture));
        this.gTexture = new TerrainTexture(loader.loadTexture(gTexture));
        this.bTexture = new TerrainTexture(loader.loadTexture(bTexture));
    }

    public TerrainTexture getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(TerrainTexture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    public TerrainTexture getrTexture() {
        return rTexture;
    }

    public void setrTexture(TerrainTexture rTexture) {
        this.rTexture = rTexture;
    }

    public TerrainTexture getgTexture() {
        return gTexture;
    }

    public void setgTexture(TerrainTexture gTexture) {
        this.gTexture = gTexture;
    }

    public TerrainTexture getbTexture() {
        return bTexture;
    }

    public void setbTexture(TerrainTexture bTexture) {
        this.bTexture = bTexture;
    }





}
