import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    public void run(){
        try {
            Socket clientSock = new Socket("localhost", Protocol.PORT);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            PrintWriter output = new PrintWriter(clientSock.getOutputStream());


            output.println(Protocol.CMD_JOKE);
            output.flush();

            for (String line = input.readLine(); line != null; line = input.readLine()) {
                System.out.println(line);
            }


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
