package controller;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class FormController implements Initializable {
    @FXML
    private TextField EmailEnvia;
    @FXML
    private TextField PasswordEnvia;
    @FXML
    private TextField ServerEnvia;
    @FXML
    private TextField PortEnvia;
    @FXML
    private TextField EmailRecibe;
    @FXML
    private TextField PasswordRecibe;
    @FXML
    private TextField ServerRecibe;
    @FXML
    private TextField PortRecibe;
    @FXML
    private TextArea TextAreaSend;
    @FXML
    private TextArea TextAreaRecibe;
    @FXML
    private Button ComprobarMails;

    


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	} 
	
	@FXML
	public void mostrarAlertError(ActionEvent event) {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setHeaderText(null);
	    alert.setTitle("Error");
	    alert.setContentText("Error en la aplicacion");
	    alert.showAndWait();
	}
	
	public Properties getServerProperties(String protocol,String host, String port) {
		System.out.println(protocol + " - " + host + " - " + port);
		Properties properties = new Properties();
		properties.put(String.format("mail.imap.host",protocol), host);
		properties.put(String.format("mail.imap.port",protocol), port);
		properties.setProperty(String.format("mail.imap.socketFactory.class",protocol), "javax.net.ssl.SSLSocketFactory");
		//properties.setProperty("mail.smtp.starttls.enable", "true" );
		properties.setProperty(String.format("mail.imap.socketFactory.fallback",protocol), "true");
		properties.setProperty(String.format("mail.imap.socketFactory.port",protocol), String.valueOf(port));
		return properties;
		}
	
	
	
	public void connection() throws MessagingException {
		
		Alert alert = new Alert(Alert.AlertType.ERROR);
		TextAreaSend.setText("");
		TextAreaRecibe.setText("");
		
		/********************************************************************************************/
		/*CONTROLADOR DEL QUE ENVÍA LOS MAILS*/
		/********************************************************************************************/
		Properties propsenvia = getServerProperties("imap", ServerEnvia.getText(), PortEnvia.getText());
		propsenvia.setProperty("mail.store.protocol", "imaps");
		Session sessionenvia = Session.getDefaultInstance(propsenvia);
		Store storeenvia = null;
		try {
			storeenvia = sessionenvia.getStore("imap");
		} catch (NoSuchProviderException e) {
			System.out.println("Error en la sesión");
			e.printStackTrace();
			 alert.setTitle("Error");
		    alert.setContentText("Error en la sesión que envía.");
		    alert.showAndWait();
		}
		try {
			storeenvia.connect(EmailEnvia.getText(), PasswordEnvia.getText());
		} catch (MessagingException e) {
			e.printStackTrace();
			alert.setTitle("Error");
		    alert.setContentText("Error en login que envía.");
		    alert.showAndWait();
		}
		Folder[] fenvia = null;
		try {
			fenvia = storeenvia.getDefaultFolder().list("*");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Folder fd:fenvia) {
		    TextAreaSend.setText(TextAreaSend.getText()+">> "+fd.getName()+"\n");
		}
		
		
		/********************************************************************************************/
		/*CONTROLADOR DEL QUE RECIBE LOS MAILS*/
		/********************************************************************************************/
		Properties propsrecibe = getServerProperties("imap", ServerRecibe.getText(), PortRecibe.getText());
		propsrecibe.setProperty("mail.store.protocol", "imaps");
		Session sessionrecibe = Session.getDefaultInstance(propsenvia);
		Store storerecibe = null;
		try {
			storerecibe = sessionrecibe.getStore("imap");
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			 alert.setTitle("Error");
		    alert.setContentText("Error en la sesión que recibe.");
		    alert.showAndWait();
		}
		try {
			storerecibe.connect(EmailRecibe.getText(), PasswordRecibe.getText());
		} catch (MessagingException e) {
			e.printStackTrace();
			alert.setTitle("Error");
		    alert.setContentText("Error en login que recibe.");
		    alert.showAndWait();
		}
		Folder[] frecibe = null;
		try {
			frecibe = storerecibe.getDefaultFolder().list("*");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Folder newfolder = null;
		for(Folder fe:fenvia) {
			newfolder = storerecibe.getFolder(fe.getFullName());
			if(! newfolder.exists()) {
				newfolder.create(Folder.HOLDS_MESSAGES);
				fe.open(Folder.READ_WRITE);
				fe.copyMessages(fe.getMessages(), newfolder);
				if(fe.isOpen()) {
					fe.close();
				}
			}
		    TextAreaRecibe.setText(TextAreaRecibe.getText()+">> "+fe.getName()+"\n");
		}
		
		
		
	}
	
	
	
	

}
