package blocks;

import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;
import blocks.BlockShapes.Cell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelSet extends StateSet implements ModelInterface {

    Set<Cell> locations = new HashSet<>();
    List<Shape> regions = new RegionHelper().allRegions();

    boolean streak = false;
    int nStreak = 0;

    // we need a constructor to initialise the regions
    public ModelSet() {
        super();
        initialiseLocations();
    }
    // method implementations below ...

    public int getScore() {
        return score;
    }

    private void initialiseLocations() {
        // having all grid locations in a set is in line with the set based approach
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                locations.add(new Cell(i, j));
            }
        }
    }

    @Override
    public boolean canPlace(Piece piece) {
        // can be placed if the cells are not occupied i.e. not in the occupiedCells set
        // though each one must be within the bounds of the grid
        // use a stream to check if all the cells are not occupied
        for (Cell c : piece.cells()){
            int i = c.x();
            int j = c.y();

            if ( i < 0 || i >= width  || j < 0 || j >= height ){
                return false;
            }
            if (occupiedCells.contains(c)){
                return false;
            }
        }

        return true;
    }

    @Override
    public void place(Piece piece) {


        occupiedCells.addAll(piece.cells());
        locations.addAll(piece.cells());


        int before = getOccupiedCells().size();

        for (Shape s : getPoppableRegions(piece)){
            remove(s);
        }
        int after = getOccupiedCells().size();

        if (before == after){
            streak = false;
            nStreak = 0;
        }
        else if (before != after){
            streak = true;
            nStreak += 1;
        }


        score += (before - after) * 10;

        if (streak && nStreak > 1){
            score += 100;
        }
    }

    @Override
    public void remove(Shape region) {
        for (Cell c : region){
            occupiedCells.remove(c);
            locations.remove(c);
        }
        // remove the cells from the occupiedCells set
    }

    @Override
    public boolean isComplete(Shape region) {
        // use a stream to check if all the cells in the region are occupied

        return region.stream().allMatch(cell -> locations.contains(cell));
    }

    @Override
    public boolean isGameOver(List<Shape> palettePieces) {
        for (Shape s : palettePieces){
            if (canPlaceAnywhere(s)){
                return false;
            }
        }
        // if any shape in the palette can be placed, the game is not over
        // use a helper function to check whether an indiviual shape can be placed anywhere
        // and
        return true;
    }

    public boolean canPlaceAnywhere(Shape shape) {


        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                Piece new_piece = new Piece(shape,new Cell(i,j));
                if (canPlace(new_piece)){
                    return true;
                }
            }
        }

        // check if the shape can be placed anywhere on the grid
        // by checking if it can be placed at any loc
        return false;
    }

    @Override
    public List<Shape> getPoppableRegions(Piece piece) {


        List<Shape> poppableRegions = new ArrayList<>();

        if (piece == null) {
            return null;
        }

        for (Shape s : regions){

            Set<Cell> tempOccupiedCells = getOccupiedCells();
            tempOccupiedCells.addAll(piece.cells());

            if (tempOccupiedCells.containsAll(s)){
                poppableRegions.add(s);
            }

        }

        // return the regions that would be popped if the piece is placed
        // to do this we need to iterate over the regions and check if the piece overlaps enough to complete it
        // i.e. we can make a new set of occupied cells and check if the region is complete
        // if it is complete, we add it to the list of regions to be popped

        return poppableRegions;

    }

    @Override
    public Set<Cell> getOccupiedCells() {
        return new HashSet<>(occupiedCells);
    }
}
