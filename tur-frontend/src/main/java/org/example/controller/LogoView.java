package org.example.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LogoView {

    public static ImageView create(double size) {
        Image img = new Image(LogoView.class.getResourceAsStream("/images/logo.png"),
                size, size, true, true);
        ImageView iv = new ImageView(img);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        return iv;
    }
}
