package test.app;

import dao.exceptions.DataAccessException;
import test.dao.UserDAO;
import test.model.User;

public class DeleteExample {

    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();

            // Supondo que queremos deletar o usuário com ID=1
            User u = userDAO.findById(1);
            if (u != null) {
                userDAO.delete(u.getId());
                System.out.println("Usuário deletado com sucesso.");
            } else {
                System.out.println("Usuário não encontrado para exclusão.");
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
