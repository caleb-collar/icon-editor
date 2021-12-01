//CSC 2910 OOP | Caleb A. Collar | P6: Bitmap Editor GUI Extended | GUI Class

//Imports
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO; //For handling image previews.
import java.nio.file.Files;
import java.awt.image.BufferedImage;
import javax.swing.event.ChangeEvent; //For handling color picker events.
import java.io.ByteArrayInputStream;
import static java.lang.Math.round;
import static javax.swing.SwingUtilities.getWindowAncestor;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.formdev.flatlaf.IntelliJTheme; //Swing theme.

/**
 * This class extends swing JFrame for the extended Bitmap Editor GUI (P6).
 * Implements basic editor functionality including line drawing and support for
 * the construction of multiple gui instances.
 * 
 * @author Caleb
 */
public class GuiExtended extends javax.swing.JFrame implements Observer, GuiExtendedInterface{
    //Data members
    private Icon icon;
    private EditQueue editQueue;
    private Component lastClicked;
    private HashMap<String,Component> buttonMap;
    private HashMap<String, Object> pixelClickData;
    private Integer width, height, bWidth, bHeight, numPanes;
    private Boolean editorActive = false, advancedEdit = false, splashMenu = false, lockAttach = false, cursorUpdate = false;
    private Boolean dVertical = false, dHorizontal = false, dColor = false, dGray = false;
    private GuiExtendedInterface extendedInterface;
    private Boolean attached = true;
    BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR);
    private java.awt.Color origButtonColor = new java.awt.Color(61, 66, 75);
    private final java.awt.Color bgColor = new java.awt.Color(40, 44, 52);
    private final java.awt.Color borderColor = new java.awt.Color(33, 37, 43);
    private GuiExtended instance;
    
    //Default contructor, creates default GUI.
    public GuiExtended() {
        initTheme(); //Changes swing's theme. (Specified in local json)
        setIcon(); //Sets runtime icon to custom.
        initComponents(); //Creates all GUI components.
        setSizeAndVisibility(); //Sets up initial GUI visibilty.
        editQueue = new EditQueue(width, height);
    }
    
    //Constructor with known image dimensions.
    public GuiExtended(Integer w, Integer h) {
        width = w;
        height = h;
        editQueue = new EditQueue(width, height);
        initTheme();
        setIcon();
        initComponents();
        setSizeAndVisibility();
        sizeButtonClicked(null);
    }
    
    //Constructor with known image dimensions and synced icon data.
    public GuiExtended(Integer w, Integer h, EditQueue eq) {
        width = w;
        height = h;
        editQueue = eq;
        extendedInterface = GuiExtendedPaneSpawner.getinterfaceInstance();
        editQueue.attach(this); //Attaches this Jframe to the observer pattern.
        initTheme();
        setIcon();
        initComponents();
        setSizeAndVisibility();
        sizeButtonClicked(null);
    }
    
    //Constructor for multi-editor panes set-up.
    public GuiExtended(boolean splash) {
        splashMenu = true; //Constructs this frame to be a set-up dialog.
        initTheme();
        setIcon();
        initComponents();
        setSizeAndVisibility();
    }
    
    @Override //Returns this instance of GuiExtended Jframe.
    public GuiExtended getInstance() {
        return instance;
    }
    
    //Contains values for multi-pane instancing. 
    private HashMap<String,Integer> getSplashValues(){
        HashMap<String,Integer> values = new HashMap<>();
        values.put("WIDTH", width);
        values.put("HEIGHT", height);
        values.put("PANES", numPanes);
        return values;
    }

    //Handles size changes.
    private void sizeButtonClicked(java.awt.event.ActionEvent evt) {
        if (splashMenu){
            commitOptions();
            writeObjToFile(getSplashValues());
            getWindowAncestor(MenuBar).dispose();
        } else {
            SizeOptions.setVisible(false);
            WorkNotice.setVisible(true);
            SizeOptions.revalidate();
            WorkNotice.repaint();
            commitOptions();
            getWindowAncestor(MenuBar).setSize(672,900);
            BitmapEditor.setVisible(true);
            editorActive = true;
            menuNewIcon.setEnabled(true);
            menuSaveBitmap.setEnabled(true);
            menuAdvanced.setEnabled(true);
            WorkNotice.setVisible(false);
            brushColorPicker.setVisible(true);
        }
    }
    
    @Override //Update is called by observer, then the method retrieves the last update.
    public void update() {
        UpdateData data = editQueue.getData();
        int x = data.getX();
        int y = data.getY();
        int r = data.getR();
        int g = data.getG();
        int b = data.getB();
        Color bColor = new Color(r,g,b);
        String target = x+","+y;
        Component button = buttonMap.get(target);
        button.setBackground(bColor);
        icon.setPixel(x, y, r, g, b);
    }
    
    //Handles when a pixel is clicked.
    private void pixelClicked(java.awt.event.ActionEvent evt, Component button) {
        cursorUpdate = true;
        pixelClickData = new HashMap<>(); //Pass data to be modified on to decorators.
        Color bColor = brushColorPicker.getColor();
        lastClicked = button;
        refreshInfoBar();
        String coord = button.getName();
        String[] toParse = coord.split(",");
        int x = Integer.parseInt(toParse[0]);
        int y = Integer.parseInt(toParse[1]);
        pixelClickData.put("bColor", bColor);
        pixelClickData.put("width", width);
        pixelClickData.put("height", height);
        pixelClickData.put("x", x);
        pixelClickData.put("y", y);
        pixelClickData.put("button", lastClicked);
        //Instantiate decorators to modify data when the GUI element is checked.
        if(dVertical){
            extendedInterface = new VerticalInvertDecorator(extendedInterface);
            extendedInterface.modifyPixel(pixelClickData);
            cursorUpdate = false;
        }
        if(dHorizontal){
            extendedInterface = new HorizontalInvertDecorator(extendedInterface);
            extendedInterface.modifyPixel(pixelClickData);
            cursorUpdate = false;
        }
        if(dColor){
            extendedInterface = new RandomColorDecorator(extendedInterface);
            extendedInterface.modifyPixel(pixelClickData);
            cursorUpdate = false;
        }
        if(dGray){
            extendedInterface = new GrayscaleDecorator(extendedInterface);
            extendedInterface.modifyPixel(pixelClickData);
            cursorUpdate = false;
        } else {
            extendedInterface = GuiExtendedPaneSpawner.getinterfaceInstance();
        }
        modifyPixel(pixelClickData); //Reflect data changes
    }
    
    @Override //Method that directly modifies GUI and icon data reflected from decorators.
    public void modifyPixel(HashMap<String, Object> pixelClickData) {
        Color bColor = (Color)pixelClickData.get("bColor");
        int x = (int)pixelClickData.get("x");
        int y = (int)pixelClickData.get("y");
        Component button = getButtonByName(x+","+y);
        //Change colors by advanced brushstroke.
        if(advancedEdit){
            advancedBrushOptions.setVisible(true);
            brushColorPicker.setVisible(false);
        }
        //Change color by single pixel.
        else {
            button.setBackground(bColor);
            icon.setPixel(x, y, bColor.getRed(), bColor.getGreen(), bColor.getBlue());
            if(attached && !lockAttach){
                editQueue.notifySetPixel(x, y, bColor.getRed(), bColor.getGreen(), bColor.getBlue());
            }
        }
        cursorUpdate = true;
    }
    
    //Handles when a pixel is moused over; creates a 'cursor' effect.
    private void pixelMouseIn(java.awt.event.MouseEvent evt, Component button) {
        origButtonColor = button.getBackground();
        if (!evt.isShiftDown()){
            Color bColor = brushColorPicker.getColor();
            button.setBackground(bColor);
        } else if (evt.isShiftDown()){
            pixelClicked(null, button);
        }
    }
    
    //Handles when a pixel is moused over and exited.
    private void pixelMouseExit(java.awt.event.MouseEvent evt, Component button) {
        if(!cursorUpdate){
            button.setBackground(origButtonColor);
        }
        cursorUpdate = false;
    }
    
    //Handles when menu save button is clicked.
    private void menuSaveBitmapActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileSaver = new JFileChooser(".");
        fileSaver.setDialogTitle("Save Bitmapped Icon...");
        fileSaver.addChoosableFileFilter(new FileNameExtensionFilter("Bitmapped Image (*.bmp)", "bmp", "BMP"));
        fileSaver.setMultiSelectionEnabled(false);
        fileSaver.setAcceptAllFileFilterUsed(false);
        int userSelection = fileSaver.showSaveDialog(BitmapEditor);
        if (userSelection == JFileChooser.APPROVE_OPTION) {       
            fileSaver.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            File selectedFile = fileSaver.getSelectedFile();
            if(selectedFile.getPath().contains(".bmp")){
                icon.createBitmapFile(selectedFile.getPath()); //Create file.
            }
            else{
                icon.createBitmapFile(selectedFile.getPath()+".bmp"); //Else, create file and add extension.
            }
        }
    }
 
    //Handles exiting program from the menu.
    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {
        int returnValue;
    	returnValue = JOptionPane.showConfirmDialog(getWindowAncestor(MenuBar), "Are you sure you want to exit?", "Exiting...", JOptionPane.YES_NO_OPTION);	
    	if (returnValue == JOptionPane.YES_OPTION){
            getWindowAncestor(MenuBar).dispose();
        }
    	else if (returnValue == JOptionPane.NO_OPTION){
            //Close dialog
        }
    }

    private void menuNewIconActionPerformed(java.awt.event.ActionEvent evt) {
        if(editorActive == true){
            width = null;
            height = null;
            setSizeAndVisibility();
            buttonMap.clear();
            attached = false;
            toggleAttached.setSelected(false);
            toggleAttached.setEnabled(false);
            lockAttach = true;
        }
    }

    //Supports toggling the advanced tools from menu bar in addition to checkbox.
    private void menuAdvancedActionPerformed(java.awt.event.ActionEvent evt) {
        if(advancedEdit == false){
            advancedEdit = true;
            toggleAdvanced.setSelected(true);
            menuAdvanced.setSelected(true);
        }
        else if(advancedEdit == true){
            advancedEdit = false;
            toggleAdvanced.setSelected(false);
            menuAdvanced.setSelected(false);
            advancedBrushOptions.setVisible(false);
            brushColorPicker.setVisible(true);
        }
    }

    //Handles commiting the advanced brush size options.
    private void confirmBrushsizeButtonClicked(java.awt.event.ActionEvent evt) {
        commitBrushOptions();
        Component bTarget;
        Color bColor = brushColorPicker.getColor();
        String[] bToParse = lastClicked.getName().split(",");
        int xOrigin = Integer.parseInt(bToParse[0]);
        int yOrigin = Integer.parseInt(bToParse[1]);
        String target;
        for (int w=0, x=xOrigin; w < bWidth; w++, x=xOrigin+w){
            for (int h=0, y=yOrigin; h < bHeight; h++, y=yOrigin+h){
                if (x < width && y < height){
                    target = x + "," + y;
                    bTarget = getButtonByName(target);
                    bTarget.setBackground(bColor);
                    try{
                        icon.setPixel(x, y, bColor.getRed(), bColor.getGreen(), bColor.getBlue());
                        if(attached && !lockAttach){
                            editQueue.notifySetPixel(x, y, bColor.getRed(), bColor.getGreen(), bColor.getBlue());
                        }
                    }
                    catch(Exception e){
                        //Exception setting icon pixel.
                    }
                }
            }
        }
        advancedBrushOptions.setVisible(false);
        brushColorPicker.setVisible(true);
    }

    //Handles importing another bitmapped image.
    private void importImagesizeButtonClicked(java.awt.event.ActionEvent evt) {
        JFileChooser fileOpener = new JFileChooser(".");
        fileOpener.setDialogTitle("Open Bitmapped Icon to Use at: ["+lastClicked.getName()+"]");
        fileOpener.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileOpener.addChoosableFileFilter(new FileNameExtensionFilter("Bitmapped Image (*.bmp)", "bmp", "BMP", "gif", "png"));
        fileOpener.setMultiSelectionEnabled(false);
        fileOpener.setAcceptAllFileFilterUsed(false);
        int userSelection = fileOpener.showOpenDialog(BitmapEditor);
        if (userSelection == JFileChooser.APPROVE_OPTION) {       
            fileOpener.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            File selectedFile = fileOpener.getSelectedFile();
            if(selectedFile.getPath().contains(".")){
                //Read selected file to image buffer.
                try{
                    byte[] bytes = Files.readAllBytes(selectedFile.toPath());
                    InputStream imgData = new ByteArrayInputStream(bytes);
                    image = ImageIO.read(imgData);
    
                    try{
                        JLabel imgLabel = new JLabel(new ImageIcon(image));
                        imgLabel.setSize(190, 230);
                        imgDisplayContainer.add(imgLabel);
                        imgPreviewPanel.setVisible(true);
                    }
                    catch (Exception e){
                        System.out.println("Can't display the image...");
                    }
                }
                catch (IOException e){
                    System.out.println("Can't read selected file as image...");
                }
            }
            else{
                System.out.println("File not saved, invalid path or filetype.");
            }
        }
    }

    //Handles image add confirmation.
    private void imgAddActionPerformed(java.awt.event.ActionEvent evt) {
        imgPreviewPanel.setVisible(false);
        advancedBrushOptions.setVisible(false);
        brushColorPicker.setVisible(true);
        imgDisplayContainer.removeAll();
        //Clear display container and add the image pixels to current icon.
        addSelectedImgPixels();
    }

    //Handles image add rejection.
    private void imgRejectActionPerformed(java.awt.event.ActionEvent evt) {
        imgPreviewPanel.setVisible(false);
        advancedBrushOptions.setVisible(false);
        brushColorPicker.setVisible(true);
        imgDisplayContainer.removeAll();
        //Clear display container and do nothing else.
    }

    //Handles decorator toggle for vertical invert.
    private void toggleVerticalInvertActionPerformed(java.awt.event.ActionEvent evt) {
        dVertical ^= true;
    }

    //Handles decorator toggle for horizontal invert.
    private void toggleHorizontalInvertActionPerformed(java.awt.event.ActionEvent evt) {
        dHorizontal ^= true;
    }

    //Handles decorator toggle for random colors.
    private void toggleRandomColorsActionPerformed(java.awt.event.ActionEvent evt) {
        dColor ^= true;
    }

    //Handles decorator toggle for grayscale colors.
    private void toggleGrayscaleActionPerformed(java.awt.event.ActionEvent evt) {
        dGray ^= true;
    }

    //Handles decorator toggle for attaching the Jframe to the observer.
    private void toggleAttachedActionPerformed(java.awt.event.ActionEvent evt) {
        if (!lockAttach){
            attached ^= true;
            if(attached){
                editQueue.attach(this);
                System.out.println("Attaching "+this.getTitle());
            } else {
                editQueue.detach(this);
                System.out.println("Detaching "+this.getTitle());
            }
        }
    }

    //Initiates a custom theme for the gui from the .json file specified.
    private void initTheme() {
        try {
            //UIManager.setLookAndFeel( new FlatDarkLaf() );
            IntelliJTheme.setup(GuiExtended.class.getResourceAsStream(
            "one_dark.theme.json"));
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize theme from json, check file and name." );
        }
    }

    //Sets initial size and visibility for GUI as the user moves through the program.
    private void setSizeAndVisibility() {
        instance = this;
        getContentPane().setBackground(bgColor);
        toggleAttached.setSelected(true);
        BitmapEditor.setVisible(false);
        panesNumLabel.setEnabled(false);
        inputNumPanes.setEnabled(false);
        clearEditor();
        WorkNotice.setVisible(false);
        advancedBrushOptions.setVisible(false);
        menuNewIcon.setEnabled(false);
        menuSaveBitmap.setEnabled(false);
        SizeOptions.setVisible(true);
        if (splashMenu){
            panesNumLabel.setEnabled(true);
            inputNumPanes.setEnabled(true);
        }
        try{
            if (GuiExtendedPaneSpawner.getNumPanes() < 2){
                toggleAttached.setSelected(false);
                toggleAttached.setEnabled(false);
            }
        } catch (Exception ex){
            //Move on in flow of control as a non mult-pane constructor was likely used.
        }
        menuAdvanced.setEnabled(false);
        toggleAdvanced.setSelected(false);
        menuAdvanced.setSelected(false);
        imgPreviewPanel.setVisible(false);
        getWindowAncestor(MenuBar).setSize(672,350);
        refreshInfoBar();
        //Anonymous inner class to listen for updates on the color chooser.
        brushColorPicker.getSelectionModel().addChangeListener((ChangeEvent arg0) -> {
            refreshInfoBar();
        });
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    //Sets up the editor portion of the GUI and handles button instantiation.
    protected void editorSetup() {
        javax.swing.JPanel resizedGrid;
        resizedGrid = new javax.swing.JPanel();
        int maxH = 440;
        int maxW = 635;
        float ratioW = (maxW / width);
        float ratioH = (maxH / height);
        float bestRatio = Math.min(ratioW, ratioH);
        int frameW = round(width * bestRatio);
        int frameH = round(height * bestRatio);
        int centerX = round(maxW / 2) - round(frameW / 2) + 10;
        int centerY = round(maxH / 2) - round(frameH / 2) + 13;
        List<Integer> numPixels = new Stack<>();
        if(width > height){
            GridFrame.add(resizedGrid, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, centerY, maxW, frameH));
        }
        else if(width < height){
            GridFrame.add(resizedGrid, new org.netbeans.lib.awtextra.AbsoluteConstraints(centerX, 13, frameW, maxH));
        }
        else{
            GridFrame.add(resizedGrid, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 13, maxH, maxH));
        }
        GridLayout pixels = new GridLayout(height, width);
        resizedGrid.setLayout(pixels);
        for(int i=0; i < (width * height); i++){
            numPixels.add(i);
        }
        resizedGrid.setFocusable(false);
        resizedGrid.setRequestFocusEnabled(false);
        //Add buttons
        for(int h=0; h<height; h++){
            for(int w=0; w<width; w++){
                JButton b = new JButton();
                resizedGrid.add(b);
                b.setName(w+","+h);
                b.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
                b.setFocusable(false);
                b.setRequestFocusEnabled(false);
                b.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
                b.addActionListener((java.awt.event.ActionEvent evt) -> {
                    pixelClicked(evt,b);
                });
                b.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        pixelMouseIn(evt,b);
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        pixelMouseExit(evt,b);
                    }
                });
            }
        }
        //Map the button coordinates for color changing.
        createComponentMap(resizedGrid);
        //System.out.println(buttonMap);//Debug - print map
        //Initialize new icon object.
        icon = Icon.init((int)width, (int)height);
        refreshInfoBar();
    }

    //Will commit jspinner values for use in icon editor.
    private void commitOptions() {
        try {
            inputWidth.commitEdit();
            inputHeight.commitEdit();
            inputNumPanes.commitEdit();
        } catch (Exception ex) {
            System.out.println("Could not parse spinner values...");
        }
        if(width == null && height == null){
            width = (Integer) inputWidth.getValue();
            height = (Integer) inputHeight.getValue();
            numPanes = (Integer) inputNumPanes.getValue();
        }
        editorSetup();
    }
    
    //Clears the editor panel.
    private void clearEditor() {
        editorActive = false;
        advancedEdit = false;
        GridFrame.removeAll();
        GridFrame.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        toggleAdvanced.setSelected(false);
        menuAdvanced.setSelected(false);
    }

    //Sets the java runtime icon that is visible on the OS taskbar/window.
    private void setIcon() {
        ImageIcon guiLogo = new ImageIcon("src/logo.png");
        setIconImage(guiLogo.getImage());
    }

    //Commits the brush option size of the jspinners to be used in brush dimensions.
    private void commitBrushOptions() {
        try {
            brushWidth.commitEdit();
            brushHeight.commitEdit();
        } catch (Exception ex) {
            System.out.println("Could not parse spinner values...");
        }
        bWidth = 0;
        bHeight = 0;
        bWidth = (Integer)brushWidth.getValue();
        bHeight = (Integer)brushHeight.getValue();         
    }
    
    //Method to create a hash map of components for easy access.
    private void createComponentMap(JPanel mapThis) {
        buttonMap = new HashMap<>();
        Component[] components = mapThis.getComponents();
        for (Component component : components) {
            buttonMap.put(component.getName(), component);
        }
    }

    //Will return the button object from the map by a given name.
    private Component getButtonByName(String name) {
            if (buttonMap.containsKey(name)) {
                    return (Component) buttonMap.get(name);
            }
            else return null;
    }

    //Refreshes the value in the bottom info bar.
    private void refreshInfoBar() {
        String rgb =brushColorPicker.getColor().getRed()+","+brushColorPicker.getColor().getGreen()+","+brushColorPicker.getColor().getBlue();
        try{
            displayColor.setText("rgb: ["+rgb+"]");
            displayDimensions.setText("["+width+","+height+"]");
            displayTarget.setText("["+lastClicked.getName()+"]");
        }
        catch(Exception e){
            //Try to update, otherwise leave last values.
        }
    }

    //Adds the slected image's pixels to the editor and the current icon.
    private void addSelectedImgPixels() {
        Component bTarget;
        int selectedImgW = image.getWidth();
        int selectedImgH = image.getHeight();
        String[] bToParse = lastClicked.getName().split(",");
        int originX = Integer.parseInt(bToParse[0]);
        int originY = Integer.parseInt(bToParse[1]);
        System.out.println("Image working origin: " + originX +","+originY);
        String target;
        for (int w=0, x=originX; w < selectedImgW; w++, x=originX+w){
            for (int h=0, y=originY; h < selectedImgH; h++, y=originY+h){
                if (x < width && y < height){
                    //Set GUI button color.
                    target = x + "," + y;
                    Color bColor = Color.decode(Integer.toString(image.getRGB(w, h)));
                    bTarget = getButtonByName(target);
                    //System.out.println("bTarget: " + x +","+y);
                    //System.out.println("bTargetColor: "+bColor);
                    bTarget.setBackground(bColor);
                    //Set icon pixel color.    
                    try{
                        icon.setPixel(x, y, bColor.getRed(), bColor.getGreen(), bColor.getBlue());
                        if(attached && !lockAttach){
                            editQueue.notifySetPixel(x, y, bColor.getRed(), bColor.getGreen(), bColor.getBlue());
                        }
                    }
                    catch (Exception e){
                        //No fill, out of range, keep looping.
                    }
                    
                }
            }
        }
        advancedBrushOptions.setVisible(false);
        brushColorPicker.setVisible(true);   
    }
    
    private void writeObjToFile(Object obj){
        try {
            File f = new File("temp");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            fos.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initComponents() {

        SizeOptions = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        widthLabel = new javax.swing.JLabel();
        panesNumLabel = new javax.swing.JLabel();
        inputWidth = new javax.swing.JSpinner();
        inputHeight = new javax.swing.JSpinner();
        confirmSize = new javax.swing.JButton();
        heightLabel = new javax.swing.JLabel();
        inputNumPanes = new javax.swing.JSpinner();
        WorkNotice = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        BitmapEditor = new javax.swing.JPanel();
        advancedBrushOptions = new javax.swing.JPanel();
        imgPreviewPanel = new javax.swing.JPanel();
        imgAdd = new javax.swing.JButton();
        imgReject = new javax.swing.JButton();
        imgDisplayContainer = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        brushWidth = new javax.swing.JSpinner();
        brushHeight = new javax.swing.JSpinner();
        confirmBrush = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        importImage = new javax.swing.JButton();
        brushColorPicker = new javax.swing.JColorChooser();
        GridFrame = new javax.swing.JPanel();
        InfoBar = new javax.swing.JPanel();
        infoAttached = new javax.swing.JLabel();
        infoColor = new javax.swing.JLabel();
        infoOnPixel = new javax.swing.JLabel();
        infoAdv = new javax.swing.JLabel();
        toggleAttached = new javax.swing.JCheckBox();
        displayColor = new javax.swing.JTextPane();
        displayTarget = new javax.swing.JTextPane();
        toggleAdvanced = new javax.swing.JCheckBox();
        infoVertical = new javax.swing.JLabel();
        toggleVerticalInvert = new javax.swing.JCheckBox();
        infoHorizontal = new javax.swing.JLabel();
        toggleHorizontalInvert = new javax.swing.JCheckBox();
        infoRandomColor = new javax.swing.JLabel();
        toggleRandomColors = new javax.swing.JCheckBox();
        infoGrayscale = new javax.swing.JLabel();
        toggleGrayscale = new javax.swing.JCheckBox();
        infoDimensions1 = new javax.swing.JLabel();
        displayDimensions = new javax.swing.JTextPane();
        MenuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuNewIcon = new javax.swing.JMenuItem();
        menuSaveBitmap = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuAdvanced = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bitmap Image & Icon Creator");
        setBackground(new java.awt.Color(69, 73, 74));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        setForeground(new java.awt.Color(69, 73, 74));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SizeOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New Icon", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Light", 0, 11))); 
        SizeOptions.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Enter Dimensions:");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        SizeOptions.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, 353, 31));

        widthLabel.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        widthLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        widthLabel.setText("Icon's width:");
        SizeOptions.add(widthLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 52, 70, -1));

        panesNumLabel.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        panesNumLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        panesNumLabel.setText("Editor panes:");
        SizeOptions.add(panesNumLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 96, 70, -1));

        inputWidth.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        inputWidth.setModel(new javax.swing.SpinnerNumberModel(1, 1, 128, 1));
        inputWidth.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inputWidth.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        inputWidth.setEditor(new javax.swing.JSpinner.NumberEditor(inputWidth, ""));
        SizeOptions.add(inputWidth, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 52, 80, -1));

        inputHeight.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        inputHeight.setModel(new javax.swing.SpinnerNumberModel(1, 1, 128, 1));
        inputHeight.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inputHeight.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        inputHeight.setEditor(new javax.swing.JSpinner.NumberEditor(inputHeight, ""));
        SizeOptions.add(inputHeight, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 74, 80, -1));

        confirmSize.setText("Customize");
        confirmSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeButtonClicked(evt);
            }
        });
        SizeOptions.add(confirmSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, -1, -1));

        heightLabel.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        heightLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        heightLabel.setText("Icon's height:");
        SizeOptions.add(heightLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 74, 70, -1));

        inputNumPanes.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        inputNumPanes.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));
        inputNumPanes.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inputNumPanes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        inputNumPanes.setEditor(new javax.swing.JSpinner.NumberEditor(inputNumPanes, ""));
        SizeOptions.add(inputNumPanes, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 96, 80, -1));

        getContentPane().add(SizeOptions, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, -1, 180));
        SizeOptions.getAccessibleContext().setAccessibleName("Icon Size");

        WorkNotice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New Icon", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Light", 0, 11), new java.awt.Color(249, 130, 108))); 
        WorkNotice.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        WorkNotice.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Working...");
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        WorkNotice.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, 353, 31));

        getContentPane().add(WorkNotice, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, -1, 150));

        BitmapEditor.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        advancedBrushOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Advanced Brush", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Light", 0, 11))); 
        advancedBrushOptions.setFocusable(false);
        advancedBrushOptions.setRequestFocusEnabled(false);
        advancedBrushOptions.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        imgPreviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selected Image Preview:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 11))); 
        imgPreviewPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        imgAdd.setText("Add");
        imgAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imgAddActionPerformed(evt);
            }
        });
        imgPreviewPanel.add(imgAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, -1, -1));

        imgReject.setText("Reject");
        imgReject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imgRejectActionPerformed(evt);
            }
        });
        imgPreviewPanel.add(imgReject, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, -1, -1));

        imgDisplayContainer.setOpaque(false);
        imgDisplayContainer.setLayout(new java.awt.GridLayout(1, 0));
        imgPreviewPanel.add(imgDisplayContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 230, 230));

        advancedBrushOptions.add(imgPreviewPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 250, 300));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("- or -");
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        advancedBrushOptions.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 180, 110, 31));

        jLabel10.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Brush  width:");
        advancedBrushOptions.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 90, 70, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Brush height:");
        advancedBrushOptions.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 110, 70, -1));

        brushWidth.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        brushWidth.setModel(new javax.swing.SpinnerNumberModel(1, 1, 7680, 1));
        brushWidth.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        brushWidth.setEditor(new javax.swing.JSpinner.NumberEditor(brushWidth, ""));
        advancedBrushOptions.add(brushWidth, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 90, 80, -1));

        brushHeight.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        brushHeight.setModel(new javax.swing.SpinnerNumberModel(1, 1, 7680, 1));
        brushHeight.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        brushHeight.setEditor(new javax.swing.JSpinner.NumberEditor(brushHeight, ""));
        advancedBrushOptions.add(brushHeight, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, 80, -1));

        confirmBrush.setText("Fill From Selected Pixel");
        confirmBrush.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmBrushsizeButtonClicked(evt);
            }
        });
        advancedBrushOptions.add(confirmBrush, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 150, -1, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Enter Dimensions to Fill:");
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        advancedBrushOptions.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, 353, 31));

        importImage.setText("Fill From Selected Pixel Using an Image");
        importImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importImagesizeButtonClicked(evt);
            }
        });
        advancedBrushOptions.add(importImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, -1, -1));

        BitmapEditor.add(advancedBrushOptions, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 460, 650, 360));

        brushColorPicker.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); 
        brushColorPicker.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Brush Color", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Light", 0, 11))); 
        brushColorPicker.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BitmapEditor.add(brushColorPicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 460, 656, 360));

        GridFrame.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pixel Editor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Light", 0, 11))); 
        GridFrame.setForeground(java.awt.SystemColor.inactiveCaption);
        GridFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        GridFrame.setFocusable(false);
        GridFrame.setRequestFocusEnabled(false);
        GridFrame.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        BitmapEditor.add(GridFrame, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 656, 460));

        InfoBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        InfoBar.setFocusable(false);
        InfoBar.setRequestFocusEnabled(false);
        InfoBar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        infoAttached.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoAttached.setText("Attached: ");
        infoAttached.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoAttached, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        infoColor.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoColor.setText("Selected Color~");
        infoColor.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 2, -1, -1));

        infoOnPixel.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoOnPixel.setText("Pixel Brush Target~");
        infoOnPixel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoOnPixel, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, -1, -1));

        infoAdv.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoAdv.setText("Advanced:");
        infoAdv.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoAdv, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 2, -1, -1));

        toggleAttached.setSelected(true);
        toggleAttached.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        toggleAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAttachedActionPerformed(evt);
            }
        });
        InfoBar.add(toggleAttached, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, -1, -1));

        displayColor.setEditable(false);
        displayColor.setBorder(null);
        displayColor.setFont(new java.awt.Font("Segoe UI", 0, 11)); 
        displayColor.setText("RGB:[255,255,255]");
        displayColor.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        InfoBar.add(displayColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 2, -1, -1));

        displayTarget.setEditable(false);
        displayTarget.setBorder(null);
        displayTarget.setFont(new java.awt.Font("Segoe UI", 0, 11)); 
        displayTarget.setText("[xx,yy]");
        displayTarget.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        InfoBar.add(displayTarget, new org.netbeans.lib.awtextra.AbsoluteConstraints(615, 20, 70, -1));

        toggleAdvanced.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        toggleAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAdvancedActionPerformed(evt);
            }
        });
        InfoBar.add(toggleAdvanced, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 2, -1, -1));

        infoVertical.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoVertical.setText("Vertical Invert:");
        infoVertical.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoVertical, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 2, -1, -1));

        toggleVerticalInvert.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        toggleVerticalInvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleVerticalInvertActionPerformed(evt);
            }
        });
        InfoBar.add(toggleVerticalInvert, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 2, -1, -1));

        infoHorizontal.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoHorizontal.setText("Horizontal Invert:");
        infoHorizontal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoHorizontal, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        toggleHorizontalInvert.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        toggleHorizontalInvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleHorizontalInvertActionPerformed(evt);
            }
        });
        InfoBar.add(toggleHorizontalInvert, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, -1, -1));

        infoRandomColor.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoRandomColor.setText("Random Colors:");
        infoRandomColor.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoRandomColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, -1, -1));

        toggleRandomColors.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        toggleRandomColors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleRandomColorsActionPerformed(evt);
            }
        });
        InfoBar.add(toggleRandomColors, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, -1, -1));

        infoGrayscale.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoGrayscale.setText("Grayscale:");
        infoGrayscale.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoGrayscale, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 2, -1, -1));

        toggleGrayscale.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        toggleGrayscale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleGrayscaleActionPerformed(evt);
            }
        });
        InfoBar.add(toggleGrayscale, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 2, -1, -1));

        infoDimensions1.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); 
        infoDimensions1.setText("Icon Dimensions~");
        infoDimensions1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        InfoBar.add(infoDimensions1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, -1, -1));

        displayDimensions.setEditable(false);
        displayDimensions.setBorder(null);
        displayDimensions.setFont(new java.awt.Font("Segoe UI", 0, 11)); 
        displayDimensions.setText("[xx,yy]");
        displayDimensions.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        InfoBar.add(displayDimensions, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 20, 50, -1));

        BitmapEditor.add(InfoBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 820, 700, 60));

        getContentPane().add(BitmapEditor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 690, 880));

        MenuBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jMenu1.setText("File");

        menuNewIcon.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuNewIcon.setText("New Icon");
        menuNewIcon.setEnabled(false);
        menuNewIcon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        menuNewIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        menuNewIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewIconActionPerformed(evt);
            }
        });
        jMenu1.add(menuNewIcon);

        menuSaveBitmap.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuSaveBitmap.setText("Save Bitmap");
        menuSaveBitmap.setEnabled(false);
        menuSaveBitmap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        menuSaveBitmap.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        menuSaveBitmap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveBitmapActionPerformed(evt);
            }
        });
        jMenu1.add(menuSaveBitmap);
        jMenu1.add(jSeparator1);

        menuExit.setText("Exit");
        menuExit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        menuExit.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        jMenu1.add(menuExit);

        MenuBar.add(jMenu1);

        jMenu2.setText("Edit");

        menuAdvanced.setText("Advanced Editing (Brush Fill)");
        menuAdvanced.setEnabled(false);
        menuAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAdvancedActionPerformed(evt);
            }
        });
        jMenu2.add(menuAdvanced);

        MenuBar.add(jMenu2);

        setJMenuBar(MenuBar);

        setSize(new java.awt.Dimension(688, 939));
        setLocationRelativeTo(null);
    }

    //Handles advanced edit checkbox toggle.
    private void toggleAdvancedActionPerformed(java.awt.event.ActionEvent evt) {
        if(advancedEdit == false){
            advancedEdit = true;
            menuAdvanced.setSelected(true);
            toggleAdvanced.setSelected(true);
        }
        else if(advancedEdit == true){
            advancedEdit = false;
            menuAdvanced.setSelected(false);
            toggleAdvanced.setSelected(false);
            advancedBrushOptions.setVisible(false);
            brushColorPicker.setVisible(true);
        }
    }

    // Swing variables declaration
    private javax.swing.JPanel BitmapEditor;
    private javax.swing.JPanel GridFrame;
    private javax.swing.JPanel InfoBar;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JPanel SizeOptions;
    private javax.swing.JPanel WorkNotice;
    private javax.swing.JPanel advancedBrushOptions;
    private javax.swing.JColorChooser brushColorPicker;
    private javax.swing.JSpinner brushHeight;
    private javax.swing.JSpinner brushWidth;
    private javax.swing.JButton confirmBrush;
    private javax.swing.JButton confirmSize;
    private javax.swing.JTextPane displayColor;
    private javax.swing.JTextPane displayDimensions;
    private javax.swing.JTextPane displayTarget;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JButton imgAdd;
    private javax.swing.JPanel imgDisplayContainer;
    private javax.swing.JPanel imgPreviewPanel;
    private javax.swing.JButton imgReject;
    private javax.swing.JButton importImage;
    private javax.swing.JLabel infoAdv;
    private javax.swing.JLabel infoAttached;
    private javax.swing.JLabel infoColor;
    private javax.swing.JLabel infoDimensions1;
    private javax.swing.JLabel infoGrayscale;
    private javax.swing.JLabel infoHorizontal;
    private javax.swing.JLabel infoOnPixel;
    private javax.swing.JLabel infoRandomColor;
    private javax.swing.JLabel infoVertical;
    private javax.swing.JSpinner inputHeight;
    private javax.swing.JSpinner inputNumPanes;
    private javax.swing.JSpinner inputWidth;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JCheckBoxMenuItem menuAdvanced;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuNewIcon;
    private javax.swing.JMenuItem menuSaveBitmap;
    private javax.swing.JLabel panesNumLabel;
    private javax.swing.JCheckBox toggleAdvanced;
    private javax.swing.JCheckBox toggleAttached;
    private javax.swing.JCheckBox toggleGrayscale;
    private javax.swing.JCheckBox toggleHorizontalInvert;
    private javax.swing.JCheckBox toggleRandomColors;
    private javax.swing.JCheckBox toggleVerticalInvert;
    private javax.swing.JLabel widthLabel;
}