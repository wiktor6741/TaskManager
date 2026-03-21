package controllers.util;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ImportanceIndicator extends HBox {

    public ImportanceIndicator(int value, int max) {
        setSpacing(3);
        setAlignment(Pos.CENTER);
        for (int i = 1; i <= max; i++) {
            Circle dot = new Circle(4);

            if (i <= value) {
                dot.setFill(getColor(value, max)); // kolor zależny od poziomu
            } else {
                dot.setFill(Color.TRANSPARENT);
            }
            dot.setStroke(Color.GRAY);
            getChildren().add(dot);
        }
    }

    private Color getColor(int value, int max) {
        double ratio = (double) value / max;

        if (ratio > 0.7) return Color.RED;
        if (ratio > 0.4) return Color.ORANGE;
        return Color.GREEN;
    }
}
