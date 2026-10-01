import org.postgresql.ds.PGSimpleDataSource;

import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();

        try (InputStream input = Main.class
                .getClassLoader()
                .getResourceAsStream("db.conf")) {

            if (input == null) {
                throw new RuntimeException("db.conf not found");
            }

            config.load(input);
        }

        String host = config.getProperty("host");
        int port = Integer.parseInt(config.getProperty("port"));
        String database = config.getProperty("database");

        String login = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        int interval = Integer.parseInt(System.getenv("PING_INTERVAL"));

        String logFile = System.getenv("LOG_FILE");

        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setServerNames(new String[]{host});
        dataSource.setPortNumbers(new int[]{port});
        dataSource.setDatabaseName(database);

        dataSource.setUser(login);
        dataSource.setPassword(password);
        dataSource.setConnectTimeout(5);
        dataSource.setSocketTimeout(5);

        while (true) {
            try {
                checkDatabase(dataSource, logFile);
            } catch (Exception e) {
                logError("Database connection error: " + e.getMessage(), logFile);
            }
            Thread.sleep(interval * 1000L);
        }
    }

    private static void checkDatabase(
            PGSimpleDataSource dataSource,
            String logFile
    ) throws Exception {

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.setQueryTimeout(5);

            try (ResultSet resultSet =
                         statement.executeQuery("SELECT VERSION();")) {

                if (resultSet.next()) {
                    String version = resultSet.getString(1);

                    logOut("Connection successful", logFile);

                    if (version != null && version.startsWith("PostgreSQL 18")) {
                        logOut("Database response: " + version, logFile);
                    } else {
                        logOut("Unexpected database response: " + version, logFile);
                    }
                } else {
                    logOut("Unexpected database response: empty result", logFile);
                }
            }
        }
    }

    private static void logOut(String message, String logFile) {
        String log = LocalDateTime.now() + " " + message;
        System.out.println(log);

        writeToFile(log, logFile);
    }

    private static void logError(String message, String logFile) {
        String log = LocalDateTime.now() + " " + message;
        System.err.println(log);

        writeToFile(log, logFile);
    }

    private static void writeToFile(String message, String logFile) {
        if (logFile == null || logFile.isBlank()) {
            return;
        }

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(message + System.lineSeparator());
        } catch (Exception e) {
            System.err.println(
                    "Failed to write log file: " + e.getMessage()
            );
        }
    }
}