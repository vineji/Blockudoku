package blocks;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;
import blocks.BlockShapes.Cell;
import blocks.BlockShapes.ShapeSet;
import blocks.BlockShapes.SpriteState;
import blocks.BlockShapes.Sprite;
import blocks.BlockShapes.ColourSet;

// this is responsible for updating the user interface

public class GameView extends JComponent {
    ModelInterface model;
    Palette palette;


    int margin = 5;
    int shapeRegionHeight;
    int cellSize = 40;
    int paletteCellSize = 20;

    Piece ghostShape = null;
    List<Shape> poppableRegions = null;

    String mode = "light";

    public GameView(ModelInterface model, Palette palette) {
        this.model = model;
        this.palette = palette;
        this.shapeRegionHeight = (cellSize * ModelInterface.height / 2);
    }

    private void paintCells(Graphics g,ColourSet colourSet, int currentX, int currentY, int cellSize, int squareSize){
        // method to paint a cell

        Polygon topTriangle = new Polygon();
        topTriangle.addPoint(currentX, currentY); // Top-left corner
        topTriangle.addPoint(currentX + cellSize, currentY); // Top-right corner
        topTriangle.addPoint(currentX + cellSize / 2, currentY + cellSize / 2); // Center top
        g.setColor(colourSet.light());
        g.fillPolygon(topTriangle);

        // Draw the bottom triangle
        Polygon bottomTriangle = new Polygon();
        bottomTriangle.addPoint(currentX, currentY + cellSize); // Bottom-left corner
        bottomTriangle.addPoint(currentX + cellSize, currentY + cellSize); // Bottom-right corner
        bottomTriangle.addPoint(currentX + cellSize / 2, currentY + cellSize / 2); // Center bottom
        g.setColor(colourSet.dark());
        g.fillPolygon(bottomTriangle);

// Draw the left triangle
        Polygon leftTriangle = new Polygon();
        leftTriangle.addPoint(currentX, currentY); // Top-left corner
        leftTriangle.addPoint(currentX, currentY + cellSize); // Bottom-left corner
        leftTriangle.addPoint(currentX + cellSize / 2, currentY + cellSize / 2); // Center left
        g.setColor(colourSet.left());
        g.fillPolygon(leftTriangle);

// Draw the right triangle
        Polygon rightTriangle = new Polygon();
        rightTriangle.addPoint(currentX + cellSize, currentY); // Top-right corner
        rightTriangle.addPoint(currentX + cellSize, currentY + cellSize); // Bottom-right corner
        rightTriangle.addPoint(currentX + cellSize / 2, currentY + cellSize / 2); // Center right
        g.setColor(colourSet.right());
        g.fillPolygon(rightTriangle);
// Draw the center square
        g.setColor(colourSet.medium()); // Set the color for the center square
        g.fillRect(currentX +cellSize/4, currentY+cellSize/4, squareSize, squareSize); // Draw the square

    }



    private void paintShapePalette(Graphics g, int cellSize, String mode) {
        // paint a background colour
        // then get the list of current shapes from the palette
        // paint the shapes in the palette by calling paintCells
        Color light = new Color(32, 32, 189);  // Top triangle (shard of dark blue)
        Color medium = new Color(53, 53, 152); // Center square (shard of dark blue)
        Color dark = new Color(26, 26, 107);   // Bottom triangle (shard of dark blue)
        Color left = new Color(69, 69, 192);  // Left triangle (shard of dark blue)
        Color right = new Color(1, 1, 128);  // Right triangle (shard of dark blue)

        ColourSet darkBlue = new ColourSet(light,medium,dark,left,right);

        if (mode == "light"){
            g.setColor(Color.lightGray);
        }
        else{
            g.setColor(Color.GRAY);
        }
        g.fillRect(margin, margin + ModelInterface.height * cellSize, ModelInterface.width * cellSize, shapeRegionHeight);

        for (Sprite s : palette.getSprites()){
            if (s.state == SpriteState.IN_PALETTE) {
                for (Cell c : s.shape) {
                    int currentX = s.px + (c.x() * paletteCellSize);
                    int currentY = s.py + (c.y() * paletteCellSize);

                    paintCells(g, darkBlue, currentX,currentY, paletteCellSize,10);

                }
            }
        }

    }

    private void paintPoppableRegions(Graphics g) {
        // paints poppable regions in green if there is any
        int x0 = margin;
        int y0 = margin;

        Color light = new Color(147, 248, 80);  // Top triangle
        Color medium = new Color(147, 229, 128); // Center square
        Color dark = new Color(95, 208, 87);   // Bottom triangle
        Color left = new Color(161, 246, 128);  // Left triangle
        Color right = new Color(72, 196, 30);  // Right triangle

        ColourSet green = new ColourSet(light,medium,dark,left,right);

        if (poppableRegions == null){
            return;
        }
        g.setColor(new Color(61, 63, 63,100));
        if (model.canPlace(ghostShape)){
            for (Shape region :poppableRegions){
                for (Cell c : region){
                    int currentX = x0 + (c.x() * cellSize);
                    int currentY = y0 + (c.y() * cellSize);
                    paintCells(g,green,currentX,currentY,cellSize,20);
                }
            }
        }


    }

    private void paintGhostShape(Graphics g) {
        // paints the ghost shape in blue to user can see where there shape would be placed on the grid when dragging the shape
        // for every cell in the shape that overlaps an occupied cell in the grid it paint that cell red to indicate that it cannot be placed
        int x0 = margin;
        int y0 = margin;

        Color light = new Color(80, 202, 248);  // Top triangle
        Color medium = new Color(128, 202, 229); // Center square
        Color dark = new Color(77, 141, 196);   // Bottom triangle
        Color left = new Color(128, 211, 246);   // Left triangle
        Color right = new Color(30, 124, 196);

        ColourSet lightBlue = new ColourSet(light,medium,dark,left,right);

        Color lightRed = new Color(255, 0, 0);  // Top triangle
        Color mediumRed = new Color(220, 20, 60); // Center square
        Color darkRed = new Color(139, 0, 0);   // Bottom triangle
        Color leftRed = new Color(255, 99, 71);   // Left triangle
        Color rightRed = new Color(128, 0, 0);

        ColourSet red = new ColourSet(lightRed,mediumRed,darkRed,leftRed,rightRed);

        if (ghostShape == null){
            return;
        }

        for (Cell c : ghostShape.cells()){
            int currentX = x0 + (c.x() * cellSize);
            int currentY = y0 + (c.y() * cellSize);

            if (model.getOccupiedCells().contains(c)) { // paints ghost shape in red if cells overlap
                paintCells(g, red, currentX, currentY, cellSize, 20);
            }
            else{
                paintCells(g,lightBlue,currentX,currentY,cellSize,20);
            }
        }





        System.out.println("painting ghost shape: " + ghostShape);
    }

    public void setMode(String mode){
        this.mode = mode;
    }

    private void paintGrid(Graphics g, String mode) {
        // paints the grid where occupied cells are purple and empty cells are either white or black depending on the colour mode
        int x0 = margin;
        int y0 = margin;
        int width = ModelInterface.width * cellSize;
        int height = ModelInterface.height * cellSize;
        Set<Cell> occupiedCells = model.getOccupiedCells();
        g.setColor(Color.BLACK);
        g.drawRect(x0, y0, width, height);

        Color light = new Color(245, 245, 245);  // Top triangle
        Color medium = new Color(235, 235, 235); // Center square
        Color dark = new Color(200, 200, 200);   // Bottom triangle
        Color left = new Color(220, 220, 220);   // Left triangle
        Color right = new Color(180, 180, 180);  // Right triangle

        ColourSet white = new ColourSet(light,medium,dark,left,right);

        Color lightBlack = new Color(50, 50, 50);  // Top triangle
        Color mediumBlack = new Color(40, 40, 40); // Center square
        Color darkBlack = new Color(20, 20, 20);   // Bottom triangle
        Color leftBlack = new Color(72, 72, 72);   // Left triangle
        Color rightBlack = new Color(10, 10, 10);  // Right triangle

        ColourSet black = new ColourSet(lightBlack,mediumBlack,darkBlack,leftBlack,rightBlack);

        Color lightPurple = new Color(140, 128, 255, 255);  // Top triangle
        Color mediumPurple = new Color(135, 113, 218); // Center square
        Color darkPurple = new Color(98, 70, 180);   // Bottom triangle
        Color leftPurple = new Color(154, 142, 236);   // Left triangle
        Color rightPurple = new Color(90, 50, 175);   // Right triangle

        ColourSet purple = new ColourSet(lightPurple,mediumPurple,darkPurple,leftPurple,rightPurple);


        for (int x = 0; x < ModelInterface.width; x++) {
            for (int y = 0; y < ModelInterface.height; y++) {
                int currentX = x0 + (x * cellSize);
                int currentY = y0 + (y * cellSize);

                paintCells(g,white,currentX,currentY,cellSize,20);

                if (mode == "dark"){ // if mode is in dark mode paint empty cell in black
                    paintCells(g,black,currentX,currentY,cellSize,20);
                }

                if (occupiedCells.contains(new Cell(x,y))) { // paints occupied cell in purple
                    paintCells(g,purple,currentX,currentY,cellSize,20);

                }
            }
        }
    }



    private void paintMiniGrids(Graphics2D g, String mode) {
        // divides the 3x3 sub squares by painting lines between them so user can indicate the sub square region to pop
        int s = ModelInterface.subSize;
        g.setStroke(new BasicStroke(2));
        if (mode == "dark"){
            g.setColor(new Color(164, 164, 164));
        }
        else{
            g.setColor(Color.BLACK);
        }
        for (int x = 0; x < ModelInterface.width; x += s) {
            for (int y = 0; y < ModelInterface.height; y += s) {
                g.drawRect(margin + x * cellSize, margin + y * cellSize, s * cellSize, s * cellSize);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintGrid(g, mode);
        paintMiniGrids((Graphics2D) g, mode); // cosmetic
        paintGhostShape(g);
        paintPoppableRegions(g);
        paintShapePalette(g, cellSize,mode);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                ModelInterface.width * cellSize + 2 * margin,
                ModelInterface.height * cellSize + 2 * margin + shapeRegionHeight
        );
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clean Blocks");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ModelInterface model = new ModelSet();
        Shape shape = new ShapeSet().getShapes().get(0);
        Piece piece = new Piece(shape, new Cell(0, 0));
        Palette palette = new Palette();
        model.place(piece);
        frame.add(new GameView(model, palette));
        frame.pack();
        frame.setVisible(true);
    }
}