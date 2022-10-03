package com.example.nemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.time.Clock;

public class Launch extends Application{
    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        try {
            String css = this.getClass().getResource("style/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception er) {
            System.out.println("Css files not found");
        }
        try {
            Image icon = new Image(this.getClass().getResource("picture/Dino07.PNG").toExternalForm()); //เปลี่ยนรูปด้านบน
            this.stage.getIcons().add(icon);
        }catch (Exception e){
            System.out.println("Icon file not found");
        }

        this.stage.setTitle("Midterm project"); //ชื่อไฟล์ด้านบน
        this.stage.setResizable(false);
        this.stage.setScene(scene);
        this.stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
