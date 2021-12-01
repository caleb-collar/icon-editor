//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Horizontal Invert Decorator

//Imports
import java.util.HashMap;

/**
 * This decorator modifies the incoming pixel data to be horizontal inverted.
 * @author Caleb
 */
public class HorizontalInvertDecorator extends IconDecorator{
    
    public HorizontalInvertDecorator(GuiExtendedInterface ei){
        super(ei);
    }
    
    @Override
    public void modifyPixel(HashMap<String, Object> pixelClickData) {
        int x = (int)pixelClickData.get("x");
        Integer width = (Integer)pixelClickData.get("width");
        width -= 1;
        x = width - x;
        pixelClickData.put("x", x);
    }

    @Override
    public GuiExtended getInstance() {
        throw new UnsupportedOperationException("Not supported.");
    }
}