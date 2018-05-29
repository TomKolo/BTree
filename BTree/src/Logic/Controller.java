/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author tomas
 */
public class Controller {

    //TODO RANDOM ACCESS FILE ??? should i use it ?
    private RandomAccessFile random_access_node;
    private DataInputStream input_node; //file with nodes

    private RandomAccessFile random_access_record;
    private DataInputStream input_record; //file with records

    public String node_path;
    public String record_path;
    public String query_path;

    private int root_pointer = 1;
    private int tree_height = 0;
    private int node_end = 2;
    private int record_end = 0;
    private int next_record = -1;
    private int next_node = -1;
    private final int size_of_node = 80;
    private final int size_od_record = 16;
    private byte[] record_buffor;
    private int record_buffor_start = -1;
    private int number_of_operations = 0;

    public Controller(String path) throws FileNotFoundException, IOException {
        this.node_path = path + "\\node.txt";
        this.record_path = path + "\\data.txt";
        this.query_path = path + "\\query.txt";
        Scanner inputReader = new Scanner(System.in);
        String input = inputReader.nextLine();

        while (!"quit".equals(input)) {
            if ("query".equals(input)) {
                parse(this.query_path);
            } else if ("load".equals(input)) {
                load();
                print_btree();
            } else if ("save".equals(input)) {
                save();
                print_btree();
            } else if ("show".equals(input)) {
                String key = inputReader.nextLine();
                int input_key = Integer.parseInt(key);
                Record tmp = new Record(-1, -1, input_key, -1);
                Record tmp2 = search_record(tmp, root_pointer);
                if (tmp2 == null) {
                    System.out.println("Nie ma tekiego rekordu");
                } else {
                    System.out.println(tmp2.toString());
                }
            } else if ("mod".equals(input)) {
                String key = inputReader.nextLine();
                int input_key = Integer.parseInt(key);
                Record tmp = new Record(-1, -1, input_key, -1);
                Record tmp2 = search_record(tmp, root_pointer);
                if (tmp2 == null) {
                    System.out.println("Nie ma tekiego rekordu");
                } else {
                    System.out.println(tmp2.toString());
                    System.out.println("Podaj nowe współrzędne");
                    input = inputReader.nextLine();
                    double x = Double.parseDouble(input);
                    input = inputReader.nextLine();
                    double y = Double.parseDouble(input);
                    tmp2 = new Record(x, y, input_key, tmp2.offset);
                    overwrite_record(tmp2.offset, tmp2, true);
                }
            } else if ("add".equals(input)) {
                System.out.println("Podaj X, Y i klucz rekordu");
                String double_x = inputReader.nextLine();
                String double_y = inputReader.nextLine();
                String int_key = inputReader.nextLine();
                Record tmp = new Record(Double.parseDouble(double_x), Double.parseDouble(double_y), Integer.parseInt(int_key), get_record_space());
                Record tmp2 = search_record(tmp, root_pointer);
                if (tmp2 == null) {
                    overwrite_record(tmp.offset, tmp, true);
                    add_record(tmp, root_pointer, null);
                    print_btree();
                } else {
                    System.out.println("powtórka w kluczu");
                }
            } else if ("print".equals(input)) {
                print_btree();
                System.out.print("Odczyty z pliku :");
                System.out.println(number_of_operations);
            } else if ("del".equals(input)) {
                String int_key = inputReader.nextLine();
                Record tmp = new Record(-1, -1, Integer.parseInt(int_key), 0);
                Record tmp2 = search_record(tmp, root_pointer);
                if (tmp2 == null) {
                    System.out.println("nie ma takiego rekordu");
                } else {
                    delete_record(tmp2);
                    delete_record(tmp, root_pointer, null);
                    print_btree();
                }
            } else if ("rand".equals(input)) {
                System.out.println("Podaj liczbę rekordów do wygenerowania");
                Random r = new Random();
                String number_of_rands = inputReader.nextLine();
                int number = Integer.parseInt(number_of_rands);
                for (int i = 0; i < number; i++) {
                    Record tmp = new Record(r.nextDouble(), r.nextDouble(), r.nextInt() % 1000 + 999, 1);
                    Record tmp2 = search_record(tmp, root_pointer);
                    if (tmp2 == null) {
                        tmp.offset = get_record_space();
                        overwrite_record(tmp.offset, tmp, true);
                        add_record(tmp, root_pointer, null);

                    } else {
                        System.out.println("powtórka w kluczu");
                    }
                }
                print_btree();
            } else if ("help".equals(input)) {
                System.out.println("Dostepne funkcje: \n  load \n save \n show \n mod \n add \n print \n rand \n del \n query \n eksp \n");
            } else if ("eksp".equals(input)) {
                System.out.println("Liczba rekordó do eksperymentu");
                Random r = new Random();
                int gen_keys = Integer.parseInt(inputReader.nextLine());
                int[] keys = new int[gen_keys];
                for (int i = 0; i < gen_keys; i++) {
                    keys[i] = r.nextInt() % 1000000 + 999999;
                }

                for (int i = 0; i < gen_keys; i++) {
                    Record tmp = new Record(r.nextDouble(), r.nextDouble(), keys[i], 1);
                    Record tmp2 = search_record(tmp, root_pointer);
                    if (tmp2 == null) {
                        tmp.offset = get_record_space();
                        overwrite_record(tmp.offset, tmp, true);
                        add_record(tmp, root_pointer, null);

                    } else {
                        //System.out.println("powtórka w kluczu");
                    }
                }
                print_btree();
                System.out.print("Odczyty z pliku :");
                System.out.println(number_of_operations);
                number_of_operations = 0;
                for (int i = 0; i < gen_keys;) {
                    Record tmp = new Record(-1, -1, keys[i], 0);
                    Record tmp2 = search_record(tmp, root_pointer);
                    if (tmp2 == null) {
                        //System.out.println("nie ma takiego rekord2u");
                    } else {
                        delete_record(tmp2);
                        delete_record(tmp, root_pointer, null);
                    }
                    i += 1;
                }
                print_btree();
                System.out.print("Odczyty z pliku :");
                System.out.println(number_of_operations);
            }
            input = inputReader.nextLine();
        }

    }

    public byte[] read_node(int offset, boolean count) throws IOException {
        if (count == true) {
            number_of_operations++;
        }

        byte[] buffor = new byte[80];
        input_node = new DataInputStream(new FileInputStream(this.node_path));
        input_node.skip(offset * size_of_node); //skips a offset nuber of bytes
        input_node.read(buffor); //read(byte[] b,int off,int len)
        input_node.close();
        return buffor;
    }

    public byte[] read_record(int offset, boolean count) throws IOException {
        byte[] buffor = new byte[size_od_record];
        if (record_buffor_start != -1 && offset >= record_buffor_start && offset < record_buffor_start + 10) {
            //mamy to w buforze
            for (int i = 0; i < size_od_record; i++) {
                buffor[i] = record_buffor[i + (offset - record_buffor_start) * size_od_record];
            }
        } else {//musimy pobrać
            //najpier zapisz poprzednie
            flush(count);
            //pobierz nowe
            record_buffor = new byte[10 * size_od_record];
            input_record = new DataInputStream(new FileInputStream(this.record_path));
            input_record.skip(offset * size_od_record); //skips a offset nuber of bytes
            input_record.read(record_buffor); //read(byte[] b,int off,int len)
            input_record.close();
            record_buffor_start = offset;
            for (int i = 0; i < size_od_record; i++) {
                buffor[i] = record_buffor[i];
            }
        }
        return buffor;
    }

    public void flush(boolean count) throws FileNotFoundException, IOException {
        if (count == true) {
            number_of_operations++;
            number_of_operations++;
        }
        if (record_buffor_start != -1) {
            random_access_record = new RandomAccessFile(record_path, "rws");
            random_access_record.seek(record_buffor_start * size_od_record);
            random_access_record.write(record_buffor);
            random_access_record.close();
        }
    }

    public void overwrite_node(int offset, Node node, boolean count) throws IOException {
        if (count == true) {
            number_of_operations++;
        }
        byte[] buffor = node.toByteArray();
        random_access_node = new RandomAccessFile(node_path, "rws");
        random_access_node.seek(offset * size_of_node);
        random_access_node.write(buffor);
        random_access_node.close();
    }

    public void overwrite_record(int offset, Record record, boolean count) throws IOException {
        byte[] buffor = record.toByteArray();
        if (record_buffor_start != -1 && offset >= record_buffor_start && offset < record_buffor_start + 10) {
            //mamy to w buforze
            for (int i = 0; i < size_od_record; i++) {
                record_buffor[i + (offset - record_buffor_start) * size_od_record] = buffor[i];
            }
        } else {//musimy pobrać
            //najpier zapisz poprzednie
            flush(count);
            //pobierz nowe
            record_buffor = new byte[10 * size_od_record];
            input_record = new DataInputStream(new FileInputStream(this.record_path));
            input_record.skip(offset * size_od_record); //skips a offset nuber of bytes
            input_record.read(record_buffor); //read(byte[] b,int off,int len)
            input_record.close();
            record_buffor_start = offset;
            for (int i = 0; i < size_od_record; i++) {
                record_buffor[i] = buffor[i];
            }
        }
    }

    public void parse(String path) throws FileNotFoundException, IOException {
        System.out.println("PATH = " + path);
        Scanner scan = new Scanner(new File(path));

        int[] keys = {0, 0, 0, 0, 0, 0};
        int[] pointers = {0, 0, 0, 0, 0, 0};
        int[] pointers2 = {0, 0, 0, 0, 0, 0, 0};

        /*
        int[] keys = {1, 2, 3, 4, 5, 6};
        int[] pointers = {7, 8, 9, 10, 11, 12};
        int[] pointers2 = {13, 14, 15, 16, 17, 18, 19};
         */
        //dodaj pustego roota 
        root_pointer = get_node_space();
        Node temp = new Node(0, keys, pointers, pointers2, root_pointer, this);
        overwrite_node(root_pointer, temp, true);
        tree_height = 1;
        //operacje się wykonują dopuki plik z operacjami ma kolejne linie
        while (scan.hasNextLine()) {
            String scaned_line = scan.nextLine();
            //System.out.println("Wczytano : " + scaned_line);
            String prefix = scaned_line.substring(0, 3);
            String double_x;
            String double_y;
            String int_key;
            //dodaj do B-Drzewa
            if ("add".equals(prefix)) {
                double_x = scan.nextLine();
                double_y = scan.nextLine();
                int_key = scan.nextLine();
                Record tmp = new Record(Double.parseDouble(double_x), Double.parseDouble(double_y), Integer.parseInt(int_key), get_record_space());
                Record tmp2 = search_record(tmp, root_pointer);
                if (tmp2 == null) {
                    overwrite_record(tmp.offset, tmp, true);
                    add_record(tmp, root_pointer, null);
                    print_btree();
                } else {
                    System.out.println("powtórka w kluczu");
                }
                //System.out.println(tmp.toString());
            }else if ("load".equals(prefix)) {
                load();
                print_btree();
            } else if ("save".equals(prefix)) {
                save();
                print_btree();
            }
            else if ("mod".equals(prefix)) {
                String key = scan.nextLine();
                int input_key = Integer.parseInt(key);
                Record tmp = new Record(-1, -1, input_key, -1);
                Record tmp2 = search_record(tmp, root_pointer);
                String input;
                if (tmp2 == null) {
                    System.out.println("Nie ma tekiego rekordu");
                } else {
                    System.out.println(tmp2.toString());
                    System.out.println("Podaj nowe współrzędne");
                    input = scan.nextLine();
                    double x = Double.parseDouble(input);
                    input = scan.nextLine();
                    double y = Double.parseDouble(input);
                    tmp2 = new Record(x, y, input_key, tmp2.offset);
                    overwrite_record(tmp2.offset, tmp2, true);
                }
            } else if ("rand".equals(prefix)) {
                System.out.println("Podaj liczbę rekordów do wygenerowania");
                Random r = new Random();
                String number_of_rands = scan.nextLine();
                int number = Integer.parseInt(number_of_rands);
                for (int i = 0; i < number; i++) {
                    Record tmp = new Record(r.nextDouble(), r.nextDouble(), r.nextInt() % 1000 + 999, 1);
                    Record tmp2 = search_record(tmp, root_pointer);
                    if (tmp2 == null) {
                        tmp.offset = get_record_space();
                        overwrite_record(tmp.offset, tmp, true);
                        add_record(tmp, root_pointer, null);

                    } else {
                        System.out.println("powtórka w kluczu");
                    }
                }
                print_btree(); //odejmij od B-Drzewa
            }
            else if ("del".equals(prefix)) {
                String int_keys = scan.nextLine();
                Record tmp = new Record(-1, -1, Integer.parseInt(int_keys), 0);
                Record tmp2 = search_record(tmp, root_pointer);
                if (tmp2 == null) {
                    System.out.println("nie ma takiego rekordu");
                } else {
                    delete_record(tmp2);
                    delete_record(tmp, root_pointer, null);
                    print_btree();
                }
            } //modyfikuj w B-Drzewie
            else if ("mod".equals(prefix)) {
                String double_x1 = scan.nextLine();
                String double_y1 = scan.nextLine();
                String double_x2 = scan.nextLine();
                String double_y2 = scan.nextLine();
            }else if ("eksp".equals(prefix)) {
                System.out.println("Liczba rekordó do eksperymentu");
                Random r = new Random();
                int gen_keyss = Integer.parseInt(scan.nextLine());
                int[] keyss = new int[gen_keyss];
                for (int i = 0; i < gen_keyss; i++) {
                    keyss[i] = r.nextInt() % 1000000 + 999999;
                }

                for (int i = 0; i < gen_keyss; i++) {
                    Record tmp = new Record(r.nextDouble(), r.nextDouble(), keyss[i], 1);
                    Record tmp2 = search_record(tmp, root_pointer);
                    if (tmp2 == null) {
                        tmp.offset = get_record_space();
                        overwrite_record(tmp.offset, tmp, true);
                        add_record(tmp, root_pointer, null);

                    } else {
                        //System.out.println("powtórka w kluczu");
                    }
                }
                print_btree();
                System.out.print("Odczyty z pliku :");
                System.out.println(number_of_operations);
                number_of_operations = 0;
                for (int i = 0; i < gen_keyss;) {
                    Record tmp = new Record(-1, -1, keyss[i], 0);
                    Record tmp2 = search_record(tmp, root_pointer);
                    if (tmp2 == null) {
                        //System.out.println("nie ma takiego rekord2u");
                    } else {
                        delete_record(tmp2);
                        delete_record(tmp, root_pointer, null);
                    }
                    i += 1;
                }
                print_btree();
                System.out.print("Odczyty z pliku :");
                System.out.println(number_of_operations);
            }
        }
        System.out.println("Koniec odczytu z pliku");
    }

    public Record add_record(Record record, int base_offset, Node prev_node) throws IOException {
        Node node = new Node(read_node(base_offset, true), base_offset); //read a node
        boolean splited = false;
        if (node.pointers_to_nodes[0] == 0 || node.pointers_to_nodes[0] == -1) {//oznacza to, że węzeł jest liściem
            if (node.number_of_keys < 6) {
                node.add(record);
                overwrite_node(base_offset, node, true);
                //node = new Node(node.toByteArray(), base_offset);
                //record zeruj
            } else { //node.number_of_keys == 6 (przepełnienie)//co jeśli root ?
                //kompensuj lub splituj
                if (!compensate_add(node, prev_node, record, base_offset)) {
                    //splituj
                    record = split(node, record);//jeśli splitowano to zwróci rekord
                    splited = true;
                }
            }
        } else { //węzeł nie jest liściem, musimy zejść niżej
            int pointer_to_next_node = node.search(record);
            record = add_record(record, pointer_to_next_node, node);
        }
        //co zrobić po splicie ?
        //po compensacji albo dodaniu trzeba wyzerować record
        if ((prev_node == null && record.pointer_to_node != -1 && splited == true) || (prev_node == null && record.pointer_to_node != -1 && node.number_of_keys == 6)) {//split na roocie !! dodaj nowego roota !
            if (splited == true) {//kiedy dodaje 7 rekord
                int prev_root = root_pointer;
                root_pointer = get_node_space();
                int[] keys_tmp = {record.key, 0, 0, 0, 0, 0};
                int[] records_tmp = {record.offset, 0, 0, 0, 0, 0};
                int[] nodes_tmp = {prev_root, record.pointer_to_node, 0, 0, 0, 0, 0};
                Node new_root = new Node(1, keys_tmp, records_tmp, nodes_tmp, root_pointer, this);
                overwrite_node(root_pointer, new_root, true);
                record.pointer_to_node = -1;
                tree_height++;
            } else {//kiedy musze splitować roota
                record = split(node, record);
                int prev_root = root_pointer;
                root_pointer = get_node_space();
                int[] keys_tmp = {record.key, 0, 0, 0, 0, 0};
                int[] records_tmp = {record.offset, 0, 0, 0, 0, 0};
                int[] nodes_tmp = {prev_root, record.pointer_to_node, 0, 0, 0, 0, 0};
                Node new_root = new Node(1, keys_tmp, records_tmp, nodes_tmp, root_pointer, this);
                overwrite_node(root_pointer, new_root, true);
                record.pointer_to_node = -1;
                tree_height++;
            }
        } else if (record.pointer_to_node != -1 && splited != true) {
            if (node.number_of_keys < 6) {
                node.add(record);
                overwrite_node(base_offset, node, true);
                record.pointer_to_node = -1;
            } else { //node.number_of_keys == 6 (przepełnienie)
                //kompensuj lub splituj
                if (!compensate_add(node, prev_node, record, base_offset)) {
                    //splituj
                    record = split(node, record);//jeśli splitowano to zwróci rekord
                } else {
                    record.pointer_to_node = -1;
                }
            }
        }
        return record;
    }

    public Record delete_record(Record record, int base_offset, Node prev_node) throws IOException {
        Node node = new Node(read_node(base_offset, true), base_offset); //read a node
        boolean merged = false;
        if (node.check(record) && !(node.pointers_to_nodes[0] == 0 || node.pointers_to_nodes[0] == -1)) {
            //tu jest ten rekord
            //trzeba swapować
            //to nie liść
            int pointer_to_next_node = swap(node, record);
            //System.out.println("po swapie");
            //print_btree();
            record = delete_record(record, pointer_to_next_node, node);
        } else if (node.pointers_to_nodes[0] == 0 || node.pointers_to_nodes[0] == -1) {//oznacza to, że węzeł jest liściem
            if (node.number_of_keys > 3 || (prev_node == null && (node.pointers_to_nodes[0] == 0 || node.pointers_to_nodes[0] == -1))) {
                node.delete(record);
                overwrite_node(base_offset, node, true);
                node = new Node(node.toByteArray(), base_offset);
                //record zeruj
            } else { //node.number_of_keys == 6 (przepełnienie)//co jeśli root ?
                //kompensuj lub splituj
                if (!compensate_del(node, prev_node, record, base_offset)) {
                    //splituj
                    record = merge(node, prev_node, record);//jeśli mergowano to zwróci rekord
                    merged = true;
                }
            }
        } else { //węzeł nie jest liściem, musimy zejść niżej
            int pointer_to_next_node = node.search(record);
            record = delete_record(record, pointer_to_next_node, node);
        }

        if (prev_node == null && record.pointer_to_node != -1) {//merge pod rootem !! usuń roota !
            if (node.number_of_keys == 1) {
                root_pointer = node.pointers_to_nodes[0];
                delete_node(node);
                tree_height--;
                record.pointer_to_node = -1;
            } else {
                node.delete(record);
                overwrite_node(root_pointer, node, true);
                record.pointer_to_node = -1;
            }
        } else if (record.pointer_to_node != -1 && merged != true) {
            if (node.number_of_keys > 3) {
                node.delete(record);
                overwrite_node(base_offset, node, true);
                record.pointer_to_node = -1;
            } else { //node.number_of_keys == 6 (przepełnienie)
                //kompensuj lub splituj
                if (!compensate_del(node, prev_node, record, base_offset)) {
                    //splituj
                    record = merge(node, prev_node, record);//jeśli mergowano to zwróci rekord
                } else {
                    record.pointer_to_node = -1;
                }
            }
        }

        return record;
    }

    private boolean compensate_add(Node base_node, Node prev_node, Record record, int base_offset) throws IOException {
        //pointery zostają te same, tylko bracia się zmieniają
        if (prev_node == null) { //root
            return false;
        } else {
            Node parent = prev_node;
            int left_brother = parent.getLeftBrother(base_offset);
            if (left_brother != -1) {
                Node left_brother_node = new Node(read_node(left_brother, true), left_brother);
                if (left_brother_node.number_of_keys < 6) {
                    //rotate left
                    rotate_left_add(left_brother_node, base_node, parent, record);

                    record.pointer_to_node = -1;
                    return true;
                }
            }
            int right_brother = parent.getRightBrother(base_offset);
            if (right_brother != -1) {
                Node right_brother_node = new Node(read_node(right_brother, true), right_brother);
                if (right_brother_node.number_of_keys < 6) {
                    //rotate right
                    rotate_left_add(base_node, right_brother_node, parent, record);

                    record.pointer_to_node = -1;
                    return true;
                }
            }
            //nie udalo się kompensować
            return false;
        }
    }

    private boolean compensate_del(Node base_node, Node prev_node, Record record, int base_offset) throws IOException {
        //pointery zostają te same, tylko bracia się zmieniają
        if (prev_node == null) { //root
            return false;
        } else {
            Node parent = prev_node;
            int left_brother = parent.getLeftBrother(base_offset);
            if (left_brother != -1) {
                Node left_brother_node = new Node(read_node(left_brother, true), left_brother);
                if (left_brother_node.number_of_keys > 3) {
                    //rotate left
                    rotate_left_del(left_brother_node, base_node, parent, record);

                    record.pointer_to_node = -1;
                    return true;
                }
            }
            int right_brother = parent.getRightBrother(base_offset);
            if (right_brother != -1) {
                Node right_brother_node = new Node(read_node(right_brother, true), right_brother);
                if (right_brother_node.number_of_keys > 3) {
                    //rotate right
                    rotate_left_del(base_node, right_brother_node, parent, record);

                    record.pointer_to_node = -1;
                    return true;
                }
            }
            //nie udalo się kompensować
            return false;
        }
    }

    private Record split(Node base_node, Record record) throws IOException {
        //węzeł nie może się konpensować i ma 6 rekordów, należy stworzyć nowy węzeł , przenieść tam 
        //połowę rekordów a środkowy (3+1+3) dodać do rodzica (tam wymołać splita ponownie
        //dodaj pustego noda   
        int pointer_to_new_node = get_node_space();
        int[] keys = {0, 0, 0, 0, 0, 0};
        int[] pointers = {0, 0, 0, 0, 0, 0};
        int[] pointers2 = {0, 0, 0, 0, 0, 0, 0};
        Node new_node = new Node(0, keys, pointers, pointers2, pointer_to_new_node, this);
        int[] temporary_keys = {0, 0, 0, 0, 0, 0, 0};
        int[] temporary_pointers_rec = {0, 0, 0, 0, 0, 0, 0};
        int[] temporary_pointers_node = {0, 0, 0, 0, 0, 0, 0, 0};
        int inserted = 0;
        temporary_pointers_node[0] = base_node.pointers_to_nodes[0]; //czemu ? , pomyśl !
        for (int i = 0; i < 6; i++) {
            if (base_node.keys[i] < record.key && inserted == 0) {
                temporary_keys[i] = base_node.keys[i];
                temporary_pointers_rec[i] = base_node.pointers_to_records[i];
                temporary_pointers_node[i + 1] = base_node.pointers_to_nodes[i + 1];
            } else if (base_node.keys[i] > record.key && inserted == 0) {//wstawiamy rekord records
                inserted = 1;
                temporary_keys[i] = record.key;
                temporary_pointers_rec[i] = record.offset;
                temporary_pointers_node[i + 1] = record.pointer_to_node;
            } else {//base_node.keys[i] > record.key & inserted == 1
                temporary_keys[i] = base_node.keys[i - inserted];
                temporary_pointers_rec[i] = base_node.pointers_to_records[i - inserted];
                temporary_pointers_node[i + 1] = base_node.pointers_to_nodes[i - inserted + 1];
            }
        }
        if (inserted == 0) {
            temporary_keys[6] = record.key;
            temporary_pointers_rec[6] = record.offset;
            temporary_pointers_node[6 + 1] = record.pointer_to_node;
        } else {
            temporary_keys[6] = base_node.keys[5];
            temporary_pointers_rec[6] = base_node.pointers_to_records[5];
            temporary_pointers_node[6 + 1] = base_node.pointers_to_nodes[5 + 1];
        }

        //set first node
        base_node.pointers_to_nodes[0] = temporary_pointers_node[0];
        for (int i = 0; i < 3; i++) {//dodaj 3 do base_node
            base_node.keys[i] = temporary_keys[i];
            base_node.pointers_to_records[i] = temporary_pointers_rec[i];
            base_node.pointers_to_nodes[i + 1] = temporary_pointers_node[i + 1];
        }

        for (int i = 3; i < 6; i++) {//wyzeruj 3 w base_node
            base_node.keys[i] = 0;
            base_node.pointers_to_records[i] = 0;
            base_node.pointers_to_nodes[i + 1] = 0;
        }

        //set second node
        new_node.pointers_to_nodes[0] = temporary_pointers_node[4];
        for (int i = 0; i < 3; i++) {//dodaj 3 do base_node
            new_node.keys[i] = temporary_keys[i + 4];
            new_node.pointers_to_records[i] = temporary_pointers_rec[i + 4];
            new_node.pointers_to_nodes[i + 1] = temporary_pointers_node[i + 1 + 4];
        }

        for (int i = 3; i < 6; i++) {//wyzeruj 3 w base_node
            new_node.keys[i] = 0;
            new_node.pointers_to_records[i] = 0;
            new_node.pointers_to_nodes[i + 1] = 0;
        }

        Record ret_record = new Record(-1, -1, temporary_keys[3], temporary_pointers_rec[3]);
        ret_record.pointer_to_node = pointer_to_new_node;
        base_node.number_of_keys = 3;
        new_node.number_of_keys = 3;
        this.overwrite_node(base_node.offset, base_node, true);
        this.overwrite_node(new_node.offset, new_node, true);
        if (ret_record.pointer_to_node == 0) {
            System.out.println("elo");
        }
        return ret_record;
    }

    private Record merge(Node base_node, Node prev_node, Record record) throws IOException {
        if (prev_node == null) {
            System.out.println("merge na roocie");
        } else {
            //łączenie dwóch węzlów base+prawy i usunięcie prawego
            //a nastepnie zwrócenie rekordu który należy usunąć w wężle wyżej   
            int[] keys = {0, 0, 0, 0, 0, 0, 0};
            int[] rec = {0, 0, 0, 0, 0, 0, 0};
            int[] node = {0, 0, 0, 0, 0, 0, 0, 0};
            int right_node_int = prev_node.getRightBrother(base_node.offset);
            Node right_node;
            if (right_node_int > 0) {
                right_node = new Node(read_node(right_node_int, true), right_node_int, this);
            } else {
                int left_brother_int = prev_node.getLeftBrother(base_node.offset);
                if (left_brother_int > 0) {
                    Node left_node = new Node(read_node(left_brother_int, true), left_brother_int, this);
                    right_node = base_node;
                    base_node = left_node;
                } else {
                    return record;
                }
            }
            node[0] = base_node.pointers_to_nodes[0];
            for (int i = 0; i < 3; i++) {
                keys[i] = base_node.keys[i];
                rec[i] = base_node.pointers_to_records[i];
                node[i + 1] = base_node.pointers_to_nodes[i + 1];
            }

            //find parent key
            int iterator = 0;
            while (prev_node.pointers_to_nodes[iterator] != base_node.offset) {
                iterator++;
            }
            keys[3] = prev_node.keys[iterator];
            rec[3] = prev_node.pointers_to_records[iterator];
            node[4] = right_node.pointers_to_nodes[0];
            for (int i = 0; i < 3; i++) {
                keys[i + 4] = right_node.keys[i];
                rec[i + 4] = right_node.pointers_to_records[i];
                node[i + 5] = right_node.pointers_to_nodes[i + 1];
            }
            //powinno być przekopiowane

            //delete node
            int deleted = 0;
            base_node.pointers_to_nodes[0] = node[0];
            for (int i = 0; i < 7; i++) {
                if (keys[i] == record.key) {
                    deleted = 1;
                } else {
                    base_node.keys[i - deleted] = keys[i];
                    base_node.pointers_to_records[i - deleted] = rec[i];
                    base_node.pointers_to_nodes[i + 1 - deleted] = node[i + 1];
                }
            }
            base_node.number_of_keys = 6;
            record = new Record(-1, -1, keys[3], rec[3]);
            record.pointer_to_node = right_node.offset;
            overwrite_node(base_node.offset, base_node, true);
            delete_node(right_node);
        }
        return record;
    }

    private int get_record_space() throws IOException {
        if (next_record > 0) {
            Record tmp = new Record(read_record(next_record, true));
            int tmp_int = next_record;
            next_record = (int) tmp.x;
            return tmp_int;
        } else {
            return record_end++;
        }
    }

    private int get_node_space() throws IOException {
        if (next_node > 0) {
            Node tmp = new Node(read_node(next_node, true), next_node);
            int tmp_int = next_node;
            next_node = tmp.number_of_keys;
            return tmp_int;
        } else {
            return node_end++;
        }
    }

    private void rotate_left_add(Node left_node, Node right_node, Node parent_node, Record record) throws IOException {
        //parent_node pointers stay the same
        int temporary_key;
        int temporary_record_pointer;
        int temporary_node_pointers;

        int temorary_key2;
        int tempoarary_record_pointer2;
        int[] tmp_keys = new int[left_node.number_of_keys + right_node.number_of_keys + 2];
        int[] tmp_rec = new int[left_node.number_of_keys + right_node.number_of_keys + 2];
        int[] tmp_node = new int[left_node.number_of_keys + right_node.number_of_keys + 3];

        //przepisz lewy
        tmp_node[0] = left_node.pointers_to_nodes[0];
        int shift = 0;
        for (; shift < left_node.number_of_keys; shift++) {
            tmp_keys[shift] = left_node.keys[shift];
            tmp_rec[shift] = left_node.pointers_to_records[shift];
            tmp_node[shift + 1] = left_node.pointers_to_nodes[shift + 1];
        }

        //dodaj środkowy z parenta
        int iterator = 0;
        while (iterator < parent_node.number_of_keys && parent_node.keys[iterator] < right_node.keys[0]) {
            iterator++;
        }
        iterator--;//tu coś nie pytka
        tmp_keys[shift] = parent_node.keys[iterator];
        tmp_rec[shift] = parent_node.pointers_to_records[iterator];
        //przepisz prawy
        tmp_node[++shift] = right_node.pointers_to_nodes[0];
        for (int i = 0; i < right_node.number_of_keys; i++) {
            tmp_keys[shift + i] = right_node.keys[i];
            tmp_rec[shift + i] = right_node.pointers_to_records[i];
            tmp_node[shift + i + 1] = right_node.pointers_to_nodes[i + 1];
        }
        //powinno być przepisane xDDDDD

        //dodaj rekord !
        int checked = 0;
        while (tmp_keys[checked] < record.key) {
            checked++;
            if (checked == left_node.number_of_keys + right_node.number_of_keys + 2) {
                checked--;
                break;
            }
        }
        int poz = checked;
        for (int i = left_node.number_of_keys + right_node.number_of_keys; i >= checked; i--) {
            tmp_keys[i + 1] = tmp_keys[i];
            tmp_rec[i + 1] = tmp_rec[i];
            tmp_node[i + 2] = tmp_node[i + 1];
        }
        //tmp_node[checked + 1] = tmp_node[checked];
        tmp_keys[poz] = record.key;
        tmp_rec[poz] = record.offset;
        tmp_node[poz + 1] = record.pointer_to_node;
        //
        int size = left_node.number_of_keys + right_node.number_of_keys + 2;
        int left_size = size / 2;
        int right_size = size - left_size - 1;
        //od nowa lewy
        left_node.pointers_to_nodes[0] = tmp_node[0];
        for (int i = 0; i < left_size; i++) {
            left_node.keys[i] = tmp_keys[i];
            left_node.pointers_to_records[i] = tmp_rec[i];
            left_node.pointers_to_nodes[i + 1] = tmp_node[i + 1];
        }
        for (int i = left_size; i < left_node.number_of_keys; i++) {
            left_node.keys[i] = 0;
            left_node.pointers_to_records[i] = 0;
            left_node.pointers_to_nodes[i + 1] = 0;
        }
        left_node.number_of_keys = left_size;

        //parent
        parent_node.keys[iterator] = tmp_keys[left_size];
        parent_node.pointers_to_records[iterator] = tmp_rec[left_size];
        if (parent_node.pointers_to_nodes[iterator + 1] == 0) {
            System.out.print("elo2");
        }
        //od nowa prawy
        right_node.pointers_to_nodes[0] = tmp_node[1 + left_size];
        for (int i = 0; i < right_size; i++) {
            right_node.keys[i] = tmp_keys[i + left_size + 1];
            right_node.pointers_to_records[i] = tmp_rec[i + left_size + 1];
            right_node.pointers_to_nodes[i + 1] = tmp_node[i + 1 + left_size + 1];
        }
        for (int i = right_size; i < right_node.number_of_keys; i++) {
            right_node.keys[i] = 0;
            right_node.pointers_to_records[i] = 0;
            right_node.pointers_to_nodes[i + 1] = 0;
        }
        right_node.number_of_keys = right_size;

        //this code should be executed after all is moved needed number of times
        overwrite_node(left_node.offset, left_node, true);
        overwrite_node(parent_node.offset, parent_node, true);
        overwrite_node(right_node.offset, right_node, true);
    }

    private void rotate_left_del(Node left_node, Node right_node, Node parent_node, Record record) throws IOException {
        //parent_node pointers stay the same

        int[] tmp_keys = new int[left_node.number_of_keys + right_node.number_of_keys + 1];
        int[] tmp_rec = new int[left_node.number_of_keys + right_node.number_of_keys + 1];
        int[] tmp_node = new int[left_node.number_of_keys + right_node.number_of_keys + 2];

        //przepisz lewy
        tmp_node[0] = left_node.pointers_to_nodes[0];
        int shift = 0;
        for (; shift < left_node.number_of_keys; shift++) {
            tmp_keys[shift] = left_node.keys[shift];
            tmp_rec[shift] = left_node.pointers_to_records[shift];
            tmp_node[shift + 1] = left_node.pointers_to_nodes[shift + 1];
        }

        //dodaj środkowy z parenta
        int iterator = 0;
        while (iterator < parent_node.number_of_keys && parent_node.keys[iterator] < right_node.keys[0]) {
            iterator++;
        }
        iterator--;
        tmp_keys[shift] = parent_node.keys[iterator];
        tmp_rec[shift] = parent_node.pointers_to_records[iterator];
        //przepisz prawy
        tmp_node[++shift] = right_node.pointers_to_nodes[0];
        for (int i = 0; i < right_node.number_of_keys; i++) {
            tmp_keys[shift + i] = right_node.keys[i];
            tmp_rec[shift + i] = right_node.pointers_to_records[i];
            tmp_node[shift + i + 1] = right_node.pointers_to_nodes[i + 1];
        }
        //powinno być przepisane xDDDDD

        //usun rekord !usun rekord 
        int deleted = 0;
        for (int i = 0; i < left_node.number_of_keys + right_node.number_of_keys + 1; i++) {
            if (tmp_keys[i] == record.key) {
                deleted = 1;
            } else {
                tmp_keys[i - deleted] = tmp_keys[i];
                tmp_rec[i - deleted] = tmp_rec[i];
                tmp_node[i + 1 - deleted] = tmp_node[i + 1];
            }
        }
        //
        int size = left_node.number_of_keys + right_node.number_of_keys - 1;
        int left_size = size / 2;
        int right_size = size - left_size;
        //od nowa lewy
        left_node.pointers_to_nodes[0] = tmp_node[0];
        for (int i = 0; i < left_size; i++) {
            left_node.keys[i] = tmp_keys[i];
            left_node.pointers_to_records[i] = tmp_rec[i];
            left_node.pointers_to_nodes[i + 1] = tmp_node[i + 1];
        }
        for (int i = left_size; i < left_node.number_of_keys; i++) {
            left_node.keys[i] = 0;
            left_node.pointers_to_records[i] = 0;
            left_node.pointers_to_nodes[i + 1] = 0;
        }
        left_node.number_of_keys = left_size;

        //parent
        parent_node.keys[iterator] = tmp_keys[left_size];
        parent_node.pointers_to_records[iterator] = tmp_rec[left_size];
        if (parent_node.pointers_to_nodes[iterator + 1] == 0) {
            System.out.print("elo2");
        }
        //od nowa prawy
        right_node.pointers_to_nodes[0] = tmp_node[1 + left_size];
        for (int i = 0; i < right_size; i++) {
            right_node.keys[i] = tmp_keys[i + left_size + 1];
            right_node.pointers_to_records[i] = tmp_rec[i + left_size + 1];
            right_node.pointers_to_nodes[i + 1] = tmp_node[i + 1 + left_size + 1];
        }
        for (int i = right_size; i < right_node.number_of_keys; i++) {
            right_node.keys[i] = 0;
            right_node.pointers_to_records[i] = 0;
            right_node.pointers_to_nodes[i + 1] = 0;
        }
        right_node.number_of_keys = right_size;

        //this code should be executed after all is moved needed number of times
        overwrite_node(left_node.offset, left_node, true);
        overwrite_node(parent_node.offset, parent_node, true);
        overwrite_node(right_node.offset, right_node, true);
    }

    public Record search_record(Record record, int base_offset) throws IOException {
        Node node = new Node(read_node(base_offset, true), base_offset); //read a node
        Record ret_record = null;
        if (node.pointers_to_nodes[0] == 0 || node.pointers_to_nodes[0] == -1) {//oznacza to, że węzeł jest liściem
            for (int i = 0; i < node.number_of_keys; i++) {
                if (node.keys[i] == record.key) {
                    ret_record = new Record(read_record(node.pointers_to_records[i], true));
                    ret_record.offset = node.pointers_to_records[i];
                    return ret_record;
                }
            }
        } else { //węzeł nie jest liściem, musimy zejść niżej
            for (int i = 0; i < node.number_of_keys; i++) {
                if (node.keys[i] == record.key) {
                    ret_record = new Record(read_record(node.pointers_to_records[i], true));
                    ret_record.offset = node.pointers_to_records[i];
                    return ret_record;
                }
            }
            int pointer_to_next_node = node.search(record);
            ret_record = search_record(record, pointer_to_next_node);
        }
        return ret_record;
    }

    public void print_btree() throws IOException {
        Node temp = new Node(read_node(root_pointer, false), root_pointer, this);
        String tree_in_string = temp.print_node(0, "");
        //    System.out.print(tree_in_string);
    }

    //number_of_keys = tree_height
    //pointer_to_records:1. record_next_space
    //pointer)to_node 1. node_next_space
    //keys 1. root_pointer;
    private void save() throws IOException {
        int[] keys = {root_pointer, 0, 0, 0, 0, 0};
        int[] pointers = {record_end, next_record, 0, 0, 0, 0};
        int[] pointers2 = {node_end, next_node, 0, 0, 0, 0, 0};
        Node save_node = new Node(tree_height, keys, pointers, pointers2, 0, this);
        overwrite_node(0, save_node, true);
    }

    private void load() throws IOException {
        Node load_node = new Node(read_node(0, true), 0);
        tree_height = load_node.number_of_keys;
        record_end = load_node.pointers_to_records[0];
        node_end = load_node.pointers_to_nodes[0];
        next_record = load_node.pointers_to_records[1];
        next_node = load_node.pointers_to_nodes[1];
        root_pointer = load_node.keys[0];
    }

    private int swap(Node node, Record record) throws IOException {
        //znajdz wezel na lewo od rekordu
        int key_tmp;
        int rec_tmp;

        int iterator = 0;
        while (node.keys[iterator] != record.key) {
            iterator++;
        }

        key_tmp = node.keys[iterator];
        rec_tmp = node.pointers_to_records[iterator];
        Node tmp = new Node(read_node(node.pointers_to_nodes[iterator], true), node.pointers_to_nodes[iterator], this);

        while (tmp.pointers_to_nodes[0] != 0 && tmp.pointers_to_nodes[0] != -1) {
            tmp = new Node(read_node(tmp.pointers_to_nodes[tmp.number_of_keys], true), tmp.pointers_to_nodes[tmp.number_of_keys], this);
        }

        node.keys[iterator] = tmp.keys[tmp.number_of_keys - 1];
        node.pointers_to_records[iterator] = tmp.pointers_to_records[tmp.number_of_keys - 1];
        tmp.keys[tmp.number_of_keys - 1] = key_tmp;
        tmp.pointers_to_records[tmp.number_of_keys - 1] = rec_tmp;
        overwrite_node(node.offset, node, true);
        overwrite_node(tmp.offset, tmp, true);
        return node.pointers_to_nodes[iterator];
    }

    public void delete_record(Record record) throws IOException {
        record.x = next_record;
        next_record = record.offset;
        overwrite_record(record.offset, record, true);
    }

    public void delete_node(Node node) throws IOException {
        node.number_of_keys = next_node;
        next_node = node.offset;
        overwrite_node(node.offset, node, true);
    }
}
