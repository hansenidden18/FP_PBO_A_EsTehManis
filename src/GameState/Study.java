package GameState;

public class Study {

  private String description;
  private String context;

  Study(String a, String b) {
    this.description = a;
    this.context = b;
  }

  public String getDesc() {
    return this.description;
  }

  public String getContext() {
    return this.context;
  }
}
