package Kaznu.Alisher;

import java.time.Year;

public class Main {
    public static void main(String[] args) {
        User[] users = new User[10];

        users[0] = new User("Иванов Иван Иванович", 1975, "ivanov@mail.kz");
        users[1] = new User("Сериков Азамат Касымович", 1990, "serikov@mail.kz");
        users[2] = new User("Петрова Елена Сергеевна", 1980, "petrova@mail.kz");
        users[3] = new User("Омаров Нурлан Серикович", 1968, "omarov@mail.kz");
        users[4] = new User("Смирнов Алексей Владимирович", 2001, "smirnov@mail.kz");
        users[5] = new User("Кузнецова Ольга Дмитриевна", 1970, "kuznetsova@mail.kz");
        users[6] = new User("Алиев Руслан Муратович", 1995, "aliev@mail.kz");
        users[7] = new User("Васильев Денис Павлович", 1960, "vasiliev@mail.kz");
        users[8] = new User("Ким Светлана Викторовна", 1984, "kim@mail.kz");
        users[9] = new User("Болатов Данияр Ерланович", 2003, "bolatov@mail.kz");

        int currentYear = Year.now().getValue();

        for (User user : users) {
            int age = currentYear - user.getBirthYear();
            if (age > 40) {
                user.printInfo();
                System.out.println("----------------------------------------");
            }
        }
    }
}

class User {
    private String fullName;
    private int birthYear;
    private String email;

    public User(String fullName, int birthYear, String email) {
        this.fullName = fullName;
        this.birthYear = birthYear;
        this.email = email;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void printInfo() {
        System.out.println("ФИО: " + fullName);
        System.out.println("Год рождения: " + birthYear);
        System.out.println("e-mail: " + email);
    }
}