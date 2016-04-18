package de.beukmann.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;
import javax.xml.ws.WebServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.beukmann.model.Order;
import de.beukmann.persistence.OrderRepository;
import de.beukmann.util.DateUtils;

@RestController
@RequestMapping("orders")
public class OrderController {


	@Autowired
	private OrderRepository orderRepository;

	/**
	 * Loads the orders of a given week and year for a particular user
	 * @param year year
	 * @param weekOfYear week of year
	 * @param auth Spring Security Authentication token
	 * @return Returns always a total of 7 orders. If none are found in the database, empty orders are created
	 */
	@RequestMapping(produces=MediaType.APPLICATION_JSON_VALUE, method=RequestMethod.GET, value="/{year}/{weekOfYear}")
	public List<Order> getOrders(@PathVariable int year, @PathVariable Integer weekOfYear, Authentication auth){
		final String username = auth.getName().toUpperCase();
		LocalDate[] weekdays = DateUtils.getWeek(weekOfYear, year);
		List<Order> orders = orderRepository.findByUsernameAndOrderDateBetweenOrderByOrderDate(username, weekdays[0], weekdays[6]);
		if (orders.size() == 0){
			orders = Order.getOrdersFromDates(weekdays);
		} else if (orders.size() != 7){
			//Fill up
			for (final LocalDate weekday : weekdays){
				if (orders.stream().noneMatch(x->x.getOrderDate().isEqual(weekday))){
					orders.add(Order.getOrderFromDate(weekday));
				}
			}
			Collections.sort(orders, (x,y) -> x.getOrderDate().compareTo(y.getOrderDate()));
		}
		return orders;
	}
	/**
	 * Saves the orders of a given week and year for a particular user
	 * @param year year
	 * @param weekOfYear week of year
	 * @param data Must contain 7 orders, which are all part of the given week
	 * @param auth Spring Security Authentication token
	 * @return the updated orders as JSON
	 */
	@RequestMapping(method=RequestMethod.POST, value="/{year}/{weekOfYear}", consumes=MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public List<Order> saveOrders(@PathVariable int year, @PathVariable int weekOfYear, @RequestBody List<Order> data, Authentication auth){
		final String username = auth.getName().toUpperCase();
		LocalDate[] weekdays = DateUtils.getWeek(weekOfYear, year);
		if (data.size() != 7){
			throw new WebServiceException("Invalid number of dates");
		}
		if (!Arrays.stream(weekdays).allMatch(weekday -> data.stream().anyMatch(x-> x.getOrderDate().isEqual(weekday)))){
			throw new WebServiceException("Invalid order data");
		}
		List<Order> orders = orderRepository.findByUsernameAndOrderDateBetweenOrderByOrderDate(username, weekdays[0], weekdays[6]);
		Order.merge(orders, data);
		for (Order order : orders){
			order.setUsername(username);
			orderRepository.save(order);
		}
		return data;
	}
}

