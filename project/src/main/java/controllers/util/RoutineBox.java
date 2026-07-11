package controllers.util;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.RoutineElement;
import util.RoutineTimeSpec;

public class RoutineBox extends VBox {
    private RoutineElement element;
    private RoutineTimeSpec timeSpec;
    public RoutineBox(RoutineElement element, RoutineTimeSpec timeSpec){
        this.element = element;
        this.timeSpec = timeSpec;
        getStyleClass().add("routine-box");
        this.setAlignment(Pos.TOP_LEFT);
        this.getChildren().add(new Label(element.getName()));
        Label timeLabel = new Label(timeSpec.start().toString() + " - " + timeSpec.end().toString());
        timeLabel.getStyleClass().add("label-small");

        this.getChildren().add(timeLabel);
    }
}
