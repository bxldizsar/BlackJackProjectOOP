/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.ui;

import blackjack.model.Card;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
/**
 *
 * @author ministar
 */

// Loads card images from the resources folder and scales them
public class CardImages {
    
    public static final int CARD_WIDTH = 64;
    public static final int CARD_HEIGHT = 92;
    
    // Image of one card, e.g. A-C.png
    public ImageIcon getImage(Card card)
    {
        return load(card.getImageName());
    }
    
    // Image of the back side (face-down card)
    public ImageIcon getBack()
    {
        return load("BACK.png");
    }
    
    // Loads and scales one image by file name; null if the file is missing
    private ImageIcon load(String fileName)
    {
        URL url = CardImages.class.getResource("/cards/" + fileName);
        if(url == null) return null;
        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
