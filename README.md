# Attack on Titan: Defense - A Tower Defense Game

Welcome to "Attack on Titan: Defense"! This is a Tower Defense game built in Java using Swing for the graphical user interface. Defend the Wall from relentless waves of Titans!

## About The Game

In this game, your main objective is to protect humanity's base (the Wall) from oncoming Titans. You will strategically place various human defenders and stationary obstacles to stop the enemy.

### Key Features:

*   **Diverse Units:**
    *   **Humans:** Three types (Basic, Strong, Fast), each with a unique attack strategy and cost. Humans can be enhanced with "Armored" and "Veteran" decorators.
    *   **Titans:** Three types (Basic, Strong, Fast), each with distinct health, damage, and speed characteristics.
    *   **Obstacles:** Blocker Walls to halt Titan advances and Spike Traps to inflict damage.
*   **Economy:** Earn resources (coins) over time, by defeating Titans, and by successfully completing waves. Spend them to place and upgrade your defenses.
*   **Levels & Waves:** Battle through multiple levels, each consisting of several increasingly challenging waves of Titans.
*   **Difficulty Settings:** Choose from three difficulty levels (Easy, Medium, Hard), affecting Titan stats, starting resources, and your base's health.
*   **The Base:** Protect the `HumanBase`. If its health drops to zero, you lose the game.
*   **Design Patterns:** The project extensively uses various design patterns to ensure code flexibility and maintainability, including Singleton, Factory, Strategy, Observer, State, Decorator, and Template Method.

## How to Run

1.  Clone the repository:
    ```bash
    git clone https://github.com/Yernur-git/final-project-titans-vs-humans.git
    ```
2.  Open the project in your favorite Java IDE.
3.  Locate and run the main class: `Main`.

## Project Structure

The project is organized into the following main packages:

*   `entities`: Contains classes for all game entities (Humans, Titans, Base, Obstacles).
*   `game`: Core game logic, state management, resource handling, and difficulty settings.
*   `gameobjects`: Auxiliary game objects, such as `Projectile`.
*   `levels`: Manages game levels, enemy waves, and level data storage.
*   `patterns`: Implementations of the design patterns used (Decorator, Factory, Observer, State, Strategy).
*   `ui`: Classes responsible for the user interface (windows, panels, game rendering).
*   `utils`: Utility class for resource loading.

## Technologies Used

*   Java
*   Swing (for GUI)

## Authors

*   Amina
*   Merey
*   Yernur

---
