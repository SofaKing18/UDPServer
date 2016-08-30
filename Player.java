import java.net.*;
public class Player {
    private float x, y;
    private float movement;
    private String nickname;
    private InetAddress IPAddress;
    private int port;
    public Player(String Name, InetAddress ip, int port) {
        x = 0;
        y = 0;
        nickname=Name;
        IPAddress = ip;
        this.port = port;
        movement = 1;
    }
    public String position () {
        return nickname+" "+Float.toString(this.x)+":"+Float.toString(this.y);
    }
    public void command(String cmd) {
        switch (cmd) {
            case "left": moveLeft(); break;
            case "right": moveRight(); break;
            case "up": moveUp(); break;
            case "down": moveDown(); break;
            default: System.out.println("unknown command");
                break;
        }
    }
    private void moveLeft()
    {
        x = x - movement;
    }
    private void moveRight()
    {
        x = x + movement;
    }
    private void moveUp()
    {
        y = y - movement;
    }
    private void moveDown()
    {
        y = y + movement;
    }
}
