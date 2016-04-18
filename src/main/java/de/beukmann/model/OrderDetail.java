package de.beukmann.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="ORDER_DETAILS", uniqueConstraints = { @UniqueConstraint(columnNames = { "order_id", "menuPosition" }) })
public class OrderDetail implements Comparable<OrderDetail>{
	@Id
	@GeneratedValue(strategy =GenerationType.SEQUENCE)
	@JsonIgnore
	private int id;
	private String menuPosition;
	
	private int amount;
	
	@ManyToOne(cascade= CascadeType.ALL)
	@JoinColumn(name="ORDER_ID")
	@JsonBackReference
	private Order order;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMenuPosition() {
		return menuPosition;
	}

	public void setMenuPosition(String menuPosition) {
		this.menuPosition = menuPosition;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	
	
	@Override
	public int compareTo(OrderDetail o) {
		if (o != null && menuPosition != null)
			return menuPosition.compareTo(o.menuPosition);
		else if (o != null){
			return 1;
		}
		else if (menuPosition != null){
			return -1;
		}
		else 
			return 0;
	}
	
	
}
