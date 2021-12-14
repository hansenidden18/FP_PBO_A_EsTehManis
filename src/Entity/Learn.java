package Entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Learn {

  private String message;
  private boolean open;
  private String highlight;

  private BufferedImage image;
  private Font font;

  public Learn() {
    try {
      image =
        ImageIO.read(
          getClass().getResourceAsStream("/Sprites/Boxes/learn-box.png")
        );
      font = new Font("Arial", Font.PLAIN, 10);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void draw(Graphics2D g) {
    if (open) {
      g.drawImage(image, 37, 93, null);
      g.setFont(font);
      g.setColor(Color.BLACK);
      g.drawString(message, 45, 110);
      g.setColor(Color.RED);
      g.drawString(highlight, 45, 122);
    }
  }

  public boolean isOpen() {
    return this.open;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }

  public void update(String newMessage, String highlight) {
    this.message = newMessage;
    this.highlight = highlight;
  }
}
