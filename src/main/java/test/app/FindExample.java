package test.app;

import java.util.List;

import dao.exceptions.DataAccessException;
import test.dao.OrderDAO;
import test.dao.RoleDAO;
import test.dao.UserDAO;
import test.model.Order;
import test.model.Role;
import test.model.User;

public class FindExample {

    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();
            RoleDAO roleDAO = new RoleDAO();
            OrderDAO orderDAO = new OrderDAO();

            // Buscar um usuário pelo ID
            User u = userDAO.findById(1);
            if (u != null) {
                System.out.println("Usuário encontrado: " + u.getName());

                // Caso tenha um método para buscar roles do usuário
                List<Integer> roleIds = userDAO.getUserRolesIds(u.getId());
                System.out.println("Roles do usuário: " + roleIds);

                // Buscar um role
                if (!roleIds.isEmpty()) {
                    Role r = roleDAO.findById(roleIds.get(0));
                    if (r != null) {
                        System.out.println("Primeira role do usuário: " + r.getRoleName());
                    }
                }

                // Buscar Orders deste usuário
                // Supondo que temos um método em OrderDAO para buscar por user_id
                List<Order> userOrders = orderDAO.findByUserId(u.getId());
                System.out.println("Orders do usuário: " + userOrders.size());
                for (Order o : userOrders) {
                    System.out.println("Order ID: " + o.getId() + ", data: " + o.getOrderDate());
                }

            } else {
                System.out.println("Usuário não encontrado.");
            }

            // Buscar todos os usuários
            List<User> allUsers = userDAO.findAll();
            System.out.println("Total de usuários: " + allUsers.size());

        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Erro de acesso a dados: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}
