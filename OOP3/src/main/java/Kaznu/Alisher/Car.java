package Kaznu.Alisher;

public class Car implements Transport {
    private int fuelLiters;

    public Car(int fuelLiters) {
        this.fuelLiters = fuelLiters;
    }

    @Override
    public boolean move(int distance, TerrainType terrain) {
        if (terrain == TerrainType.DENSE_FOREST || terrain == TerrainType.SWAMP) {
            System.out.println("Машина не может проехать через: " + terrain.getDescription());
            return false;
        }
        if (fuelLiters >= distance) {
            fuelLiters -= distance;
            System.out.println("Машина успешно проехала " + distance + " км. Остаток бензина: " + fuelLiters + " л.");
            return true;
        }
        System.out.println("У машины не хватает бензина для преодоления " + distance + " км.");
        return false;
    }
}