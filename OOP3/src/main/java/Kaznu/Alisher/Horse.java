package Kaznu.Alisher;

public class Horse implements Transport {
    private int stamina;

    public Horse(int stamina) {
        this.stamina = stamina;
    }

    @Override
    public boolean move(int distance, TerrainType terrain) {
        if (terrain == TerrainType.SWAMP) {
            System.out.println("Лошадь отказывается идти через: " + terrain.getDescription());
            return false;
        }
        if (stamina >= distance) {
            stamina -= distance;
            System.out.println("Лошадь проскакала " + distance + " км. Остаток сил: " + stamina);
            return true;
        }
        System.out.println("Лошадь слишком устала, чтобы пройти " + distance + " км.");
        return false;
    }
}