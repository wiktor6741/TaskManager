package controllers.util;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.RoutineElement;
import util.ElementTimePair;
import util.RoutineTimeSpec;

import java.util.function.Consumer;

public class RoutineBox extends VBox {
    private ElementTimePair pair;
    private Consumer<ElementTimePair> onClick;
    public RoutineBox(ElementTimePair pair){
        this.pair = pair;
        getStyleClass().add("routine-box");
        this.setAlignment(Pos.TOP_LEFT);
        this.getChildren().add(new Label(pair.element().getName()));
        Label timeLabel = new Label(pair.timeSpec().start().toString() + " - " + pair.timeSpec().end().toString());
        timeLabel.getStyleClass().add("label-small");

        this.getChildren().add(timeLabel);

        setOnMouseClicked(e -> { if (onClick != null) {onClick.accept(pair); }});
    }

    public void setOnClick(Consumer<ElementTimePair> onClick){
        this.onClick = onClick;
    }
}
