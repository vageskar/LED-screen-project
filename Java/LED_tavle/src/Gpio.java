import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.GpioController;

/**
 *
 * @author RobinBergseth
 */
public class Gpio {
    private GpioController gpio;
    private GpioPinDigitalOutput cki;
    private GpioPinDigitalOutput sdi;
    
    public Gpio(){
        gpio = GpioFactory.getInstance();
        cki = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
        sdi = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01); 
        //this.run();
    }

    private void run() {
            for(int led = 0; led < 3200; led ++){
                int color = 0xFF0000;
                for(int colorBit = 23; colorBit >= 0; colorBit--){
                    cki.low();
                    int mask = 1;
                    int bit = color >> colorBit;
                
                    if((bit & mask) == 1){
                        sdi.high();
                        this.delay(10000);
                    }
                    else{
                        sdi.low();
                        this.delay(10000);
                    }
                    cki.high();
                }
            }
            cki.low();
            long time = System.currentTimeMillis() + 1;
            while(time > System.currentTimeMillis());
            System.out.println("Ferdig");
        }

    private void delay(long i) {
        long time = System.nanoTime() + i;
        while(time > System.nanoTime());
    }
    
    public void ckiHigh(){
        cki.high();
    }
    
    public void ckiLow(){
        cki.low();
    }
    
    public void sdiHigh(){
        sdi.high();
    }
    public void sdiLow(){
        sdi.low();
    }
    
    public void sdiTest(long delay){
        for(int x = 0; x < 10; x++){
            sdi.high();
            this.delay(delay);
            sdi.low();
            this.delay(delay);
        }
    }
    
}