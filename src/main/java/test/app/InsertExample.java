package test.app;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dao.exceptions.DataAccessException;
import test.dao.AddressDAO;
import test.dao.OrderDAO;
import test.dao.ProductDAO;
import test.dao.RoleDAO;
import test.dao.UserDAO;
import test.model.Address;
import test.model.Order;
import test.model.Product;
import test.model.Role;
import test.model.User;

public class InsertExample {

    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();
            RoleDAO roleDAO = new RoleDAO();
            OrderDAO orderDAO = new OrderDAO();
            AddressDAO addressDAO = new AddressDAO();
            ProductDAO productDAO = new ProductDAO();

            // Inserir Roles
            Role roleAdmin = new Role();
            roleAdmin.setRoleName("ADMIN");
            roleDAO.insert(roleAdmin);

            Role roleUser = new Role();
            roleUser.setRoleName("USER");
            roleDAO.insert(roleUser);

            Role roleGuest = new Role();
            roleGuest.setRoleName("GUEST");
            roleDAO.insert(roleGuest);

            System.out.println("Roles inseridas: " + Arrays.asList(roleAdmin.getId(), roleUser.getId(), roleGuest.getId()));

            // Inserir alguns Users
            User u1 = new User();
            u1.setName("Alice");
            userDAO.insert(u1);
            System.out.println("Usuário inserido: ID=" + u1.getId() + ", name=" + u1.getName());
            userDAO.addRoles(u1, Arrays.asList(roleAdmin.getId(), roleUser.getId()));
            System.out.println("Roles ADMIN e USER adicionadas ao usuário " + u1.getName());

            User u2 = new User();
            u2.setName("Bob");
            userDAO.insert(u2);
            System.out.println("Usuário inserido: ID=" + u2.getId() + ", name=" + u2.getName());
            userDAO.addRoles(u2, Arrays.asList(roleUser.getId(), roleGuest.getId()));
            System.out.println("Roles USER e GUEST adicionadas ao usuário " + u2.getName());

            User u3 = new User();
            u3.setName("Carol");
            userDAO.insert(u3);
            System.out.println("Usuário inserido: ID=" + u3.getId() + ", name=" + u3.getName());
            // Carol sem roles por enquanto

            // Inserir Address (OneToOne com User)
            Address addr1 = new Address();
            addr1.setStreet("Main St");
            addr1.setCity("Metropolis");
            addr1.setUser(u1); // OneToOne
            addressDAO.insert(addr1);
            System.out.println("Endereço inserido: ID=" + addr1.getId() + " para o usuário " + u1.getName());

            Address addr2 = new Address();
            addr2.setStreet("Second Ave");
            addr2.setCity("Gotham");
            addr2.setUser(u2);
            addressDAO.insert(addr2);
            System.out.println("Endereço inserido: ID=" + addr2.getId() + " para o usuário " + u2.getName());

            // Inserir Orders (ManyToOne com User)
            Order o1 = new Order();
            o1.setUser(u1); 
            o1.setOrderDate(new Date());
            orderDAO.insert(o1);
            System.out.println("Order inserido: ID=" + o1.getId() + " para o user " + u1.getName());

            Order o2 = new Order();
            o2.setUser(u1);
            o2.setOrderDate(new Date());
            orderDAO.insert(o2);
            System.out.println("Order inserido: ID=" + o2.getId() + " para o user " + u1.getName());

            Order o3 = new Order();
            o3.setUser(u2);
            o3.setOrderDate(new Date());
            orderDAO.insert(o3);
            System.out.println("Order inserido: ID=" + o3.getId() + " para o user " + u2.getName());

            // Inserir Products (OneToMany do Order p/ Product - Product tem ManyToOne para Order)
            Product p1 = new Product();
            p1.setName("Produto A");
            p1.setPrice(10.0);
            p1.setOrder(o1);
            productDAO.insert(p1);

            Product p2 = new Product();
            p2.setName("Produto B");
            p2.setPrice(20.0);
            p2.setOrder(o1);
            productDAO.insert(p2);

            Product p3 = new Product();
            p3.setName("Produto C");
            p3.setPrice(5.0);
            p3.setOrder(o2);
            productDAO.insert(p3);

            Product p4 = new Product();
            p4.setName("Produto D");
            p4.setPrice(15.0);
            p4.setOrder(o3);
            productDAO.insert(p4);

            System.out.println("Produtos inseridos: A, B no primeiro Order do Alice; C no segundo Order do Alice; D no Order do Bob");

            // Insert Example finalizado
            System.out.println("Inserções concluídas com sucesso!");

        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("Erro de acesso a dados: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}
