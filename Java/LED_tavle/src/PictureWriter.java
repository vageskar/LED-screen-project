/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
/**
 *
 * @author RobinBergseth
 */
public class PictureWriter extends Thread  implements SerialPortEventListener {
    private Enumeration portList;
    private CommPortIdentifier portId;
    private HashMap<String, CommPortIdentifier> comList;
    private byte[] dataArray;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 38400; // 9600 19200 38400 57600 74880 115200 230400 250000
    private SerialPort serialPort;
    private BufferedReader input;
    private OutputStream output;
    private boolean demo = false;
    private boolean serialReady = false;
    private int sequencePos;
    private int sequenceLength;
    private static final int UPDATE_RATE = 100;
    private Timer seqTimer;
    private Timer updateTimer;
    private boolean seqTimerStarted = false;
    
    /**
     * Constructor
     * When a new object off the class PictureWriter is created it search the
     * computer for available serial ports and puts them in a HashMap
     */
    public PictureWriter(){
        comList = new HashMap<>();
        portList = CommPortIdentifier.getPortIdentifiers();
        while(portList.hasMoreElements()){
            int x = 1;
            portId = (CommPortIdentifier) portList.nextElement();
            comList.put(portId.getName(), portId);
        }
    }

    /**
     * GetComList returns the list of serial ports available
     * @return List filled with the serial ports available
     */
    public HashMap getComList(){
        return this.comList;
    }
    
    /**
     * This method is inherited from the Runnable class and makes the 
     * PictureWriter class run in its own thread.
     */
    @Override
    public void run(){
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                if(!demo && serialReady){
                    print(dataArray);
                }
            }
        }, UPDATE_RATE, UPDATE_RATE);
    }
    
    /**
     * This method sets up serial communication to the given serial port name
     * @param port The serial port name which to connect to
     */
    public void initialize(String port){
        if(!port.equals("No serial port found")){
            try{
                CommPortIdentifier portId = comList.get(port);
                serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
                serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
                input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                output = serialPort.getOutputStream();
                serialReady = true;
                serialPort.addEventListener(this);
		serialPort.notifyOnDataAvailable(true);
            }
            catch(Exception e){
                System.err.println(e.toString());
            }
        }
    }
    
    /**
     * Gets run when the serial port should close
     */
    public synchronized void close() {
	if (serialPort != null) {
		serialPort.removeEventListener();
		serialPort.close();
	}
    }

    /**
     * Event listener method which react when serial communication is received
     * If "demo" is received demo is set to true and if serial is received 
     * demo is set to false
     * @param spe 
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent spe) {
        if(spe.getEventType() == SerialPortEvent.DATA_AVAILABLE){
            try{
                String inputLine = input.readLine();
                if(inputLine.equals("demo")){
                    demo = true;
                }
                else if(inputLine.equals("serial")){
                    demo = false;
                }
            }
            catch(Exception e){
                System.err.println(e.toString());
            }
        }
    }
    /**
     * This method sends an array of bytes over the serial port
     * @param data Byte array of data to be send over the serial port
     */
    private void print(byte[] data){
        try{
            output.write(data);
        }
        catch(IOException e){
            System.out.println("Could not print data");
            System.err.println(e.toString());
        }
    }

    /**
     * This method takes an ArrayList filled with byte arrays and shuffels 
     * trough them at a given intervall.
     * @param list ArrayList with a set of byte arrays
     */
    public void sequence(ArrayList list){
        sequenceLength = list.size();
        sequencePos = 0;
        seqTimer = new Timer();
        seqTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                setWriteArray((byte[]) list.get(sequencePos), true);
                sequencePos++;
                if(sequencePos == sequenceLength){
                    sequencePos = 0;
                }
            }
        }, 160, 160);
        seqTimerStarted = true;
    }
    
    /**
     * This method is used to set the desired array to be printed to the screen
     * The seq arguments cancels an ongoing sequens from further interfearing
     * @param array Byte array containing the data
     * @param seq True if sequence is used, false if not.
     */
    public synchronized void setWriteArray(byte[] array, boolean seq){
        dataArray = array;
        if(!seq && seqTimerStarted){
            try{
                seqTimer.cancel();
            }
            catch(Exception e){
                System.err.println(e.toString());
            }
        }
    }
    
}
