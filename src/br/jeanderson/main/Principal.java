/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jeanderson.main;

import br.jeanderson.controller.TelaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author jeand
 */
public class Principal extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        FXMLLoader carregar = new FXMLLoader(getClass().getResource("/br/jeanderson/view/TelaPrincipal.fxml"));
        Parent root = carregar.load();
        Scene cena = new Scene(root);
        primaryStage.setTitle("PlayMusic by Jeanderson");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/br/jeanderson/img/playMusic_icon.png")));
        primaryStage.setScene(cena);
        TelaController controller = carregar.getController();
        primaryStage.setOnCloseRequest(evento -> {
            controller.apagarArqTemp();
            System.exit(0);
        });
        primaryStage.show();
        controller.iniciarServidor();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
