package eecs285.proj4.Yahtzee;

import static java.lang.System.out;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Vector;

public class ClientServerSocket
{
   private String ipAddr;
   private int portNum;
   private Socket socket;
   private DataOutputStream outData;
   private DataInputStream inData;
   ServerSocket serverSock;
   private ArrayList<ServerClient> clients;

   public ClientServerSocket(String inIPAddr, int inPortNum)
   {
      ipAddr = inIPAddr;
      portNum = inPortNum;
      inData = null;
      outData = null;
      socket = null;
   }

   public void startClient()
   {
      try {
         socket = new Socket(ipAddr, portNum);
         socket.setSoTimeout(2000);
         outData = new DataOutputStream(socket.getOutputStream());
         inData = new DataInputStream(socket.getInputStream());
      } catch (IOException ioe) {
         out.println("ERROR: Unable to connect - " + "is the server running?");
         out.printf("error message %s\n", ioe);
         System.exit(10);
      }
   }

   public void startServer()
   {
      ServerSocket serverSock;
      try {
         serverSock = new ServerSocket(portNum);
         out.println("Waiting for client to connect...");
         socket = serverSock.accept();
         // socket.setSoTimeout(10000);
         outData = new DataOutputStream(socket.getOutputStream());
         inData = new DataInputStream(socket.getInputStream());
         out.println("Client connection accepted");
      } catch (IOException ioe) {
         out.println("ERROR: Caught exception starting server");
         System.exit(7);
      }
   }

   public void getClient()
   {
      if (clients == null) {
         clients = new ArrayList<ServerClient>();
      }

      try {
         if (serverSock == null) {
            serverSock = new ServerSocket(portNum);
         }
         out.printf("Waiting for client to connect...\n");
         socket = serverSock.accept();
         // socket.setSoTimeout(2000);
         ServerClient client = new ServerClient();
         client.outData = new DataOutputStream(socket.getOutputStream());
         client.inData = new DataInputStream(socket.getInputStream());
         clients.add(client);
         out.printf("Client connection accepted\n");
      } catch (IOException ioe) {
         out.println("ERROR: Caught exception getting client");
         System.exit(7);
      }
   }

   public boolean sendString(String strToSend)

   {
      boolean success = false;
      try {
         outData.writeBytes(strToSend);
         outData.writeByte(0); // send 0 to signal the end of the string
         success = true;
      } catch (IOException e) {
         System.out.println("Caught IOException Writing To Socket Stream!");
         System.exit(-1);
      }
      return (success);
   }

   public boolean sendStringToAll(String strToSend)
   // reserved for the server side
   {
      boolean success = false;
      if (clients == null) {
         return success;
      }
      try {
         for (ServerClient client : clients) {
            client.outData.writeBytes(strToSend);
            client.outData.writeByte(0);
         }
         success = true;
      } catch (IOException e) {
         System.out.println("Caught IOException Writing To Socket Stream!");
         System.exit(-1);
      }
      return (success);
   }

   public String waitForString(int currentPlayer)
   // reserved for the server side
   {
      Vector<Byte> byteVec = new Vector<Byte>();
      byte[] byteAry;
      byte recByte;
      String receivedString = "";
      try {
         // socket.setSoTimeout(2000);
         System.out.println("about to hang!!!!");
         recByte = clients.get(currentPlayer).inData.readByte();
         System.out.println("done hanging");
         while (recByte != 0) {
            byteVec.add(recByte);
            recByte = clients.get(currentPlayer).inData.readByte();
         }
         byteAry = new byte[byteVec.size()];
         for (int ind = 0; ind < byteVec.size(); ind++) {
            byteAry[ind] = byteVec.elementAt(ind).byteValue();
         }
         receivedString = new String(byteAry);
         if (receivedString.length() > 0) {
            return receivedString;
         }
      } catch (IOException ioe) {
         out.println("ERROR: waiting for string from socket");
      }
      try {
         socket.setSoTimeout(0);
      } catch (SocketException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return (receivedString);
   }

   public String recvString()
   // reserved for the client side
   {
      Vector<Byte> byteVec = new Vector<Byte>();
      byte[] byteAry;
      byte recByte;
      String receivedString = "";
      try {
         recByte = inData.readByte();
         while (recByte != 0) {
            byteVec.add(recByte);
            recByte = inData.readByte();
         }
         byteAry = new byte[byteVec.size()];
         for (int ind = 0; ind < byteVec.size(); ind++) {
            byteAry[ind] = byteVec.elementAt(ind).byteValue();
         }
         receivedString = new String(byteAry);
      } catch (IOException ioe) {
         out.println("ERROR: receiving string from socket");
      }
      return (receivedString);
   }

   public int recvInt()
   // reserved for the client side
   {
      int recvInt = 0;
      try {
         recvInt = inData.readInt();
      } catch (IOException ioe) {
         out.println("ERROR: receiving int from socket");
         System.exit(10);
      }
      return (recvInt);
   }

   public void sendInt(int inSendInt)
   // reserved for the client side
   {
      try {
         outData.writeInt(inSendInt);
      } catch (IOException ioe) {
         System.out.println("ERROR: sending int");
         System.exit(11);
      }
   }

   public void sendIntToAll(int inSendInt)
   // reserved for the server side
   {
      try {
         for (ServerClient client : clients) {
            client.outData.writeInt(inSendInt);
         }
      } catch (IOException ioe) {
         System.out.println("ERROR: sending int");
         System.exit(11);
      }
   }

   public int waitForInt(int currentPlayer)
   // reserved for the server side
   {
      int recvInt = 0;
      boolean success = false;
      while (!success) {
         try {
            recvInt = clients.get(currentPlayer).inData.readInt();
            success = true;
         } catch (IOException ioe) {
            success = false;
            // out.println("ERROR: waiting for int from socket");
         }
         if (success) {
            return recvInt;
         }
      }
      // out.printf("recieved int %d\n", recvInt);
      return (recvInt);
   }

   public class ServerClient
   {
      public ServerSocket serverSock;
      public DataOutputStream outData;
      public DataInputStream inData;
   }

}