package de.beukmann.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import de.beukmann.model.Order;
/**
 * Repository for Order Objects
 * @author Stefan
 *
 */
public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {
	/**
	 * Finds all orders with a given username and daterange 
	 * @param username username
	 * @param orderDateFrom lower date bound
	 * @param orderDateTo upper date bound
	 * @return order objects
	 */
	List<Order> findByUsernameAndOrderDateBetweenOrderByOrderDate(String username, LocalDate orderDateFrom, LocalDate orderDateTo);
	
	
	/**
	 * Deletes all orders of a given user within a daterange
	 * @param username username
	 * @param orderDateFrom lower date bound
	 * @param orderDateTo upper date bound
	 * @return deleted database rows
	 */
	@Modifying
	@Query("delete from Order o where o.username = :username and o.orderDate between :orderDateFrom and :orderDateTo")
	int deleteByUsernameAndOrderDateBetween(@Param("username") String username, @Param("orderDateFrom") LocalDate orderDateFrom, @Param("orderDateTo") LocalDate orderDateTo );
}
