//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | GUI Launcher

//Imports
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The gui launcher contains the methods for adding a window listener to the
 * first gui launched to act as user input for determining icon dimensions and
 * number of editor panes.
 * @author Caleb
 */
public class GuiExtendedLauncher {
    public GuiExtendedLauncher(){
        AtomicBoolean closed = new AtomicBoolean(false);
        GuiExtended guiSplash = new GuiExtended(true);
        //Adds a window listener to step foward the flow of control when the 'splash' gui pane is closed rather than concurrently.
        guiSplash.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                synchronized(closed) {
                    closed.set(true);
                    closed.notify();
                }
                super.windowClosed(e);
            }
        });
        System.out.println("Launching Splash Pane...");
        guiSplash.setVisible(true);
        synchronized(closed) {
            while (!closed.get()) {
                try {
                    closed.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("Launching Editor Panes...");
        new GuiExtendedPaneSpawner();
    }    
}