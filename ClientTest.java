package eecs285.proj4.rileynat;

public class ClientTest
{
   public final static String IPADDRESS = "10.0.0.21";

   public static void main(String[] args)
   {
      // TODO Auto-generated method stub
      ClientServerSocket theClient;
      String recvdStr;
      theClient = new ClientServerSocket(IPADDRESS, 45547);
      theClient.startClient();
      theClient.sendString("Hello to the server!");

      // int result = theClient.recvInt();
      // System.out.println("timestamp1: " + result);
      // result = theClient.recvInt();
      // System.out.println("timestamp2: " + result);
      // result = theClient.recvInt();
      // System.out.println("timestamp3: " + result);

      // theClient.sendInt(2634);
      recvdStr = theClient.recvString();
      System.out.println("Received this message from server: " + recvdStr);
      int numPlayers = theClient.recvInt();
      System.out.println("Received number of players from server: "
            + numPlayers);

      recvdStr = theClient.recvString();
      System.out.println("Received this message from server: " + recvdStr);
      for (int i = 0; i < numPlayers; i++) {
         recvdStr = theClient.recvString();
         System.out.println("Received player name from server: " + recvdStr);
      }

      recvdStr = theClient.recvString();
      System.out.println("Received this message from server: " + recvdStr);
      recvdStr = theClient.recvString();
      System.out.println("Received this message from server: " + recvdStr);

      for (long i = 0; i < 2054775807; i++) {
         System.out.print('G');
      }
      theClient.sendInt(40);
   }

}
