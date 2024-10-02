package project;

/*
        Author: Oleg Poliakov
        Start Time: 2024-09-30, Mon, 11:56
        End Time:
*/

import java.util.Random;
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

        void take_damage(int damage) {
            println("You took " + damage + " points of damage.");
            println("Your health is at " + health + " - " + damage + " = " + (health - damage) + ".");
            health -= damage;
        }

        public void attack(Enemy enemy, int damage, String move) {
            switch (move) {
                case "repel" -> {
                    println(enemy.name + " anticipated the attack from that direction and easily repelled it.");
                    take_damage(enemy.attack_damage);
                }
                case "counter" -> {
                    println("You attack and land a successful hit, dealing damage.");
                    enemy.take_damage(damage);
                    println("However, " + enemy.name + " quickly retaliates with a counterattack.");
                    take_damage(enemy.attack_damage);
                }
                case "block" -> {
                    println(enemy.name + " The monster attempted to block your attack.");
                    if (enemy.block < damage) {
                        println("However, it was only partially successful, allowing you to deal some damage anyway.");
                        println(enemy.block + " damage blocked.");
                        enemy.take_damage(damage - enemy.block);
                    } else {
                        println(enemy.name + " fully blocked your attack, as you didn't deal enough damage to break through its defenses.");
                    }
                }
                case "none" -> {
                    println("You attack and land a successful hit, dealing damage.");
                    enemy.take_damage(damage);
                }
            }

        }
    }

    static Player player;

    public static class Enemy {
        String name;
        int health;
        int block;
        int attack_damage;
        int level;
        int frustration;
        int frustration_limit;
        String[] moves_str;
        String[] moves;
        String escape_death;


        public void take_damage(int damage) {
            println(name + " took " + damage + " points of damage.");
            println(name + " health is at " + health + " - " + damage + " = " + (health - damage) + ".");
            health -= damage;
        }
    }


    public static void print_options(String[] options) {
        for (int i = 0; i < options.length; i++) {
            if (!options[i].equals("none")) {
                if (options[i].equals("back")) {
                    println("0. back");
                } else {
                    println((i + 1) + ". " + options[i]);
                }
            }
        }
    }

    public static int get_user_input(int min, int max) {
        skip = false;
        while (true) {
            print(">>> ");
            String input = scan.nextLine();

            switch (input) {
                case "print speed" -> {
                    set_print_speed();
                    continue;
                }
                case "!exit" -> System.exit(0);
                case "!restart" -> start_game();
            }


            if (input.startsWith("/")) {
                input = input.substring(1);
                skip = true;
            }

            try {
                int int_input = Integer.parseInt(input);
                if (int_input < min || int_input > max) {
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

    public static void fight(String enemy_str) {
        print_banner(" THE FIGHT BEGINS ");


        Enemy enemy = new Enemy();

        switch (enemy_str) {
            case "forester's monster" -> {
                enemy.name = "The forester's monster";
                enemy.health = 24;
                enemy.block = 3;
                enemy.attack_damage = 20;
                enemy.level = 5;
                enemy.frustration = 0;
                enemy.frustration_limit = 4;
                enemy.moves_str = new String[]{"gazes intently at you.", "leaned to the right.", "leaned to the left.", "crouched down.", "dropped his hands tiredly"};
//                                          top bottom left right
                enemy.moves = new String[]{"repel,none repel,none repel,none repel,none", "counter,none counter,none counter,none repel,attack", "counter,none counter,none repel,attack counter,none", "block,none block,none block,none block,none", "none,none none,none none,none none,none"};

                enemy.escape_death = "You tried to escape from the forester's monster,\nand you managed to run to the front door of the house.\nUnfortunately, when you tried to open it, you found that it was locked.\nBy the time you turned around, it was already too late.";
            }
            case "bat" -> {
                enemy.name = "bat";
                enemy.health = 30;
                enemy.level = 3;
                enemy.moves_str = new String[]{"gazes intently at you", "leaned to the right", "leaned to the left", "crouched down"};
            }
            default -> throw new IllegalStateException("Unexpected value: " + enemy_str);
        }

        Random rand = new Random();

        while (enemy.health > 0) {
            int move;
            if (enemy.frustration < enemy.frustration_limit) {
                move = rand.nextInt(enemy.moves_str.length - 1);
            } else {
                move = enemy.moves_str.length;
            }
            println(enemy.name + " " + enemy.moves_str[move]);
            while (true) {
                println("What will you do?");
                print_options(new String[]{"attack", "block", "action", "escape"});
                int input = get_user_input(1, 4);
                if (input == 1) {

                    StringBuilder weapon_options_string = new StringBuilder();
                    for (String weapon : new String[]{"knife", (player.inventory.axe > 0) ? "axe" : "none", "back"}) {
                        if (!weapon.equals("none")) {
                            weapon_options_string.append(weapon).append(" ");
                        }

                    }
                    String[] weapon_options = weapon_options_string.toString().split(" ");
                    print_options(weapon_options);
                    input = get_user_input(0, weapon_options.length - 1);

                    if (input == 0) {
                        continue;
                    }

                    int damage = switch (weapon_options[input - 1]) {
                        case "knife" -> 5;
                        case "axe" -> 12;
                        default -> throw new IllegalStateException("Unexpected value: " + weapon_options[input - 1]);
                    };

                    println("How do you hit with a " + weapon_options[input - 1] + "?");
                    print_options(new String[]{"from above", "from below", "from the left", "from the right", "back"});
                    input = get_user_input(0, 4);
                    if (input == 0) {
                        continue;
                    }
                    player.attack(enemy, damage, enemy.moves[move].split(" ")[input - 1].split(",")[0]);
                    break;

                } else if (input == 2) {
                    break;
//                    TODO
                } else if (input == 3) {
//                    TODO
                }

                if (input == 4) {
                    println("Are you sure you want to escape?");
                    print_options(new String[]{"yes", "back"});
                    input = get_user_input(0, 1);
                    if (input == 1) {
                        println(enemy.escape_death);
                        gameover();
                    } else if (input == 0) {
                        continue;
                    }
                }
            }

//            enemy.attack();
//          TODO
        }

        print_banner(" THE FIGHT IS OVER ");
        println("You won!");
        println(enemy.name + " is defeated!");

//        TODO
    }


    public static void draw_ascii(String name) {
        switch (name) {
            case "gameover" -> println("""
                    ╔══════════════════════════════════════════════════════════════════════════════════╗
                    ║  _|_|_|    _|_|    _|      _|  _|_|_|_|    _|_|    _|      _|  _|_|_|_|  _|_|_|  ║\s
                    ║_|        _|    _|  _|_|  _|_|  _|        _|    _|  _|      _|  _|        _|    _|║\s
                    ║_|  _|_|  _|_|_|_|  _|  _|  _|  _|_|_|    _|    _|  _|      _|  _|_|_|    _|_|_|  ║\s
                    ║_|    _|  _|    _|  _|      _|  _|        _|    _|    _|  _|    _|        _|    _|║\s
                    ║  _|_|_|  _|    _|  _|      _|  _|_|_|_|    _|_|        _|      _|_|_|_|  _|    _|║\s
                    ╚══════════════════════════════════════════════════════════════════════════════════╝\s
                    """);
            case "axe" -> println("""
                    ╔════════════╗
                    ║  /'-./\\_   ║
                    ║ :    ||,>  ║
                    ║  \\.-'||    ║
                    ║      ||    ║
                    ║      ||    ║
                    ║      ||    ║
                    ╚════════════╝
                    """);
            case "lantern" -> println("""
                    ╔═════════╗
                    ║   ___   ║
                    ║  |   |  ║
                    ║   \\_/   ║
                    ║    :    ║
                    ║    o    ║
                    ║ .d8|8b. ║
                    ║ T""T""T ║
                    ║ ] (|) [ ║
                    ║  [_|_]  ║
                    ╚═════════╝
                    """);
            case "first aid kit" -> println("""
                    ╔══════════════════════╗
                    ║      _____________   ║
                    ║     /     __      /| ║
                    ║    /   __/ /_    / / ║
                    ║   /   /_  __/   / // ║
                    ║  /     /_/     / //  ║
                    ║ /_____________/ //   ║
                    ║ |______&______|//    ║
                    ║ |_____________|/     ║
                    ╚══════════════════════╝
                    """);
            case "rope" -> println("""
                    ╔════════╗
                    ║  \\\\//  ║
                    ║   //   ║
                    ║  //\\\\  ║
                    ║  \\\\//  ║
                    ║   //   ║
                    ║  //\\\\  ║
                    ║  \\\\//  ║
                    ║   //   ║
                    ║  //\\\\  ║
                    ║  \\\\//  ║
                    ║   //   ║
                    ║  //\\\\  ║
                    ║  \\\\//  ║
                    ║   //   ║
                    ║  //\\\\  ║
                    ║  \\\\//  ║
                    ║   //   ║
                    ║  //\\\\  ║
                    ╚════════╝
                    """);
        }
    }

    private static void gameover() {
        draw_ascii("gameover");

        print("Press enter to restart");
        scan.nextLine();
        start_game();
    }

    public static void start_game() {

        player = new Player();


        println("You decided to go hunting what will you take with you: ");

        String[] tools_options = {"axe", "lantern", "first aid kit", "rope"};

        print_options(tools_options);

        int input = get_user_input(1, 4);

        switch (input) {
            case 1 -> {
                player.inventory.axe = 1;
                draw_ascii("axe");
            }
            case 2 -> {
                player.inventory.lantern = 1;
                draw_ascii("lantern");
            }
            case 3 -> {
                player.inventory.first_aid_kit = 1;
                draw_ascii("first aid kit");
            }
            case 4 -> {
                player.inventory.rope = 1;
                draw_ascii("rope");
            }
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
        input = get_user_input(1, 2);

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
                input = get_user_input(1, 2);
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
                input = get_user_input(1, 2);
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
        println("What are you going to do next?");
        print_options(new String[]{"Sleep on the floor in the living room", "Explore the room to your right (kitchen)", "Explore the attic"});
        int input = get_user_input(1, 3);
        if (input == 1) {
            println("You had chosen to sleep on the floor in the living room, hoping to catch some rest,");
            println("but your attempt was disrupted by a noise coming from the room on your right.");
            print_options(new String[]{"Ignore the noise", "Check the source of the noise."});
            input = get_user_input(1, 2);
            if (input == 1) {
                println("You decided that the noise was just your imagination and you shouldn't worry.");
//                TODO
            } else if (input == 2) {
                println("You approached the room to the right, being cautious as you entered.");
                println("But the creaking of the floor woke up the one who was sleeping peacefully there.");
                println("A forest monster has attacked you.");
                fight("forester's monster");

//                TODO
            }

        } else if (input == 2) {
            println("You approached the room to the right, being cautious as you entered.");
            println("But the creaking of the floor woke up the one who was sleeping peacefully there.");
            println("A forest monster has attacked you.");
            fight("forester's monster");

//            TODO

        } else if (input == 3) {
//            TODO

        }

    }


    public static void main(String[] args) {
        start_game();
    }

}