//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Random Color Decorator

//Imports
import java.awt.Color;
import java.util.Random;
import java.util.HashMap;

/**
 * This decorator modifies the incoming pixel data to be random colors.
 * @author Caleb
 */
public class RandomColorDecorator extends IconDecorator{
    
    public RandomColorDecorator(GuiExtendedInterface ei) {
        super(ei);
    }
    
    Random rand = new Random();
    int upperbound = 255;
    
    @Override
    public void modifyPixel(HashMap<String, Object> pixelClickData) {
        int r = rand.nextInt(upperbound);
        int g = rand.nextInt(upperbound);
        int b = rand.nextInt(upperbound);
        Color bColor = new Color(r, g, b);
        pixelClickData.put("bColor", bColor);
    }

    @Override
    public GuiExtended getInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}