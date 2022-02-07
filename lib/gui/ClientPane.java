package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.client.ImageClient;

public class ClientPane extends Pane {

	private VBox root = new VBox(25);
	private TextField tfID = new TextField();
	private TextField tfID2 = new TextField();
	private Label fileChoosen = new Label();
	private Text ta = new Text();
	private Text toptitle = new Text("Click Buttons!");
	private Button btnData = new Button("Server List");
	private Button btnIMGRET  = new Button("Get Image");
	private Button btnSend = new Button("SEND");
	private Button btnClear = new Button("Clear Screen");
	private Button btnFileCHooser = new Button("Choose File");
	private ImageView imageView = new ImageView();
	private File imgFile = null;
	private int buttonHeight = 40;
	private int buttonWidth = 200;
	public ClientPane() {
		
		setUpRoot();
		setupButtonListeners();
		getChildren().add(root);
		coolFunction();
	}
	
	private void setUpRoot()
	{
		//Add text field to view list
		HBox hb = new HBox(25);
		hb.getChildren().addAll(ta, imageView);
		root.getChildren().addAll(toptitle, hb);
		//set area size
		imageView.setFitHeight(450);
		imageView.setFitWidth(600);
		root.setPadding(new Insets(10));
		addTextFields();
	}
	
	/**
	 * debugging buttons
	 */
	private void coolFunction()
	{
		imageView.setOnMouseClicked(e ->
		{
			System.out.println(e.getTarget());
		});
	}
	
	
	/**
	 * Method to organise lower nodes
	 * Buttons and text fields
	 */
	private void addTextFields()
	{
		//Create a grid pane for node layout
		GridPane gp = new GridPane();
		gp.addRow(0, btnData, btnClear, btnFileCHooser);
		gp.addRow(1, btnIMGRET, new Label("ID:"), tfID);
		gp.addRow(2, btnSend);
		gp.setHgap(15);
		gp.setVgap(15);
		//Set button sizes
		btnData.setPrefSize(buttonWidth, buttonHeight);
		btnIMGRET.setPrefSize(buttonWidth, buttonHeight);
		btnSend.setPrefSize(buttonWidth, buttonHeight);
		btnClear.setPrefSize(buttonWidth, buttonHeight);
		btnFileCHooser.setPrefSize(buttonWidth, buttonHeight);
		
		//Set text field sizes
		tfID.setPrefSize(buttonWidth, buttonHeight);
		tfID2.setPrefSize(buttonWidth, buttonHeight);
		//Add grid pane to root
		root.getChildren().addAll(gp, fileChoosen);
	}
	
	/**
	 * Method to Handle button clicked event
	 */
	private void setupButtonListeners()
	{
		//Handle event for show button
		//Send DATA request to the server
		btnData.setOnMouseClicked(e ->
		{
			//clear screen
			ta.setText("");
			toptitle.setText("Server List");
			//CLear image view
			imageView.setImage(null);
			//Send request to server
			//Get list from server and display to client
			String l = new ImageClient().requestDATA();
			//Display list on text area 
			ta.setText(l);
		});
		
		//Handle event for IMGRET button
		//Sends the IMGRET request to the server
		btnIMGRET.setOnMouseClicked(e ->
		{
			ta.setText("");
			//CLear image view
			imageView.setImage(null);
			//Get ID from text field
			String id = tfID.getText().trim();	
			
			if(id.isEmpty())
			{
				System.err.println("Please enter ID of image to return");
				return;
			}
			//Send ID to server to request file from server
			String path = new ImageClient().requestIMGRET(id);
			
			if(!path.equals("NP"))
			{
				//Load image to gui
				try {
					toptitle.setText("Image from Server with Id: " + id);
					imageView.setImage(new Image(new FileInputStream(path)));
				} catch (FileNotFoundException e1) {
					System.err.println("Image not found, path: " + path);
					e1.printStackTrace();
				}
				tfID.setText("");
			}
		});
		
		//Handle event for clear button
		btnClear.setOnMouseClicked(e ->
		{
			//Clear text field
			ta.setText("");
			toptitle.setText("Click Buttons!");
			//CLear image view
			imageView.setImage(null);
		});
		
		//Handle event for IMGSEND button
		//Sends the IMGSEND request to the server
		btnSend.setOnMouseClicked(e ->
		{
			//Check if file has data
			ta.setText("");
			toptitle.setText("Image sent to server!");
			if(imgFile != null) {
				
				//Send request to server
				new ImageClient().requestIMGSEND(imgFile);
				fileChoosen.setText("");
				tfID2.setText("");
				imgFile = null;
			}
			else
			{
				//Alert user that file is empty
				System.err.println("Empty file, please choose an image file");
			}
			
		});
		
		//Handle file chooser button event
		//Choose file to be send to the server
		btnFileCHooser.setOnMouseClicked(e ->
		{
			//Get pdf file from client
			final FileChooser fc = new FileChooser();
			fc.setTitle("Choose Image");
			File file = fc.showOpenDialog(new Stage());
			if(file != null) {
				//Check if file is an image
				/*Image files ending with *.jpeg/ png/ jpg*/ 
				boolean isImage = file.getName().endsWith("jpeg") 
						|| file.getName().endsWith("png")
						|| file.getName().endsWith("jpg");
									
				if(isImage)
				{
					imgFile = file;
					fileChoosen.setText("File choosen: "+ imgFile.getName());
					try {
						imageView.setImage(new Image(new FileInputStream(file)));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					//Alert user that the file is not an image
					System.err.println("Selected file is not an image.");
				}
					
			}
		});
	}
	
	
}
