package controllers.util;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.geometry.Pos;

public class ImportanceSelector extends HBox {

    private final int max;
    private int selected = 0;
    private final Circle[] dots;

    public ImportanceSelector(int max) {
        this.max = max;
        this.dots = new Circle[max];

        setSpacing(3);
        setAlignment(Pos.CENTER);

        for (int i = 0; i < max; i++) {
            Circle dot = new Circle(6); // większa kropka dla interakcji
            dot.setFill(Color.TRANSPARENT);
            dot.setStroke(Color.GRAY);

            final int index = i;
            dot.setOnMouseEntered(e -> highlight(index + 1));
            dot.setOnMouseExited(e -> highlight(selected));
            dot.setOnMouseClicked(e -> selected = index + 1);

            dots[i] = dot;
            getChildren().add(dot);
        }
    }

    private void highlight(int upTo) {
        for (int i = 0; i < max; i++) {
            if (i < upTo) {
                dots[i].setFill(getColor(upTo, max));
            } else {
                dots[i].setFill(Color.TRANSPARENT);
            }
        }
    }

    private Color getColor(int value, int max) {
        double ratio = (double) value / max;
        if (ratio > 0.7) return Color.RED;
        if (ratio > 0.4) return Color.ORANGE;
        return Color.GREEN;
    }

    public int getValue() {
        return selected;
    }

    public void setValue(int value) {
        selected = value;
        highlight(selected);
    }
}
