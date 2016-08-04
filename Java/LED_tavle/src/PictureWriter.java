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
    
    public PictureWriter(){
        comList = new HashMap<>();
        portList = CommPortIdentifier.getPortIdentifiers();
        while(portList.hasMoreElements()){
            int x = 1;
            portId = (CommPortIdentifier) portList.nextElement();
            comList.put(portId.getName(), portId);
        }
    }
    
    public HashMap getComList(){
        return this.comList;
    }
    
    
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
    
    public synchronized void close() {
	if (serialPort != null) {
		serialPort.removeEventListener();
		serialPort.close();
	}
    }

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
    
    private void print(byte[] data){
        try{
            output.write(data);
        }
        catch(IOException e){
            System.out.println("Could not print data");
            System.err.println(e.toString());
        }
    }
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
