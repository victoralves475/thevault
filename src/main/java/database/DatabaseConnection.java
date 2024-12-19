package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import database.exceptions.ConnectionException;

public class DatabaseConnection {

    // Instância única da classe
    private static DatabaseConnection instance;

    // Configurações do banco de dados
    private String url;
    private String username;
    private String password;

    // Construtor privado
    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")){
                if (input == null) {
                    throw new ConnectionException("Arquivo database.properties não encontrado!");
                }
                props.load(input);
            }

            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");

            // Verificar se as propriedades não são nulas
            if (this.url == null || this.username == null || this.password == null) {
                throw new ConnectionException("Propriedades do banco de dados não definidas corretamente.");
            }

        } catch (IOException e) {
            throw new ConnectionException("Falha ao carregar as propriedades do banco de dados.", e);
        }
    }

    // Método para obter a instância única
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    // Método para obter conexão com o banco
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(this.url, this.username, this.password);
        } catch (SQLException e) {
            throw new ConnectionException("Falha ao obter conexão com o banco de dados.", e);
        }
    }
}
