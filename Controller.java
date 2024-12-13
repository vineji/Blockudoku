package blocks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import blocks.BlockShapes.Sprite;
import blocks.BlockShapes.PixelLoc;
import blocks.BlockShapes.SpriteState;
import blocks.BlockShapes.Piece;


public class Controller extends MouseAdapter {
    GameView view;
    ModelInterface model;
    Palette palette;
    JFrame frame;
    Sprite selectedSprite = null;
    Piece ghostShape = null;
    String title = "Blocks Puzzle";
    boolean gameOver = false;
    int scoreBefore;
    int streak = 0;

    public Controller(GameView view, ModelInterface model, Palette palette, JFrame frame) {
        this.view = view;
        this.model = model;
        this.palette = palette;
        this.frame = frame;
        frame.setTitle(title);

        // force palette to do a layout
        palette.doLayout(view.margin, view.margin + ModelInterface.height * view.cellSize, view.paletteCellSize);
        System.out.println("Palette layout done: " + palette.sprites);
    }

    public void mousePressed(MouseEvent e) {

        System.out.println("Mouse pressed: " + e);
        PixelLoc loc = new PixelLoc(e.getX(), e.getY());
        selectedSprite = palette.getSprite(loc, view.paletteCellSize);
        if (selectedSprite == null) {
            return;
        }
        if (selectedSprite.state == SpriteState.PLACED){
            return;
        }
        selectedSprite.state = SpriteState.IN_PLAY;
        System.out.println("Selected sprite: " + selectedSprite);
        scoreBefore = model.getScore();
        view.repaint();

    }

    public void mouseDragged(MouseEvent e) {


        if (selectedSprite == null) {
            return;
        }
        if (selectedSprite.state == SpriteState.PLACED){
            return;
        }

        selectedSprite.px = e.getX();
        selectedSprite.py = e.getY();

        selectedSprite.state = SpriteState.IN_PLAY;




        ghostShape = selectedSprite.snapToGrid(view.margin, view.cellSize);
        view.ghostShape = ghostShape;
        view.poppableRegions = model.getPoppableRegions(ghostShape);


        view.repaint();


    }

    public void mouseReleased(MouseEvent e) {


        if (selectedSprite == null) {
            return;
        }
        if (selectedSprite.state == SpriteState.PLACED){
            return;
        }


        Piece selectedPiece = selectedSprite.snapToGrid(view.margin, view.cellSize);


        if (model.canPlace(selectedPiece)){
            // if the piece can be placed , place it in the grid
            model.place(selectedPiece);
            selectedSprite.state = SpriteState.PLACED;

        }
        else{
            // return the piece to the palette if it cannot be placed
            selectedSprite.state = SpriteState.IN_PALETTE;
            palette.doLayout(view.margin,view.margin +ModelInterface.height*view.cellSize, view.paletteCellSize);

        }

        view.ghostShape = null;
        view.poppableRegions = null;


        selectedSprite = null;
        palette.replenish(); // replenishes the palette if all 3 pieces have been placed



        gameOver = model.isGameOver(palette.getShapesToPlace());

        // update the title with the score and whether the game is over
        frame.setTitle(getTitle());
        view.repaint();

    }

    private String getTitle() {
        // make the title from the base title, score, and add GameOver if the game is over
        String title = this.title + " Score: " + model.getScore();
        if (gameOver) {
            title += " Game Over!";
            streak = 0;
        }
        else {
            if (model.getScore() > scoreBefore) {
                streak += 1;


            } else {
                streak = 0;
            }
            if (streak > 1){
                title += " Streak : " + streak;
            }
        }
        return title;
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //ModelInterface model = new ModelSet();
        ModelInterface model = new Model2dArray();
        Palette palette = new Palette();
        GameView view = new GameView(model,palette);
        Controller controller = new Controller(view, model, palette, frame);
        view.addMouseListener(controller);
        view.addMouseMotionListener(controller);
        frame.setLayout(new BorderLayout());


        // button to change between light mode and dark mode
        final boolean[] dark = {false};
        final String[] mode = {"dark"};
        JButton button = new JButton("change mode");
        button.addActionListener(e -> {
            if (dark[0] == false){
                mode[0] = "dark";
                dark[0] = true;
                view.setMode(mode[0]);
            }
            else{
                mode[0] = "light";
                dark[0] = false;
                view.setMode(mode[0]);
            }
            view.repaint();
        });

        frame.add(button,BorderLayout.CENTER);
        frame.add(view,BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
    }
}