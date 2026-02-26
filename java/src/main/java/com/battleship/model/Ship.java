package com.battleship.model;

public class Ship {
    private int length;
    private int startX;
    private int startY;
    private int orientation; // 0 = horizontal, 1 = vertical
    private int shipId; // Identificador único para cada barco (0-4)

    public Ship(int shipId, int length) {
        this.shipId = shipId;
        this.length = length;
        this.startX = -1;
        this.startY = -1;
        this.orientation = 0; // Horizontal por defecto
    }

    public Ship(int shipId, int length, int startX, int startY, int orientation) {
        this.shipId = shipId;
        this.length = length;
        this.startX = startX;
        this.startY = startY;
        this.orientation = orientation;
    }

    public boolean isPlaced() {
        return startX >= 0 && startY >= 0;
    }

    public void rotate() {
        this.orientation = (this.orientation == 0) ? 1 : 0;
    }

    public void placeAt(int x, int y) {
        this.startX = x;
        this.startY = y;
    }

    public void remove() {
        this.startX = -1;
        this.startY = -1;
        this.orientation = 0;
    }

    public String toProtocolString() {
        if (!isPlaced()) {
            return "";
        }
        return startX + "," + startY + "," + orientation;
    }

    public int getLength() {
        return length;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getShipId() {
        return shipId;
    }

    public boolean isHorizontal() {
        return orientation == 0;
    }

    public boolean isVertical() {
        return orientation == 1;
    }

    @Override
    public String toString() {
        return "Ship[id=" + shipId + ", size=" + length + ", at=(" + startX + "," + startY + "), orient=" + (orientation == 0 ? "H" : "V") + "]";
    }
}
