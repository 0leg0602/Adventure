package project;

/*
        Author: Oleg Poliakov
        Start Time: 2024-09-30, Mon, 11:56
        End Time:
*/

import java.util.*;

public class Main {

    static Scanner scan = new Scanner(System.in);
//    static Random rand = new Random();

    static int print_speed = 20;

    static boolean skip = false;


    public static class Player {
        int health = 100;
        HashMap<String, Integer> inventory = new HashMap<>();

        public void collect(String item, int quantity) {
            println("You have collected " + quantity + " " + item.replace("_", " "));
            if (inventory.get(item) > 1) {
                println("Now you have " + inventory.get(item) + " + " + quantity + " = " + (inventory.get(item) + quantity));
            }
            inventory.replace(item, (inventory.get(item) + quantity));
        }

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


    public static int get_user_input(int min, int max) {
        skip = false;
        while (true) {
            print(">>> ");
            String input = scan.nextLine();

            switch (input) {
                case "?" -> {
                    println("Health: " + player.health);
                    for (String item_str : player.inventory.keySet()) {
                        if (player.inventory.get(item_str) > 0) {
                            println(item_str.replace('_', ' ') + ": " + player.inventory.get(item_str));
                        }
                    }
                    continue;
                }
                case "print speed" -> {
                    set_print_speed();
                    continue;
                }
                case "!exit" -> System.exit(0);
                case "!restart" -> start_game();
                case "!@#" -> chapter2();
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

    private static String get_user_choice(String[] choices) {
        println("");
        int min = 1;

        StringBuilder options_string = new StringBuilder();
        for (String weapon : choices) {
            if (!weapon.equals("none")) {
                options_string.append(weapon).append("\n");
            }

        }
        String[] options = options_string.toString().split("\n");

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals("back")) {
                println("0. back");
                min = 0;
            } else {
                println((i + 1) + ". " + options[i]);
            }
        }

        int user_number_input = (get_user_input(min, options.length - ((min == 0) ? 1 : 0)));

        if (user_number_input == 0) {
            return "back";
        }

        return options[user_number_input - 1];

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
                enemy.health = 20;
                enemy.max_health = 20;
                enemy.block = 0;
                enemy.attack_damage = 6;
                enemy.level = 3;
                enemy.frustration = 0;
                enemy.frustration_limit = 4;
                enemy.moves_str = new String[]{"flew above your head", "flew to the right.", "flew to the left.", "landed on the ground tired"};
//                                          top bottom left right
                enemy.moves = new String[]{"repel,attack none,none none,none none,none", "none,none none,none none,none repel,attack", "none,none none,none repel,attack none,none", "none,none none,none none,none none,none"};

                enemy.escape_death = "You tried to escape from the bat,\nbut while running you did no notice the hole in the floor and had fallen through it\nUnfortunately, while falling, your body flipped awkwardly, and you landed on the back of your head.\nThe impact was too much for your neck to withstand, and it snapped.";
            }
            default -> throw new IllegalStateException("Unexpected value: " + enemy_str);
        }

        print_banner(enemy.name + ": level: " + enemy.level + ", health: " + enemy.health + "/" + enemy.max_health + ", attack:" + enemy.attack_damage + ", block:" + enemy.block);


        while (enemy.health > 0) {
            int move;
            int player_block_dir = 0;
            if (enemy.frustration < enemy.frustration_limit) {
                Random rand = new Random();
                move = rand.nextInt(enemy.moves_str.length - 1);
            } else {
                move = enemy.moves_str.length - 1;
                enemy.frustration = 0;
            }
            println(enemy.name + " " + enemy.moves_str[move]);
            while (true) {
                println("What will you do?");
                String input = get_user_choice(new String[]{"attack", "block", "action"});
                if (input.equals("attack")) {

                    input = get_user_choice(new String[]{
                            "knife",
                            (player.inventory.get("axe") > 0) ? "axe" : "none",
                            (player.inventory.get("old_frying_pan") > 0) ? "old frying pan" : "none",
                            "back"
                    });

                    if (input.equals("back")) {
                        continue;
                    }

                    int damage = switch (input) {
                        case "knife" -> 5;
                        case "old frying pan" -> 10;
                        case "axe" -> 12;
                        default -> throw new IllegalStateException("Unexpected value: " + input);
                    };

                    println("How do you hit with a " + input + "?");
                    input = get_user_choice(new String[]{"from above", "from below", "from the left", "from the right", "back"});
                    if (input.equals("back")) {
                        continue;
                    }
                    switch (input) {
                        case "from above" ->
                                player.attack(enemy, damage, enemy.moves[move].split(" ")[0].split(",")[0]);
                        case "from below" ->
                                player.attack(enemy, damage, enemy.moves[move].split(" ")[1].split(",")[0]);
                        case "from the left" ->
                                player.attack(enemy, damage, enemy.moves[move].split(" ")[2].split(",")[0]);
                        case "from the right" ->
                                player.attack(enemy, damage, enemy.moves[move].split(" ")[3].split(",")[0]);
                    }

                    break;

                } else if (input.equals("block")) {
                    println("From which direction would you like to defend against an attack?");
                    input = get_user_choice(new String[]{"from above", "from below", "from the left", "from the right", "back"});
                    if (input.equals("back")) {
                        continue;
                    }

                    switch (input) {
                        case "from above" -> player_block_dir = 1;
                        case "from below" -> player_block_dir = 2;
                        case "from the left" -> player_block_dir = 3;
                        case "from the right" -> player_block_dir = 4;
                    }


                    break;

                } else if (input.equals("action")) {
                    input = get_user_choice(new String[]{"do nothing", "escape", "back"});
                    if (input.equals("do nothing")) {
                        break;
                    } else if (input.equals("escape")) {
                        println("Are you sure you want to escape?");
                        input = get_user_choice(new String[]{"yes", "back"});
                        if (input.equals("yes")) {
                            println(enemy.escape_death);
                            gameover();
                        } else if (input.equals("no")) {
                            continue;
                        }

                    } else if (input.equals("back")) {
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
            case "first_aid_kit" -> println("""
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
            case "cross" -> println("""
                    ╔════════════╗
                    ║ _        _ ║
                    ║  \\\\    //  ║
                    ║   \\\\  //   ║
                    ║    \\\\//    ║
                    ║     ||     ║
                    ║    //\\\\    ║
                    ║   //  \\\\   ║
                    ║ _//    \\\\_ ║
                    ║            ║
                    ╚════════════╝
                    """);
            case "triangle" -> println("""
                    ╔════════════╗
                    ║   ______   ║
                    ║  /\\_____\\  ║
                    ║ _\\ \\__/_/_ ║
                    ║/\\_\\ \\_____\\║
                    ║\\ \\ \\/ / / /║
                    ║ \\ \\/ /\\/ / ║
                    ║  \\/_/\\/_/  ║
                    ║            ║
                    ╚════════════╝
                    """);
            case "light" -> println("""
                    ╔═════════════════════╗
                    ║   *****      *****  ║
                    ║  *     *    *     * ║
                    ║ *  ***  *  *  ***  *║
                    ║*   ***   *   ***   *║
                    ║ *  ***  *  *  ***  *║
                    ║  *     *    *     * ║
                    ║   *****      *****  ║
                    ╚═════════════════════╝
                    """);
            case "cube" -> println("""
                    ╔═════════════╗
                    ║ _________   ║
                    ║|  _____  |\\ ║
                    ║| |\\ ___| | \\║
                    ║| | |   | | |║
                    ║| | |___| | |║
                    ║\\ | |____\\| |║
                    ║ \\|_________|║
                    ║             ║
                    ╚═════════════╝
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

        player.inventory.put("golden_coins", 0);
        player.inventory.put("axe", 0);
        player.inventory.put("lantern", 0);
        player.inventory.put("first_aid_kit", 0);
        player.inventory.put("rope", 0);
        player.inventory.put("wooden_key", 0);
        player.inventory.put("old_frying_pan", 0);
        player.inventory.put("backpack", 0);


        println("You decided to go hunting what will you take with you: ");


        String input = get_user_choice(new String[]{"axe", "lantern", "first aid kit", "rope"});


        String tool_to_take = input.replace(' ', '_');

        player.inventory.replace(tool_to_take, 1);
        draw_ascii(tool_to_take);

        println("You decided to venture into the forest, carrying a " + input + ".");
        println("The afternoon sun was beginning to set, signaling that evening was approaching.");
        println("You walked through the dense underbrush from tree to tree,");
        println("but apart from a couple of squirrels, you encountered no one.");
        println("As it became clear that you were lost, another problem began to emerge:");
        println("Night was already coming.");
        println("");
        println("It was essential to find shelter for the night before darkness fell.");
        println("Ahead, you spotted a hut - an abandoned, overgrown forester's cottage.");


        input = get_user_choice(new String[]{"Go into the forester's house.", "Walk past the forester's house."});

        if (input.equals("Go into the forester's house.")) {

            chapter1_page1();

        } else if (input.equals("Walk past the forester's house.")) {

            println("You decided to pass the forester's house, continuing on your way");
            println("The darkness deepened rapidly, and you couldn't see even the tip of your finger.");
            println("In just a few moments, total darkness had descended.");

            if (player.inventory.get("lantern") == 1) {

                println("You lit the lantern - your last hope.");
                println("Under its faint light, you continued to wander through the forest in search of shelter.");
                println("After walking a little farther, you came across the forester's house once again.");
                input = get_user_choice(new String[]{"Go into the forester's house.", "Walk past the forester's house."});

                if (input.equals("Go into the forester's house.")) {

                    chapter1_page1();

                } else if (input.equals("Walk past the forester's house.")) {

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
                input = get_user_choice(new String[]{"Lay down on the ground, trying to rest until the morning.", "Attempt to make your way blindly through the forest in search of a source of light."});

                if (input.equals("Lay down on the ground, trying to rest until the morning.")) {

                    println("You lay down on the ground and fell asleep.");
                    println("But unfortunately, you didn't wake up in the morning.");
                    gameover();

                } else if (input.equals("Attempt to make your way blindly through the forest in search of a source of light.")) {

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

        String input = get_user_choice(new String[]{"Sleep on the floor in the living room.", "Explore the room to your right (kitchen).", "Explore the attic."});

        if (input.equals("Explore the attic.")) {

            println("You had climbed up the ladder in an attempt to explore the attic.");
            println("However, as you ascended further from the firelit room below,");
            println("the light from the fireplace was unable to penetrate the darkness,");
            println("making it impossible for you to see anything.");


            input = get_user_choice(new String[]{"Go back downstairs into the main room.", "Attempt to explore the attic in the darkness, feeling your way through the space.", (player.inventory.get("lantern") == 1) ? "Light up the lantern." : "none"});


            if (input.equals("Attempt to explore the attic in the darkness, feeling your way through the space.")) {

                println("You had attempted to navigate through the attic,");
                println("relying on your sense of sight as your guide.");
                println("And then, suddenly, you caught sight of something: a dimly lit light source in the distance.");
                println("You moved closer to it, but only realized too late that it was not a lantern or a light source");
                println("it was actually a hole in the floor.");
                println("As you fell through the opening, everything went black");

                get_user_choice(new String[]{"Open your eyes."});

                println("You saw yourself gazing up at the fearsome form of the forester's monster looming above you");
                gameover();

            } else if (input.equals("Light up the lantern.")) {

                println("You lit up your lantern, casting its warm glow into the space around you.");
                println("But in doing so, you inadvertently disturbed one who was peacefully sleeping there");
                println("an angry bat stirred from its slumber and flew towards you with a snarl.");
                println("As it attacked, its sharp claw swiped at your face, causing you to recoil in pain.");

                get_user_choice(new String[]{"Defend yourself."});

                println("Trying to defend yourself against the ferocious creature, you lost your grip on the ladder.");
                println("Unfortunately, while falling, your body flipped awkwardly, and you landed on the back of your head.");
                println("The impact was too much for your neck to withstand, and it snapped.");
                gameover();

            }

            input = get_user_choice(new String[]{"Sleep on the floor in the living room.", "Explore the room to your right (kitchen)."});


        }

        if (input.equals("Sleep on the floor in the living room.")) {

            println("You decided to sleep on the floor in the living room, hoping to catch some rest,");
            println("but your attempt was disrupted by a noise coming from the room on your right.");

            input = get_user_choice(new String[]{"Check the source of the noise.", "Ignore the noise."});

            if (input.equals("Ignore the noise.")) {
                println("You decided that the noise was just your imagination and you shouldn't worry.");
                chapter1_page2(false);

            } else if (input.equals("Check the source of the noise.")) {

                println("You approached the room to the right, being cautious as you entered.");
                println("But the creaking of the floor woke up the one who was sleeping peacefully there.");
                println("A forest monster has attacked you.");
                fight("forester's monster");
                println("You had taken a few steps and exited the living room before");
                println("being too tired to even stand, whereupon you collapsed to the ground.");
                chapter1_page2(true);
            }

        } else if (input.equals("Explore the room to your right (kitchen).")) {
            println("You approached the room to the right, being cautious as you entered.");
            println("But the creaking of the floor woke up the one who was sleeping peacefully there.");
            println("A forest monster has attacked you.");
            fight("forester's monster");
            println("You had taken a few steps and exited the living room before");
            println("being too tired to even stand, whereupon you collapsed to the ground.");
            chapter1_page2(true);
        }


    }

    public static void chapter1_page2(boolean is_forester_monster_dead) {
        println("You fell asleep peacefully without being disturbed throughout the night. ");
        println("You woke up early in the morning feeling refreshed.");

        boolean is_kitchen_explored = false;
        boolean is_attic_explored = false;

        while (true) {
            if (is_kitchen_explored && player.inventory.get("wooden_key") == 1 && player.inventory.get("old_frying_pan") == 0) {
                println("Now that you have a backpack, you can take the old frying pan with you.");

                String input = get_user_choice(new String[]{"Take it.", "Do not take it."});

                if (input.equals("Take it.")) {

                    println("You decided to take the frying pan with you.");
                    player.inventory.replace("old_frying_pan", 1);

                } else if (input.equals("Do not take it.")) {

                    println("You decided not to take the frying pan with you.");
                }
            }


            String input = get_user_choice(new String[]{"Leave the cottage.",
                    (!is_kitchen_explored) ? "Explore the room to your right (kitchen)." : "none",
                    (!is_attic_explored) ? "Explore the attic." : "none"});


            if (input.equals("Leave the cottage.")) {
                println("You approached the front door of the cottage.");
                println("However, when you attempted to open the door, you found that it was locked.");
                println("What will you do next?");

                while (true) {

                    input = get_user_choice(new String[]{
                            "Try to knock down the door.",
                            (player.inventory.get("axe") == 1) ? "Break down the door with an axe." : "none",
                            "Return to the living room.",
                            (player.inventory.get("wooden_key") == 1) ? "Open the door with a wooden key." : "none"
                    });


                    if (input.equals("Break down the door with an axe.")) {
                        for (int i = 0; i <= 3; i++) {
                            println("You swung an axe.");

                            get_user_choice(new String[]{"Hit the door with an axe."});

                            println("Splinters flew out of the door");
                        }
                        println("However, this time the axe was stopped by a hidden metal mechanism inside the door.");
                        println("As you listened, a strange, metallic sound echoed through the air,");
                        println("and within a few seconds, your body was riddled with a hundred metallic darts.");
                        gameover();

                    } else if (input.equals("Try to knock down the door.")) {
                        println("The door wouldn't budge.");
                    } else if (input.equals("Return to the living room.")) {
                        break;
                    } else if (input.equals("Open the door with a wooden key.")) {
                        println("Upon opening the door you hear strange metallic noise.");
                        println("You had triggered some kind of mechanism.");
                        println("A single metallic dart shot you in the back.");
                        player.take_damage(5);
                        chapter2();
                    }
                }
                continue;

            }

            if (input.equals("Explore the attic.")) {
                println("You climbed up the ladder leading to the attic, being cautious as you entered.");
                println("But the creaking of the floor woke up the one who was sleeping peacefully there.");
                println("A bat has attacked you.");
                fight("bat");
                println("Within seconds, the bat's corpse had disappeared, leaving only one wing and a few coins.");
                println("You picked up the bat's wing");
                player.collect("golden_coins", 3);

                get_user_choice(new String[]{"Go further."});

                println("Going further you found a chest.");

                get_user_choice(new String[]{"Open the chest."});

                println("Inside the chest you found a wooden key and a backpack.");
                println("Now you can finally carry more things with you.");
                player.inventory.replace("backpack", 1);
                player.inventory.replace("wooden_key", 1);
                is_attic_explored = true;
                get_user_choice(new String[]{"Return to the living room."});
                continue;
            }
            if (input.equals("Explore the room to your right (kitchen).")) {
                println("You approached the room to the right, being cautious as you entered.");
                if (is_forester_monster_dead) {
                    println("You notice that the forester's monster corpse is gone, and in its place there is a small pile of gold coins.");
                    player.collect("golden_coins", 10);
                }
                println("Upon closer examination of the room, you were certain it was a kitchen,");
                println("yet you couldn't see any food out in the open.");
                println("Therefore, you decided to search through the cabinets.");

                boolean closet1 = false;
                boolean closet2 = false;
                boolean closet3 = false;
                boolean closet4 = false;
                boolean closet5 = false;

                while (true) {
                    println("Which cabinet to search?");


                    input = get_user_choice(new String[]{
                            (!closet1) ? "cabinet on the left bottom" : "none",
                            (!closet2) ? "cabinet on the right bottom" : "none",
                            (!closet3) ? "cabinet on the left top" : "none",
                            (!closet4) ? "cabinet on the right top" : "none",
                            (!closet5) ? "cabinet that is in front of you" : "none",

                    });

                    switch (input) {
                        case "cabinet on the left bottom" -> closet1 = true;
                        case "cabinet on the right bottom" -> closet2 = true;
                        case "cabinet on the left top" -> closet3 = true;
                        case "cabinet on the right top" -> closet4 = true;
                        case "cabinet that is in front of you" -> closet5 = true;
                    }

                    if (closet1 && closet2 && closet3 && closet4 && closet5) {
                        println("Finally, one of the cabinets wasn't empty after all.");
                        println("In that cabinet, you found an old frying pan.");
                        println("It was still pretty durable, so you could use it as a potential weapon.");
                        if (player.inventory.get("backpack") == 1) {
                            input = get_user_choice(new String[]{"Take it.", "Do not take it."});
                            if (input.equals("Take it.")) {
                                println("You decided to take the frying pan with you.");
                                player.inventory.replace("old_frying_pan", 1);
                            } else if (input.equals("Do not take it.")) {
                                println("You decided not to take the frying pan with you.");
                            }
                        } else {
                            println("However, you didn't have a bag to take the frying pan with you, so you had to leave it behind.");
                        }
                        break;
                    }

                    println("You open the cabinet, but it is completely empty.");


                }

                println("You leave the kitchen and return to the living room.");
                is_kitchen_explored = true;


            }


        }

    }

    public static void chapter2() {
        println("You leave the cottage and go outside.");
        print_banner("Chapter 2: Outside");
        get_user_choice(new String[]{"Look around."});
        println("You look around, but you're unable to find any kind of trail.");
        get_user_choice(new String[]{"Go round the cottage."});
        println("To your surprise, there are no trails around the cottage.");
        println("It was just in the middle of the forest, with no path leading to it.");
        println("So you had only one choice.");
        get_user_choice(new String[]{"Attempt to go through the forest."});
        println("You attempt to go through the forest.");

        int distance = 3;
        int number_of_tries = 0;
        Random rand = new Random();

        String[] random_case = {"cube","light","cross","triangle"};
        for (int i = random_case.length - 1; i > 0; i--)
        {
            int index = rand.nextInt(i + 1);
            String  a = random_case[index];
            random_case[index] = random_case[i];
            random_case[i] = a;
        }

        System.out.println(Arrays.toString(random_case));

        while (distance < 10) {

            if (distance <= 0){
                println("Making your way through the forest you noticed that you had made a loop and returned back to the cottage.");
                get_user_choice(new String[]{"Attempt to go through the forest again."});
                distance = 3;
            }
            int random_sign = rand.nextInt(4) + 1;
            String sign = switch (random_sign) {
                case 1 -> "cross";
                case 2 -> "triangle";
                case 3 -> "light";
                case 4 -> "cube";
                default -> throw new IllegalStateException("Unexpected value");
            };
            println("You continue walking until you notice a tree with a strange sign carved into it:");
            println("");
            int prev_print_speed = print_speed;
            print_speed = (print_speed <= 5) ? 0 : 5;
            draw_ascii(sign);
            print_speed = prev_print_speed;
            String input = get_user_choice(new String[]{"go forward", "go back", "go left", "go right"});
            if (
                    input.equals("go forward") && sign.equals(random_case[0]) ||
                            input.equals("go back") && sign.equals(random_case[1]) ||
                            input.equals("go left") && sign.equals(random_case[2]) ||
                            input.equals("go right") && sign.equals(random_case[3])

            ) {
                if (number_of_tries >= 3) {
                    println((rand.nextInt(2) == 0) ? "You won't recognize this part of the forest."
                            : (rand.nextInt(2) == 0) ? "The trees began to look a little different." :
                            "It seems that the tree branches have begun to shift, letting in more sunlight");
                }
                distance++;
            } else {
                if (number_of_tries >= 3) {
                    println((rand.nextInt(2) == 0) ? "It feels like you've been here before."
                            : (rand.nextInt(2) == 0) ? "The trees around you looked familiar." :
                            "The familiar landmarks seem to be repeating themselves");
                }
                distance--;
            }
            number_of_tries++;
        }

        println("You made you way out of the forest.");
        println("To be continued...");
        gameover();
    }


    public static void main(String[] args) {
        start_game();
    }

}