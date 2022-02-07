import gui.ClientPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.server.ImageServer;

public class Main extends Application{

	public static void main(String[] args) {
		//Start the server
		new ImageServer().startServer();
		
		//Launch the application
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//Create client node
		ClientPane pane  = new ClientPane();
		
		//Add pane to the scene
		Scene s = new Scene(pane, 780,800,Color.ALICEBLUE);
		primaryStage.setScene(s);
		primaryStage.setTitle("Image Transfer");
		primaryStage.show();
	}
	
	

}
