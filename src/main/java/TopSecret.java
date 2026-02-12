/**
 * Commmand Line Utility
 */

public class TopSecret{
    public static void main(String[] args){
        ProgramControl control = new ProgramControl();
        Userinterface ui = new Userinterface(control);
        ui.run(args);
    }
}
