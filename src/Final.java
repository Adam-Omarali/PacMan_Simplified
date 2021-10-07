import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Final extends Application{
    // Set up variables to use for the layout dimensions
    static double menu_height = 500;
    static double menu_width = 500;
    static double game_width = 700;
    static double game_height = 700;

    //creates a manager for the menu scene
    static Pane menu_layout = new Pane();
    static Scene menu_scene = new Scene(menu_layout, menu_width, menu_height);

    //scene and pane for help screen
    static Pane help_layout = new Pane();
    static Scene help_scene = new Scene(help_layout, menu_width, menu_height);

    //game scene
    static Pane game_layout = new Pane();
    static Scene game_scene = new Scene(game_layout, game_width, game_height);

    //game over scene
    static Pane end_layout = new Pane();
    static Scene end_scene = new Scene(end_layout, menu_width, menu_height);

    //game loop
    static AnimationTimer gameLoop;

    //create global game variables
    static Font font = Font.font("Inter", FontWeight.BOLD, 20);
    //game bounds seperates the top half of screen where score is kept from the rest of the field
    static int game_bounds = 40;

    static double player_speed = 80/60.0; //pixels per frame
    static double ghost_speedX = 65/60.0;
    static double ghost_speedY = 65/60.0;
    static int direction = 0;
    // second direction to control pacman when he goes off screen
    static int direction2 = 0;
    static int score = 0;
    static boolean win = true;
    static int lives = 3;
    //reset board if live is lost
    static boolean stopped = false;
    //check if pacman is energized
    static boolean energized = false;
    //keep track of time for energizer
    static long then = 0;
    //see if pac_man can move for when its off screen
    static boolean can_move = true;



    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage mainWindow) {

        //start of buttons, text and labels for menus
        //button styles
        String styles =
        "-fx-text-fill: #ffffff;" +
        "-fx-border-color: #ffff00;" +
        "-fx-background-color: #000000;";

        //create black background for game
        Rectangle menu_background = new Rectangle(menu_width, menu_height);
        Rectangle game_background = new Rectangle(game_width, game_height);
        Rectangle help_background = new Rectangle(menu_width, menu_height);
        Rectangle end_background = new Rectangle(menu_width, menu_height);
        menu_layout.getChildren().add(menu_background);
        game_layout.getChildren().add(game_background);
        help_layout.getChildren().add(help_background);
        end_layout.getChildren().add(end_background);

        //click button to start game
        Button start_button = new Button("Start Game!");
        start_button.setFont(font);
        start_button.setStyle(styles);
        menu_layout.getChildren().add(start_button);
        start_button.minWidth(100);

        //button to see cmd and instructions
        Button help_button = new Button("Help");
        help_button.setStyle(styles);
        menu_layout.getChildren().add(help_button);
        help_button.minWidth(100);

        //back button on help scene
        Button back_button = new Button("Back");
        back_button.setStyle(styles);
        help_layout.getChildren().add(back_button);
        back_button.minWidth(100);


        //text for main screen
        Text intro = new Text("Let's Play Pacman");
        intro.setFont(font);
        intro.setFill(Color.WHITE);
        intro.setTextAlignment(TextAlignment.CENTER);
        Group intro_group = new Group(intro);   
        menu_layout.getChildren().add(intro_group);

        //pacman logo for main screen
        ImageView pacman_logo = new ImageView(new Image("Pacman.gif"));
        pacman_logo.setFitHeight(200);
        pacman_logo.setFitWidth(200);
        pacman_logo.relocate(menu_width / 2 - (pacman_logo.getFitHeight() / 2), menu_height / 2 - 250);
        menu_layout.getChildren().add(pacman_logo);
        mainWindow.setTitle("Pacman");

        //text for end screen
        Text end_text = new Text("Game Over");
        end_text.setFont(font);
        end_text.setFill(Color.WHITE);
        end_layout.getChildren().add(end_text);
        end_text.relocate(menu_width / 2 - 80, menu_height / 2-20);

        Label help_info = new Label("- Use the Up, Left, Right and Down Arrow Keys to Move Pacman\n\n" + 
                                    "- To start the game when on the game screen, just press any key \n\n" + 
                                    "- When the game pauses and you still have lives left, press\n" + 
                                    "the ENTER key or an arrow, and pacman will respawn at the center\n\n" + 
                                    "- Each Engergizer lasts for 10 seconds\n\n" +
                                    "- Every Ghost Consumed will be +50 points and cherry +100\n\n" + 
                                    "- Once a ghost is consumed, it will dissapear and radomly respawn when \nthe energizer effects wears off");
        help_info.setTextFill(Color.WHITE);
        // help_info.setTextAlignment(TextAlignment.CENTER);
        help_layout.getChildren().add(help_info);
        help_info.relocate(20, menu_height / 2 - 130);
        //end of buttons, text and labels for menus

        //start of players/objects for the game
        //pacman player
        ImageView pacman = new ImageView(new Image("Pacman.gif"));
        pacman.setFitHeight(35);
        pacman.setFitWidth(35);
        pacman.relocate(game_width / 2 - (pacman.getFitHeight() / 2), game_height / 2);
        double pacmanX = pacman.getTranslateX();
        double pacmanY = pacman.getTranslateY();
        game_layout.getChildren().add(pacman);

        //spacing the dots across the screen, 17 per row, for 11 rows
        Circle dots[] = new Circle[17*11];
        //keep track of the dots consumed to check for win condition (if they are all consumed) and open space to place new objects
        int game_state[] = new int[17*11];
        int positionX = 15;
        int positionY = 50;
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new Circle(5);
            dots[i].relocate(positionX, positionY);
            dots[i].setFill(Color.WHITE);
            if (i % 46 == 0) {
                dots[i].setFill(Color.YELLOW);
            }
            positionX += 40;
            if(positionX > game_width - 20){
                positionX = 15;
                positionY += 60;
            }
            game_state[i] = 0;
            game_layout.getChildren().add(dots[i]);
        }

        //array for each of the ghosts
        //blue ghosts
        ImageView blue_ghost[] = new ImageView[4];
        //create a place to store the data of each individual ghost
        double[][] blue_ghost_data = new double[blue_ghost.length][4];
        for (int i = 0; i < blue_ghost.length; i++) {
            blue_ghost[i] = new ImageView(new Image("blue sheet.png"));
            blue_ghost[i].setFitHeight(20);
            blue_ghost[i].setFitWidth(20);
            blue_ghost[i].relocate(Math.random() * (game_width -10 - 10 + 1) + 10, Math.random() * ((game_height - game_height / 2 - game_bounds) - (0 + game_bounds) + 1) + game_bounds);
            game_layout.getChildren().add(blue_ghost[i]);

            //store the ghostX and Y speed individually
            blue_ghost_data[i][0] = ghost_speedX; 
            blue_ghost_data[i][1] = ghost_speedY;
            blue_ghost_data[i][2] = blue_ghost[i].getTranslateX();
            blue_ghost_data[i][3] = blue_ghost[i].getTranslateY();
        }

        //pink ghosts
        ImageView pink_ghost[] = new ImageView[5];
        //create a place to store the data of each individual ghost
        double[][] pink_ghost_data = new double[pink_ghost.length][4];
        for (int i = 0; i < pink_ghost.length; i++) {
            pink_ghost[i] = new ImageView(new Image("pink ghost.png"));
            pink_ghost[i].setFitHeight(20);
            pink_ghost[i].setFitWidth(20);
            pink_ghost[i].relocate(Math.random() * (game_width - 10 - 10 + 1) + 10, Math.random() * (((game_height / 2) - game_bounds) - (0 + game_bounds) + 1) + game_bounds);
            game_layout.getChildren().add(pink_ghost[i]);

            //store x and y spped individually
            pink_ghost_data[i][0] = ghost_speedX;
            pink_ghost_data[i][1] = ghost_speedY;
            //store starting points to reset after lives are lost
            pink_ghost_data[i][2] = pink_ghost[i].getTranslateX();
            pink_ghost_data[i][3] = pink_ghost[i].getTranslateY();
        }

        //yellow ghosts
        ImageView yellow_ghost[] = new ImageView[2];
        double[][] yellow_ghost_data = new double[yellow_ghost.length][3];
        for (int i = 0; i < yellow_ghost.length; i++) {
            yellow_ghost[i] = new ImageView(new Image("yellow ghost.png"));
            yellow_ghost[i].setFitHeight(20);
            yellow_ghost[i].setFitWidth(20);
            // set it outside the game plane
            game_layout.getChildren().add(yellow_ghost[i]);

            //set a random y value for each ghost to move by (speed)
            yellow_ghost_data[i][0] = Math.random() * (13 - 0 + 1) + 0;

        }
        yellow_ghost[0].relocate(-100, 50);
        yellow_ghost[1].relocate(game_width + 100, 50);

        //cherry
        ImageView cherry = new ImageView(new Image("Cherry.png"));
        cherry.setFitHeight(20);
        cherry.setFitWidth(15);
        game_layout.getChildren().add(cherry);
        cherry.relocate(-30, -10);

        //labels to display the score
        Label title_score = new Label("Score: ");
        title_score.setTextFill(Color.WHITE);
        game_layout.getChildren().add(title_score);

        Label score_label = new Label();
        score_label.setTextFill(Color.WHITE);
        game_layout.getChildren().add(score_label);
        score_label.relocate(game_bounds, 0);

        //labels to display the lives
        Label title_lives = new Label("Lives: ");
        title_lives.setTextFill(Color.WHITE);
        game_layout.getChildren().add(title_lives);
        title_lives.relocate(game_width - 60, 0);

        Label lives_label = new Label();
        lives_label.setTextFill(Color.WHITE);
        game_layout.getChildren().add(lives_label);
        lives_label.relocate(game_width - 25, 0);
        


        gameLoop = new AnimationTimer(){
            @Override
            public void handle(long nano_time) {
                win = true;

                if (score % 200 == 0 && score != 0) {
                    for (int i = 0; i < blue_ghost_data.length; i++) {
                        blue_ghost_data[i][0] += 0.5/60.0;
                        blue_ghost_data[i][1] += 0.5/60.0;
                    }
                    for (int i = 0; i < pink_ghost_data.length; i++) {
                        pink_ghost_data[i][0] += 0.5/60.0;
                        pink_ghost_data[i][1] += 0.5/60.0;
                    }
                    cherry.setTranslateX(game_width / 2 - (cherry.getFitHeight() / 2));
                    cherry.setTranslateY(game_height / 2);
                }



                //if pacman hits the reaches the end of the game screen, adjust accordingly
                if(pacman.getBoundsInParent().getMinX() <= 0) {
                    direction2 = 4;
                    can_move = false;
                    if (direction == 4)
                        pacman.setTranslateX(0-pacmanX + (game_width - pacmanX - 10) - 210);
                }
                else if(pacman.getBoundsInParent().getMaxX() >= game_width) {
                    direction2 = 3;
                    can_move = false;
                    if (direction == 3)
                        pacman.setTranslateX(0-pacmanX - (game_width - pacmanX - 10) + 210);
                }
                else if(pacman.getBoundsInParent().getMinY() <= 0) {
                    direction2 = 1;
                    can_move = false;
                    if (direction == 1)
                        pacman.setTranslateY(0-pacmanY + (game_height - pacmanY - 10) - 210);
                }
                else if(pacman.getBoundsInParent().getMaxY() >= game_height) {
                    direction2 = 2;
                    can_move = false;
                    if (direction == 2)
                        pacman.setTranslateY(0 - game_height + pacmanY + 210);
                }
                else {
                    can_move = true;
                }
                

                
                //keep track of score and lives
                String string_score = Integer.toString(score);
                score_label.setText(string_score);
                String string_lives = Integer.toString(lives);
                lives_label.setText(string_lives);
                Bounds pacman_bounds = pacman.getBoundsInParent();
                //create a smaller bounding box for pacman so he has to be closer to the balls inorder to consume them
                int offset = 10;
                BoundingBox box = new BoundingBox(pacman_bounds.getMinX()+offset, pacman_bounds.getMinY()+offset, pacman_bounds.getWidth()-offset, pacman_bounds.getHeight()-offset);
                for (int i = 0; i < dots.length; i++) {
                    //Check for collision detection between pacman and the balls
                    Bounds dots_bound = dots[i].getBoundsInParent();
                    if (box.intersects(dots_bound)) {
                        //remove the circle from the game board
                        dots[i].setCenterX(-game_width);
                        if (i % 46 == 0) {
                            energized = true;
                            then = nano_time/1000000000;
                            for (int j = 0; j < blue_ghost.length; j++) {
                                blue_ghost[j].setImage(new Image("vulnerable ghost.png"));
                            }
                            for (int j = 0; j < yellow_ghost.length; j++) {
                                yellow_ghost[j].setImage(new Image("vulnerable ghost.png"));
                            }
                            for (int j = 0; j < pink_ghost.length; j++) {
                                pink_ghost[j].setImage(new Image("vulnerable ghost.png"));
                            }
                        }
                        else {
                            score += 5;
                            // now we know a circle does not exist there
                        }
                        game_state[i] = 1;
                    }
                }
                
                // true after 10 seconds
                if (nano_time / 1000000000 > then + 10 && then != 0) {
                    energized = false;
                    then = 0;
                    for (int i = 0; i < blue_ghost.length; i++) {
                        //change image to regular ghost and reset position
                        blue_ghost[i].setImage(new Image("blue sheet.png"));
                        blue_ghost[i].setTranslateX(0-blue_ghost_data[i][2]);
                        blue_ghost[i].setTranslateY(0-blue_ghost_data[i][3]);
                    }
                    for (int i = 0; i < yellow_ghost.length; i++) {
                        yellow_ghost[i].setImage(new Image("yellow ghost.png"));
                    }
                    for (int i = 0; i < pink_ghost.length; i++) {
                        pink_ghost[i].setImage(new Image("pink ghost.png"));
                        pink_ghost[i].setTranslateX(0-pink_ghost_data[i][2]);
                        pink_ghost[i].setTranslateY(0-pink_ghost_data[i][3]);
                    }
                }

                if(box.intersects(cherry.getBoundsInParent())) {
                    score += 100;
                    cherry.setTranslateX(-game_width);
                }
                
                //movement for blue ghosts: they will bounce of walls and move randomly
                // for each of the blue ghosts
                for (int i = 0; i < blue_ghost.length; i++) {
                    Bounds blue_ghost_bounds = blue_ghost[i].getBoundsInParent();

                    //check if they hit the left, right, top or bottom ends
                    if (blue_ghost_bounds.getMaxX() <= 10 || blue_ghost_bounds.getMinX() >= game_width - 20) {
                        // if so, change that's ghost individual translate x to be the opposite
                        blue_ghost_data[i][0] = -blue_ghost_data[i][0];
                    }
                    if (blue_ghost_bounds.getMaxY() <= 0 || blue_ghost_bounds.getMinY() >= game_height - 20) {
                        blue_ghost_data[i][1] = -blue_ghost_data[i][1];
                    }

                    //move the ghost inidvidually and differently using direction and random offset
                    switch(i) {
                        case 0:
                            blue_ghost[i].setTranslateY(blue_ghost[i].getTranslateY() + blue_ghost_data[i][1]); 
                            blue_ghost[i].setTranslateX(blue_ghost[i].getTranslateX() + blue_ghost_data[i][0]); 
                            break;
                        case 1:
                            blue_ghost[i].setTranslateY(blue_ghost[i].getTranslateY() + blue_ghost_data[i][1]); 
                            blue_ghost[i].setTranslateX(blue_ghost[i].getTranslateX() - blue_ghost_data[i][0]); 
                            break;
                        case 2:
                            blue_ghost[i].setTranslateY(blue_ghost[i].getTranslateY() - blue_ghost_data[i][1]); 
                            blue_ghost[i].setTranslateX(blue_ghost[i].getTranslateX() - blue_ghost_data[i][0]); 
                            break;
                        case 3:
                            blue_ghost[i].setTranslateY(blue_ghost[i].getTranslateY() - blue_ghost_data[i][1]); 
                            blue_ghost[i].setTranslateX(blue_ghost[i].getTranslateX() + blue_ghost_data[i][0]); 
                            break;
                    }

                    //check if the ghost and pacman intersect
                    if (box.intersects(blue_ghost_bounds)) {
                        if (energized == false) {
                            lives -= 1;
                            stopped = true;
                            gameLoop.stop();
                        }
                        else {
                            score += 50;
                            blue_ghost[i].setTranslateY(blue_ghost[i].getTranslateY() - game_height);
                        }
                        
                    }
                }

                //movement for pink ghosts: they will bounce of walls and only move and up, down, left or right, no diagnols.
                // for each of the pink ghosts
                for (int i = 0; i < pink_ghost.length; i++) {
                    Bounds pink_ghost_bounds = pink_ghost[i].getBoundsInParent();
                    //check if they hit the left, right, top or bottom ends
                    if (pink_ghost_bounds.getMaxX() <= 10 || pink_ghost_bounds.getMinX() >= game_width - 20) {
                        // if so, change that's ghost individual translate x to be the opposite
                        pink_ghost_data[i][0] = -pink_ghost_data[i][0];
                    }
                    if (pink_ghost_bounds.getMaxY() <= 0 || pink_ghost_bounds.getMinY() >= game_height - 20) {
                        pink_ghost_data[i][1] = -pink_ghost_data[i][1];
                    }

                    // check if a new point total has been reached and then switch direction 
                    if (score % 30 == 0) {
                        //move the ghost inidvidually up and down
                        switch(i) {
                            case 0:
                            case 1:
                                pink_ghost[i].setTranslateY(pink_ghost[i].getTranslateY() + pink_ghost_data[i][1]);
                                break;
                            default:
                                pink_ghost[i].setTranslateY(pink_ghost[i].getTranslateY() - pink_ghost_data[i][1]);
                        }
                    }
                    else {
                        //move the ghost inidvidually left and right
                        switch(i) {
                            case 0:
                            case 1:
                                pink_ghost[i].setTranslateX(pink_ghost[i].getTranslateX() - pink_ghost_data[i][0]);
                                break;
                            default:
                                pink_ghost[i].setTranslateX(pink_ghost[i].getTranslateX() + pink_ghost_data[i][0]);
                        }
                    }


                    //check if the ghost and pacman intersect
                    if (box.intersects(pink_ghost_bounds)) {
                        if (energized == false) {
                            lives -= 1;
                            stopped = true;
                            gameLoop.stop();
                        }
                        else {
                            score += 50;
                            pink_ghost[i].setTranslateY(pink_ghost[i].getTranslateY() - game_height);
                        }
                        
                    }
                }

                //variables that will change based on time
                if(nano_time % 400 == 0) {
                    for (int i = 0; i < yellow_ghost_data.length; i++) {
                        yellow_ghost_data[i][0] = Math.random() * (13 - 0 + 1) + 0;
                    }
                }

                //movement for the pink ghosts
                for (int i = 0; i < yellow_ghost.length; i++) {
                    Bounds yellow_ghost_bounds = yellow_ghost[i].getBoundsInParent();

                    switch(i) {
                        case 0:
                            yellow_ghost[i].setTranslateX(yellow_ghost[i].getTranslateX() + 5);
                            yellow_ghost[i].setTranslateY(yellow_ghost[i].getTranslateY() + yellow_ghost_data[i][0]);

                            if (yellow_ghost[i].getTranslateX() > game_width + 50) {
                                yellow_ghost[i].setTranslateX(0-yellow_ghost[i].getTranslateX());
                                yellow_ghost[i].setTranslateY(0-yellow_ghost[i].getTranslateY());
                            }
                            break;
                        case 1:
                            yellow_ghost[i].setTranslateX(yellow_ghost[i].getTranslateX() - 5);
                            yellow_ghost[i].setTranslateY(yellow_ghost[i].getTranslateY() + yellow_ghost_data[i][0]);

                            if (yellow_ghost[i].getTranslateX() < 0 - 100 - game_width) {
                                yellow_ghost[i].setTranslateX(0-yellow_ghost[i].getTranslateX());
                                yellow_ghost[i].setTranslateY(0-yellow_ghost[i].getTranslateY());
                            }
                            break;
                    }

                    //check if the ghost and pacman intersect
                    if (box.intersects(yellow_ghost_bounds)) {
                        if (energized == false) {
                            lives -= 1;
                            stopped = true;
                            gameLoop.stop();
                        }
                        else {
                            score += 50;
                            yellow_ghost[i].setTranslateY(yellow_ghost[i].getTranslateY() - game_height);
                        }
                        
                    }
                }

                // if all the dots have been consumed, end the game
                for (int i = 0; i < game_state.length; i++) {
                    if (game_state[i] == 0) {
                        win = false;
                        break;
                    }
                }
                if (win == true) {
                    end_text.setText("Game Over: Win");
                    mainWindow.setScene(end_scene);
                    gameLoop.stop();
                }
                

                //rotate pacman's image and move him according to the player speed from his current position
                if (can_move == true) {
                    switch(direction){
                        case 1:
                            pacman.setTranslateY(pacman.getTranslateY() - player_speed);
                            pacman.setRotate(90);
                            break;
                        case 2:
                            pacman.setTranslateY(pacman.getTranslateY() + player_speed);
                            pacman.setRotate(-90);
                            break;
                        case 3:
                            pacman.setTranslateX(pacman.getTranslateX() + player_speed);
                            pacman.setRotate(180);
                            break;
                        case 4:
                            pacman.setTranslateX(pacman.getTranslateX() - player_speed);
                            pacman.setRotate(0);
                            break;
                    }
                }
                else {
                    switch(direction2){
                        case 1:
                            pacman.setTranslateY(pacman.getTranslateY() + player_speed);
                            pacman.setRotate(-90);
                            break;
                        case 2:
                            pacman.setTranslateY(pacman.getTranslateY() - player_speed);
                            pacman.setRotate(90);
                            break;
                        case 3:
                            pacman.setTranslateX(pacman.getTranslateX() - player_speed);
                            pacman.setRotate(0);
                            break;
                        case 4:
                            pacman.setTranslateX(pacman.getTranslateX() + player_speed);
                            pacman.setRotate(180);
                            break;
                    }
                }
            }
        };


        // check for key pressed and change direction accordingly
        game_scene.setOnKeyPressed((event) -> {


            if (stopped == true) {
                pacman.setTranslateX(0 - pacmanX);
                pacman.setTranslateY(0 - pacmanY);
                for (int i = 0; i < yellow_ghost.length; i++) {
                    yellow_ghost[i].setTranslateX(0-yellow_ghost[i].getTranslateX());
                    yellow_ghost[i].setTranslateY(0-yellow_ghost[i].getTranslateY());
                }
                for (int i = 0; i < pink_ghost.length; i++){
                    pink_ghost[i].setTranslateX(0-pink_ghost_data[i][2]);
                    pink_ghost[i].setTranslateY(0-pink_ghost_data[i][3]);
                }
                for (int i = 0; i < blue_ghost.length; i++){
                    blue_ghost[i].setTranslateX(0-blue_ghost_data[i][2]);
                    blue_ghost[i].setTranslateY(0-blue_ghost_data[i][3]);
                }
                stopped = false;
                gameLoop.start();
            }
            if (lives == 0) {
                end_text.setText("Game Over: Loss");
                mainWindow.setScene(end_scene);
                gameLoop.stop();
            }
            else {
                gameLoop.start();
                switch(event.getCode()){
                    case UP:
                        direction = 1;
                        break;
                    case DOWN:  
                        direction = 2;
                        break;
                    case RIGHT:
                        direction = 3;
                        break;
                    case LEFT:
                        direction = 4;
                        break;
                }
            }
        });


        //click event for the button
        start_button.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            mainWindow.setScene(game_scene);
        });

        help_button.setOnMouseClicked((event) -> {
            mainWindow.setScene(help_scene);
        });
        back_button.setOnMouseClicked((event) ->{
            mainWindow.setScene(menu_scene);
        });

        mainWindow.setScene(menu_scene);
        mainWindow.show();

        //position menu buttons
        intro_group.relocate(menu_width / 2 - (start_button.getWidth() / 2) - 15, menu_height / 2);
        help_button.relocate(menu_width / 2 - (help_button.getWidth() / 2), menu_height - 75);
        start_button.relocate(menu_width / 2 - (start_button.getWidth() / 2), menu_height - 150);
        back_button.relocate(20, menu_height / 2 + 100);
    }
    
}
