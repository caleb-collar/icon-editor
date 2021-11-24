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

## Given Specification:

>grid- a 2D group of buttons. Click a button and that button will take on a color. The corresponding pixel in the bitmap will get the same RGB value. ✓

>color chooser- allows you to choose a color. Perhaps three sliders would be sufficient to allow you to select a value from 0 - 255 for red, green, and blue. There should be a preview of the selected color every time the sliders change. ✓

>last five colors- show the last five colors used. When someone clicks on a previously used color, adjust the color sliders to take on that color. ✓

>advanced checkbox- when this is checked, a selected button doesn't take on the color from the color chooser. Instead, it brings up an 'advanced' dialog. ✓

>advanced dialog part 1- ask for the number of rows and columns from the clicked button onward to fill with a color. The rows will always be from the selected button and rows beneath and the columns will always be from the selected button and to the right. ✓

>advanced dialog part 2- allow the user to open up **any** 24-bit bitmap and add the pixels inside the file to your bitmap. The top, left pixel of the selected bitmap will go in the selected button. Always show a preview of the selected file before adding it to the current bitmap. ✓

>create bitmap- clicking this button will show a file chooser to select where the file will be stored. Then allow the user to enter a name and save the bitmap file at that location. ✓
