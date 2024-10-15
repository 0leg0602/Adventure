package project;

/*
        Author: Oleg Poliakov
        Start Time: 2024-09-30, Mon, 11:56
        End Time: 2024-10-14, Mon, 19:25

        # Extra features:
        1. The text is not printed instantly, but is printed character by character.
        2. User is capable of change the print speed of the text.
        3. Program will not break if incorrect input is entered.
        4. Player has an inventory.
        5. User can check player's statistics and inventory at any time.
        6. There is a lot of ascii art to help user understand the story.
        7. There are a lot of different enemies in the game.
        8. The game has unique and interesting combat mechanics.
        9. Each enemy in the game is unique.
        10. There are many weapons in the game.
        11. Some enemies have their own unique weak points.
        12. The game has a timer that shows how long it took you to complete the game.
        13. Each function that was repeated was placed in a separate method:

        13.1 print() and println() for outputting text character by character.
        13.2 get_user_choice() for printing options and receiving user's input.
        13.3 print_banner() for printing text inside a banner.
        13.4 fight() for initializing fight with an enemy.
        13.5 draw_ascii() for printing ascii art.
        13.6 gameover() for ending the game and prompting user for restart.
        13.6 Player.collect() for collecting items.
        13.6 Player.lose() for losing items.
        13.6 Player.take_damage() for moments when the player takes damage.
        13.6 Player.attack() for moments when the player attacks an enemy.
        13.6 Player.heal() for moments when the player uses a first aid kit.
        13.6 Enemy.take_damage() for moments when an enemy takes damage.
        13.6 Enemy.attack() for moments when an enemy attacks the player.

        And many more...

*/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static Scanner scan = new Scanner(System.in);

    static int print_speed = 20;

    static boolean skip = false;

    static boolean cheats = false;
    static boolean in_a_fight = false;

    static long start_time = 0;


    public static class Player {
        public String name;
        int health = 100;
        HashMap<String, Integer> inventory = new HashMap<>();

        public void collect(String item, int quantity) {
            println("You collected " + quantity + " " + item.replace("_", " ") + ".");
            if (inventory.get(item) > 1) {
                println("Now you have " + inventory.get(item) + " + " + quantity + " = " + (inventory.get(item) + quantity) + " " + item.replace("_", " ") + ".");
            } else {
                println("Now you have " + (inventory.get(item) + quantity) + " " + item.replace("_", " ") + ".");
            }
            inventory.replace(item, (inventory.get(item) + quantity));
        }

        public void lose(String item, int quantity) {
            println("You lost " + quantity + " " + item.replace("_", " ") + ".");
            if (inventory.get(item) > 1) {
                println("Now you have " + inventory.get(item) + " - " + quantity + " = " + (inventory.get(item) - quantity) + " " + item.replace("_", " ") + ".");
            } else {
                println("Now you have " + (inventory.get(item) - quantity) + " " + item.replace("_", " ") + ".");
            }
            inventory.replace(item, (inventory.get(item) - quantity));
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
                    if (enemy.block + enemy.armor < damage) {
                        println("However, it was only partially successful, allowing you to deal some damage anyway.");
                        println(enemy.block + enemy.armor + " damage blocked.");
                        enemy.take_damage(damage - (enemy.block + enemy.armor));
                    } else {
                        println(enemy.name + " fully blocked your attack, as you didn't deal enough damage.");
                    }
                }

                case "none" -> {
                    if (enemy.armor > 0) {
                        if (enemy.armor >= damage) {
                            println(enemy.name + " armor was too strong and you couldn't penetrate it");
                            return;
                        } else {
                            println("You were able to penetrate the enemy's armor but it still partially blocked some damage.");
                            println(damage + " - " + enemy.armor + " = " + (damage - enemy.armor));
                            damage -= enemy.armor;

                            println("You attack and land a successful hit, dealing damage.");
                            enemy.take_damage(damage);
                            return;
                        }
                    }
                    println("You attack and land a successful hit, dealing damage.");
                    enemy.take_damage(damage);

                }
            }

        }

        public void heal() {
            if (inventory.get("first_aid_kit") > 0) {
                println("You heal yourself using first aid kit.");
                lose("first_aid_kit", 1);
                int prev_health = health;
                health += 50;
                if (health > 100) {
                    health = 100;
                }
                println("You health is " + prev_health + " + 50 = " + health);
            } else {
                println("You don't have any first aid kits with you.");
            }
        }
    }

    static HashMap<String, Integer> items_cost = new HashMap<>();
    static HashMap<String, Integer> shop_items = new HashMap<>();

    static Player player;

    public static class Enemy {
        String name;
        int health;
        int max_health;
        int armor;
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
                case "!print speed", "!ps" -> {
                    set_print_speed();
                    continue;
                }
                case "!exit" -> System.exit(0);
                case "!restart" -> start_game();
                case "!cheats" -> {
                    cheats = !cheats;
                    println("cheats " + ((cheats) ? "enabled" : "disabled"));
                    continue;
                }
                case "!heal" -> {
                    if (in_a_fight){
                        println("You are in a fight and cannot heal using commands.");
                    } else {
                        player.heal();
                    }
                    continue;
                }
                case "!help" -> {
                    println("!help -> show this list.");
                    println("!heal -> heal using a first aid kit.");
                    println("!exit -> close the program.");
                    println("!restart -> restart the game.");
                    println("!print speed or !ps -> set the print speed.");
                    println("!cheats -> enable/disable cheats.");
                    continue;
                }
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

        List<String> options_list = new ArrayList<>();
        for (String weapon : choices) {
            if (!weapon.equals("none")) {
                options_list.add(weapon);
            }

        }
        String[] options = options_list.toArray(new String[0]);

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
        in_a_fight = true;
        print_banner(" THE FIGHT BEGINS ");


        Enemy enemy = new Enemy();

        switch (enemy_str) {
            case "forester's monster" -> {
                enemy.name = "The forester's monster";
                enemy.health = 24;
                enemy.max_health = 100;
                enemy.armor = 1;
                enemy.block = 3;
                enemy.attack_damage = 20;
                enemy.level = 5;
                enemy.frustration = 0;
                enemy.frustration_limit = 4;
                enemy.moves_str = new String[]{"gazes intently at you.", "leaned to the right.", "leaned to the left.", "ducked.", "dropped his hands tiredly."};
//                                          top bottom left right
                enemy.moves = new String[]{"repel,none repel,none repel,none repel,none", "none,none none,none none,none counter,attack", "none,none none,none counter,attack none,none", "block,rest block,rest block,rest block,rest", "none,none none,none none,none none,none"};

                enemy.escape_death = "You tried to escape from the forester's monster,\nand you managed to run to the front door of the house.\nUnfortunately, when you tried to open it, you found that it was locked.\nBy the time you turned around, it was already too late.";
            }
            case "bat" -> {
                enemy.name = "bat";
                enemy.health = 20;
                enemy.max_health = 20;
                enemy.armor = 0;
                enemy.block = 0;
                enemy.attack_damage = 6;
                enemy.level = 3;
                enemy.frustration = 0;
                enemy.frustration_limit = 4;
                enemy.moves_str = new String[]{"flew above your head.", "flew to the right.", "flew to the left.", "landed on the ground tired."};
//                                          top bottom left right
                enemy.moves = new String[]{"counter,attack none,none none,none none,none", "none,none none,none none,none counter,attack", "none,none none,none counter,attack none,none", "none,none none,none none,none none,none"};

                enemy.escape_death = "You tried to escape from the bat,\nbut while running you did no notice the hole in the floor and had fallen through it\nUnfortunately, while falling, your body flipped awkwardly, and you landed on the back of your head.\nThe impact was too much for your neck to withstand, and it snapped.";
            }
            case "guard" -> {
                enemy.name = "The knight in metal armor";
                enemy.health = 200;
                enemy.max_health = 200;
                enemy.armor = 30;
                enemy.block = 20;
                enemy.attack_damage = 35;
                enemy.level = 22;
                enemy.frustration = 0;
                enemy.frustration_limit = 6;
                enemy.moves_str = new String[]{"raised his sword above his head.", "tilted his sword to the right.", "tilted his sword to the left.", "took a defensive stance.", "lowered his sword tiredly."};
//                                          top bottom left right
                enemy.moves = new String[]{"counter,attack none,none none,none none,none", "none,none none,none none,none counter,attack", "none,none none,none counter,attack none,none", "block,none block,none block,none block,none", "none,none none,none none,none none,none"};

                enemy.escape_death = "You looked around, trying to find a way to escape,\nbut there was nowhere to run - the knight blocked the only exit.\nUnfortunately, while looking around,\nyou did not notice how the knight raised his sword and swung it at you.";
            }
            case "boar" -> {
                enemy.name = "The boar";
                enemy.health = 40;
                enemy.max_health = 40;
                enemy.armor = 2;
                enemy.block = 3;
                enemy.attack_damage = 17;
                enemy.level = 4;
                enemy.frustration = 0;
                enemy.frustration_limit = 4;
                enemy.moves_str = new String[]{"lowered his head down.", "took a step to the right", "took a step to the left", "leaned towards the ground.", "tiredly took a step back."};
//                                          top bottom left right
                enemy.moves = new String[]{"none,none counter,attack none,none none,none", "none,none none,none counter,attack none,none", "none,none none,none none,none counter,attack", "counter,attack none,none none,none none,none", "none,none none,none none,none none,none"};

                enemy.escape_death = "You tried to run away from the boar through the meadows.\nYou ran through the forest as fast as you could, not looking where you were going,\nbut then you heard a strange metallic sound beneath your feet.\nYOU'VE BEEN SNARED! The bear trap has sprung,\nand you're now dangling from its deadly metal jaws.";
            }
            case "living_tree" -> {
                enemy.name = "The living tree";
                enemy.health = 120;
                enemy.max_health = 120;
                enemy.armor = 4;
                enemy.block = 7;
                enemy.attack_damage = 21;
                enemy.level = 7;
                enemy.frustration = 0;
                enemy.frustration_limit = 6;
                enemy.moves_str = new String[]{"leaned to the right.", "took a step to the left", "leaned towards the ground.", "remained motionless.",};
//                                          top bottom left right
                enemy.moves = new String[]{"counter,attack none,none none,none none,none", "none,none counter,attack none,none none,none", "none,none none,none none,none counter,attack", "none,none none,none none,none none,none",};

                enemy.escape_death = "You tried to run away from the living tree through the dense forest.\nYou ran through the forest as fast as you could, not looking where you were going,\nbut then you heard a strange metallic sound beneath your feet.\nYOU'VE BEEN SNARED! The bear trap has sprung,\nand you're now dangling from its deadly metal jaws.";
            }
            case "robot" -> {
                enemy.name = "The robot";
                enemy.health = 100;
                enemy.max_health = 100;
                enemy.armor = 8;
                enemy.block = 10;
                enemy.attack_damage = 32;
                enemy.level = 9;
                enemy.frustration = 0;
                enemy.frustration_limit = 6;
                enemy.moves_str = new String[]{"leaned to the right.", "leaned to the left.", "took a step to the right.", "took a step to the left.", "overheated."};
//                                          top bottom left right
                enemy.moves = new String[]{"none,none none,none counter,attack none,none", "none,none none,none counter,attack none,none", "none,none none,none none,none counter,attack", "none,none none,none none,none counter,attack", "none,none none,none none,none none,none"};

                enemy.escape_death = "You tried to run away from the robot through the iron forest.\nYou ran through the forest as fast as you could, not looking where you were going,\nbut then you heard a strange metallic sound beneath your feet.\nYOU'VE BEEN SNARED! The bear trap has sprung,\nand you're now dangling from its deadly metal jaws.";


                

            }
            case "dark_knight" -> {
                enemy.name = "The Dark Knight";
                enemy.health = 200;
                enemy.max_health = 200;
                enemy.armor = 8;
                enemy.block = 20;
                enemy.attack_damage = 32;
                enemy.level = 20;
                enemy.frustration = 0;
                enemy.frustration_limit = 6;
                enemy.moves_str = new String[]{"raised his sword above his head.", "tilted his sword to the right.", "tilted his sword to the left.", "placed his sword on his left shoulder.", "placed his sword on his right shoulder.", "lowered his sword tiredly."};
//                                          top bottom left right
                enemy.moves = new String[]{"counter,attack none,none none,none none,none", "none,none none,none none,none counter,attack", "none,none none,none counter,attack none,none", "none,none counter,attack none,none none,none", "counter,attack none,none none,none none,none", "none,none none,none none,none none,none"};

                enemy.escape_death = "You turned back and ran towards the light,\nbut while you were running you didn't notice the stones on the floor and you tripped,\nunfortunately the Dark Knight was not merciful to you.";
            }
            case "?????" -> {
                enemy.name = "?????";
                enemy.health = 100;
                enemy.max_health = 1000;
                enemy.armor = 4;
                enemy.block = 0;
                enemy.attack_damage = 30;
                enemy.level = 30;
                enemy.frustration = 0;
                enemy.frustration_limit = 8;
                enemy.moves_str = new String[]{"???????????????.", "???????????????.", "???????????????.", "???????????????.", "???????????????.", "???????????????."};
//                                          top bottom left right
                enemy.moves = new String[]{"counter,attack none,none none,none none,none", "none,none none,none none,none counter,attack", "none,none none,none counter,attack none,none", "none,none counter,attack none,none none,none", "counter,attack none,none none,none none,none", "none,none none,none none,none none,none"};

                enemy.escape_death = "You tried to run but you couldn't,\nyou didn't understand why,\nbut you never came out of the cave.";
            }
            default -> throw new IllegalStateException("Unexpected value: " + enemy_str);
        }

        if (enemy.name.equals("?????")) {
            print_banner(enemy.name + ": level: ????? | health: ?????/????? | armor: ????? | attack: ????? | block: ?????");
        } else {
            print_banner(enemy.name + ": level: " + enemy.level + " | health: " + enemy.health + "/" + enemy.max_health + " | armor: " + enemy.armor + " | attack:" + enemy.attack_damage + " | block:" + enemy.block);
        }


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

            if (enemy.name.equals("The robot")){
                if (enemy.frustration >= 3) {
                    if (!Arrays.equals(enemy.moves, new String[]{"none,none none,none none,none counter,attack", "none,none none,none none,none counter,attack", "none,none none,none counter,attack none,none", "none,none none,none counter,attack none,none", "none,none none,none none,none none,none"})) {
                        println("You hear a very loud beep and the robot's LED turns red.");
                        enemy.moves = new String[]{"none,none none,none none,none counter,attack", "none,none none,none none,none counter,attack", "none,none none,none counter,attack none,none", "none,none none,none counter,attack none,none", "none,none none,none none,none none,none"};
                    }
                } else {
                    if (!Arrays.equals(enemy.moves, new String[]{"none,none none,none counter,attack none,none", "none,none none,none counter,attack none,none", "none,none none,none none,none counter,attack", "none,none none,none none,none counter,attack", "none,none none,none none,none none,none"})){
                        println("You hear a very loud beep and the robot's LED goes out.");
                        enemy.moves = new String[]{"none,none none,none counter,attack none,none", "none,none none,none counter,attack none,none", "none,none none,none none,none counter,attack", "none,none none,none none,none counter,attack", "none,none none,none none,none none,none"};
                    }
                }
            }

            println(enemy.name + " " + enemy.moves_str[move]);
            while (true) {
                println("What will you do?");
                String input = get_user_choice(new String[]{"attack", "block", "action", (cheats) ? "kill" : "none"});
                if (input.equals("attack")) {

                    input = get_user_choice(new String[]{
                            "knife",
                            (player.inventory.get("axe") > 0) ? "axe" : "none",
                            (player.inventory.get("old_frying_pan") > 0) ? "old frying pan" : "none",
                            (player.inventory.get("sharp_axe") > 0) ? "sharp axe" : "none",
                            (player.inventory.get("sword") > 0) ? "sword" : "none",
                            (player.inventory.get("dark_sword") > 0) ? "dark sword" : "none",
                            (player.inventory.get("hammer") > 0) ? "hammer" : "none",
                            "back"
                    });

                    if (input.equals("back")) {
                        continue;
                    }

                    String weapon = input;

                    int damage = switch (weapon) {
                        case "knife" -> 5;
                        case "old frying pan", "hammer" -> 10;
                        case "axe" -> 12;
                        case "sharp axe" -> 16;
                        case "sword" -> 20;
                        case "dark sword" -> 30;
                        default -> throw new IllegalStateException("Unexpected value: " + weapon);
                    };

                    println("How do you hit with a " + weapon + "?");
                    input = get_user_choice(new String[]{"from above", "from below", "from the left", "from the right", "back"});
                    if (input.equals("back")) {
                        continue;
                    }

                    if (enemy.name.equals("The living tree") && weapon.contains("axe")) {
                        println("Since trees are vulnerable to axes your damage is doubled.");
                        println(damage + " x 2 = " + (damage * 2));
                        damage = damage * 2;
                    }
                    if (enemy.name.equals("The robot") && weapon.equals("hammer")) {
                        println("Since robots are vulnerable to hammers your damage is tripled.");
                        println(damage + " x 3 = " + (damage * 3));
                        damage = damage * 3;
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
                        default -> throw new IllegalStateException("Unexpected value: " + input);
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
                    input = get_user_choice(new String[]{"do nothing", "escape", (player.inventory.get("first_aid_kit") > 0) ? "heal" : "none", "back"});
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

                    } else if (input.equals("heal")) {
                        player.heal();
                        continue;

                    } else if (input.equals("back")) {
                        continue;
                    }
                } else if (input.equals("kill") && cheats){
                    enemy.take_damage(enemy.health);
                    break;

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
        in_a_fight = false;
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

        start_time = System.currentTimeMillis() / 1000L;

        player = new Player();

        player.inventory.put("golden_coins", 0);
        player.inventory.put("axe", 0);
        player.inventory.put("lantern", 0);
        player.inventory.put("first_aid_kit", 0);
        player.inventory.put("rope", 0);
        player.inventory.put("wooden_key", 0);
        player.inventory.put("old_frying_pan", 0);
        player.inventory.put("backpack", 0);
        player.inventory.put("bat_wings", 0);
        player.inventory.put("golden_bell", 0);

        player.inventory.put("hammer", 0);
        player.inventory.put("sharp_axe", 0);
        player.inventory.put("sword", 0);
        player.inventory.put("antidote", 0);
        player.inventory.put("dark_sword", 0);

        player.inventory.put("boar_tusk", 0);
        player.inventory.put("leather", 0);
        player.inventory.put("plank", 0);
        player.inventory.put("wooden_heart", 0);
        player.inventory.put("metal_nail", 0);
        player.inventory.put("red_LED", 0);
        player.inventory.put("antenna", 0);


        items_cost.put("axe", 50);
        items_cost.put("lantern", 60);
        items_cost.put("first_aid_kit", 70);
        items_cost.put("rope", 47);
        items_cost.put("wooden_key", 15);
        items_cost.put("old_frying_pan", 34);
        items_cost.put("backpack", 0);
        items_cost.put("bat_wings", 30);
        items_cost.put("golden_bell", 0);
        items_cost.put("dark_sword", 200);

        items_cost.put("boar_tusk", 13);
        items_cost.put("leather", 20);
        items_cost.put("plank", 8);
        items_cost.put("wooden_heart", 14);
        items_cost.put("metal_nail", 3);
        items_cost.put("red_LED", 7);
        items_cost.put("antenna", 12);


        items_cost.put("hammer", 50);
        items_cost.put("sharp_axe", 110);
        items_cost.put("sword", 130);
        items_cost.put("antidote", 360);

        shop_items.put("hammer", 60);
        shop_items.put("sharp_axe", 120);
        shop_items.put("sword", 140);
        shop_items.put("antidote", 670);


        println("You decided to go hunting what will you take with you: ");

        String input = get_user_choice(new String[]{"axe", "lantern", "first aid kit", "rope"});


        String tool_to_take = input.replace(' ', '_');

        player.collect(tool_to_take, 1);
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
                    player.collect("old_frying_pan", 1);

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
                println("Within seconds, the bat's corpse had disappeared, leaving only two wings and a few coins.");
                player.collect("bat_wings", 2);
                player.collect("golden_coins", 3);

                get_user_choice(new String[]{"Go further."});

                println("Going further you found a chest.");

                get_user_choice(new String[]{"Open the chest."});

                println("Inside the chest you found a wooden key and a backpack.");
                println("Now you can finally carry more things with you.");
                player.collect("backpack", 1);
                player.collect("wooden_key", 1);
                is_attic_explored = true;
                get_user_choice(new String[]{"Return to the living room."});
                continue;
            }
            if (input.equals("Explore the room to your right (kitchen).")) {
                println("You approached the room to the right, being cautious as you entered.");
                if (is_forester_monster_dead) {
                    println("You notice that the forester's monster corpse is gone, and in its place there is a small pile of gold coins.");
                    player.collect("golden_coins", 30);
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
                                player.collect("old_frying_pan", 1);
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
//        int distance = 12;
//        DEBUG
        int number_of_tries = 0;
        Random rand = new Random();

        String[] random_case = {"cube", "light", "cross", "triangle"};
        for (int i = random_case.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            String a = random_case[index];
            random_case[index] = random_case[i];
            random_case[i] = a;
        }

        while (distance < 12) {

            if (distance <= 0) {
                println("Making your way through the forest you noticed that you had made a loop ");
                println("and returned back to the cottage.");
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

        println("You made you way out of the forest and found a trail.");
        get_user_choice(new String[]{"Follow the trail"});
        println("As you was walking along the road, you noticed a sign with three arrows. ");
        println("One arrow pointed to the left and said \"The Road to Ravenwood\".");
        println("Second arrow pointed straight ahead and said \"Danger\".");
        println("The third arrow pointed to the right and said \"Forvet the merchant\".");

        boolean returned_to_the_sign = false;
        boolean been_at_the_shop = false;
        boolean is_dark_knight_defeated = false;
        boolean first_time_at_exit = true;
        boolean bridge_repaired = false;

        int tried_to_sell_the_bell = 0;

        boolean q1 = true;
        boolean q2 = true;
        boolean q3 = true;
        boolean q4 = true;
        boolean q5 = true;

        while (true) {

            if (returned_to_the_sign) {
                println("You are in front of the sign again");
                println("Where are you going to go?");

            }
            String input = get_user_choice(new String[]{"The Road to Ravenwood.", "Danger.", "Forvet the merchant."});
            if (input.equals("The Road to Ravenwood.")) {
                println("You turn left and follow the winding path that leads to an entrance of a cave.");
                if (player.inventory.get("golden_bell") == 0) {
                    println("As you get closer you notice a golden bell hanging above the entrance to the cave.");
                    println("If you jump, you can rip the bell off the rope. Will you try?");

                    input = get_user_choice(new String[]{"yes", "no"});

                    if (input.equals("yes")) {
                        println("You jump up and rip the bell off the rope.");
                        println("You put the bell in your backpack.");
                        player.collect("golden_bell", 1);
                    } else if (input.equals("no")) {
                        println("You decided to ignore the golden bell.");
                    }
                }
                println("There is complete darkness inside the cave.");
                println("What will you do next?");
                input = get_user_choice(new String[]{
                        "Go back to the sign.",
                        (player.inventory.get("lantern") == 1) ? "Light the lantern and head further into the darkness." : "Attempt to head further into the cave in the darkness"
                });
                if (input.equals("Attempt to head further into the cave in the darkness")) {
                    println("You take a cautious step forward into the darkness, careful not to stumble on uneven terrain.");
                    println("The air grows colder and you begin to hear the sound of rushing water.");

                    get_user_choice(new String[]{"Proceed further."});

                    if (!is_dark_knight_defeated) {

                        println("As you moved forward, since you couldn't see anything, you began to listen very carefully.");

                        println("Suddenly you hear heavy, loud metallic steps.");
                        println("And then a voice rang out: \"Heus quis est? Dic nomen tuum, aut ego caput tuum praecidam!\"");

                        input = get_user_choice(new String[]{"Try to answer the voice.", "Run out of the cave and return to the sign."});

                        if (input.equals("Try to answer the voice.")) {
                            println("What are you going to say?");
                            String player_says = "";
                            while (player_says.isEmpty()) {
                                print(">>>");
                                player_says = scan.nextLine();
                            }
                            println("The voice answers: \"Estne nomen tuum? Nescio qui sis, peregrini hic non suscipiuntur! Mori parate!\"");

                            println("You hear the blade cutting through the air.");
                            println("And then you feel a large metal sword cutting your neck.");
                            player.take_damage(100);
                            gameover();


                        } else if (input.equals("Run out of the cave and return to the sign.")) {
                            println("You turn around and run towards the light.");
                            println("You exit the cave and follow the path back to the sign.");
                            continue;

                        }
                    } else {
                        do {
                            println("As you moved forward, you heard a strange scream.");
                            println("You were attacked, but since it was too dark you couldn't see who attacked you.");
                            fight("?????");
                            input = get_user_choice(new String[]{"Proceed further.", "Run out of the cave and return to the sign."});
                        } while (input.equals("Proceed further."));
                        println("You turn around and run towards the light.");
                        println("You exit the cave and follow the path back to the sign.");
                        continue;
                    }

                } else if (input.equals("Light the lantern and head further into the darkness.")) {
                    if (!is_dark_knight_defeated) {
                        println("You light the lantern and walk further into the cave until you notice someone.");
                        println("You notice a knight in full black armor with a large black sword.");
                        println("The knight also notices you.");
                        println("The knight shouts: \"Non te cognosco, quis es? Dic mihi nomen tuum, aut tibi caput auferam!\"");

                        input = get_user_choice(new String[]{"Raise your hands and try to talk to the knight.", "Attack the knight."});

                        if (input.equals("Raise your hands and try to talk to the knight.")) {
                            println("You raised your hands.");

                            println("What are you going to say?");
                            String player_says = "";
                            while (player_says.isEmpty()) {
                                print(">>>");
                                player_says = scan.nextLine();
                            }
                            println("The voice answers: \"Estne nomen tuum? Nescio qui sis, peregrini hic non suscipiuntur! Mori parate!\"");

                            println("Before you could react, the knight raised his sword and cut off your head.");
                            player.take_damage(100);
                            gameover();
                        } else if (input.equals("Attack the knight.")) {
                            println("You attack The Dark Knight.");
                            fight("dark_knight");
                            is_dark_knight_defeated = true;
                            println("The Dark Knight falls dead to the ground.");

                            get_user_choice(new String[]{"Attempt to pick up his sword."});

                            println("You try to lift the sword.");
                            println("Surprisingly, his sword is much lighter than it looks, you can easily pick it up and put it in your backpack.");
                            player.collect("dark_sword", 1);

                            get_user_choice(new String[]{"Proceed further."});

                            println("As you move forward, you notice a light in the distance ahead.");
                            println("And the sound of rushing water starts to get louder.");

                            get_user_choice(new String[]{"Go towards this light."});

                            println("You go towards this light.");

                        }
                    } else {
                        println("You pass though the cave and reach its exit.");
                    }
                    println("Now you are at the exit of the cave.");
                    if (first_time_at_exit) {
                        println("The exit from the cave is on the edge of a cliff and below it is a waterfall,");
                        println("now you realize that the sound of rushing water was the waterfall,");
                        println("in front of you is a wooden suspension bridge, but it is broken.");
                        println("Perhaps you can fix it.");
                        first_time_at_exit = false;
                    }

                    println("What are you going to do?");

                    if (!bridge_repaired) {

                        input = get_user_choice(new String[]{"Attempt to fix the bridge.", "Return to the sign through the cave."});

                        if (input.equals("Attempt to fix the bridge.")) {
                            println("To fix the bridge you need:");
                            println("rope");
                            println("hammer");
                            println("20 metal nails");
                            println("16 planks");
                            println("");
                            println("You have:");
                            if (player.inventory.get("hammer") == 1) {
                                println("hammer");
                            }
                            if (player.inventory.get("rope") == 1) {
                                println("rope");
                            }
                            println(player.inventory.get("metal_nail") + " metal nails");
                            println(player.inventory.get("plank") + " planks");
                            println("");
                            if (
                                    player.inventory.get("hammer") == 1 &&
                                            player.inventory.get("rope") == 1 &&
                                            player.inventory.get("metal_nail") >= 20 &&
                                            player.inventory.get("plank") >= 16
                            ) {
                                println("You have everything you need to repair the bridge.");

                                input = get_user_choice(new String[]{"Repair the bridge.", "Return to the sign through the cave."});

                                if (input.equals("Repair the bridge.")) {
                                    player.lose("plank", 16);
                                    player.lose("metal_nail", 20);
                                    player.lose("rope", 1);
                                    println("You have successfully repaired the bridge.");
                                    bridge_repaired = true;



                                }
                            } else {
                                println("");
                                println("You don't have enough resources to repair the bridge.");
                                println("You need to get:");
                                if (player.inventory.get("hammer") == 0) {
                                    println("hammer");
                                }
                                if (player.inventory.get("rope") == 0) {
                                    println("rope");
                                }
                                if (player.inventory.get("metal_nail")<20) {
                                    println((20 - player.inventory.get("metal_nail")) + " metal nails");
                                }
                                if (player.inventory.get("plank")<16) {
                                    println((16 - player.inventory.get("plank")) + " planks");
                                }
                                get_user_choice(new String[]{"Return to the sign through the cave."});

                            }
                        }


                    }

                    if (bridge_repaired) {
                        input = get_user_choice(new String[]{"Cross the bridge to the other side.", "Return to the sign through the cave."});

                        if (input.equals("Cross the bridge to the other side.")) {
                            println("You stepped hesitantly onto the bridge.");
                            println("You took a few steps and made sure that the bridge was strong enough to support you.");
                            println("The bridge was strong.");
                            get_user_choice(new String[]{"Continue walking."});
                            println("And now you walked much more confidently to the middle of the bridge.");
                            println("But suddenly you had some strange feeling,");
                            println("and after a couple seconds your eyes went black and you fell to your knees.");

                            input = get_user_choice(new String[]{"Try to get up from your knees.", (player.inventory.get("antidote") == 1) ? "Use the antidote." : "none"});

                            println("It looks like the dart that hit you in the forester's hut was poisonous.");
                            println("And the poison took effect only now.");

                            if (input.equals("Try to get up from your knees.")){
                                println("You tried to get up from your knees, but the poison was too strong.");
                                println("After a couple of seconds, your body completely stopped obeying you and you could no longer move.");
                                gameover();
                            } else if (input.equals("Use the antidote.")){
                                println("Without seeing anything by touch, you took the antidote and drank it.");
                                println("After a couple of seconds you felt better.");
                            }

                            get_user_choice(new String[]{"Get up."});

                            println("You got up from your knees.");

                            get_user_choice(new String[]{"Continue walking."});

                            chapter3();

                        }
                    }

                    println("You light the lantern and walk through the cave.");
                    println("You exit the cave and follow the winding path until you reach the sign.");
                    continue;


                }
            } else if (input.equals("Danger.")) {
                println("You go straight and follow the winding path until you reach the middle of the meadows.");
                String location = "meadows";

                while (true) {
                    input = get_user_choice(new String[]{"Keep moving forward.", "Go back.", (player.inventory.get("golden_bell") == 1) ? "Ring the golden bell." : "none"});
                    if (input.equals("Keep moving forward.")) {
                        location = switch (location) {
                            case "meadows" -> "dense forest";
                            case "dense forest" -> "iron forest";
                            case "iron forest" -> "meadows";

                            default -> throw new IllegalStateException("Unexpected value: " + location);
                        };
                        println("You go forward and follow the winding path until you reach the middle of the " + location + ".");
                    } else if (input.equals("Go back.")) {
                        location = switch (location) {
                            case "iron forest" -> "dense forest";
                            case "dense forest" -> "meadows";
                            case "meadows" -> "back";

                            default -> throw new IllegalStateException("Unexpected value: " + location);
                        };

                        if (location.equals("back")) {
                            println("You go back and follow the winding path until you reach the sign.");
                            break;
                        }
                        println("You go back and follow the winding path until you reach the middle of the " + location + ".");
                    } else if (input.equals("Ring the golden bell.")) {
                        println("The loud ringing of the bell attracted the creatures around you.");
                        switch (location) {
                            case "meadows" -> {
                                println("You are bing attacked by a boar.");
                                fight("boar");
                                player.collect("boar_tusk", 2);
                                player.collect("leather", 1);
                            }
                            case "dense forest" -> {
                                println("You are bing attacked by a living tree.");
                                fight("living_tree");
                                player.collect("plank", 3);
                                player.collect("wooden_heart", 1);
                            }
                            case "iron forest" -> {
                                println("You are bing attacked by a robot.");
                                fight("robot");
                                player.collect("metal_nail", 4);
                                player.collect("red_LED", 1);
                                player.collect("antenna", 1);
                            }
                        }
                    }


                }

            } else if (input.equals("Forvet the merchant.")) {
                println("You turn right and follow the winding path that leads to a " + ((been_at_the_shop) ? "Forvet's shop." : "small wooden building."));
                if (!been_at_the_shop) {
                    println("You approach the door of the building.");
                    println("On the door it says:");
                    print_banner("Forvet the merchant.");
                    print_banner("Everyone is welcome!");

                    get_user_choice(new String[]{"Open the door."});

                    println("You open the door and walk in.");
                    println("You look around and confirm your suspicions: this is a shop.");
                    println("There is a person standing in front of you.");
                    println("This person says: \"Hello stranger, what is your name?\"");
                    String username;
                    do {
                        print(">>> ");
                        username = scan.nextLine();
                    } while (username.isEmpty());
                    player.name = username;
                    println("Hello " + player.name + ", nice to meet you and welcome to my store, make yourself at home.");

                } else {
                    println("You walk up to the shop, open the door and walk in.");
                    println("Hello " + player.name + ", I'm glad to see you in my shop.");
                }

                while (true) {
                    input = get_user_choice(new String[]{"Buy.", "Sell.", (!q1 && !q2 && !q3 && !q4 && !q5) ? "none" : "Ask.", "Leave the shop."});

                    if (input.equals("Buy.")) {
                        println("Forvet: \"Take a look.\"");

                        println("");

                        while (true) {

                            println("You have " + player.inventory.get("golden_coins") + " golden coins.");

                            List<String> items_list = new ArrayList<>();

                            items_list.add((player.inventory.get("axe") == 0) ? "axe: " + (items_cost.get("axe") + 10) : "none");
                            items_list.add((player.inventory.get("lantern") == 0) ? "lantern: " + (items_cost.get("lantern") + 10) : "none");
                            items_list.add("first aid kit: " + (items_cost.get("first_aid_kit") + 10));
                            items_list.add((player.inventory.get("rope") == 0) ? "rope: " + (items_cost.get("rope") + 10) : "none");

                            for (String item : shop_items.keySet()) {
                                if (player.inventory.get(item) == 0) {
                                    items_list.add(item.replace('_', ' ') + ": " + shop_items.get(item));
                                }
                            }
                            items_list.add("back");

                            input = get_user_choice(items_list.toArray(new String[0])).replace(' ', '_');


                            if (input.equals("back")) {
                                break;
                            }

                            String item = input.split(":_")[0];
                            int price = Integer.parseInt(input.split(":_")[1]);

                            if (player.inventory.get("golden_coins") < price) {
                                println("Forvet: \"You don't have enough gold coins, you need " + (price - player.inventory.get("golden_coins")) + " more.\"");
                                continue;
                            }

                            println("Forvet: \"Are you sure?\"");
                            input = get_user_choice(new String[]{"yes", "no"});

                            println("Forvet: \"Okay.\"");

                            if (input.equals("yes")) {
                                player.lose("golden_coins", price);
                                player.collect(item, 1);
                            } else if (input.equals("no")) {
                                continue;
                            }
                        }


                    } else if (input.equals("Sell.")) {
                        println("Forvet: \"Show me what you want to sell, and I will tell you the price at which I will buy it.\"");

                        while (true) {
                            List<String> items_list = new ArrayList<>();
                            for (String item : player.inventory.keySet()) {
                                if (!(item.equals("golden_coins") || item.equals("backpack"))) {
                                    if (player.inventory.get(item) > 0) {
                                        items_list.add(item.replace('_', ' ') + ": " + player.inventory.get(item));
                                    }
                                }
                            }
                            items_list.add("back");
                            input = get_user_choice(items_list.toArray(new String[0])).replace(' ', '_');
                            if (input.equals("back")) {
                                break;
                            }
                            input = input.substring(0, input.indexOf(':'));

                            if (input.equals("golden_bell")) {
                                if (tried_to_sell_the_bell == 0) {
                                    println("Forvet: \"Wait a minute, where did you get that bell from?\"");

                                    input = get_user_choice(new String[]{
                                            "I tore it from the entrance to a nearby cave.",
                                            "I found it lying on the ground.",
                                            "It's none of your business."
                                    });

                                    if (input.equals("I tore it from the entrance to a nearby cave.")) {
                                        println("Forvet: \"Since you told the truth, I will not call guards on you, but I will not buy stolen goods either.\"");
                                    } else {
                                        println("Forvet shouted loudly: \"Guards!\"");
                                        println("You hear heavy metallic footsteps as a knight in metal armor enters the store.");
                                        println("The knight blocks the exit and points his sword at you.");
                                        fight("guard");
                                        gameover();
                                        println("ERROR");
//                                        Impossible scenario, the guard cannot be defeated without cheats.
                                    }
                                    tried_to_sell_the_bell = 1;
                                } else if (tried_to_sell_the_bell == 1) {
                                    println("Forvet: \"I already told you I will not buy stolen goods.\"");
                                    tried_to_sell_the_bell = 2;

                                } else if (tried_to_sell_the_bell == 2) {
                                    println("Forvet: \"I guess you just want me to call the guards.\"");
                                    println("Forvet shouted loudly: \"Guards!\"");
                                    println("You hear heavy metallic footsteps as a knight in metal armor enters the store.");
                                    println("The knight blocks the exit and points his sword at you.");
                                    fight("guard");
                                    gameover();
                                    println("ERROR");
//                                        Impossible scenario, the guard cannot be defeated without cheats.
                                    tried_to_sell_the_bell = 3;
                                }


                            } else if (player.inventory.get(input) == 1) {

                                if (input.equals("dark_sword")) {
                                    println("Forvet: \"Wow, this is a really high quality sword!\"");
                                }

                                println("Forvet: \"I will buy this item for " + items_cost.get(input) + " golden coins.\"");
                                println("Forvet: \"Do you want to sell it to me?\"");

                                String input2 = get_user_choice(new String[]{"yes", "no"});

                                println("Forvet: \"Okay.\"");

                                if (input2.equals("yes")) {
                                    player.collect("golden_coins", items_cost.get(input));
                                    player.lose(input, 1);
                                } else if (input2.equals("no")) {
                                    continue;
                                }
                            } else if (player.inventory.get(input) > 1) {
                                println("Forvet: \"I will buy one of these items for " + items_cost.get(input) + " golden coins.\"");
                                println("Forvet: \"How much do you want to sell?\"");

                                int amount = get_user_input(0, player.inventory.get(input));

                                println("Forvet: \"Okay.\"");
                                if (amount > 0) {
                                    player.collect("golden_coins", (items_cost.get(input)) * amount);
                                    player.lose(input, amount);
                                }

                            }
                        }

                    } else if (input.equals("Ask.")) {
                        while (true) {

                            if (!q1 && !q2 && !q3 && !q4 && !q5) {
                                println("Enough questions, did you come here to talk to me or to use my store?");
                                break;
                            }

                            input = get_user_choice(new String[]{
                                            (q1) ? "Who are you?" : "none",
                                            (q2) ? "Where am I?" : "none",
                                            (q3) ? "Who am I?" : "none",
                                            (q4) ? "How do I get out of this forest?" : "none",
                                            (q5) ? "Can you give me some golden coins?" : "none",
                                            "back"
                                    }
                            );

                            if (input.equals("back")) {
                                break;
                            }

                            print("Forvet: \"");

                            switch (input) {
                                case "Who are you?" -> {
                                    print("I am Forvet the merchant.");
                                    q1 = false;
                                }
                                case "Where am I?" -> {
                                    print("You're in my store, silly.");
                                    q2 = false;
                                }
                                case "Who am I?" -> {
                                    print("You are " + player.name + ".");
                                    q3 = false;
                                }
                                case "How do I get out of this forest?" -> {
                                    print("The same way you got here.");
                                    q4 = false;
                                }
                                case "Can you give me some golden coins?" -> {
                                    print("No.");
                                    q5 = false;
                                }
                            }
                            println("\"");
                        }

                    } else if (input.equals("Leave the shop.")) {
                        println("Forvet: \"Goodbye " + player.name + ", take care.\"");
                        println("You leave the shop and follow the path back to the sign.");
                        break;
                    }
                }


                been_at_the_shop = true;
            }
            returned_to_the_sign = true;
        }

    }

    public static void chapter3() {
        println("You did it! You left this forest.");
        println("It took you " + ((System.currentTimeMillis() / 1000L) - start_time) + " second to beat the game.");
        println("To be continued...");
        gameover();
    }


    public static void main(String[] args) {
        println("Type \"?\" at any time to view your statistics and inventory.");
        println("Type \"!help\" at any time to see all available commands.");
        println("");
        start_game();
    }

}