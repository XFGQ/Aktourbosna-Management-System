package org.example;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main  extends Application   // we added because of javafx GUI its makes code that its include GUI things
{

    public static void main(String[] args) {


        launch(args);//its command of javafx application start gui

    }
    public void start(Stage primStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primStage.setTitle("Aktour ViaBalkan Management System");
        primStage.setScene(new Scene(root,800,800));
        primStage.show();

    }

}

