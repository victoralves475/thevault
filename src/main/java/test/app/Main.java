package test.app;

import java.util.Arrays;
import java.util.Date;

import dao.exceptions.DataAccessException;
import database.DatabaseConnection;
import metadata.extractor.MetadataExtractor;
import metadata.extractor.PostgresTypeMapping;
import metadata.extractor.relationships.ManyToOneHandler;
import metadata.extractor.relationships.interfaces.RelationshipHandler;
import schema.SchemaGenerator;
import test.dao.OrderDAO;
import test.dao.UserDAO;
import test.model.Order;
import test.model.User;

public class Main {

    public static void main(String[] args) {
        try {
            // Inicializar conexão com o banco
            DatabaseConnection.getInstance();

            // Handlers de relacionamento
            RelationshipHandler manyToOneHandler = new ManyToOneHandler();
            MetadataExtractor extractor = new MetadataExtractor(new PostgresTypeMapping(), Arrays.asList(manyToOneHandler));

            // Criar tabelas caso não existam
            SchemaGenerator schemaGenerator = new SchemaGenerator(extractor);
            schemaGenerator.createTableIfNotExists(User.class);
            schemaGenerator.createTableIfNotExists(Order.class);

            // Criar DAOs
            UserDAO userDAO = new UserDAO();
            OrderDAO orderDAO = new OrderDAO();

            // Inserir um usuário
            User u = new User();
            u.setName("Alice");
            userDAO.insert(u);
            System.out.println("Usuário inserido: ID=" + u.getId() + ", name=" + u.getName());

            // Inserir um pedido para esse usuário
            Order o = new Order();
            o.setUser(u);
            o.setOrderDate(new Date());
            orderDAO.insert(o);
            System.out.println("Pedido inserido: ID=" + o.getId() + ", user_id=" + u.getId());

            // Buscar o usuário
            User uFound = userDAO.findById(u.getId());
            System.out.println("Usuário encontrado: ID=" + uFound.getId() + ", name=" + uFound.getName());

            // Buscar o pedido
            Order oFound = orderDAO.findById(o.getId());
            System.out.println("Pedido encontrado: ID=" + oFound.getId() +
                    ", user_id=" + oFound.getUser().getId() +
                    ", user_name=" + oFound.getUser().getName() +
                    ", order_date=" + oFound.getOrderDate());

        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Erro de acesso a dados: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}
