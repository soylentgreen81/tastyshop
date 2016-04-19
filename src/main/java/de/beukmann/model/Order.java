package de.beukmann.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import de.beukmann.config.LocalDatePersistenceConverter;

@Entity
@Table(name = "ORDERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "orderDate", "username" }) })
public class Order {
	public static int MENU_COUNT = 7;
	private Order() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonIgnore
	private int id;
	
	@Version
	private long version;
	
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Convert(converter = LocalDatePersistenceConverter.class)
	private LocalDate orderDate;

	@JsonIgnore
	@Convert(converter = LocalDatePersistenceConverter.class)
	private LocalDate modifiedDate;


	@ElementCollection
	@MapKeyColumn(name = "menu_position")
	@Column(name = "amount")
    @CollectionTable(name="order_details", joinColumns=@JoinColumn(name="order_id"))
	private Map<Integer, Integer> orderDetails;

	@JsonIgnore
	private String username;

	private boolean isOrdered;

	@Transient
	public boolean isPersistent() {
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
	

	
	@PostLoad
	private void fillUpMap(){
		for (int i=1;i<=MENU_COUNT;i++){
			if (!orderDetails.containsKey(i)){
				orderDetails.put(i, 0);
			}
		}
	} 
	@PrePersist
	@PreUpdate
	private void minimizeMap(){
		for (int i=1;i<=MENU_COUNT;i++){
			if (orderDetails.containsKey(i) && orderDetails.get(i) <= 0){
				orderDetails.remove(i);
			}
		}
	}

	public Map<Integer, Integer> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(Map<Integer, Integer> orderDetails) {
		this.orderDetails = orderDetails;
	}

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
		newOrder.setOrderDetails(new HashMap<Integer, Integer>());
		newOrder.fillUpMap();
		return newOrder;
	}

	public static void merge(List<Order> base, List<Order> newData) {
		// Sync existing orders
		for (Order order : base) {
			if (!order.isOrdered) {
				Optional<Order> newEntry = newData.stream().filter(o -> o.orderDate.isEqual(order.orderDate))
						.findFirst();
				if (newEntry.isPresent()) {
					order.copyMenus(newEntry.get());
				}
			}
		}
		// Add missing orders
		newData.stream().filter(o -> base.stream().noneMatch(x -> x.orderDate.isEqual(o.orderDate)))
				.forEach(x -> base.add(x));
	}

	public void copyMenus(Order other) {
		if (this.isOrdered)
			throw new IllegalArgumentException("Cannot overwrite ordered entry");
		for (Integer key : orderDetails.keySet()) {
			orderDetails.put(key, other.getOrderDetails().get(key));
		}
	}

}
