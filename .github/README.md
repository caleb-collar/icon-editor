# Icon Bitmapper GUI

## Screenshots
### Dimension Selection
![dimensions](https://github.com/Mindstormer-0/icon-editor/blob/main/.github/dimensions_screen.png?raw=true)
### Main Editor
![editor](https://github.com/Mindstormer-0/icon-editor/blob/main/.github/editor_screen.png?raw=true)
### Advanced Dialog
![advanced](https://github.com/Mindstormer-0/icon-editor/blob/main/.github/advanced_screen.png?raw=true)
### Saving an Icon to 24-Bit Bitmap
![save](https://github.com/Mindstormer-0/icon-editor/blob/main/.github/save_screen.png?raw=true)

## Folder Structure

The workspace contains two main folders, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

## Features:

>grid- a 2D group of buttons. Click a button and that button will take on a color. The corresponding pixel in the bitmap will get the same RGB value. ✓

>color chooser- allows you to choose a color. Perhaps three sliders would be sufficient to allow you to select a value from 0 - 255 for red, green, and blue. There should be a preview of the selected color every time the sliders change. ✓

>last five colors- show the last five colors used. When someone clicks on a previously used color, adjust the color sliders to take on that color. ✓

>advanced checkbox- when this is checked, a selected button doesn't take on the color from the color chooser. Instead, it brings up an 'advanced' dialog. ✓

>advanced dialog part 1- ask for the number of rows and columns from the clicked button onward to fill with a color. The rows will always be from the selected button and rows beneath and the columns will always be from the selected button and to the right. ✓

>advanced dialog part 2- allow the user to open up **any** 24-bit bitmap and add the pixels inside the file to your bitmap. The top, left pixel of the selected bitmap will go in the selected button. Always show a preview of the selected file before adding it to the current bitmap. ✓

>create bitmap- clicking this button will show a file chooser to select where the file will be stored. Then allow the user to enter a name and save the bitmap file at that location. ✓

_v1.1.0 Supports observer and decorator patterns for multi-pane editing and input manipulation_

Note, preview features are required for this executable.

e.g.  ``` java --enable-preview -jar .\icon-editor.jar```


### Line Drawing: ✓

>The first feature allows the user to draw lines by moving the mouse over the buttons rather than clicking them. This functionality is enabled any time that the ```shift``` key is pressed on the keyboard. 

### Multiple Windows: ✓
>The second feature allows the user to display multiple bitmap editor windows at the same time.
>
>When one is changed all of the others will reflect the change more or less instantaneously. In order to accomplish this you will use the Observer pattern. You will now have to capture some data about each edit. For example, you need to know the row and column of the pixel that is being edited along with the color that the pixel will be set to.
>
>Class **EditQueue** will be informed about each edit from any of the bitmap editors. This class will be the Subject in the Observer pattern. When one bitmap editor is being edited it will pass information about the most recent edit to the EditQueue and it will notify all of the observers so that they can make the same edit. The bitmap editors will be the Observers and they must react to being notified of an edit.
>
>Include a way for the user to detach or re-attach a GUI from the Subject.

### GUI Design: ✓

>In each bitmap editor there will be controls to alter an edit received from the EditQueue. An edit can be inverted either vertically or horizontally and each edit's color may be turned into a shade of gray or a random color. Altering an edit's data will be accomplished with the Decorator pattern.
>
>The decorators will be called VerticalInvertBitmapEdit, HorizontalInvertBitmapEdit, RandomColorBitmapEdit, and GrayBitmapEdit.
>
>When each bitmap editor is notified of a new edit it will check to see if any of the decorators should be applied (using some swing widgets like JCheckBox and JRadioButton). If so, the bitmap editor will be wrapped in a decorator to add the desired functionality. Multiple decorators can be applied to the same edit.

### Decorators: ✓
 
>**VerticalInvertBitmapEdit** ✓
>
>Invert the row number that the edit took place on. If the dimensions of the bitmap being edited are 10 rows and 18 columns and the edit happens on row 3 column 4 then the decorated edit will take place on row 7 column 4 (7 is calculated by taking the height, 10, and subtracting the row number of the edit, 3).

>**HorizontalInvertBitmapEdit** ✓
>
>This is similar to the previous decorator except that the column will be updated. For example, in the previous example the new edit will take place on row 3 column 14 (14 is calculated by taking the width, 18, and subtracting the column number of the edit, 4).

>**RandomColorBitmapEdit** ✓
>
>This decorator will generate a random color for an edit.

>**GrayBitmapEdit** ✓
>
>This decorator will generate a shade of gray for an edit. All shades of gray have identical red, green, and blue values. Take the average of the edit's red, green, and blue and use the average to create a shade of gray that will be used for the edit.
