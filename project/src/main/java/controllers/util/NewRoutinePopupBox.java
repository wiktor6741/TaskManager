package controllers.util;

import model.Routine;
import model.RoutineElement;
import model.RoutineService;

import java.io.IOException;
import java.util.function.Consumer;

public class NewRoutinePopupBox extends  RoutinePopupBox{
    private Consumer<Routine> onSave;
    public NewRoutinePopupBox(RoutineService service){
        super(service);
    }

    @Override
    protected void handleSaveButton() {
        try {
            Routine routine = routineService.createRoutine(nameTextField.getText(), weekCountComboBox.getValue());
            onSave.accept(routine);
        } catch (IOException e) {
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            errorLabel.setText(e.getMessage());
        }

    }

    public void setOnSave(Consumer<Routine> onSave){
        this.onSave = onSave;
    }
}
