package Ablaze.Ablaze;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author reegan
 */
public class ComboBoxEditable extends Application {

    Node sub;

    @Override
    public void start(Stage primaryStage) {

        ComboBox mainCombo = new ComboBox(listofCombo());
        Button  save = new Button("Save");
        sub = new ComboBox(listofCombo());
        HBox root = new HBox(20);
        root.getChildren().addAll(mainCombo, sub,save);

        mainCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == "Others") {
                    sub = new TextField();
                } else {
                    sub = new ComboBox(listofCombo());
                }
                root.getChildren().remove(1);
                root.getChildren().add(1, sub);
            }
        });
        save.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println(mainCombo.getValue());
                if(sub.getClass() == ComboBox.class) {
                    ComboBox sub1 = (ComboBox)sub;
                    System.out.println(sub1.getValue());
                } else {
                    TextField field = (TextField)sub;
                    System.out.println(field.getText());
                }
            }
        });


        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList listofCombo() {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf("Hello" + i));
        }
        list.add("Others");
        return list;
    }

}