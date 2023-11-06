// Lab 2.3

import java.util.*;

enum TYPE {
    POINT,
    CIRCLE
}

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

interface Movable {
    void MoveUp() throws ObjectCanNotBeMovedException;
    void MoveDown() throws ObjectCanNotBeMovedException;
    void MoveLeft() throws ObjectCanNotBeMovedException;
    void MoveRight() throws ObjectCanNotBeMovedException;
    int getCurrentXPosition();
    int getCurrentYPosition();
}

class ObjectCanNotBeMovedException extends Exception {
    ObjectCanNotBeMovedException(int x, int y) {
        super(String.format("Point (%d,%d) is out of bounds", x, y));
    }
}

class MovableObjectNotFittableException extends Exception {
    MovableObjectNotFittableException(String msg) {
        super(msg);
    }
}

class MovablePoint implements Movable {
    private int x;
    private int y;
    private int xSpeed;
    private int ySpeed;

    MovablePoint(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void MoveUp() throws ObjectCanNotBeMovedException {
        if(y < 0 || y + ySpeed > MovablesCollection.Y_MAX) {
            throw new ObjectCanNotBeMovedException(x, y + ySpeed);
        }
        y += ySpeed;
    }

    public void MoveDown() throws ObjectCanNotBeMovedException {
        if(y - ySpeed < 0 || y > MovablesCollection.Y_MAX) {
            throw new ObjectCanNotBeMovedException(x, y - ySpeed);
        }
        y -= ySpeed;
    }

    public void MoveRight() throws ObjectCanNotBeMovedException {
        if(x < 0 || x + xSpeed > MovablesCollection.X_MAX) {
            throw new ObjectCanNotBeMovedException(x + xSpeed, y);
        }
        x += xSpeed;
    }

    public void MoveLeft() throws ObjectCanNotBeMovedException {
        if(x - xSpeed < 0 || x > MovablesCollection.X_MAX) {
            throw new ObjectCanNotBeMovedException(x - xSpeed, y);
        }
        x -= xSpeed;
    }

    public int getCurrentXPosition() {
        return x;
    }

    public int getCurrentYPosition() {
        return y;
    }

    @Override
    public String toString() {
        return "Movable point with coordinates (" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovablePoint that = (MovablePoint) o;
        return x == that.x && y == that.y && xSpeed == that.xSpeed && ySpeed == that.ySpeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, xSpeed, ySpeed);
    }
}

class MovableCircle implements Movable {
    private int radius;
    private MovablePoint point;

    MovableCircle(int radius, MovablePoint point) {
        this.radius = radius;
        this.point = point;
    }

    public void MoveUp() throws ObjectCanNotBeMovedException {
        int x = getCurrentXPosition(), y = getCurrentYPosition();
        if(y < 0 || y + radius > MovablesCollection.Y_MAX) {
            throw new ObjectCanNotBeMovedException(x, y);
        }
        point.MoveUp();
    }

    public void MoveDown() throws ObjectCanNotBeMovedException {
        int x = getCurrentXPosition(), y = getCurrentYPosition();
        if(y - radius < 0 || y > MovablesCollection.Y_MAX) {
            throw new ObjectCanNotBeMovedException(x, y);
        }
        point.MoveDown();
    }

    public void MoveRight() throws ObjectCanNotBeMovedException {
        int x = getCurrentXPosition(), y = getCurrentYPosition();
        if(x < 0 || x + radius> MovablesCollection.X_MAX) {
            throw new ObjectCanNotBeMovedException(x, y);
        }
        point.MoveRight();
    }

    public void MoveLeft() throws ObjectCanNotBeMovedException {
        int x = getCurrentXPosition(), y = getCurrentYPosition();
        if(x - radius < 0 || x > MovablesCollection.X_MAX) {
            throw new ObjectCanNotBeMovedException(x, y);
        }
        point.MoveLeft();
    }

    public int getCurrentXPosition() {
        return point.getCurrentXPosition();
    }

    public int getCurrentYPosition() {
        return point.getCurrentYPosition();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Movable circle with center coordinates (" + getCurrentXPosition() + "," + getCurrentYPosition() + ") and radius " + radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovableCircle that = (MovableCircle) o;
        return radius == that.radius && Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius, point);
    }
}

class MovablesCollection {
    private Movable [] movable;
    public static int X_MAX;
    public static int Y_MAX;

    MovablesCollection(int x_max, int y_max) {
        movable = new Movable[0];
        X_MAX = x_max;
        Y_MAX = y_max;
    }

    public static void setxMax(int x_max) {
        X_MAX = x_max;
    }

    public static void setyMax(int y_max) {
        Y_MAX = y_max;
    }

    void addMovableObject(Movable m) throws MovableObjectNotFittableException {
        int x = m.getCurrentXPosition();
        int y = m.getCurrentYPosition();

        MovablePoint point = m instanceof MovablePoint ? ((MovablePoint) m) : null;
        MovableCircle circle = m instanceof MovableCircle ? ((MovableCircle) m) : null;

        if(point != null) {
            if(x < 0 || x > X_MAX) {
                throw new MovableObjectNotFittableException(m + " can not be fitted into the collection");
            }
            if(y < 0 || y > Y_MAX) {
                throw new MovableObjectNotFittableException(m + " can not be fitted into the collection");
            }
        } else if(circle != null) {
            int r = circle.getRadius();
            if(x - r < 0 || x + r > X_MAX) {
                throw new MovableObjectNotFittableException("Movable circle with center (" + x + "," + y + ") and radius " + r + " can not be fitted into the collection");
            }
            if(y - r < 0 || y + r > Y_MAX) {
                throw new MovableObjectNotFittableException("Movable circle with center (" + x + "," + y + ") and radius " + r + " can not be fitted into the collection");
            }
        }

//        MovableCircle circle = (MovableCircle)m;

        Movable [] newMovable = Arrays.copyOf(movable, movable.length + 1);
        newMovable[newMovable.length - 1] = m;
        movable = newMovable;
    }

    void moveObjectsFromTypeWithDirection(TYPE type, DIRECTION direction) {
        for(Movable m : movable) {
            MovablePoint movablePoint = m instanceof MovablePoint ? ((MovablePoint) m) : null;
            MovableCircle movableCircle = m instanceof MovableCircle ? ((MovableCircle) m) : null;

            if((type == TYPE.POINT && movablePoint == null) || (type == TYPE.CIRCLE && movableCircle == null)) {
                continue;
            }

            if(direction == DIRECTION.UP) {
                try {
                    m.MoveUp();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if(direction == DIRECTION.DOWN) {
                try {
                    m.MoveDown();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if(direction == DIRECTION.LEFT) {
                try {
                    m.MoveLeft();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if(direction == DIRECTION.RIGHT) {
                try {
                    m.MoveRight();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Collection of movable objects with size ").append(movable.length).append(":\n");
        for(Movable m : movable) {
            str.append(m).append("\n");
        }
        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovablesCollection that = (MovablesCollection) o;
        return Arrays.equals(movable, that.movable);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(movable);
    }
}

public class CirclesTest {

    public static void main(String[] args) {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);

            if (Integer.parseInt(parts[0]) == 0) { //point
                try {
                    collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else { //circle
                int radius = Integer.parseInt(parts[5]);
                try {
                    collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        }
        System.out.println(collection.toString());

        System.out.println("MOVE POINTS TO THE LEFT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES DOWN");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);
        System.out.println(collection.toString());

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setxMax(90);
        MovablesCollection.setyMax(90);

        System.out.println("MOVE POINTS TO THE RIGHT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES UP");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);
        System.out.println(collection.toString());


    }


}
