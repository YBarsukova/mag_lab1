import org.postgresql.ds.PGSimpleDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();

        try (InputStream input = Main.class
                .getClassLoader()
                .getResourceAsStream("db.conf")) {

            if (input == null) {
                throw new RuntimeException("db.conf не найден");
            }

            config.load(input);
        }

        String host = config.getProperty("host");
        int port = Integer.parseInt(config.getProperty("port"));
        String database = config.getProperty("database");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Логин: ");
        String login = scanner.nextLine();

        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setServerNames(new String[]{host});
        dataSource.setPortNumbers(new int[]{port});
        dataSource.setDatabaseName(database);

        dataSource.setUser(login);
        dataSource.setPassword(password);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT VERSION();")) {

            if (resultSet.next()) {
                System.out.println("done");
                System.out.println(resultSet.getString(1));
            }
        }
    }
}