package chatsocket;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author dadda
 */
public class ClientChat extends Application{
    private TextField textUserName = new TextField();
    private TextField textHost = new TextField("localhost");
    private TextField textPort = new TextField("8000");
    private TextField textMessage = new TextField();
    private Button btnConnect;
    private Button btnEnvoyer;
    private Button btnLogout;
    GridPane gridAuth;
    PrintWriter pw;
    ObservableList<String> listUser;
    private String currentUser;
    
    protected List<String> usersAuth = new ArrayList<String>();
    
    private Scene sceneAuth, sceneChat;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        gridAuth = new GridPane();
        //root1.setStyle("-fx-background-image: url('file:background.jpeg')");
        Image imageAvatar = new Image("files/img/avatar.png");
        ImageView picAvatar = new ImageView();
        picAvatar.setFitWidth(80);
        picAvatar.setFitHeight(80);
        picAvatar.setImage(imageAvatar);
        
        Image imageHeaderUPM = new Image("files/img/chat.gif");
        ImageView picHeaderUPM = new ImageView();
        ImageView picHeaderUPMChat = new ImageView();
        picHeaderUPM.setX(50);
        picHeaderUPM.setY(50);
        picHeaderUPM.setImage(imageHeaderUPM);
        picHeaderUPMChat.setImage(imageHeaderUPM);
        
        gridAuth.setPrefSize(400, 500);
        gridAuth.setPadding(new Insets(0, 20, 20, 20)); // add 10px padding
        gridAuth.setVgap(15); // set vertical gap
        gridAuth.setHgap(5); // set horizontal gap
        gridAuth.setAlignment(Pos.CENTER);
        GridPane.setHalignment(picAvatar, HPos.CENTER);
        gridAuth.add(picAvatar, 0, 0);
        GridPane.setHalignment(picHeaderUPM, HPos.CENTER);
        gridAuth.add(picHeaderUPM, 0, 1);
        gridAuth.add(new Label("Entrer votre nom: "), 0, 2);
        gridAuth.add(new Label("Entrer le Host: "), 0, 4);
        gridAuth.add(new Label("Entre le Port: "), 0, 6);
        gridAuth.add(textUserName, 0, 3);
        gridAuth.add(textHost, 0, 5);
        gridAuth.add(textPort, 0, 7);
        
        Image loginIcon=new Image("files/img/login.png");
        ImageView iconLogin=new ImageView(loginIcon);
        iconLogin.setFitHeight(30);
        iconLogin.setFitWidth(30);
        
        btnConnect = new Button("Connexion", iconLogin);
        btnConnect.setStyle("-fx-border-color: #ddd; -fx-background-color: transparent; -fx-cursor: hand;");
        GridPane.setHalignment(btnConnect, HPos.CENTER);
        gridAuth.add(btnConnect, 0, 8);
        
        sceneAuth = new Scene(gridAuth);
        primaryStage.setScene(sceneAuth);
        primaryStage.setTitle("Application de CHAT");
        //primaryStage.setResizable(false);
        primaryStage.show();
        
        btnConnect.setOnAction((evt)->{
            int indexPort = Integer.parseInt(textPort.getText());
            String username = textUserName.getText();
            if( indexPort==8000 && !(textHost.getText().isEmpty()) && !(username.isEmpty()) ){
                String host = textHost.getText();
                int port = Integer.parseInt(textPort.getText());
                
                try{
                    Socket socket = new Socket(host, port);
                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br =  new BufferedReader(isr);
                    pw = new PrintWriter(socket.getOutputStream(), true);

                    pw.println(textUserName.getText());
                    
                    //updateListUserAuth();
                    
                    BorderPane borderPane = new BorderPane();

                    Image logoutIcon=new Image("files/img/logout.png");
                    ImageView iconLogout=new ImageView(logoutIcon);
                    iconLogout.setFitHeight(30);
                    iconLogout.setFitWidth(30);
                    btnLogout = new Button("Se déconnecté", iconLogout);
                    btnLogout.setStyle("-fx-border-color: #ddd; -fx-background-color: transparent; -fx-cursor: hand;");
                    
                    //borderPane.setTop(btnLogout);
                    //BorderPane.setMargin(btnLogout, new Insets(10));
                    //BorderPane.setAlignment(btnLogout, Pos.CENTER_LEFT);
                    
                    HBox hBoxTop = new HBox();
                    hBoxTop.setSpacing(10);
                    hBoxTop.setPadding(new Insets(10));
                    
                    Region spaceBetween = new Region();
                    HBox.setHgrow(spaceBetween, Priority.ALWAYS);
                    
                    hBoxTop.getChildren().addAll(btnLogout, spaceBetween, picHeaderUPMChat);
                    borderPane.setTop(hBoxTop);
                    
                    VBox vBox = new VBox();
                    vBox.setSpacing(10);
                    vBox.setPadding(new Insets(10));
                    ObservableList<String> listModel = FXCollections.observableArrayList();
                    ListView<String> listView = new ListView<String>(listModel);
                    vBox.getChildren().add(listView);
                    borderPane.setCenter(vBox);

                    VBox vBoxListUser = new VBox();
                    vBoxListUser.setSpacing(10);
                    vBoxListUser.setPadding(new Insets(10));
                    listUser = FXCollections.observableArrayList();
                    ListView<String> listViewUsers = new ListView<String>(listUser);
                    listViewUsers.setPrefSize(150, 400);
                    vBoxListUser.getChildren().add(listViewUsers);
                    borderPane.setRight(vBoxListUser);

                    textMessage.setPrefSize(650, 35);
                    HBox hBox2 = new HBox();
                    hBox2.setSpacing(10);
                    hBox2.setPadding(new Insets(10));

                    Image sendIcon=new Image("files/img/send.png");
                    ImageView iconSend=new ImageView(sendIcon);
                    iconSend.setFitHeight(22);
                    iconSend.setFitWidth(22);
                    btnEnvoyer = new Button("Envoyer", iconSend);
                    btnEnvoyer.setStyle("-fx-border-color: #ddd; -fx-background-color: transparent; -fx-cursor: hand;");
                    //btnEnvoyer = new Button("Envoyer", );

                    hBox2.getChildren().addAll(textMessage, btnEnvoyer);
                    borderPane.setBottom(hBox2);

                    sceneChat = new Scene(borderPane, 800, 500);
                    primaryStage.setScene(sceneChat);

                    new Thread(()->{
                        while(true){
                            try{
                                String response = br.readLine();
                                Platform.runLater(()->{
                                    if(response.contains("@")){
                                        String[] request = response.split("@");
                                        if(request.length == 2){
                                            String clientId = request[0];
                                            String user = request[1];
                                            String msgBienvenue = "Bienvenue, "+user+", votre ID = "+clientId;
                                            listModel.add(msgBienvenue);
                                            currentUser = clientId+":"+user;
                                        }
                                    updateListUserAuth();
                                    }else{
                                        listModel.add(response);
                                        updateListUserAuth();
                                    }
                                    //updateListUserAuth();
                                    //listModel.add(response);
                                });
                                
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }catch(IOException e){
                    e.printStackTrace();
                }
                
                btnEnvoyer.setOnAction((evtSend)->{
                    String message = textMessage.getText();
                    pw.println(message);
                    textMessage.setText("");
                });

                // Quand le client se déconnecte 
                btnLogout.setOnAction((evtLogout)->{
                    System.exit(0);
                });
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de connection");
                alert.setHeaderText("Vérifier votre host ou port de connexion");
                alert.setContentText("Merci de contacter notre support !");
                alert.showAndWait();
            }
        }); 
    }
    
    public void updateListUserAuth(){
        
        listUser.clear();
        /*if(!listUser.isEmpty()){
            listUser.clear();
        }*/
        BufferedReader br = null;
        
        try{
            
            br = new BufferedReader(new FileReader("authUser.txt"));
            String line;
            while((line = br.readLine()) != null){
                if(!line.equals(currentUser))
                    listUser.add(line);
            }
            
            //System.out.println(listUser);
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
