
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author RobinBergseth
 */
public class Pong extends javax.swing.JFrame {
    
    private final PictureWriter writer;
    private final ArrayConverter converter;
    private final int[][] pongMatrix;
    private int player1pos = 0;
    private int player2pos = 0;
    private final int RACKET_SIZE = 15;
    private int ballPosX = 40;
    private int ballPosY = 20;
    private byte[] writeArray;
    private boolean game = false;
    private boolean timerStarted = false;
    private int score1 = 0;
    private int score2 = 0;
    private Timer updateTimer;
    private int dirX = 1;
    private int dirY = 1;
    private int speed = 1;
    
    /**
     * Constructor
     * Creates new form Pong
     * @param pw Takes a PictureWriter in
     */
    public Pong(PictureWriter pw) {
        KeyListener listener = new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
                int code = ke.getKeyCode();
                if(code == KeyEvent.VK_ESCAPE){
                    pause();
                }
                if(code == KeyEvent.VK_SPACE){
                    play();
                }
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                int c = ke.getKeyCode();
                if(c == KeyEvent.VK_W){
                    if(player1pos != 0){
                        player1pos --;
                    }
                }
                if(c == KeyEvent.VK_S){
                    if((player1pos + RACKET_SIZE) != 40){
                        player1pos ++;
                    }
                }
                if(c == KeyEvent.VK_UP){
                    if(player2pos != 0){
                        player2pos --;
                    }
                }
                if(c == KeyEvent.VK_DOWN){
                    if((player2pos + RACKET_SIZE) != 40){
                        player2pos ++;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                
            }
        };
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we){
                try{
                    if(timerStarted){
                        updateTimer.cancel();
                    }
                }
                catch(Exception evt){
                    System.err.println(evt.toString());
                }
            }
        });
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runGame();
            }
        }, 100, 100);
        this.writer = pw;
        this.converter = new ArrayConverter();
        pongMatrix = new int[80][40];
        clearMatrix();
        addKeyListener(listener);
        initComponents();
        this.setVisible(true);
        this.setFocusable(true);
    }
    
    /**
     * Updates the position of the ball and rackets
     */
    public void update(){
        clearMatrix();
        updateBallPos();
        updateRacktPos();
        writeArray = converter.getWriteArray(pongMatrix);
        writer.setWriteArray(writeArray, false);
    }
    
    /**
     * Updates the position of the rackets
     */
    public void updateRacktPos(){
        for(int pos = player1pos; pos < player1pos + RACKET_SIZE; pos++){
            pongMatrix[0][pos] = 0xFFFFFF;
        }
        for(int pos = player2pos; pos < player2pos + RACKET_SIZE; pos++){
            pongMatrix[79][pos] = 0xFFFFFF;
        }
    }

    /**
     * Updates the positions of the ball
     */
    public void updateBallPos(){
        pongMatrix[ballPosX][ballPosY] = 0xFFFFFF;
        pongMatrix[ballPosX][ballPosY + 1] = 0xFFFFFF;
        pongMatrix[ballPosX + 1][ballPosY] = 0xFFFFFF;
        pongMatrix[ballPosX + 1][ballPosY + 1] = 0xFFFFFF;
        
    }
    
    /**
     * Clears the matrix of all content
     */
    public void clearMatrix(){
        for(int x = 0; x < pongMatrix.length; x++){
            for(int y = 0; y < pongMatrix[0].length; y++){
                pongMatrix[x][y] = 0;
            }
        }
    }
    
    /**
     * Moves the ball and makes it bounce off the walls
     */
    public void moveBall(){
        
        if(ballPosX == 78 || ballPosX == 0){
            dirX *= -1; 
        }
        if(ballPosY == 38 || ballPosY == 0){
            dirY *= -1;
        }
        ballPosX += dirX;
        ballPosY += dirY;
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        player1Score = new javax.swing.JLabel();
        player2Score = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        startButton.setText("Start");
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startButtonMouseClicked(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                resetButtonKeyPressed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Player 1");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Player 2");

        player1Score.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        player1Score.setText("0");

        player2Score.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        player2Score.setText("0");

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(240, 240, 240));
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("W:\t\tPlayer 1 up\nS:\t\tPlayer 1 down\nArrow up:\tPlayer 2 up\nArrow down:\tPlayer 2 down\nESC:\t\tPause\nSpace:\t\tPlay");
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(player1Score, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                .addComponent(player2Score, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(player1Score, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(player2Score, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startButtonMouseClicked
        play();
    }//GEN-LAST:event_startButtonMouseClicked

    private void resetButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resetButtonKeyPressed
        reset();
    }//GEN-LAST:event_resetButtonKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel player1Score;
    private javax.swing.JLabel player2Score;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables


    /**
     * The logic of the game
     * Check if the ball hits the racket or not and give points if the ball hits
     * the wall behind a players racket
     */
    private void logic() {
        if(ballPosX == 1 && dirX < 0){
            boolean hitRacket = false;
            for(int pos = player1pos - 1; pos < player1pos + RACKET_SIZE; pos++){
                if(ballPosY == pos){
                    hitRacket = true;
                }
            } 
            if(hitRacket){
                dirX *= -1;
            }
        }
        else if(ballPosX == 77 && dirX > 0){
            boolean hitRacket = false;
            for(int pos = player2pos - 1; pos < player2pos + RACKET_SIZE; pos++){
                if(ballPosY == pos){
                    hitRacket = true;
                }
            } 
            if(hitRacket){
                dirX *= -1;
            }
        }
        if(ballPosX == 0 || ballPosX == 79){
            point();
        }
    }
    
    /**
     * Sets the state of the game to true
     */
    private void play() {
        game = true;
    }
    
    /**
     * Resets the game
     */
    private void reset() {
        game = false;
        score1 = 0;
        score2 = 0;
        player1Score.setText("" + score1);
        player2Score.setText("" + score2);
    }
    
    /**
     * Sets the state of the game to false
     */
    private void pause(){
        game = false;
    }
    
    /**
     * Runs the game.
     */
    private void runGame(){
        update();
        if(game){
            moveBall();
        }
        logic();
    }
    
    /**
     * Give point when a player scores
     */
    private void point() {
        if(ballPosX == 0){
            score1++;
            player1Score.setText("" + score1);
        }
        if(ballPosX == 79){
            score2++;
            player2Score.setText("" + score2);
        }
        ballPosX = 40;
        ballPosY = 20;
        game = false;
    }

}
