package blocks;

// this handles the game logic

import blocks.BlockShapes.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model2dArray extends State2dArray implements ModelInterface {
    List<Shape> regions = new RegionHelper().allRegions();

    boolean streak = false;
    int nStreak = 0;

    public Model2dArray() {
        // in this adaptation the grid is represented by true and false values
        // false = empty
        // true = occupied
        grid = new boolean[width][height];

        // initially the grid will be empty
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = false;
            }
        }
    }

    public int getScore() {
        return score;
    }




    // checks if it is possible to place a piece at this location
    public boolean canPlace(Piece piece) {
        for (Cell c : piece.cells()){
            int i = c.x();
            int j = c.y();

            if ( i < 0 || i >= width  || j < 0 || j >= height ){
                return false;
            }
            if (grid[i][j]){
                return false;
            }
        }

        return true;
    }

    // places the piece in the grid by assigning the corresponding cells in the grid to true
    @Override
    public void place(Piece piece) {


        for (Cell c : piece.cells()){
            int i = c.x();
            int j = c.y();

            grid[i][j] = true;
        }

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


        score += (before - after) * 10; // finds difference between size of occupied cell and multiplies by 10 to get a score
        // if a region is popped the before will be grater than the after

        // bonus point of 100 is incremented to the score for every successive pop after 1.
        // e.g. 2 successive pops = 100 bonus points, 3 successive pops = 200 bonus points, etc
        if (streak && nStreak > 1){
            score += 100;
        }


    }


    // pops the occupied cells in the grid by removing them from occupied cells and assigning the corresponding grid to false
    @Override
    public void remove(Shape region) {

        for (Cell c : region){
            int i = c.x();
            int j = c.y();
            getOccupiedCells().remove(c);
            grid[i][j] = false;
        }
    }

    // checks if a region is complete (column, row or subsquare)
    public boolean isComplete(Shape region) {
        for (Cell c : region){
            int i = c.x();
            int j = c.y();

            if (grid[i][j] == false){
                return false;
            }
        }
        // check if the shape is complete, i.e. all cells are occupied
        return true;
    }

    private boolean wouldBeComplete(Shape region, List<Cell> toAdd) {

        for (Cell c : region){
            int i = c.x();
            int j = c.y();

            if (!grid[i][j] && !toAdd.contains(c)){
                return false;
            }
        }

        return true;
    }

    // check for every piece in the palette if it can be placed anywhere
    // if none can be placed the game will be over
    @Override
    public boolean isGameOver(List<Shape> palettePieces) {

        // if any shape in the palette can be placed, the game is not over

        for (Shape s : palettePieces){
            if (canPlaceAnywhere(s)){
                return false;
            }
        }
        return true;
    }

    // check if a piece can be placed anywhere by going through every cell in the grid
    public boolean canPlaceAnywhere(Shape shape) {
        // check if the shape can be placed anywhere on the grid
        // by checking if it can be placed at any loc

        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                Piece new_piece = new Piece(shape,new Cell(i,j));
                if (canPlace(new_piece)){
                    return true;
                }
            }
        }

        return false;
    }


    // it returns the poppable regions if the piece was placed
    @Override
    public List<Shape> getPoppableRegions(Piece piece) {

        if (piece == null){
            return null;
        }

        // iterate over the regions
        List<Shape> poppableRegions = new ArrayList<>();

        for (Shape region : regions){
            if (wouldBeComplete(region,piece.cells())){
                poppableRegions.add(region);
            }

        }
        return poppableRegions;
    }

    //get occupied cells by filtering if the corresponding grid location of the cell return true.
    @Override
    public Set<Cell> getOccupiedCells() {

        Set<Cell> occupiedCells = new HashSet<>();
        for(int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                if (grid[i][j] == true){
                    occupiedCells.add(new Cell(i,j));
                }
            }
        }
        return occupiedCells;
    }
}

