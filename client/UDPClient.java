import java.io.*;
import java.net.*;

class UDPClient
{
   private static Thread mThread, sThread;
   public static void main(String args[]) throws Exception
   {
      BufferedReader inFromUser =
         new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("192.168.10.101");
      byte[] sendData = new byte[64];
      byte[] receiveData = new byte[64];
      mThread = new Thread(new Runnable() {
         public void run() {
            try {DatagramSocket clientSocket = new DatagramSocket();}
            catch (SocketException e) {}
            try {InetAddress IPAddress = InetAddress.getByName("127.0.0.1");}
            catch (UnknownHostException e){ }
                           byte[] sendData = new byte[64];
               byte[] receiveData = new byte[64];
            while (true) { 
               String sentence = "";
               try { sentence = inFromUser.readLine();} catch (IOException e) {}
               sendData = sentence.getBytes();
               DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
               try {
                  clientSocket.send(sendPacket);
               } catch (IOException e) {
                  System.out.println(e.getMessage());
               }
            }
         }}
      );
      mThread.start();
      sThread = new Thread(new Runnable() {
         public void run() {
            while (true) { 
               byte[] receiveData = new byte[64];
               DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
               try{
                  clientSocket.receive(receivePacket);
               }
               catch (IOException e) {}
               finally {
                  String sentence = new String(receivePacket.getData());
                  System.out.println("Recieved:" + sentence);
               }
            }
         }
      }
      );
      sThread.start();
   }
}