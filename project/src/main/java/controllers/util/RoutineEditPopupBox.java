package controllers.util;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.Routine;
import model.RoutineService;

import java.io.IOException;
import java.util.function.Consumer;

public class RoutineEditPopupBox extends RoutinePopupBox{
    private Runnable onSave;
    private Consumer<Routine> onDelete;
    private Routine routine;
    private Button deleteButton, yesButton, noButton;
    private HBox confirmationHBox;
    public RoutineEditPopupBox(RoutineService service){
        super(service);
        routine = service.getCurrentViewedRoutine();
        nameTextField.setText(routine.getName());
        weekCountComboBox.setValue(routine.getWeekCount());
        deleteButton = new Button("Delete");
        buttonHBox.getChildren().add(1, deleteButton);
        yesButton = new Button("Yes");
        noButton = new Button("No");
        Label sureLabel = new Label("Are you sure?");
        sureLabel.setStyle("-fx-font-size: 14px; -fx-padding: 8 8 4 8");
        confirmationHBox = new HBox(sureLabel, yesButton, noButton);
        confirmationHBox.setSpacing(5);
        confirmationHBox.setManaged(false);
        confirmationHBox.setVisible(false);
        getChildren().add(confirmationHBox);

        deleteButton.setOnMouseClicked(e -> {confirmationHBox.setManaged(true); confirmationHBox.setVisible(true);});
        noButton.setOnMouseClicked(e -> {confirmationHBox.setManaged(false); confirmationHBox.setVisible(false);});
        yesButton.setOnMouseClicked(e -> deleteRoutine());
    }

    @Override
    protected void handleSaveButton() {
        try {
            routineService.editRoutineName(routine, nameTextField.getText());
            routineService.editRoutineWeekCount(routine, weekCountComboBox.getValue());
            onSave.run();
        } catch (IOException e) {
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            errorLabel.setText(e.getMessage());;
        }
    }

    public void setOnSave(Runnable onSave){
        this.onSave = onSave;
    }

    public void setOnDelete(Consumer<Routine> onDelete){
        this.onDelete = onDelete;
    }

    public void deleteRoutine(){
        routineService.deleteRoutine(routine);
        onDelete.accept(routine);
    }
}
