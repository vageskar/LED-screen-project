
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author RobinBergseth
 */
public class Pong extends javax.swing.JFrame implements Runnable{
    
    private PictureWriter writer;
    private ArrayConverter converter;
    private int[][] pongMatrix;
    private int player1pos = 0;
    private int player2pos = 0;
    private final int RACKET_SIZE = 15;
    private int ballPosX = 40;
    private int ballPosY = 20;
    private byte[] writeArray;
    private boolean game = false;
    private int score1 = 0;
    private int score2 = 0;
    private Timer timer;
    private int dirX = 1;
    private int dirY = 1;
    private int speed = 1;
    
    /**
     * Creates new form Pong
     */
    public Pong(PictureWriter pw) {
        KeyListener listener = new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
                
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                switch(ke.getKeyChar()){
            case 'q':
                if(player1pos != 0){
                    player1pos --;
                }
                break;
            case 'a':
                if((player1pos + RACKET_SIZE) != 40){
                    player1pos ++;
                }
                break;
            case 'o':
                if(player2pos != 0){
                    player2pos --;
                }
                break;
            case 'l':
                if((player2pos + RACKET_SIZE) != 40){
                    player2pos ++;
                }
                break;
        }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                
            }
        };
        this.writer = pw;
        this.converter = new ArrayConverter();
        pongMatrix = new int[80][40];
        clearMatrix();
        initComponents();
        this.addKeyListener(listener);
        this.setVisible(true);
        this.setFocusable(true);
    }
    
    public void update(){
        clearMatrix();
        updateBallPos();
        updateRacktPos();
        writeArray = converter.getWriteArray(pongMatrix);
        writer.print(writeArray);
    }
    
    public void updateRacktPos(){
        for(int pos = player1pos; pos < player1pos + RACKET_SIZE; pos++){
            pongMatrix[0][pos] = 0xFFFFFF;
        }
        for(int pos = player2pos; pos < player2pos + RACKET_SIZE; pos++){
            pongMatrix[79][pos] = 0xFFFFFF;
        }
    }
    public void updateBallPos(){
        pongMatrix[ballPosX][ballPosY] = 0xFFFFFF;
        pongMatrix[ballPosX][ballPosY + 1] = 0xFFFFFF;
        pongMatrix[ballPosX + 1][ballPosY] = 0xFFFFFF;
        pongMatrix[ballPosX + 1][ballPosY + 1] = 0xFFFFFF;
        
    }
    
    public void clearMatrix(){
        for(int x = 0; x < pongMatrix.length; x++){
            for(int y = 0; y < pongMatrix[0].length; y++){
                pongMatrix[x][y] = 0;
            }
        }
    }
    
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
        input = new javax.swing.JTextField();

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

        input.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        input.setText("Use Q and A for player 1 and O and L for player 2");
        input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inputKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(input)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(player1Score, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(player2Score, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addComponent(input, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startButtonMouseClicked
        game = true;
        update();
        input.requestFocusInWindow();
        timer = new Timer(20, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                moveBall();
                update();
            }
        });
        timer.start();
        
    }//GEN-LAST:event_startButtonMouseClicked

    private void resetButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resetButtonKeyPressed
        game = false;
        timer.stop();
    }//GEN-LAST:event_resetButtonKeyPressed

    private void inputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputKeyReleased
        switch(evt.getKeyChar()){
            case 'q':
                if(player1pos != 0){
                    player1pos --;
                }
                break;
            case 'a':
                if((player1pos + RACKET_SIZE) != 40){
                    player1pos ++;
                }
                break;
            case 'o':
                if(player2pos != 0){
                    player2pos --;
                }
                break;
            case 'l':
                if((player2pos + RACKET_SIZE) != 40){
                    player2pos ++;
                }
                break;
        }
    }//GEN-LAST:event_inputKeyReleased

    private void inputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputKeyTyped
        evt.consume();
        input.setText("Use Q and A for player 1 and O and L for player 2");
    }//GEN-LAST:event_inputKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel player1Score;
    private javax.swing.JLabel player2Score;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        update();
    }

}