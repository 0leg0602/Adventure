package project;

/*
        Author: Oleg Poliakov
        Start Time: 2024-09-30, Mon, 11:56
        End Time:
*/

import java.util.Scanner;

public class Main {

    static Scanner scan = new Scanner(System.in);

    public static class Player {
        int health = 100;
    }


    public static void print_options(String[] options){
        for (int i = 0; i < options.length; i++) {
            System.out.println((i+1) + ". " + options[i]);
        }
    }

    public static int get_user_input(int available_range){
        while (true) {
            System.out.print(">>> ");
            String input = scan.nextLine();
            try {
                int int_input = Integer.parseInt(input);
                if (int_input < 1 || int_input > available_range) {
                    throw new Exception("Not in range");
                }
                return int_input;
            } catch (Exception e) {
                String error = e.toString();
                if (error.contains("NumberFormatException")) {
                    System.out.println("Invalid value, try again");
                } else if (error.contains("Not in range")) {
                    System.out.println("Not in range, try again");
                }
            }
        }


    }

    public static void start_game() {

        Player player = new Player();

        System.out.println("You decided to go hunting what will you take with you: ");

        print_options(new String[]{"axe", "lantern", "first aid kit", "rope"});
        get_user_input(4);


    }

    public static void main(String[] args) {
        start_game();
    }

}