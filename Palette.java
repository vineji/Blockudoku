package blocks;

import java.util.ArrayList;
import java.util.List;

import blocks.BlockShapes.Shape;
import blocks.BlockShapes.ShapeSet;
import blocks.BlockShapes.SpriteState;
import blocks.BlockShapes.Sprite;
import blocks.BlockShapes.PixelLoc;


public class Palette {
    ArrayList<Shape> shapes = new ArrayList<>();
    List<Sprite> sprites;
    int nShapes = 3;
    int nReady;


    public Palette() {
        shapes.addAll(new ShapeSet().getShapes());
        sprites = new ArrayList<>();
        replenish();
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public ArrayList<Shape> getShapesToPlace() {

        ArrayList<Shape> palette_shapes = new ArrayList<>();

        for (Sprite sprite : getSprites()){
            if (sprite.state == SpriteState.IN_PALETTE){
                palette_shapes.add(sprite.shape);
            }
        }

        // return a list of shapes that are in the palette - could use streams to filter this
        return palette_shapes;
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

    // gets the sprite at mousePoint
    public Sprite getSprite(PixelLoc mousePoint, int cellSize) {

        for (Sprite s : getSprites()){
            if (s.contains(mousePoint, cellSize)){
                return s;
            }
        }
        return null;
    }

    // keeps count how many shapes are in the palette to see if it needs to be replenished
    private int nReadyPieces() {
        int count = 0;
        for (Sprite sprite : sprites) {
            if (sprite.state == SpriteState.IN_PALETTE || sprite.state == SpriteState.IN_PLAY) {
                count++;
            }
        }
        System.out.println("nReadyPieces: " + count);
        nReady = count;
        return count;
    }


    // This is used to layout the sprites in the paletee
    public void doLayout(int x0, int y0, int cellSize) {
        int currentX = x0;
        int currentY = y0 + 30;

        for (Sprite s : getSprites()){
            s.px = currentX;
            s.py = currentY;
            currentX += cellSize * 6;
        }
    }

    // clears the sprites arraylist and new random sprites from blockshapes
    public void replenish() {

        if (nReadyPieces() > 0) {
            return;
        }
        sprites.clear();
        for (int i = 0; i < nShapes; i++){
            int r = (int) (Math.random()*getShapes().size());
            Shape shape = getShapes().get(r);

            Sprite new_sprite = new Sprite(shape,0,0);
            new_sprite.state = SpriteState.IN_PALETTE;
            sprites.add(new_sprite);
        }
        doLayout(5, 5 + 40 * ModelInterface.height, 20);
        System.out.println("Replenished: " + sprites);
    }

    public static void main(String[] args) {
        Palette palette = new Palette();
        System.out.println(palette.shapes);
        System.out.println(palette.sprites);
        palette.doLayout(0, 0, 20);
        System.out.println(palette.sprites);
    }
}
