package controllers.util;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import javafx.scene.Group;

public class ClockIcon extends Group {

    private Circle circle;
    private Line longHand;
    private Line shortHand;
    private double radius;

    public ClockIcon(double size, Color color) {
        this.radius = size / 2.0;

        circle = new Circle(radius, radius, radius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(color);
        circle.setStrokeWidth(2);

        longHand = new Line(radius, radius, radius, 0);
        longHand.setStroke(color);
        longHand.setStrokeWidth(2);

        shortHand = new Line(radius, radius, radius + radius/2, 3* radius/4);
        shortHand.setStroke(color);
        shortHand.setStrokeWidth(2);

        getChildren().addAll(circle, longHand, shortHand);
    }


    public void setUrgencyColor(Color color) {
        circle.setStroke(color);
        longHand.setStroke(color);
        shortHand.setStroke(color);
    }

    public void rotateHands(double longAngle, double shortAngle) {
        longHand.setRotate(longAngle);
        shortHand.setRotate(shortAngle);
    }
}
