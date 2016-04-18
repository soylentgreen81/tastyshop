package de.beukmann.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.beukmann.config.LocalDatePersistenceConverter;

@Entity
@Table(name = "ORDERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "orderDate", "username" }) })
public class Order {

	private Order(){		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonIgnore
	private int id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Convert(converter = LocalDatePersistenceConverter.class)
	private LocalDate orderDate;
	
	@OneToMany(mappedBy="order", orphanRemoval = true, cascade=CascadeType.ALL)
	@MapKey(name="menuPosition")
	@JsonManagedReference
	private Map<String, OrderDetail> orderDetails;
	
	@JsonIgnore
	private String username;

	private boolean isOrdered;
	
	@Transient
	public boolean isPersistent(){
		return id != 0;
	}


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




	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	

	public boolean isOrdered() {
		return isOrdered;
	}

	public void setOrdered(boolean isOrdered) {
		this.isOrdered = isOrdered;
	}
	
	public Map<String, OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(Map<String, OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	

	public static List<Order> getOrdersFromDates(LocalDate... dates) {
		List<Order> orders = new ArrayList<Order>(dates.length);
		for (LocalDate date : dates) {
			orders.add(getOrderFromDate(date));
		}
		return orders;
	}

	private static final String[] MENU_IDS = {"MENU_1", "MENU_2", "MENU_3", "MENU_4", "MENU_5", "MENU_6", "MENU_7"};  
	
	public static Order getOrderFromDate(LocalDate date) {

		Order newOrder = new Order();
		newOrder.setOrderDate(date);
		newOrder.setOrderDetails(new HashMap<String, OrderDetail>());
		for (String menuId : MENU_IDS){
			OrderDetail newDetail = new OrderDetail();
			newDetail.setMenuPosition(menuId);
			newDetail.setAmount(0);
			newDetail.setOrder(newOrder);
			newOrder.getOrderDetails().put(menuId, newDetail);
		}
		return newOrder;
	}
	public static void merge(List<Order> base, List<Order> newData){
		//Sync existing orders
		for (Order order : base){
			if (!order.isOrdered){
				Optional<Order> newEntry = newData.stream().filter(o -> o.orderDate.isEqual(order.orderDate)).findFirst();
				if (newEntry.isPresent()){
					order.copyMenus(newEntry.get());
				}
			}
		}
		//Add missing orders
		newData.stream().filter(o -> base.stream().noneMatch( x-> x.orderDate.isEqual(o.orderDate))).forEach(x -> base.add(x));
	}
	
	public void copyMenus(Order other){
		if (this.isOrdered)
			throw new IllegalArgumentException("Cannot overwrite ordered entry");
		for (String key : orderDetails.keySet()){
			final OrderDetail thisDetail = orderDetails.get(key);
			final OrderDetail otherDetail = other.getOrderDetails().get(key);
			thisDetail.setAmount(otherDetail.getAmount());
		}
	}
	
}
