
//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Icon Decorator

//Imports
import java.util.HashMap;

/**
 * This decorator abstraction is the basis for all other icon data decorators.
 * @author Caleb
 */
public abstract class IconDecorator implements GuiExtendedInterface{

    private final GuiExtendedInterface decoratedComponent;
    
    public IconDecorator(GuiExtendedInterface dc){
        decoratedComponent = dc;
    }

    @Override
    public void modifyPixel(HashMap<String, Object> pixelClickData) {
        decoratedComponent.modifyPixel(pixelClickData);
    }
}