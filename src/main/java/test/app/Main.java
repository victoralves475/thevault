package test.app;

import java.util.Arrays;
import java.util.List;

import database.DatabaseConnection;
import metadata.extractor.MetadataExtractor;
import metadata.extractor.PostgresTypeMapping;
import metadata.extractor.relationships.ManyToManyHandler;
import metadata.extractor.relationships.ManyToOneHandler;
import metadata.extractor.relationships.OneToManyHandler;
import metadata.extractor.relationships.OneToOneHandler;
import metadata.extractor.relationships.interfaces.RelationshipHandler;
import schema.SchemaGenerator;
import test.model.Address;
import test.model.Order;
import test.model.Product;
import test.model.Role;
import test.model.User;

public class Main {

    public static void main(String[] args) {
        try {
            // Configurar conexão
            DatabaseConnection.getInstance();

            // Lista de handlers de relacionamento
            List<RelationshipHandler> handlers = Arrays.asList(
                new ManyToOneHandler(),
                new OneToOneHandler(),
                new OneToManyHandler(),
                new ManyToManyHandler()
            );

            // Criar extractor e schema
            MetadataExtractor extractor = new MetadataExtractor(new PostgresTypeMapping(), handlers);
            SchemaGenerator schemaGenerator = new SchemaGenerator(extractor);

            schemaGenerator.createTableIfNotExists(Role.class);
            schemaGenerator.createTableIfNotExists(User.class);
            schemaGenerator.createTableIfNotExists(Address.class);
            schemaGenerator.createTableIfNotExists(Order.class);
            schemaGenerator.createTableIfNotExists(Product.class);

              
          
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro na criação das tabelas: " + e.getMessage());
        }
        
        System.out.println("Tabelas criadas com sucesso");
    }
}
