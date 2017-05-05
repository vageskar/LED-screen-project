/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author RobinBergseth
 */
public class ArrayConverter {
    private byte[] writeArray;
    
    /**
     * This method generates an byte array from an int matrix
     * @param picture The picture matrix to be transformed
     * @return Byte array containing picture data
     */
    public byte[] getWriteArray(int[][] picture){
        int arrayLength = (picture.length * picture[0].length) * 3;
        writeArray = new byte[arrayLength];
        int pixels = 0;
        // Read the picture matrix to a writable array for the LED screen
        for(int y = 0; y < picture[0].length; y++){
            // If the row number is even read the data from left to right
            if((y%2)==0){
                for(int x = 0; x < picture.length; x++){
                    for(int RGB = 16; RGB >= 0; RGB -= 8, pixels++){
                        writeArray[pixels] = (byte) (picture[x][y] >>> RGB);
                    }
                }
            }
            // If the row number is odd read the data from right to left
            else{
                for(int x = (picture.length - 1); x >= 0; x--){
                    for(int RGB = 16; RGB >= 0; RGB -= 8, pixels++){
                        writeArray[pixels] = (byte) (picture[x][y] >>> RGB);
                    }
                }
            }
        }
        return writeArray;
    }
}
