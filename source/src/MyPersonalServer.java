import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A very simple example of TCP server. When the server starts, it binds a
 * server socket on any of the available network interfaces and on port 2205. It
 * then waits until one (only one!) client makes a connection request. When the
 * client arrives, the server does not even check if the client sends data. It
 * simply writes the current time, every second, during 15 seconds.
 *
 * To test the server, simply open a terminal, do a "telnet localhost 2205" and
 * see what you get back. Use Wireshark to have a look at the transmitted TCP
 * segments.
 *
 * @author Olivier Liechti
 * @modifs Maurice Lehmann
 */
public class MyPersonalServer implements Runnable {

    static final Logger LOG = Logger.getLogger(MyPersonalServer.class.getName());
    private final int LISTEN_PORT = Protocol.PORT;

    /**
     * This method does the entire processing.
     */
    public void run() {
        LOG.info("Starting server...");
        new Thread(new Receptionist()).start();
    }


    private class Receptionist implements Runnable {

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        @Override
        public void run() {
            LOG.info("Starting server...");
            try {

                LOG.log(Level.INFO, "Creating a server socket and binding it on any of the available network interfaces and on port {0}", new Object[]{Integer.toString(LISTEN_PORT)});
                serverSocket = new ServerSocket(LISTEN_PORT);
                logServerSocketAddress(serverSocket);


                while (true) {
                    LOG.log(Level.INFO, "Waiting (blocking) for a connection request on {0} : {1}", new Object[]{serverSocket.getInetAddress(), Integer.toString(serverSocket.getLocalPort())});
                    clientSocket = serverSocket.accept();

                    LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
                    logSocketAddress(clientSocket);

                    LOG.log(Level.INFO, "Getting a Reader and a Writer connected to the client socket...");
                    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    writer = new PrintWriter(clientSocket.getOutputStream());

                    writer.println(String.format("Bonjour ! Entrez une commande svp : "));
                    writer.flush();

                    /**
                     * Delegation of the task to the worker
                     */

                    new Thread(new Worker(clientSocket)).start();
                }

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage());
            }
        }
    }
    /**
     * This worker will take a task as arguments and response to the client
     */

    private class Worker implements Runnable{

        Socket clientSocket;
        BufferedReader reader = null;
        PrintWriter writer = null;


        public Worker(Socket clientSocket){
            try {
                this.clientSocket = clientSocket;
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream());
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                String userInput;
                while ((userInput = reader.readLine()) != null) {

                    writer.print(String.format(">"));

                    LOG.log(Level.INFO, userInput);
                    switch (userInput) {
                        case Protocol.CMD_TIME:
                            sendTime(writer);
                            break;
                        case Protocol.CMD_HELP:
                            sendHelp(writer);
                            break;
                        case Protocol.CMD_JOKE:
                            sendJoke(writer);
                            break;
                        default:
                            writer.println(String.format("Commande invalide!"));
                            break;
                    }
                    writer.flush();
                }
                reader.close();
                writer.close();
                clientSocket.close();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        private void sendJoke(PrintWriter writer) throws IOException {
            Process process = Runtime.getRuntime().exec("curl -H \"Accept: text/plain\" https://icanhazdadjoke.com/");
            InputStream in = process.getInputStream();

            StringBuilder stringBuilder = new StringBuilder();
            String joke;

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()))) {
                while ((joke = bufferedReader.readLine()) != null) {
                    stringBuilder.append(joke);
                }
            }

            writer.println(stringBuilder.toString());
            in.close();
        }

        private void sendHelp(PrintWriter writer) {
            writer.println(String.format("############# Liste de commandes ###################### \n" +
                    "time      : Donne l'heure et la date actuelle \n" +
                    "joke      : Raconte une bonne blague !\n" +
                    "########################################################"));
            writer.flush();
        }

        private void sendTime(PrintWriter writer) {
            writer.println(String.format("Il est exactement : " + Date.from(Instant.now())));
            writer.flush();
        }
    }





    /**
     * A utility method to print server socket information
     *
     * @param serverSocket the socket that we want to log
     */
    private void logServerSocketAddress(ServerSocket serverSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(serverSocket.getLocalPort())});
        LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
    }

    /**
     * A utility method to print socket information
     *
     * @param clientSocket the socket that we want to log
     */
    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }
}