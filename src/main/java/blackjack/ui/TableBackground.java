/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
/**
 *
 * @author ministar
 */

// A panel that paints the table image as its background, plain green if the image is missing
public class TableBackground extends JPanel{
    
    private static final Color FALLBACK_GREEN = new Color(0, 100, 20);
    
    // A dark green layer over the image: greener table, more readable white text
    private static final Color OVERLAY = new Color(0, 60, 0, 110);
    
    private final Image image;
    
    public TableBackground()
    {
        URL url = TableBackground.class.getResource("/table.jpg");
        if(url == null)
        {
            image = null;
        }
        else
        {
            image = new ImageIcon(url).getImage();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(image == null)
        {
            g.setColor(FALLBACK_GREEN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        else
        {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            g.setColor(OVERLAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
