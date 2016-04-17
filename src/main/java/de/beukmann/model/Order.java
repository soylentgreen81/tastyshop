package de.beukmann.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import de.beukmann.config.LocalDatePersistenceConverter;

@Entity
@Table(name = "ORDERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "orderDate", "username" }) })
public class Order {

	public static List<Order> getOrdersFromDates(LocalDate... dates) {
		List<Order> orders = new ArrayList<Order>(dates.length);
		for (LocalDate date : dates) {
			orders.add(getOrderFromDate(date));
		}
		return orders;
	}

	public static Order getOrderFromDate(LocalDate date) {

		Order newOrder = new Order();
		newOrder.setOrderDate(date);
		return newOrder;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonIgnore
	private int id;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Convert(converter = LocalDatePersistenceConverter.class)
	private LocalDate orderDate;
	private boolean menu1;
	private boolean menu2;
	private boolean menu3;
	private boolean menu4;
	private boolean menu5;
	private boolean menu6;
	
	@JsonIgnore
	private String username;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public boolean isMenu1() {
		return menu1;
	}

	public void setMenu1(boolean menu1) {
		this.menu1 = menu1;
	}

	public boolean isMenu2() {
		return menu2;
	}

	public void setMenu2(boolean menu2) {
		this.menu2 = menu2;
	}

	public boolean isMenu3() {
		return menu3;
	}

	public void setMenu3(boolean menu3) {
		this.menu3 = menu3;
	}

	public boolean isMenu4() {
		return menu4;
	}

	public void setMenu4(boolean menu4) {
		this.menu4 = menu4;
	}

	public boolean isMenu5() {
		return menu5;
	}

	public void setMenu5(boolean menu5) {
		this.menu5 = menu5;
	}

	public boolean isMenu6() {
		return menu6;
	}

	public void setMenu6(boolean menu6) {
		this.menu6 = menu6;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", orderDate=" + orderDate + ", menu1=" + menu1 + ", menu2=" + menu2 + ", menu3="
				+ menu3 + ", menu4=" + menu4 + ", menu5=" + menu5 + ", menu6=" + menu6 + ", username=" + username + "]";
	}
	
}
