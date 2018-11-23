import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import static java.lang.Math.*;

public class Game extends JPanel implements MouseListener{

    class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            //detects which button was pressed, and based on that performs the appropriate action
            if(e.getActionCommand().equals("Add Particles")){
                reds.add(new Ball(true, true));
                reds.add(new Ball(true, false));
                blues.add(new Ball(false, true));
                blues.add(new Ball(false, false));
            }
            else{
                reds.clear();
                blues.clear();
                reds.add(new Ball(true, true));
                reds.add(new Ball(true, false));
                blues.add(new Ball(false, true));
                blues.add(new Ball(false, false));
            }
        }
    }

    class Ball{
        int xCoord, yCoord;
        double xVelocity, yVelocity;
        int xLeft, xRight;          // the left & right bounds for the x coordinate
        boolean right;

        // first parameter refers to if ball is red, second determines if the ball spawns on right side
        public Ball(boolean red, boolean rightSide){

            //randomly decides the spawn point for new balls
            xCoord = rightSide?
                    ThreadLocalRandom.current().nextInt(610, 1090):
                    ThreadLocalRandom.current().nextInt(110, 590);
            yCoord = ThreadLocalRandom.current().nextInt(210, 590);

            //randomly decides if the balls will have a pos. or neg. velocity in the X direction
            boolean direction = new Random().nextBoolean();
            int dir = direction? 1: -1;
            xVelocity = red? 2 * dir: dir;  // if it is a red ball, it doubles the velocity

            //randomly decides if the balls will have a pos. or neg. in the Y direction
            direction = new Random().nextBoolean();
            dir = direction? 1: -1;
            yVelocity = red? 2 * dir: dir;  // if it is a red ball, it doubles the velocity

            right = rightSide;           // update's the balls side to allow for proper bounds checking

            // sets the X-coordinate bounds depending on side, so that the ball stays in the frame
            xLeft = right? 585: 100;
            xRight = right? 1085: 585;

        }

        // moves the ball, adjusting as appropriate if a x or y bound has been reached
        public void move(){
            right = xCoord > 600;

            if(right){              // if the ball is on the right half of the screen
                if(door) {          // if door == true, then the door is visible
                    xLeft = 600;
                    xRight = 1085;
                }
                else{
                    // determines if the ball is capable of passing through the door
                    if(yCoord > 343 && yCoord < 450) {
                        xLeft = 100;
                        xRight = 1085;
                    }
                    else{
                        xLeft = 600;
                        xRight = 1085;
                    }
                }
            }
            else{
                if(door) {
                    xLeft = 100;
                    xRight = 585;
                }
                else{
                    if(yCoord > 343 && yCoord < 450) {
                        xLeft = 100;
                        xRight = 1085;
                    }
                    else{
                        xLeft = 100;
                        xRight = 585;
                    }
                }
            }

            // switches x velocity to opposite direction if bound has been reached
            if(xCoord <= xLeft + 2)
                xVelocity = abs(xVelocity);
            else if(xCoord >= xRight - 2)
                xVelocity = abs(xVelocity)*(-1);

            // switches y velocity to opposite direction if bound has been reached
            if(yCoord <= 199)
                yVelocity = abs(yVelocity);
            else if(yCoord >= 585)
                yVelocity = abs(yVelocity)*(-1);

            xCoord += xVelocity;
            yCoord += yVelocity;
        }
    }

    private ArrayList<Ball> reds;
    private ArrayList<Ball> blues;
    boolean door;
    JButton reset;
    JButton addBalls;
    JLabel tLeft, tRight;       // JLabels for temperature on left and right sides
    ButtonHandler buttons;      // listens to which button was pressed
    Color myColor;              // custom color

    // the Game object will only be constructed once, as there is only 1 game running in main
    Game(){
        super();
        this.setVisible(true);
        this.setSize(800,400);
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        myColor = new Color(232, 215, 95);
        door = true;

        // one array for each ball color
        reds = new ArrayList<>();
        blues = new ArrayList<>();

        // by default, one red and one blue ball is added to each side of the door
        reds.add(new Ball(true, true));
        reds.add(new Ball(true, false));
        blues.add(new Ball(false, true));
        blues.add(new Ball(false, false));

        addMouseListener(this);
        buttons = new ButtonHandler();

        // creates/configures the RESET button

        reset = new JButton("RESET");
        reset.setIcon(new ImageIcon(System.getProperty("user.dir") + "/images/defaultReset.png"));
        reset.setPressedIcon(new ImageIcon(System.getProperty("user.dir") + "/images/onclickReset.png"));
        reset.setBorderPainted(false);
        reset.setForeground(Color.WHITE);
        reset.setBounds(260,160,206,43);
        reset.addActionListener(buttons);
        reset.setFocusPainted(false);
        reset.setContentAreaFilled(false);

        // creates/configures the Add Particles button
        addBalls = new JButton("Add Particles");
        addBalls.setIcon(new ImageIcon(System.getProperty("user.dir") + "/images/defaultAdd.png"));
        addBalls.setPressedIcon(new ImageIcon(System.getProperty("user.dir") + "/images/onclickAdd.png"));
        addBalls.setBorderPainted(false);
        addBalls.setForeground(Color.WHITE);
        addBalls.setBounds(720,160,220,43);
        addBalls.addActionListener(buttons);
        addBalls.setFocusPainted(false);
        addBalls.setContentAreaFilled(false);

        // temperature on left half
        tLeft = new JLabel();
        tLeft.setForeground(myColor);
        tLeft.setFont(tLeft.getFont().deriveFont(16.0f));
        tLeft.setBounds(235,600, 250,50);

        // temperature on right half
        tRight = new JLabel();
        tRight.setForeground(myColor);
        tRight.setFont(tRight.getFont().deriveFont(16.0f));
        tRight.setBounds(735, 600, 250, 50);

        ImageIcon fireIcon = new ImageIcon(System.getProperty("user.dir") + "/images/fire.gif");

        // creates a bunch of "fire" gifs at diff x-coordinates so they can span the display
        JLabel fire = new JLabel();
        fire.setIcon(fireIcon);
        fire.setBounds(0, 657, 258, 124);
        JLabel fire2 = new JLabel();
        fire2.setIcon(fireIcon);
        fire2.setBounds(256, 657, 258, 124);
        JLabel fire3 = new JLabel();
        fire3.setIcon(fireIcon);
        fire3.setBounds(512, 657, 258, 124);
        JLabel fire4 = new JLabel();
        fire4.setIcon(fireIcon);
        fire4.setBounds(768, 657, 258, 124);
        JLabel fire5 = new JLabel();
        fire5.setIcon(fireIcon);
        fire5.setBounds(1024, 657, 258, 124);

        // devil icon *yes, I know it is technically a demon, not a devil
        JLabel devil = new JLabel();
        devil.setIcon(new ImageIcon(System.getProperty("user.dir") + "/images/demon.png"));
        devil.setBounds(1012, 50, 150, 150);

        // first half of title
        JLabel maxwell = new JLabel();
        maxwell.setIcon(new ImageIcon(System.getProperty("user.dir") + "/images/maxwell.png"));
        maxwell.setBounds(100,-20,550,200);

        // second half of title
        JLabel demonText = new JLabel();
        demonText.setIcon(new ImageIcon(System.getProperty("user.dir") + "/images/demonText.png"));
        demonText.setBounds(670,-20,350,200);

        // adds all the GUI elements
        this.add(fire);
        this.add(fire2);
        this.add(fire3);
        this.add(fire4);
        this.add(fire5);
        this.add(devil);
        this.add(maxwell);
        this.add(demonText);

        this.add(reset);
        this.add(addBalls);
        this.add(tLeft);
        this.add(tRight);
    }

    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){
        door = !door;
    }
    public void mouseReleased(MouseEvent e){
        door = !door;
    }


    @Override
    public void paint(Graphics g1){
        super.paint(g1);

        // converting to Graphics2D to change stroke width
        Graphics2D g = (Graphics2D) g1;

        g.setStroke(new BasicStroke(2));
        g.setColor(myColor);    // change color to custom color

        g.drawRect(100,200,1000,400);
        g.drawLine(600,200,600,333);
        g.drawLine(600,467,600,600);

        if(door){
            g.drawLine(600,334,600,466);
        }

        g.setColor(Color.RED);

        // variable for computing temperature on each side
        double tempLeft  = 0, tempRight = 0, numLeft = 0, numRight = 0;

        // paints all the red balls and counts how many there are on each side
        // adds 8 to temp for each side since all red balls move at +/- 2 pixels/unit of time
        //      8 = (sqrt(2^2 + 2^2))^2, which is square of diagonal velocity
        for(Ball temp: reds){
            if(temp.right) {
                tempRight += 8;
                numRight++;
            }
            else {
                tempLeft += 8;
                numLeft++;
            }

            temp.move();
            g.fillOval(temp.xCoord, temp.yCoord, 15, 15);
        }

        // same process as above, but for blue particles
        g.setColor(Color.BLUE);
        for(Ball temp: blues){
            if(temp.right) {
                tempRight += 1;
                numRight++;
            }
            else {
                tempLeft += 1;
                numLeft++;
            }

            temp.move();
            g.fillOval(temp.xCoord, temp.yCoord, 15, 15);
        }

        // computes the temperatures and updates the corresponding JLabels
        tempRight /= numRight;
        tempLeft /= numLeft;

        // temperatures scaled by 21 to make the numbers more interesting
        tLeft.setText("TEMP: " + String.format("%.2f", 21*tempLeft) + " Demon Units");
        tRight.setText("TEMP: " + String.format("%.2f", 21*tempRight) + " Demon Units");
    }

    public static void main(String[] args) {

        JFrame screen = new JFrame();

        screen.setSize(1200,800);
        screen.setResizable(false);
        screen.setVisible(true);
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Game game = new Game();

        screen.getContentPane().add(game);

        /*
         * the next 10 lines determine how much the delay for the timer should be so that
         * blue particles will move approx (double to int precision loss) 3 cm/second if
         * the blue particles are moved by 1 pixel in each direction whenever repaint is called
         */
        double pixelsPerCM = Toolkit.getDefaultToolkit().getScreenResolution();
        pixelsPerCM /= 2.54;

        double blueV = 0, slow = 0;
        for(double i = 200; blueV != 1; --i){
            blueV = floor(pixelsPerCM*3/i);
            slow = i;
        }

        double delay = 1000/slow;
        int delayI = (int) ceil(delay);

        Timer timer = new Timer(delayI, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                game.repaint();
            }
        });

        timer.start();

    }

}