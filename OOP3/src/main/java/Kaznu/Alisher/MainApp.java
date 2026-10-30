package Kaznu.Alisher;

public class MainApp {
    public static void main(String[] args) {
        Person azamat = new Person("Азамат");
        Car car = new Car(50);
        Horse horse = new Horse(30);
        Bicycle bike = new Bicycle();
        AllTerrainVehicle atv = new AllTerrainVehicle(100);

        azamat.move(5, TerrainType.DENSE_FOREST);

        azamat.mount(car);
        azamat.move(20, TerrainType.PLAIN);
        azamat.move(10, TerrainType.SWAMP);

        azamat.dismount();
        azamat.mount(horse);
        azamat.move(15, TerrainType.DENSE_FOREST);
        azamat.move(20, TerrainType.PLAIN);

        azamat.dismount();
        azamat.mount(atv);
        azamat.move(30, TerrainType.SWAMP);
    }
}