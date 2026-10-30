package Kaznu.Alisher;

public class Bicycle implements Transport {
    @Override
    public boolean move(int distance, TerrainType terrain) {
        if (terrain == TerrainType.SWAMP) {
            System.out.println("На велосипеде невозможно проехать через: " + terrain.getDescription());
            return false;
        }
        System.out.println("Велосипед успешно проехал " + distance + " км.");
        return true;
    }
}