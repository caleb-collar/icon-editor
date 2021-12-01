//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Vertical Invert Decorator

//Imports
import java.util.HashMap;

/**
 * This decorator modifies the incoming pixel data to be vertically inverted.
 * @author Caleb
 */
public class VerticalInvertDecorator extends IconDecorator{
    
    public VerticalInvertDecorator(GuiExtendedInterface ei) {
        super(ei);
    }
    
    @Override
    public void modifyPixel(HashMap<String, Object> pixelClickData) {
        int y = (int)pixelClickData.get("y");
        Integer height = (Integer)pixelClickData.get("height");
        height -= 1;
        y = height - y;
        pixelClickData.put("y", y);
    }

    @Override
    public GuiExtended getInstance() {
        throw new UnsupportedOperationException("Not supported.");
    }
}