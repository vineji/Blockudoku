# Blockudoku

Developed a Java-based Blockudoku game: A puzzle game combining Sudoku and block-fitting elements, where players fit shapes onto a grid to complete rows, columns, or subgrids and earn points.

OOP Techniques Used:
- Inheritance: Used to extend game logic, e.g., `Model2dArray` extends `State2dArray` for flexible grid implementations.
- Abstraction: Created `ModelInterface` to define core methods, allowing interchangeable grid models without affecting game logic.
- Encapsulation: Private attributes (e.g., grid, piece states) with getter/setter methods ensure data integrity and reduce dependencies.
- Polymorphism: Different classes like `Model2dArray` and `ModelSet` implement `ModelInterface`, enabling flexible interaction and easy extension of the game model.

Design Patterns Used:
- Observer Pattern: The `Controller` observes user inputs and updates the game state, notifying the `GameView` to update the UI.
- State Pattern: The `SpriteState` enum defines game piece states (e.g., `IN_PALETTE`, `IN_PLAY`, `PLACED`), changing behavior based on state.
- Singleton Pattern: `RegionHelper` is a singleton to manage grid regions, ensuring only one instance is used for consistency and memory efficiency.

Strategy Pattern: 
- `ModelInterface` allows the game to interact with different model implementations (e.g., `Model2dArray`, `ModelSet`), enabling easy model switching.
- `Model2dArray` uses 2D arrays to implement the game logic.
- `ModelSet` uses Sets to implement the game logic.

MVC Design Pattern:
- Model: Handles game state and logic (e.g., `Model2dArray`,`ModelSaet`).
- View: Displays the UI and updates the grid, pieces, and score (e.g., `GameView`).
- Controller: Manages user input, coordinating updates between the model and view (e.g., `Controller` class).
