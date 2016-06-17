package com.interrupt.doomtest;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class Line {
    public Vector2 start;
    public Vector2 end;
    public boolean solid = true;

    public Sector left = null;
    public Sector right = null;

    public Line(Vector2 start, Vector2 end, boolean solid, Sector left) {
        this.start = start;
        this.end = end;
        this.solid = solid;
        this.left = left;
    }

    public Line(Vector2 start, Vector2 end, boolean solid, Sector left, Sector right) {
        this.start = start;
        this.end = end;
        this.solid = solid;
        this.left = left;
        this.right = right;
    }

    public float getLength() {
        Vector2 tempV2 = new Vector2();
        return tempV2.set(start).sub(end).len();
    }

    public boolean isEqualTo(Line other) {
        if(other == null)
            return false;
        if(other.start.equals(start) && other.end.equals(end))
            return true;
        if(other.end.equals(start) && other.start.equals(end))
            return true;
        return false;
    }

    public Vector2 findIntersection(Vector2 s, Vector2 e) {
        Vector2 intersection = new Vector2();
        if(Intersector.intersectSegments(s, e, this.start, this.end, intersection)) {
            return intersection;
        }
        return null;
    }

    public Sector getHigherFloorSector() {
        if(left == null || right == null) return null;
        if(left.getFloorHeight() >= right.getFloorHeight()) return left;
        return right;
    }

    public Sector getLowerFloorSector() {
        if(left == null || right == null) return null;
        if(left.getFloorHeight() < right.getFloorHeight()) return left;
        return right;
    }

    public Sector getHigherCeilingSector() {
        if(left == null || right == null) return null;
        if(left.getCeilingHeight() >= right.getCeilingHeight()) return left;
        return right;
    }

    public Sector getLowerCeilingSector() {
        if(left == null || right == null) return null;
        if(left.getCeilingHeight() < right.getCeilingHeight()) return left;
        return right;
    }
}
