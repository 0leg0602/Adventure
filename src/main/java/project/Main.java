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
        int golden_coins = 0;
        int axe = 0;
        int lantern = 0;
        int first_aid_kit = 0;
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
                    player.take_damage(enemy.attack_damage);
                }

                case "counter" ->
                        println(enemy.name + " anticipated the attack from that direction and fully blocked it.");

                case "block" -> {
                    println(enemy.name + " attempted to block your attack.");
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

        public void collect_coins(int coins) {
            println("You have collected " + coins + "coins.");
            println("You have " + inventory.golden_coins + " + " + coins + "= " + (inventory.golden_coins + coins) + " golden coins.");
            inventory.golden_coins += coins;
        }
    }

    static Player player;

    public static class Enemy {
        String name;
        int health;
        int max_health;
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

        public void attack(int player_block_dir, int move) {
            String[] split = moves[move].split(" ");
            int attack_dir = 0;
            for (int i = 0; i < split.length; i++) {
                String action = split[i];
                if (action.contains("attack")) {
                    attack_dir = i + 1;
                }

            }

            if (split[0].contains("rest")) {
                frustration--;
            }

            String attack_dir_string = switch (attack_dir) {
                case 1 -> "from above";
                case 2 -> "from below";
                case 3 -> "from the left";
                case 4 -> "from the right";
                default -> "none";
            };
            if (attack_dir > 0) {

                println(name + " attacks " + attack_dir_string + ".");
                if (attack_dir != player_block_dir) {
                    player.take_damage(attack_damage);
                } else {
                    println("You have fully blocked the attack.");
                }

            } else if (attack_dir == 0 && player_block_dir > 0) {
                println("You attempted to block, but " + name + " did not attack.");
            }

        }
    }


    public static void print_options(String[] options) {
        println("");
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
                println("");
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
                enemy.max_health = 100;
                enemy.block = 3;
                enemy.attack_damage = 20;
                enemy.level = 5;
                enemy.frustration = 0;
                enemy.frustration_limit = 4;
                enemy.moves_str = new String[]{"gazes intently at you.", "leaned to the right.", "leaned to the left.", "ducked.", "dropped his hands tiredly"};
//                                          top bottom left right
                enemy.moves = new String[]{"repel,none repel,none repel,none repel,none", "none,none none,none none,none counter,attack", "none,none none,none counter,attack none,none", "block,rest block,rest block,rest block,rest", "none,none none,none none,none none,none"};

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

        print_banner(enemy.name + ": level: " + enemy.level + ", health: " + enemy.health + "/" + enemy.max_health + ", attack:" + enemy.attack_damage + ", block:" + enemy.block);


        Random rand = new Random();

        while (enemy.health > 0) {
            int move;
            int player_block_dir = 0;
            if (enemy.frustration < enemy.frustration_limit) {
                move = rand.nextInt(enemy.moves_str.length - 1);
            } else {
                move = enemy.moves_str.length - 1;
                enemy.frustration = 0;
            }
            println(enemy.name + " " + enemy.moves_str[move]);
            while (true) {
                println("What will you do?");
                print_options(new String[]{"attack", "block", "action"});
                int input = get_user_input(1, 3);
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
                    println("From which direction would you like to defend against an attack?");
                    print_options(new String[]{"from above", "from below", "from the left", "from the right", "back"});
                    input = get_user_input(0, 4);
                    if (input == 0) {
                        continue;
                    }
                    player_block_dir = input;
                    break;

                } else if (input == 3) {
                    print_options(new String[]{"do nothing", "escape", "back"});
                    input = get_user_input(0, 2);
                    if (input == 1) {
                        break;
                    } else if (input == 2) {
                        println("Are you sure you want to escape?");
                        print_options(new String[]{"yes", "back"});
                        input = get_user_input(0, 1);
                        if (input == 1) {
                            println(enemy.escape_death);
                            gameover();
                        } else if (input == 0) {
                            continue;
                        }

                    } else if (input == 0) {
                        continue;
                    }
                }


            }

            if (enemy.health > 0) {
                enemy.attack(player_block_dir, move);
                enemy.frustration++;
            }
            if (player.health < 1) {
                gameover();
            }
        }

        print_banner(" THE FIGHT IS OVER ");
        println("You won!");
        println(enemy.name + " is defeated!");
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
            chapter1_page1();
        } else if (input == 2) {
            println("You decided to pass the forester's house, continuing on your way");
            println("The darkness deepened rapidly, and you couldn't see even the tip of your finger.");
            println("In just a few moments, total darkness had descended.");
            if (player.inventory.lantern == 1) {
                println("You lit the lantern - your last hope.");
                println("Under its faint light, you continued to wander through the forest in search of shelter.");
                println("After walking a little farther, you came across the forester's house once again.");
                print_options(new String[]{"Go into the forester's house", "Walk past the forester's house"});
                input = get_user_input(1, 2);
                if (input == 1) {
                    chapter1_page1();
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
                print_options(new String[]{"Lay down on the ground, trying to rest until the morning", "Attempt to make your way blindly through the forest in search of a rce of light"});
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


    private static void chapter1_page1() {
        print_banner("Chapter 1 : An Abandoned, overgrown forester's cottage");
        println("You have opened the door entered the forester's cottage.");
        println("You walked into the living room.");
        println("The fireplace in the living room was lit, and due to this light, you were able to see. ");
        println("You noticed that there was a room to your right that looked like a kitchen,");
        println("and at the far end of the room, there was a ladder leading up to the attic.");
        println("What are you going to do next?");

        print_options(new String[]{"Sleep on the floor in the living room.", "Explore the room to your right (kitchen).", "Explore the attic."});
        int input = get_user_input(1, 3);

        if (input == 3) {
            println("You had climbed up the ladder in an attempt to explore the attic.");
            println("However, as you ascended further from the firelit room below,");
            println("the light from the fireplace was unable to penetrate the darkness,");
            println("making it impossible for you to see anything.");

            if (player.inventory.lantern == 1) {
                print_options(new String[]{"Go back downstairs into the main room.", "Attempt to explore the attic in the darkness, feeling your way through the space.", "Light up the lantern."});
                input = get_user_input(1, 3);
            } else {
                print_options(new String[]{"Go back downstairs into the main room.", "Attempt to explore the attic in the darkness, feeling your way through the space."});
                input = get_user_input(1, 2);
            }

            if (input == 2) {
                println("You had attempted to navigate through the attic,");
                println("relying on your sense of sight as your guide.");
                println("And then, suddenly, you caught sight of something: a dimly lit light source in the distance.");
                println("You moved closer to it, but only realized too late that it was not a lantern or a light source");
                println("it was actually a hole in the floor.");
                println("As you fell through the opening, everything went black");

                print_options(new String[]{"Open your eyes."});
                get_user_input(1, 1);

                println("You saw yourself gazing up at the fearsome form of the forester's monster looming above you");
                gameover();

            } else if (input == 3) {
                println("You lit up your lantern, casting its warm glow into the space around you.");
                println("But in doing so, you inadvertently disturbed one who was peacefully sleeping there");
                println("an angry bat stirred from its slumber and flew towards you with a snarl.");
                println("As it attacked, its sharp claw swiped at your face, causing you to recoil in pain.");

                print_options(new String[]{"Defend yourself."});
                get_user_input(1, 1);

                println("Trying to defend yourself against the ferocious creature, you lost your grip on the ladder.");
                println("Unfortunately, while falling, your body flipped awkwardly, and you landed on the back of your head.");
                println("The impact was too much for your neck to withstand, and it snapped.");
                gameover();

            }

            print_options(new String[]{"Sleep on the floor in the living room.", "Explore the room to your right (kitchen)."});
            input = get_user_input(1, 2);

        }

        if (input == 1) {
            println("You decided to sleep on the floor in the living room, hoping to catch some rest,");
            println("but your attempt was disrupted by a noise coming from the room on your right.");

            print_options(new String[]{"Ignore the noise", "Check the source of the noise."});
            input = get_user_input(1, 2);

            if (input == 1) {
                println("You decided that the noise was just your imagination and you shouldn't worry.");
                chapter1_page2(false);
            } else if (input == 2) {
                println("You approached the room to the right, being cautious as you entered.");
                println("But the creaking of the floor woke up the one who was sleeping peacefully there.");
                println("A forest monster has attacked you.");
                fight("forester's monster");
                println("Too tired to even stand, you collapsed to the ground.");
                chapter1_page2(true);
            }

        } else if (input == 2) {
            println("You approached the room to the right, being cautious as you entered.");
            println("But the creaking of the floor woke up the one who was sleeping peacefully there.");
            println("A forest monster has attacked you.");
            fight("forester's monster");
            println("Too tired to even stand, you collapsed to the ground.");
            chapter1_page2(true);
        }


    }

    public static void chapter1_page2(boolean is_forester_monster_dead) {
        println("You fell asleep peacefully without being disturbed throughout the night. ");
        println("You woke up early in the morning feeling refreshed.");

        boolean is_kitchen_explored = false;
        boolean is_living_room_explored = false;
        boolean is_attic_explored = false;
        boolean is_secret_room_found = false;
        boolean is_secret_room_explored = false;

        while (true) {

            StringBuilder options_string = new StringBuilder();
            for (String option : new String[]{"Leave the cottage.",
                    (!is_kitchen_explored) ? "Explore the room to your right (kitchen)." : "none",
                    (!is_living_room_explored) ? "Explore the living room." : "none",
                    (!is_secret_room_found) ? "Explore the cottage in more detail." : "none",
                    (!is_secret_room_explored && is_secret_room_found) ? "Explore the secret room" : "none",
                    (!is_attic_explored) ? "Explore the attic." : "none"}) {
                if (!option.equals("none")) {
                    options_string.append(option).append("split");
                }

            }
            String[] options = options_string.toString().split("split");


            print_options(options);
            int input = get_user_input(1, options.length);

            System.out.println(options[input-1]);

            if (options[input - 1].equals("Explore the room to your right (kitchen).")){
                println("You approached the room to the right, being cautious as you entered.");
                if (is_forester_monster_dead){
                    println("You notice that the forester's monster corpse is gone, and in its place there is a small pile of gold coins.");
                    player.collect_coins(10);
                }

            }


        }

    }


    public static void main(String[] args) {
        start_game();
    }

}