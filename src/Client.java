import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

class Client {
    private boolean connected = true;
    Socket socket;

    Client(String address, int port) throws IOException {
        socket = new Socket(address,port);
        Send send = new Send(socket.getOutputStream());
        Receive receive = new Receive(socket.getInputStream());
        Thread sendThread = new Thread(send);
        sendThread.setDaemon(true);
        sendThread.start();
        new Thread(receive).start();
    }

    void disconnect(){
        connected = false;//todo should be accessed by a disconnect button on GUI
        System.out.println("Disconnected from "+socket.getRemoteSocketAddress());
    }
    class Send implements Runnable{
        private DataOutputStream out;

        Send(OutputStream out){
            this.out = new DataOutputStream(out);
        }
        @Override
        public void run() {
            Scanner in = new Scanner(System.in);//todo convert System.in to desired input method
            while(connected){
                try {
                    send(in.nextLine());
                } catch (IOException e) {
                    e.printStackTrace();//todo deal with exceptions in a user-friendly way
                    disconnect();
                }
            }
        }
        private void send(String message) throws IOException {
            out.writeUTF(message);
        }
    }
    class Receive implements Runnable{
        private DataInputStream in;

        Receive(InputStream in){
            this.in = new DataInputStream(in);
        }
        @Override
        public void run() {
            while(connected){
                try {
                    System.out.println(receive());
                } catch (SocketException ignored){
                    disconnect();
                } catch (IOException e) {
                    e.printStackTrace();//todo deal with exceptions in a user-friendly way
                    disconnect();
                }
            }
        }
        private String receive() throws IOException {
            return in.readUTF();
        }
    }
}
