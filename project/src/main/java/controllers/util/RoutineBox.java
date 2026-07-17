package controllers.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.RoutineElement;
import util.ElementTimePair;
import util.RoutineTimeSpec;

import java.time.Duration;
import java.util.function.Consumer;

public class RoutineBox extends VBox {
    private ElementTimePair pair;
    private Consumer<ElementTimePair> onClick;
    private Label nameLabel;
    private double computedHeight;
    public RoutineBox(ElementTimePair pair, double computedHeight){
        this.pair = pair;
        this.computedHeight = computedHeight;
        getStyleClass().add("routine-box");
        this.setAlignment(Pos.TOP_LEFT);
        nameLabel = new Label(pair.element().getName());
        Duration duration = Duration.between(pair.timeSpec().start(), pair.timeSpec().end());


        this.getChildren().add(nameLabel);
        Label timeLabel = new Label(pair.timeSpec().start().toString() + " - " + pair.timeSpec().end().toString());
        timeLabel.getStyleClass().add("label-small");
        if (duration.toMinutes() >= 90) {
            this.getChildren().add(timeLabel);
        }
        if (duration.toMinutes() < 70){
            Platform.runLater(() -> smallBoxAdjustments());
        }
        setOnMouseClicked(e -> { if (onClick != null) {onClick.accept(pair); }});
    }

    public void setOnClick(Consumer<ElementTimePair> onClick){
        this.onClick = onClick;
    }

    public void smallBoxAdjustments(){
        applyCss();
        nameLabel.setStyle("-fx-padding: 0 0 0 0");

        System.out.println(nameLabel.getStyle());
        Insets padding = getPadding();
        double currentHeight = 26 + padding.getTop() + padding.getBottom();
        System.out.println("box object: " + this);
        System.out.println("Padding: " + padding);
        System.out.println("Current height: " + currentHeight);
        if (currentHeight > computedHeight){
            if (computedHeight > 26){
                int desiredPadding = ((int) computedHeight - 26) / 2;
                setStyle(String.format("-fx-padding: %d %d %d %d", desiredPadding, (int) padding.getRight(),
                        desiredPadding, (int) padding.getLeft()));
            }
            else {
                setStyle(String.format("-fx-padding: %d %d %d %d", 0, (int) padding.getRight(),
                        0, (int) padding.getLeft()));
                int desiredFontSize = (int) (computedHeight * (17.0/26.0));
                nameLabel.setStyle(String.format("-fx-font-size: %dpx; -fx-padding: 0 0 0 0", desiredFontSize));
            }
        }
    }
}
