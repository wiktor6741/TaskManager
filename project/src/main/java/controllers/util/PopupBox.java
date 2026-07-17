package controllers.util;

import javafx.scene.layout.VBox;
import model.RoutineService;

public abstract class PopupBox extends VBox {
    protected final RoutineService routineService;
    public PopupBox(RoutineService routineService){
        this.routineService = routineService;
        getStyleClass().add("popup-box");
    }
}
