package Kaznu.Alisher;

public class AllTerrainVehicle implements Transport {
    private int fuelLiters;

    public AllTerrainVehicle(int fuelLiters) {
        this.fuelLiters = fuelLiters;
    }

    @Override
    public boolean move(int distance, TerrainType terrain) {
        if (fuelLiters >= distance * 2) {
            fuelLiters -= distance * 2;
            System.out.println("Вездеход преодолел " + distance + " км по местности '" + terrain.getDescription() + "'. Остаток бензина: " + fuelLiters + " л.");
            return true;
        }
        System.out.println("У вездехода не хватает бензина.");
        return false;
    }
}