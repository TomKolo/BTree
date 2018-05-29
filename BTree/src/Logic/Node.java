/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author tomas
 */
public class Node {

    public int offset;
    public int number_of_keys;
    public int[] keys;
    public int[] pointers_to_records;
    public int[] pointers_to_nodes;
    public Controller controller;

    public Node(byte[] buffor, int offset) {
        this.offset = offset;
        int shift = 0;
        int records_read = 0;
        keys = new int[6];
        pointers_to_records = new int[6];
        pointers_to_nodes = new int[7];
        //read previous_node
        byte[] bytes = new byte[4];

        //read number_of_keys
        for (int i = 0; i < 4; i++) {
            bytes[i] = buffor[shift + i];
        }
        shift += 4;
        number_of_keys = toInt(bytes);

        while (records_read < 6) {
            //wskażniki na węzły

            for (int i = 0; i < 4; i++) {
                bytes[i] = buffor[shift + i];
            }
            pointers_to_nodes[records_read] = toInt(bytes);
            shift += 4;

            //klucze
            for (int i = 0; i < 4; i++) {
                bytes[i] = buffor[shift + i];
            }
            keys[records_read] = toInt(bytes);
            shift += 4;

            //wskaźniki na rekordy
            bytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                bytes[i] = buffor[shift + i];
            }
            pointers_to_records[records_read] = toInt(bytes);
            shift += 4;

            records_read++;
        }
        bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = buffor[shift + i];
        }
        pointers_to_nodes[6] = toInt(bytes);
    }

    public Node(byte[] buffor, int offset, Controller controller) {
        this.controller = controller;
        this.offset = offset;
        int shift = 0;
        int records_read = 0;
        keys = new int[6];
        pointers_to_records = new int[6];
        pointers_to_nodes = new int[7];
        //read previous_node
        byte[] bytes = new byte[4];

        //read number_of_keys
        for (int i = 0; i < 4; i++) {
            bytes[i] = buffor[shift + i];
        }
        shift += 4;
        number_of_keys = toInt(bytes);

        while (records_read < 6) {
            //wskażniki na węzły

            for (int i = 0; i < 4; i++) {
                bytes[i] = buffor[shift + i];
            }
            pointers_to_nodes[records_read] = toInt(bytes);
            shift += 4;

            //klucze
            for (int i = 0; i < 4; i++) {
                bytes[i] = buffor[shift + i];
            }
            keys[records_read] = toInt(bytes);
            shift += 4;

            //wskaźniki na rekordy
            bytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                bytes[i] = buffor[shift + i];
            }
            pointers_to_records[records_read] = toInt(bytes);
            shift += 4;

            records_read++;
        }
        bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = buffor[shift + i];
        }
        pointers_to_nodes[6] = toInt(bytes);
    }

    public Node(int size, int[] keys_new, int[] pointers_to_records_new, int[] pointers_to_nodes_new, int offset, Controller controller) {
        this.controller = controller;
        this.offset = offset;
        this.number_of_keys = size;
        this.keys = keys_new;
        this.pointers_to_records = pointers_to_records_new;
        this.pointers_to_nodes = pointers_to_nodes_new;
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[80];
        int shift = 0;
        int records_read = 0;
        byte[] buffor;
        buffor = Node.intToByteArray(this.number_of_keys);
        for (int i = 0; i < 4; i++) {
            bytes[shift + i] = buffor[i];
        }
        shift += 4;

        while (records_read != 6) {
            //wskażniki na węzły
            buffor = Node.intToByteArray(this.pointers_to_nodes[records_read]);
            for (int i = 0; i < 4; i++) {
                bytes[shift + i] = buffor[i];
            }
            shift += 4;

            //klucze
            buffor = Node.intToByteArray(this.keys[records_read]);
            for (int i = 0; i < 4; i++) {
                bytes[shift + i] = buffor[i];
            }
            shift += 4;

            //wskaźniki na rekordy
            buffor = Node.intToByteArray(this.pointers_to_records[records_read]);
            for (int i = 0; i < 4; i++) {
                bytes[shift + i] = buffor[i];
            }
            shift += 4;

            records_read++;
        }
        buffor = Node.intToByteArray(this.pointers_to_nodes[6]);
        for (int i = 0; i < 4; i++) {
            bytes[shift + i] = buffor[i];
        }

        return bytes;
    }

    public void add(Record record) {
        int checked_key = 0;
        int[] keys_tmp = {0, 0, 0, 0, 0, 0};
        int[] records_tmp = {0, 0, 0, 0, 0, 0};
        int[] nodes_tmp = {0, 0, 0, 0, 0, 0, 0};
        if (number_of_keys >= 6) {
            System.out.println("Próba wpisania za wielu rekordów do jednego noda");
        }
        if ((this.pointers_to_nodes[0] != -1 && this.pointers_to_nodes[0] != 0) && record.pointer_to_node == 0) {
            System.out.println("nonono");
        }

        while (this.keys[checked_key] < record.key && this.keys[checked_key] != 0 && this.keys[checked_key] != -1) {
            checked_key++;
        }

        int copy_keys = 0;
        nodes_tmp[0] = this.pointers_to_nodes[0];
        while (copy_keys < checked_key) {
            keys_tmp[copy_keys] = this.keys[copy_keys];
            records_tmp[copy_keys] = this.pointers_to_records[copy_keys];
            nodes_tmp[copy_keys + 1] = this.pointers_to_nodes[copy_keys + 1];
            copy_keys++;
        }

        keys_tmp[checked_key] = record.key;
        records_tmp[copy_keys] = record.offset;
        if (record.pointer_to_node == -1) {
            nodes_tmp[copy_keys + 1] = 0;
        } else {
            nodes_tmp[copy_keys + 1] = record.pointer_to_node;
        }

        while (copy_keys < 5) {
            keys_tmp[copy_keys + 1] = this.keys[copy_keys];
            records_tmp[copy_keys + 1] = this.pointers_to_records[copy_keys];
            nodes_tmp[copy_keys + 1 + 1] = this.pointers_to_nodes[copy_keys + 1];
            copy_keys++;
        }

        number_of_keys++;
        keys = keys_tmp;
        pointers_to_records = records_tmp;
        pointers_to_nodes = nodes_tmp;

    }

    public void delete(Record record) {
        int[] keys_tmp = {0, 0, 0, 0, 0, 0};
        int[] records_tmp = {0, 0, 0, 0, 0, 0};
        int[] nodes_tmp = {0, 0, 0, 0, 0, 0, 0};

        if (number_of_keys <= 3) {
  //          System.out.println("Próba usunięcia poniżej 3 rekordów w węźle");
        }
        int deleted = 0;
        nodes_tmp[0] = this.pointers_to_nodes[0];
        for (int i = 0; i < this.number_of_keys; i++) {
            if (this.keys[i] == record.key) {
                deleted = 1;
            } else {
                keys_tmp[i - deleted] = this.keys[i];
                records_tmp[i - deleted] = this.pointers_to_records[i];
                nodes_tmp[i - deleted + 1] = this.pointers_to_nodes[i + 1];
            }
        }

        this.keys = keys_tmp;
        this.pointers_to_records = records_tmp;
        this.pointers_to_nodes = nodes_tmp;
        this.number_of_keys--;
    }

    public int search(Record record) {
        if (number_of_keys == 0) {
            System.out.println("Szukanie w nodzie o size = 0");
        }
        for (int i = 0; i < number_of_keys; i++) {
            if (record.key < keys[i]) {
                return pointers_to_nodes[i];
            }

        }
        return pointers_to_nodes[number_of_keys];
    }

    public int getLeftBrother(int base) {//rerwrite
        int left_brother = -1;
        for (int i = 0; i <= number_of_keys; i++) {
            if (pointers_to_nodes[i] == base) {
                return left_brother;
            }
            left_brother = pointers_to_nodes[i];
        }
        return -1;
    }

    public int getRightBrother(int base) {
        int iterator = 0;
        for (; iterator < number_of_keys; iterator++) {
            if (pointers_to_nodes[iterator] == base) {
                break;
            }
        }
        if (iterator < number_of_keys) {
            return pointers_to_nodes[iterator + 1];
        } else {
            return -1;
        }
    }

    private static byte[] doubleToByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    private static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(value);
        return bytes;
    }

    private static byte[] longToByteArray(long value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(value);
        return bytes;
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    private static int toInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    private static long toLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public String print_node(int space, String base_string) throws IOException {
        base_string = "";

        for (int i = 0; i < space; i++) {
            base_string += "  ";
        }
        base_string += this.offset;
        base_string += ". | ";
        for (int i = 0; i < number_of_keys; i++) {
            if (pointers_to_nodes[i] == -1) {
                base_string += '0';
            } else {
                base_string += pointers_to_nodes[i];
            }
            base_string += " | ";
            base_string += keys[i];
            base_string += " ";
            base_string += pointers_to_records[i];
            base_string += " | ";
        }
        if (pointers_to_nodes[number_of_keys] == -1) {
            base_string += '0';
        } else {
            base_string += pointers_to_nodes[number_of_keys];
        }
        base_string += " | ";
        Node tmp;
        System.out.println(base_string);
        if (pointers_to_nodes[0] != 0 && pointers_to_nodes[0] != -1) {
            for (int i = 0; i < number_of_keys + 1; i++) {
                tmp = new Node(controller.read_node(pointers_to_nodes[i],false), pointers_to_nodes[i], controller);
                tmp.print_node(space + 1, base_string);
            }
        }
        return base_string;
    }

    public boolean check(Record rec) {
        for (int i = 0; i < this.number_of_keys; i++) {
            if (rec.key == this.keys[i]) {
                return true;
            }
        }
        return false;
    }

}
