package Kaznu.Alisher;

public class Person {
    private String name;
    private Transport currentTransport;

    public Person(String name) {
        this.name = name;
    }

    public void mount(Transport transport) {
        this.currentTransport = transport;
        System.out.println(name + " садится на выбранный транспорт.");
    }

    public void dismount() {
        this.currentTransport = null;
        System.out.println(name + " спешивается/выходит из транспорта.");
    }

    public boolean move(int distance, TerrainType terrain) {
        if (currentTransport == null) {
            System.out.println(name + " идет пешком " + distance + " км по местности: " + terrain.getDescription());
            return true;
        } else {
            System.out.print(name + " пытается переместиться: ");
            return currentTransport.move(distance, terrain);
        }
    }
}