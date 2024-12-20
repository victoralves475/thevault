package test.app;

import dao.exceptions.DataAccessException;
import test.dao.UserDAO;
import test.model.User;

public class ValidationExample {

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        try {
            // Tentar inserir um usuário com nome vazio
            User u1 = new User();
            u1.setName(""); // Vazio, viola @NotBlank
            userDAO.insert(u1);
            System.out.println("Inseriu usuário com nome vazio, o que não era esperado!");

        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Como esperado, erro ao inserir usuário com nome vazio: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Tentar inserir um usuário com nome nulo
            User u2 = new User();
            u2.setName(null); // Nulo, também viola @NotBlank
            userDAO.insert(u2);
            System.out.println("Inseriu usuário com nome nulo, o que não era esperado!");

        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Como esperado, erro ao inserir usuário com nome nulo: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Tentar inserir um usuário com nome válido
            User u3 = new User();
            u3.setName("Valid User");
            userDAO.insert(u3);
            System.out.println("Usuário válido inserido com sucesso: ID=" + u3.getId() + ", Name=" + u3.getName());
        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Não era esperado falhar no usuário válido!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
