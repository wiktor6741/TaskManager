package controllers.util;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import model.RoutineService;

public abstract class RoutinePopupBox extends PopupBox{
    protected final TextField nameTextField;
    protected final ComboBox<Integer> weekCountComboBox;
    protected final Button saveButton, cancelButton;
    protected final HBox buttonHBox;
    protected Runnable onCancel;
    protected Label errorLabel;
    public RoutinePopupBox(RoutineService routineService){
        super(routineService);

        nameTextField = new TextField();
        weekCountComboBox = new ComboBox<>();
        HBox nameHBox = new HBox(new Label("Name: "), nameTextField);
        HBox weekCountHBox = new HBox(new Label("Week Count: "), weekCountComboBox);
        for (int i = 1; i <= 10; i++) {
            weekCountComboBox.getItems().add(i);
        }
        weekCountComboBox.setValue(1);

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");
        buttonHBox = new HBox(saveButton, cancelButton);
        buttonHBox.setSpacing(5);

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-message-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Region spacer = new Region();
        setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(nameHBox, weekCountHBox,spacer, errorLabel, buttonHBox);
        setSpacing(10);

        cancelButton.setOnMouseClicked(e -> {if (onCancel != null )onCancel.run();});
        saveButton.setOnMouseClicked(e -> handleSaveButton());
    }

    public void setOnCancel(Runnable onCancel){
        this.onCancel = onCancel;
    }

    protected abstract void handleSaveButton();
}
