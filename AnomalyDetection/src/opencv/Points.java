package opencv;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Points extends JPanel {

  public void paintComponent(Graphics g, int x, int y) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;

    g2d.setColor(Color.red);

      g2d.drawLine(x, y, x, y);
    

  }

  public static void main(String[] args) {
    Points points = new Points();
    JFrame frame = new JFrame("Points");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(points);

    frame.setSize(250, 200);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

  }
}