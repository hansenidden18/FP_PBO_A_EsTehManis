package Entity;

import TileMap.TileMap;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Message extends MapObject {

  private String description;
  private String context;
  private BufferedImage[] sprites;
  private BufferedImage[] openSprites;
  private boolean open;

  public Message(TileMap tm, String description, String context) {
    super(tm);
    this.description = description;
    this.context = context;

    width = 30;
    height = 60;
    cwidth = 20;
    cheight = 20;
    // load sprites
    try {
      BufferedImage spritesheet = ImageIO.read(
        getClass().getResourceAsStream("/Sprites/boxes/box.png")
      );
      BufferedImage openspritesheet = ImageIO.read(
        getClass().getResourceAsStream("/Sprites/boxes/open-box.png")
      );
      sprites = new BufferedImage[5];
      openSprites = new BufferedImage[5];
      for (int i = 0; i < sprites.length; i++) {
        sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
      }
      for (int i = 0; i < openSprites.length; i++) {
        openSprites[i] =
          openspritesheet.getSubimage(i * width, 0, width, height);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    animation = new Animation();
    animation.setFrames(sprites);
    animation.setDelay(100);
  }

  public boolean isOpen() {
    return this.open;
  }

  public String getDesc() {
    return this.description;
  }

  public String getContext() {
    return this.context;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }

  public void checkTouch(Player person) {
    if (intersects(person)) {
      setOpen(true);
    } else {
      setOpen(false);
    }
  }

  public void update() {
    checkTileMapCollision();
    if (open) {
      animation.setFrames(openSprites);
      animation.setDelay(300);
    }
    animation.update();
  }

  public void draw(Graphics2D g) {
    //if(notOnScreen()) return;
    setMapPosition();
    super.draw(g);
  }
}
