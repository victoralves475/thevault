package test.app;

import java.util.Arrays;
import java.util.Date;

import dao.exceptions.DataAccessException;
import test.dao.OrderDAO;
import test.dao.RoleDAO;
import test.dao.UserDAO;
import test.model.Order;
import test.model.Role;
import test.model.User;

public class InsertExample {

    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();
            RoleDAO roleDAO = new RoleDAO();
            OrderDAO orderDAO = new OrderDAO();

            // Inserir Roles
            Role roleAdmin = new Role();
            roleAdmin.setRoleName("ADMIN");
            roleDAO.insert(roleAdmin);

            Role roleUser = new Role();
            roleUser.setRoleName("USER");
            roleDAO.insert(roleUser);

            // Inserir User
            User u = new User();
            u.setName("Alice"); // NotBlank
            userDAO.insert(u);
            System.out.println("Usuário inserido: ID=" + u.getId() + ", name=" + u.getName());

            // Associar Roles ao usuário (ManyToMany)
            userDAO.addRoles(u, Arrays.asList(roleAdmin.getId(), roleUser.getId()));
            System.out.println("Roles adicionadas ao usuário " + u.getName());

            // Inserir Order (ManyToOne com User)
            Order o = new Order();
            o.setUser(u); 
            o.setOrderDate(new Date());
            orderDAO.insert(o);
            System.out.println("Order inserido: ID=" + o.getId() + " para o user " + u.getName());

        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Erro de acesso a dados: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}
