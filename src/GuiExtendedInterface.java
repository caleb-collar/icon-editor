import java.util.HashMap;

//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Gui Interface
/**
 * The basic interface for the extended GUI and decorators.
 * @author Caleb
 */
public interface GuiExtendedInterface {
    public void modifyPixel(HashMap<String, Object> pixelClickData);
    public GuiExtended getInstance();
}