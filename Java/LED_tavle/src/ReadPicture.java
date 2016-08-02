

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author RobinBergseth
 */
public class ReadPicture {
    
    /**
     * Constructor
     */
    public ReadPicture(){
        
    }
    
    public int[][] readImage(String imageName) throws IOException{
        BufferedImage image = ImageIO.read(new File(imageName));
        
        int[][] imMatrix = new int [image.getWidth()][image.getHeight()];
        // Read color from picture
        
        for(int xPixel = 0; xPixel < image.getWidth(); xPixel++){
            for(int yPixel = 0; yPixel < image.getHeight(); yPixel++){
                int color = image.getRGB(xPixel, yPixel);
                int alpha = (color >>> 24);
                color = color << 8;
                int red = (color >>> 24);
                color = color << 8;
                int green = (color >>> 24);
                color = color << 8;
                int blue = (color >>> 24);
            }
        }
        return imMatrix;
    }
    
}
