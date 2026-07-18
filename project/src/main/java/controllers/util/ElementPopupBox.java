package controllers.util;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import model.RoutineElement;
import model.RoutineService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ElementPopupBox extends PopupBox{
    private TextField nameTextField;
    private ComboBox<RoutineElement> elementComboBox;
    private Button newButton, editButton, deleteButton, saveButton, cancelButton, yesButton, noButton;
    private List<Node> createNodes;
    private List<Node> defaultNodes;
    private HBox confirmationHBox;
    private Label errorLabel;
    private enum Mode {
        DEFAULT,
        CREATE,
        EDIT,
    }

    private Mode mode;

    private Runnable onEdit;

    public ElementPopupBox(RoutineService service){
        super(service);
        nameTextField = new TextField();
        elementComboBox = new ComboBox<>();
        newButton = new Button("New");
        editButton = new Button("Edit");
        deleteButton = new Button("Delete");
        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        elementComboBox.setItems(FXCollections.observableArrayList(routineService.getRoutineElementList()));
        if (!elementComboBox.getItems().isEmpty()){
            elementComboBox.setValue(elementComboBox.getItems().get(0));
        }

        yesButton = new Button("Yes");
        noButton = new Button("No");
        Label sureLabel = new Label("Are you sure?");
        sureLabel.setStyle("-fx-font-size: 14px; -fx-padding: 8 8 4 8");
        confirmationHBox = new HBox(sureLabel, yesButton, noButton);
        confirmationHBox.setSpacing(5);
        confirmationHBox.setManaged(false);
        confirmationHBox.setVisible(false);
        deleteButton.setOnMouseClicked(e -> {if (elementComboBox.getValue() != null ) {confirmationHBox.setManaged(true); confirmationHBox.setVisible(true);}});
        noButton.setOnMouseClicked(e -> {confirmationHBox.setManaged(false); confirmationHBox.setVisible(false);});
        yesButton.setOnMouseClicked(e -> deleteRoutineElement());

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-message-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        createNodes = new ArrayList<>(List.of(nameTextField, saveButton, cancelButton));
        defaultNodes = new ArrayList<>(List.of(newButton, editButton, deleteButton, elementComboBox));

        saveButton.setOnMouseClicked(e -> handleSaveButton());
        cancelButton.setOnMouseClicked(e -> toggleDefaultMode());
        newButton.setOnMouseClicked(e -> toggleCreateMode());
        editButton.setOnMouseClicked(e -> toggleEditMode());


        HBox buttonHbox = new HBox(newButton, editButton, deleteButton, saveButton, cancelButton);
        buttonHbox.setSpacing(5);

        Region spacer = new Region();
        setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(nameTextField, elementComboBox, spacer, errorLabel, buttonHbox, confirmationHBox);
        toggleDefaultMode();
    }

    private void toggleDefaultMode(){
        for (Node n : createNodes){
            n.setManaged(false);
            n.setVisible(false);
        }

        for (Node n : defaultNodes){
            n.setManaged(true);
            n.setVisible(true);
        }
        mode = Mode.DEFAULT;
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
    }

    private void toggleCreateMode() {
        for (Node n : defaultNodes) {
            n.setManaged(false);
            n.setVisible(false);
        }

        for (Node n : createNodes) {
            n.setManaged(true);
            n.setVisible(true);
        }
        confirmationHBox.setVisible(false);
        confirmationHBox.setManaged(false);
        nameTextField.setText("");
        mode = Mode.CREATE;
    }

    private void toggleEditMode(){
        for (Node n : defaultNodes) {
            n.setManaged(false);
            n.setVisible(false);
        }

        for (Node n : createNodes) {
            n.setManaged(true);
            n.setVisible(true);
        }
        confirmationHBox.setVisible(false);
        confirmationHBox.setManaged(false);
        nameTextField.setText(elementComboBox.getValue().getName());
        mode = Mode.EDIT;
    }

    private void handleSaveButton(){
        if (mode == Mode.EDIT){
            RoutineElement element = elementComboBox.getValue();
            try {
                routineService.editRoutineElementName(element, nameTextField.getText());
                elementComboBox.setValue(null);
                elementComboBox.setValue(element);
                toggleDefaultMode();
                onEdit.run();
            } catch (IOException e) {
                errorLabel.setManaged(true);
                errorLabel.setVisible(true);
                errorLabel.setText(e.getMessage());
            }
        }

        if (mode == Mode.CREATE){
            try {
                RoutineElement element = routineService.createRoutineElement(nameTextField.getText());
                elementComboBox.getItems().add(element);
                toggleDefaultMode();;
            } catch (IOException e) {
                errorLabel.setManaged(true);
                errorLabel.setVisible(true);
                errorLabel.setText(e.getMessage());
            }
        }
    }

    private void deleteRoutineElement(){
        RoutineElement element = elementComboBox.getValue();
        routineService.deleteRoutineElement(element);
        elementComboBox.getItems().remove(element);
        if (!elementComboBox.getItems().isEmpty()){
            elementComboBox.setValue(elementComboBox.getItems().getFirst());
        }
        confirmationHBox.setVisible(false);
        confirmationHBox.setManaged(false);
        onEdit.run();
    }

    public void setOnEdit(Runnable onEdit){
        this.onEdit = onEdit;
    }
}
