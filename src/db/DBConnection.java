package db;

import java.util.List;
import java.util.Set;

import entity.Item;

public interface DBConnection {
	/**
	 * close connection
	 */
	public void close();
	
	/**
	 * Insert favorite items for a user
	 * @param userId
	 * @param itemIds
	 */
	public void setFavoriteItems(String userId, List<String> itemIds);
	
	/**
	 * Delete favorite items for a user 
	 * @param userId
	 * @param itemIds
	 */
	public void unsetFavoriteItems(String userId, List<String> itemIds);
	
	/**
	 * Get favorite item IDs for a user
	 * @param userId
	 * @return itemIds
	 */
	public Set<String> getFavoriteItemIds(String userId);
	
	/**
	 * Get favorite items for a user
	 * @param userId
	 * @return items
	 */
	public Set<Item> getFavoriteItems(String userId);
	
	/**
	 * Get categories based on item id
	 * @param itemId
	 * @return set of categories
	 */
	public Set<String> getCategories(String itemId);
	
	/**
	 * Search items near a geolocation and a term
	 * @param lat
	 * @param lon
	 * @param term (Nullable)
	 * @return list of items
	 */
	public List<Item> searchItems(double lat, double lon, String term);
	
	/**
	 * Save item into db
	 * @param item
	 */
	public void saveItem(Item item);
	
	/**
	 * Get full name of a user
	 * @param userId
	 * @return full name
	 */
	public String getFullname(String userId);
	
	/**
	 * Check whether credential is correct
	 * @param userId
	 * @param password
	 * @return boolean
	 */
	public boolean verifyLogin(String userId, String password);
}
