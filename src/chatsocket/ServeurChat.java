package chatsocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dadda
 */
public class ServeurChat extends Thread{
    private String username;
    private int clientId = 0;
    private List<ClientConnect> clients = new ArrayList<ClientConnect>();
    
    public static void main(String[] args) {
        new ServeurChat().start();
    }
    
    @Override
    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(8000);
            while(true){
                Socket socket = serverSocket.accept();
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                username = br.readLine();
                ++clientId;
                ClientConnect client = new ClientConnect(socket, clientId, username);
                clients.add(client);
                client.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    class ClientConnect extends Thread{
        protected Socket socketClient;
        protected String username;
        protected int numero;
        

        public ClientConnect(Socket socketClient, int numero, String username){
            this.socketClient = socketClient;
            this.numero = numero;
            this.username = username;
        }
        
        public void saveUser(int cliendId,String username){
            try{
                File file = new File("authUser.txt");
                if(!file.exists()){
                    file.createNewFile();
                }
                PrintWriter pw = new PrintWriter(new FileWriter(file, true));
                pw.println(cliendId+":"+username);
                pw.close();
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        public void removeUser(int cliendId,String username){
            try{
                File inputFile = new File("authUser.txt");
                File tempFile = new File("authUserTemp.txt");

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String lineToRemove = cliendId+":"+username;
                String currentLine;

                while((currentLine = reader.readLine()) != null) {
                    // trim newline when comparing with lineToRemove
                    String trimmedLine = currentLine.trim();
                    if(trimmedLine.equals(lineToRemove)) continue;
                    writer.write(currentLine + System.getProperty("line.separator"));
                }
                writer.close(); 
                reader.close();
                inputFile.delete();
                boolean rename = tempFile.renameTo(inputFile);
                System.out.println(rename);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        
        public void sendMessage(String message, Socket socket, int idClient){
            try {
                for(ClientConnect client:clients){
                    if(client.socketClient != socket){
                        if(client.numero == idClient || idClient == -1){
                            PrintWriter print = new PrintWriter(client.socketClient.getOutputStream(), true);
                            print.println(message);
                        }
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            try{
                InputStream is = socketClient.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                PrintWriter pw = new PrintWriter(socketClient.getOutputStream(), true);
                String ip = socketClient.getRemoteSocketAddress().toString();
                //pw.println("Bienvenue, vous êtes le client N°"+numero+", Nom : "+username);
                pw.println(numero+"@"+username);
                sendMessage(username+" : est connecté", socketClient, -1);
                //sendMessage(numero+"@"+username, socketClient, -1);
                saveUser(numero, username);
                System.out.println("Connexion du client numéro"+numero+", IP="+ip+", Username = "+username);
                try{
                    while(true){
                        String req = br.readLine();
                        if(req.contains("=>")){
                            String[] requestParams = req.split("=>");
                            if(requestParams.length == 2){
                                String message = requestParams[1];
                                int idClient = Integer.parseInt(requestParams[0]);
                                sendMessage(numero+"/"+username+" : "+message, socketClient, idClient);
                            }
                        }else{
                            sendMessage(numero+"/"+username+" : "+req, socketClient, -1);
                        }
                    }
                }catch(Exception e){
                    clients.remove(this);
                    sendMessage(username+" : est déconnecté", socketClient, -1);
                    removeUser(numero, username);
                    System.out.println(username+" est déconnecté");
                    }
            }catch(IOException e){
                //clients.remove(this);
                e.printStackTrace();
            }
        }
    }
}
