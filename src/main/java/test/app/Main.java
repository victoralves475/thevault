package test.app;

import java.util.Arrays;
import java.util.List;

import dao.exceptions.DataAccessException;
import database.DatabaseConnection;
import metadata.extractor.MetadataExtractor;
import metadata.extractor.PostgresTypeMapping;
import metadata.extractor.relationships.ManyToManyHandler;
import metadata.extractor.relationships.ManyToOneHandler;
import metadata.extractor.relationships.OneToManyHandler;
import metadata.extractor.relationships.OneToOneHandler;
import metadata.extractor.relationships.interfaces.RelationshipHandler;
import schema.SchemaGenerator;
import test.dao.RoleDAO;
import test.dao.UserDAO;
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
            // A join table (users_roles) será criada automaticamente quando processar User ou Role
            // dependendo da sua implementação.

            // Testar inserção
            UserDAO userDAO = new UserDAO();
            RoleDAO roleDAO = new RoleDAO();

            Role roleAdmin = new Role();
            roleAdmin.setRoleName("ADMIN");
            roleDAO.insert(roleAdmin); // inserir role
            System.out.println(roleAdmin.getId());

            Role roleUser = new Role();
            roleUser.setRoleName("USER");
            roleDAO.insert(roleUser); // inserir role
            System.out.println(roleUser.getId());
            

            User u = new User();
            u.setName("Alice"); // NotBlank, não pode ser vazio
            userDAO.insert(u);
            System.out.println("Usuário inserido: ID=" + u.getId() + ", name=" + u.getName());

            // Adicionar roles ao usuário
            userDAO.addRoles(u, Arrays.asList(roleAdmin.getId(), roleUser.getId()));
            System.out.println("Roles adicionadas ao usuário " + u.getName());

            // Buscar roles do usuário
            List<Integer> userRoles = userDAO.getUserRolesIds(u.getId());
            System.out.println("Roles do usuário " + u.getName() + ": " + userRoles);

        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Erro de acesso a dados: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}
