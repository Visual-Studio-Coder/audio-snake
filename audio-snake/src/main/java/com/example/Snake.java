package com.example;

import java.util.ArrayList;

public class Snake {

    public ArrayList<Coordinate> snakeCoordinates = new ArrayList<>(); //make private later

    public Snake() {
        snakeCoordinates.add(new Coordinate(6, 6));
        snakeCoordinates.add(new Coordinate(6, 7));
        // initialized the Snake object in the middle of the board (a 11x11 board as of
        // now)
    }

    public boolean isIn(Coordinate c) {
        for (int i = 0; i < getLength(); i++) {
            if (snakeCoordinates.get(i).equals(c)) {
                return true;
            }
        }
        return false;
    }

    public int getLength() {
        return snakeCoordinates.size();
    }

    public Coordinate getHeadCoordinate() {
        return snakeCoordinates.getFirst(); // 0th index
    }

    public boolean willEatApple(char dir, Coordinate appleCoordinate) {
        if (dir == 'r') {
            if (
                getHeadCoordinate().getX() + 1 == appleCoordinate.getX() &&
                getHeadCoordinate().getY() == appleCoordinate.getY()
            ) {
                return true;
            }
        } else if (dir == 'l') {
            if (
                getHeadCoordinate().getX() - 1 == appleCoordinate.getX() &&
                getHeadCoordinate().getY() == appleCoordinate.getY()
            ) {
                return true;
            }
        } else if (dir == 'u') {
            if (
                getHeadCoordinate().getY() - 1 == appleCoordinate.getY() &&
                getHeadCoordinate().getX() == appleCoordinate.getX()
            ) {
                return true;
            }
        } else if (dir == 'd') {
            // dir == 'd'
            if (
                getHeadCoordinate().getY() + 1 == appleCoordinate.getY() &&
                getHeadCoordinate().getX() == appleCoordinate.getX()
            ) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public void eatApple(Coordinate appleCoordinate) {
        snakeCoordinates.addFirst(
            new Coordinate(appleCoordinate.getX(), appleCoordinate.getY())
        );
    }

    // move up
    public void moveUp() {
        snakeCoordinates.addFirst(
            new Coordinate(
                getHeadCoordinate().getX(),
                getHeadCoordinate().getY() - 1
            )
        );
        snakeCoordinates.removeLast();
    }

    // move down
    public void moveDown() {
        snakeCoordinates.addFirst(
            new Coordinate(
                getHeadCoordinate().getX(),
                getHeadCoordinate().getY() + 1
            )
        );
        snakeCoordinates.removeLast();
    }

    // move right
    public void moveRight() {
        snakeCoordinates.addFirst(
            new Coordinate(
                getHeadCoordinate().getX() + 1,
                getHeadCoordinate().getY()
            )
        );
        snakeCoordinates.removeLast();
    }

    // move left
    public void moveLeft() {
        snakeCoordinates.addFirst(
            new Coordinate(
                getHeadCoordinate().getX() - 1,
                getHeadCoordinate().getY()
            )
        );
        snakeCoordinates.removeLast();
    }

    public Coordinate getBody(int index) {
        return snakeCoordinates.get(index);
    }

    public boolean isDead() {
        if (getLength() > 3) {
            for (int i = 1; i < getLength(); i++) {
                if (getHeadCoordinate().equals(snakeCoordinates.get(i))) {
                    System.out.println("is dead (i think)!");
                    return true;
                }
            }
        }
        //assuming board is 10x10
        if (
            getHeadCoordinate().getX() < 0 ||
            getHeadCoordinate().getX() > 9 ||
            getHeadCoordinate().getY() < 0 ||
            getHeadCoordinate().getY() > 9
        ) {
            return true;
        }

        // check if it is outside the 10x10 boundary

        return false;
    }
}
