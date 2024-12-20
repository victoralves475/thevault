package test.app;

import dao.exceptions.DataAccessException;
import test.dao.UserDAO;
import test.model.User;

public class UpdateExample {

    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();

            // Buscar usuário pelo ID
            User u = userDAO.findById(1); 
            if (u != null) {
                System.out.println("Usuário antes do update: " + u.getName());
                u.setName("Alice Updated");
                userDAO.update(u);
            } else {
                System.out.println("Usuário não encontrado para update.");
            }

        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Erro de acesso a dados: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}
