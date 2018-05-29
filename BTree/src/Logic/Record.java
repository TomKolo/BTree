/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import static java.lang.Math.sqrt;
import java.nio.ByteBuffer;

/**
 *
 * @author tomas
 */

public class Record {

    public double x;
    private double y;
    public int key;
    public int offset;
    public int pointer_to_node = -1; //used only when split
    public Record(double x, double y,int key,int offset) {
        this.key = key;
        this.offset = offset;
        this.x = x;
        this.y = y;
        /*System.out.print(x);
        System.out.print(" ");
        System.out.print(y);
        System.out.print(" ");
        System.out.println(key);*/
    }

    public Record(byte[] bytes) {
        byte[] x_b = new byte[8];
        for (int i = 0; i < 8; i++) {
            x_b[i] = bytes[i];
        }
        this.x = toDouble(x_b);

        byte[] y_b = new byte[8];
        for (int i = 0; i < 8; i++) {
            y_b[i] = bytes[i + 8];
        }
        this.y = toDouble(y_b);
        /*System.out.print(x);
        System.out.print(" ");
        System.out.println(y);*/

    }

    public double Distance() {
        return sqrt(x * x + y * y);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return " " + Double.toString(x) + " " + Double.toString(y) + " ";
    }

    private static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    private static byte[] doubleToByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[16];

        byte[] x_b = doubleToByteArray(this.x);
        for (int i = 0; i < 8; i++) {
            bytes[i] = x_b[i];
        }

        byte[] y_b = doubleToByteArray(this.y);
        for (int i = 0; i < 8; i++) {
            bytes[i + 8] = y_b[i];
        }
        return bytes;
    }
}
