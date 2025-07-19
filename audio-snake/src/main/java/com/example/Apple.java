package com.example;

import java.util.ArrayList;
import java.util.Random;

public class Apple {

    static Coordinate currentPosition = new Coordinate(0, 0);

    public static Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public static void relocateApple(Snake snake, int dimension) {
        System.out.println("relocating apple...");
        Random random = new Random();
        ArrayList<Coordinate> listAvailable = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (!snake.isIn(new Coordinate(x, y))) listAvailable.add(
                    new Coordinate(x, y)
                );
            }
        }

        Coordinate newAppleSpot = listAvailable.get(
            random.nextInt(listAvailable.size())
        );

        currentPosition.setX(newAppleSpot.getX());
        currentPosition.setY(newAppleSpot.getY());

        System.out.println("relocated apple");
    }
}
