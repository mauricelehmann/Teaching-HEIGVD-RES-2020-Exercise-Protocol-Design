public class Application {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Thread(new MyPersonalServer()).start();

        new Thread(new Client()).start();
        new Thread(new Client()).start();
    }
}
