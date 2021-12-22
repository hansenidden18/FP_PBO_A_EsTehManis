package GameState;

import Audio.AudioPlayer;
import Entity.*;
import Entity.Enemies.*;
import Main.GamePanel;
import TileMap.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Level1State extends GameState {

  private TileMap tileMap;
  private Background bg;

  private Player player;

  private ArrayList<Enemy> enemies;
  private ArrayList<Message> boxes;

  private ArrayList<Explosion> explosions;

  private HUD hud;
  private Learn learn;

  private boolean isEnter;
  private boolean isBoxOpen;
  private boolean isDisabled;

  private AudioPlayer bgMusic;
  private boolean eventFinish;
  private boolean eventDead;
  private boolean eventStart;
  private ArrayList<Rectangle> tb;
  private boolean blockInput = false;
  private int eventCount = 0;

  public Level1State(GameStateManager gsm) {
    this.gsm = gsm;
    init();
  }

  public void setIsEnter(boolean enter) {
    this.isEnter = enter;
  }

  public void init() {
    tileMap = new TileMap(30);
    tileMap.loadTiles("/Tilesets/tile-02.png");
    tileMap.loadMap("/Maps/level1-1.map");
    tileMap.setPosition(0, 0);
    tileMap.setTween(1);

    bg = new Background("/Backgrounds/gamebg.png", 0.1);

    player = new Player(tileMap);
    player.setPosition(100, 100);
    player.setHealth(PlayerSave.getHealth());
    player.setLives(PlayerSave.getLives());
    player.setTime(PlayerSave.getTime());

    populateEnemies();
    populateBoxes();

    explosions = new ArrayList<Explosion>();

    hud = new HUD(player);

    learn = new Learn();

    bgMusic = new AudioPlayer("/Music/level1-1.mp3");
    bgMusic.play();

    eventStart = true;
    tb = new ArrayList<Rectangle>();
    eventStart();
  }

  private void populateEnemies() {
    enemies = new ArrayList<Enemy>();

    Slugger s;
    Point[] points = new Point[] {
      new Point(200, 100),
      new Point(1000, 100),
    };
    for (int i = 0; i < points.length; i++) {
      s = new Slugger(tileMap);
      s.setPosition(points[i].x, points[i].y);
      enemies.add(s);
    }
  }

  private void reset() {
    player.reset();
    player.setPosition(80, 161);
    populateEnemies();
    blockInput = true;
    eventCount = 0;
    eventStart = true;
    eventStart();
  }

  private void eventStart() {
    eventCount++;
    if (eventCount == 1) {
      tb.clear();
      tb.add(new Rectangle(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
      tb.add(new Rectangle(0, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
      tb.add(
        new Rectangle(
          0,
          GamePanel.HEIGHT / 2,
          GamePanel.WIDTH,
          GamePanel.HEIGHT / 2
        )
      );
      tb.add(
        new Rectangle(
          GamePanel.WIDTH / 2,
          0,
          GamePanel.WIDTH / 2,
          GamePanel.HEIGHT
        )
      );
    }
    if (eventCount > 1 && eventCount < 60) {
      tb.get(0).height -= 4;
      tb.get(1).width -= 6;
      tb.get(2).y += 4;
      tb.get(3).x += 6;
    }
    if (eventCount == 60) {
      eventStart = blockInput = false;
      eventCount = 0;
      tb.clear();
    }
  }

  private void eventDead() {
    eventCount++;
    if (eventCount == 1) {
      player.setDead();
      player.stop();
    }
    if (eventCount == 60) {
      tb.clear();
      tb.add(new Rectangle(GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
    } else if (eventCount > 60) {
      tb.get(0).x -= 6;
      tb.get(0).y -= 4;
      tb.get(0).width += 12;
      tb.get(0).height += 8;
    }
    if (eventCount >= 120) {
      if (player.getLives() == 0) {
        gsm.setState(GameStateManager.MENUSTATE);
      } else {
        eventDead = blockInput = false;
        eventCount = 0;
        player.loseLife();
        reset();
      }
    }
  }

  private void eventFinish() {
    eventCount++;
    if (eventCount == 1) {
      player.stop();
    } else if (eventCount == 120) {
      tb.clear();
      tb.add(new Rectangle(GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
    } else if (eventCount > 120) {
      tb.get(0).x -= 6;
      tb.get(0).y -= 4;
      tb.get(0).width += 12;
      tb.get(0).height += 8;
    }
    if (eventCount == 180) {
      PlayerSave.setHealth(player.getHealth());
      PlayerSave.setLives(player.getLives());
      PlayerSave.setTime(player.getTime());
      gsm.setState(GameStateManager.MENUSTATE);
    }
  }

  private void populateBoxes() {
    Study[] materials = new Study[] {
      new Study(
        "Selamat datang di Life of Prog(r)amer!",
        "Gunakan arrow kiri dan kanan untuk bergerak"
      ),
      new Study("Saat ini, bahasa pemrograman mencapai", "700 Bahasa!"),
      new Study(
        "Programming lama kelamaan akan sama pentingnya",
        "dengan membaca lho!"
      ),
      new Study("Virus pertama Komputer adalah", "Creeper"),
      new Study("Programming bisa membuat", "otak semakin cerdas"),
    };

    boxes = new ArrayList<Message>();
    Message s;
    Point[] points = new Point[] {
      new Point(120, 155),
      new Point(800, 95),
      new Point(1575, 95),
      new Point(1680, 95),
      new Point(1800, 95),
    };
    for (int i = 0; i < points.length; i++) {
      s =
        new Message(tileMap, materials[i].getDesc(), materials[i].getContext());
      s.setPosition(points[i].x, points[i].y);
      boxes.add(s);
    }
  }

  public void update() {
    //if dead
    if (player.getHealth() == 0 || player.gety() >= 210) {
      eventDead = blockInput = true;
    }

    if (player.getx() >= 5100) {
      eventFinish = true;
    }

    if (eventStart) eventStart();
    if (eventDead) eventDead();
    if (eventFinish) eventFinish();

    // update player
    player.update();

    isBoxOpen = false;
    for (int i = 0; i < boxes.size(); i++) {
      Message e = boxes.get(i);
      e.checkTouch(player);
      e.update();
      if (e.isOpen()) {
        isBoxOpen = true;
        learn.update(e.getDesc(), e.getContext());
      }
    }
    if (isBoxOpen && !isEnter) {
      learn.setOpen(true);
      isDisabled = true;
    } else if (isBoxOpen && isEnter) {
      learn.setOpen(false);
      isDisabled = false;
    } else {
      learn.setOpen(false);
      isEnter = false;
    }

    tileMap.setPosition(
      GamePanel.WIDTH / 2 - player.getx(),
      GamePanel.HEIGHT / 2 - player.gety()
    );

    // set background
    bg.setPosition(tileMap.getx(), tileMap.gety());

    // attack enemies
    player.checkAttack(enemies);

    // update all enemies
    for (int i = 0; i < enemies.size(); i++) {
      Enemy e = enemies.get(i);
      e.update();
      if (e.isDead()) {
        enemies.remove(i);
        i--;
        explosions.add(new Explosion(e.getx(), e.gety()));
      }
    }

    // update explosions
    for (int i = 0; i < explosions.size(); i++) {
      explosions.get(i).update();
      if (explosions.get(i).shouldRemove()) {
        explosions.remove(i);
        i--;
      }
    }
  }

  public void draw(Graphics2D g) {
    // draw bg
    bg.draw(g);

    // draw tilemap
    tileMap.draw(g);

    // draw player
    player.draw(g);

    // draw enemies
    for (int i = 0; i < enemies.size(); i++) {
      enemies.get(i).draw(g);
    }
    // draw boxes
    for (int i = 0; i < boxes.size(); i++) {
      boxes.get(i).draw(g);
    }
    // draw explosions
    for (int i = 0; i < explosions.size(); i++) {
      explosions
        .get(i)
        .setMapPosition((int) tileMap.getx(), (int) tileMap.gety());
      explosions.get(i).draw(g);
    }
    // draw hud
    hud.draw(g);

    g.setColor(java.awt.Color.BLACK);
    for (int i = 0; i < tb.size(); i++) {
      g.fill(tb.get(i));
    }

    //draw learn box
    learn.draw(g);
  }

  public void keyPressed(int k) {
    if (isDisabled) {
      if (k == KeyEvent.VK_ENTER) {
        setIsEnter(true);
        isDisabled = false;
      }
    } else {
      if (k == KeyEvent.VK_LEFT) player.setLeft(true);
      if (k == KeyEvent.VK_RIGHT) player.setRight(true);
      if (k == KeyEvent.VK_UP) player.setUp(true);
      if (k == KeyEvent.VK_DOWN) player.setDown(true);
      if (k == KeyEvent.VK_W) player.setJumping(true);
      if (k == KeyEvent.VK_S) player.setGliding(true);
      if (k == KeyEvent.VK_A) player.setScratching();
      if (k == KeyEvent.VK_D) player.setFiring();
    }
  }

  public void keyReleased(int k) {
    if (k == KeyEvent.VK_LEFT) player.setLeft(false);
    if (k == KeyEvent.VK_RIGHT) player.setRight(false);
    if (k == KeyEvent.VK_UP) player.setUp(false);
    if (k == KeyEvent.VK_DOWN) player.setDown(false);
    if (k == KeyEvent.VK_W) player.setJumping(false);
    if (k == KeyEvent.VK_S) player.setGliding(false);
  }
}
