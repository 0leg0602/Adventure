package project;

/*
        Author: Oleg Poliakov
        Start Time: 2024-09-30, Mon, 11:56
        End Time:
*/

import java.util.Scanner;

public class Main {

    static Scanner scan = new Scanner(System.in);

    static int print_speed = 20;

    static boolean skip = false;

    public static class Inventory {
        int axe = 0;
        int lantern = 0;
        Integer first_aid_kit = 0;
        int rope = 0;
    }

    public static class Player {
        int health = 100;
        Inventory inventory = new Inventory();
    }


    public static void print_options(String[] options) {
        for (int i = 0; i < options.length; i++) {
            println((i + 1) + ". " + options[i]);
        }
    }

    public static int get_user_input(int available_range) {
        skip = false;
        while (true) {
            print(">>> ");
            String input = scan.nextLine();
            if (input.equals("print speed")) {
                set_print_speed();
                continue;
            }

            if (input.startsWith("/")) {
                input = input.substring(1);
                skip = true;
            }

            try {
                int int_input = Integer.parseInt(input);
                if (int_input < 1 || int_input > available_range) {
                    throw new Exception("Not in range");
                }
                return int_input;
            } catch (Exception e) {
                skip = false;
                String error = e.toString();
                if (error.contains("NumberFormatException")) {
                    println("Invalid value, try again");
                } else if (error.contains("Not in range")) {
                    println("Not in range, try again");
                }
            }
        }


    }

    public static void set_print_speed() {
        while (true) {
            System.out.print("Current print speed: " + print_speed + ". Set print speed >>> ");
            String input = scan.nextLine();

            try {
                int int_input = Integer.parseInt(input);
                if (int_input < 0) {
                    throw new Exception("<0");
                }
                if (int_input > 1000) {
                    throw new Exception(">1000");
                }
                print_speed = int_input;
                return;
            } catch (Exception e) {
                String error = e.toString();
                if (error.contains("NumberFormatException")) {
                    System.out.println("Invalid value, try again");
                } else if (error.contains("<0")) {
                    System.out.println("Print speed cannot be lower than 0, try again");
                } else if (error.contains(">1000")) {
                    System.out.println("Print speed cannot be higher than 1000, try again");
                }
            }
        }


    }

    public static void print(String s) {
        char[] letters = s.toCharArray();
        for (char letter : letters) {
            System.out.print(letter);
            if (!skip) {
                try {
                    Thread.sleep(print_speed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void println(String s) {
        char[] letters = s.toCharArray();
        for (char letter : letters) {
            System.out.print(letter);
            if (!skip) {
                try {

                    Thread.sleep(print_speed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.print("\n");
    }

    public static void print_banner(String text) {
        print("╔");
        for (int i = 0; i < text.length(); i++) {
            print("═");
        }
        print("╗");
        println("");
        println("║" + text + "║");
        print("╚");
        for (int i = 0; i < text.length(); i++) {
            print("═");
        }
        print("╝\n");
    }

    private static void gameover() {
        println("""
                ╔══════════════════════════════════════════════════════════════════════════════════╗
                ║  _|_|_|    _|_|    _|      _|  _|_|_|_|    _|_|    _|      _|  _|_|_|_|  _|_|_|  ║\s
                ║_|        _|    _|  _|_|  _|_|  _|        _|    _|  _|      _|  _|        _|    _|║\s
                ║_|  _|_|  _|_|_|_|  _|  _|  _|  _|_|_|    _|    _|  _|      _|  _|_|_|    _|_|_|  ║\s
                ║_|    _|  _|    _|  _|      _|  _|        _|    _|    _|  _|    _|        _|    _|║\s
                ║  _|_|_|  _|    _|  _|      _|  _|_|_|_|    _|_|        _|      _|_|_|_|  _|    _|║\s
                ╚══════════════════════════════════════════════════════════════════════════════════╝\s
                """);
        print("Press enter to restart");
        scan.nextLine();
        start_game();
    }

    public static void start_game() {

        Player player = new Player();


        println("You decided to go hunting what will you take with you: ");

        String[] tools_options = {"axe", "lantern", "first aid kit", "rope"};

        print_options(tools_options);

        int input = get_user_input(4);

        switch (input) {
            case 1 -> player.inventory.axe = 1;
            case 2 -> player.inventory.lantern = 1;
            case 3 -> player.inventory.first_aid_kit = 1;
            case 4 -> player.inventory.rope = 1;
        }

        println("You decided to venture into the forest, carrying a " + tools_options[input - 1] + ".");
        println("The afternoon sun was beginning to set, signaling that evening was approaching.");
        println("You walked through the dense underbrush from tree to tree,");
        println("but apart from a couple of squirrels, you encountered no one.");
        println("As it became clear that you were lost, another problem began to emerge:");
        println("Night was already coming.");
        println("");
        println("It was essential to find shelter for the night before darkness fell.");
        println("Ahead, you spotted a hut - an abandoned, overgrown forester's cottage.");

        print_options(new String[]{"Go into the forester's house", "Walk past the forester's house"});
        input = get_user_input(2);

        if (input == 1) {
            chapter1();
        } else if (input == 2) {
            println("You decided to pass the forester's house, continuing on your way");
            println("The darkness deepened rapidly, and you couldn't see even the tip of your finger.");
            println("In just a few moments, total darkness had descended.");
            if (player.inventory.lantern > 0) {
                println("You lit the lantern - your last hope.");
                println("Under its faint light, you continued to wander through the forest in search of shelter.");
                println("After walking a little farther, you came across the forester's house once again.");
                print_options(new String[]{"Go into the forester's house", "Walk past the forester's house"});
                input = get_user_input(2);
                if (input == 1) {
                    chapter1();
                } else if (input == 2) {
                    println("Despite going into the forester's house being the most obvious decision.");
                    println("You decided to pass by it.");
                    println("A few hours later, so exhausted that you didn't even have the strength to hold the lantern,");
                    println("you collapsed to the ground and fell asleep.");
                    println("But unfortunately, you didn't wake up in the morning.");
                    gameover();
                }
            } else {
                println("You gazed around, but everything remained completely dark,");
                println("the forest's density blocked out even a glimpse of moonlight.");
                println("What will you do next?");
                print_options(new String[]{"Lay down on the ground, trying to rest until the morning", "Attempt to make your way blindly through the forest in search of a source of light"});
                input = get_user_input(2);
                if (input == 1) {
                    println("You lay down on the ground and fell asleep.");
                    println("But unfortunately, you didn't wake up in the morning.");
                    gameover();
                } else if (input == 2) {
                    println("You tried to blindly make you way though the forest,");
                    println("but then you hear a strange metallic sound under your foot.");
                    println("YOU'VE BEEN SNARED! The bear trap has sprung, and you're now dangling from its deadly metal jaws.");
                    gameover();
                }

            }
        }


    }


    private static void chapter1() {
        print_banner("Chapter 1 : An Abandoned, overgrown forester's cottage");
        println("You have opened the door entered the forester's cottage.");
        println("You walked into the living room.");
        println("To be continued...");
        gameover();
//        println("What are you going to do next?");
//        print_options(new String[]{"Sleep on the floor in the living room", "Explore the room to your right (kitchen)", "Explore the attic"});
//        int input = get_user_input(3);

    }


    public static void main(String[] args) {
        start_game();
    }

}