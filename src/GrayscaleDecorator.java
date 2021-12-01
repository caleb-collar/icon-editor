//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Grayscale Decorator

//Imports
import java.awt.Color;
import java.util.HashMap;

/**
 * This decorator modifies the incoming pixel data to be grayscale.
 * @author Caleb
 */
public class GrayscaleDecorator extends IconDecorator{
    
    public GrayscaleDecorator(GuiExtendedInterface ei) {
        super(ei);
    }
    
    @Override
    public void modifyPixel(HashMap<String, Object> pixelClickData) {
        Color bColor = (Color)pixelClickData.get("bColor");
        int r = bColor.getRed();
        int g = bColor.getGreen();
        int b = bColor.getBlue();
        int gr = (r+g+b)/3; //The grayscale is an average of the r,g,&b values.
        Color grayScale = new Color(gr, gr, gr);
        pixelClickData.put("bColor", grayScale);
    }

    @Override
    public GuiExtended getInstance() {
        throw new UnsupportedOperationException("Not supported.");
    }
}