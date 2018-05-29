/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import Logic.Controller;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * @author tomas
 */
public class BTree {

    private static Controller controller;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Wskaż folder z plikami    C:\\Users\\tomas\\Documents\\NetBeansProjects\\BTree");
        Scanner inputReader = new Scanner(System.in);
        String path = inputReader.nextLine();
        System.out.println("Wpisałeś : " + path);
        controller = new Controller(path);
    }

}
