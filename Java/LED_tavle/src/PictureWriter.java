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
import javax.swing.JOptionPane;
/**
 *
 * @author RobinBergseth
 */
public class PictureWriter extends Thread  implements SerialPortEventListener {
    private Enumeration portList;
    private CommPortIdentifier portId;
    private HashMap<String, CommPortIdentifier> comList;
    private int[] dataArray;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 19200;
    private SerialPort serialPort;
    private BufferedReader input;
    private OutputStream output;
    
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
                System.out.println(inputLine);
            }
            catch(Exception e){
                System.err.println(e.toString());
            }
        }
    }
    
    public void print(byte[] data){
        try{
            output.write(data);
        }
        catch(IOException e){
            System.out.println("Could not print data");
            System.err.println(e.toString());
        }
    }
    public void sequence(ArrayList list, int times){
        for(int sequenceTimes = 0; sequenceTimes < times; sequenceTimes++){
            for(int x = 0; x < list.size(); x++){
                try{
                    output.write((byte[]) list.get(x));
                }
                catch(IOException e){
                    System.out.println("Could not print data");
                    System.err.println(e.toString());
                }
                try{
                    Thread.sleep(100);
                }
                catch(InterruptedException e){
                    System.out.println("Thread couldn't sleep");
                    System.err.println(e.toString());
                }
            }
        }
    }
    
    public void threadTest(){
        long time = System.currentTimeMillis() + 5000;
        while(time > System.currentTimeMillis()){
            
        }
    }
}
