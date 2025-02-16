package test.app;

import dao.exceptions.DataAccessException;
import database.DatabaseConnection;
import test.dao.OrderDAO;
import test.dao.ProductDAO;
import test.dao.RoleDAO;
import test.dao.UserDAO;
import test.model.Product;

import java.util.List;

public class ExemploFindComWhere {

	public static void main(String[] args) {
		try {
			UserDAO userDAO = new UserDAO();
			RoleDAO roleDAO = new RoleDAO();
			OrderDAO orderDAO = new OrderDAO();
			ProductDAO productDAO = new ProductDAO();

			List<Product> products = productDAO.findByOrderAndPrice("1", "10");
			for (Product p : products) {
				System.out.println("Pedido: " + p.getOrder().getId() + " - Preço: " + p.getPrice());
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
