package client_robi_swing;

import java.util.HashMap;
import java.util.Map;

public class cm_notes {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}

abstract class Command {
    abstract public void run();
}

class SetColor extends Command {
    public void run() {
        // Mettre le code qu'on aurait mit dans la classe Robot ici
    }
}
class Robo {
    Map<String, Command> commands;

    // Copilot completion ici mdr
//    public void addCommand(String name, Command command) {
//        commands.put(name, command);
//    }
    public Robo() {
        commands = new HashMap<>();
        commands.put("setColor", new SetColor());
    }

    void execute (String nom) {
        Command c = commands.get(nom);
        c.run();
    }
}
