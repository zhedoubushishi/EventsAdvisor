package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;


public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendItems = new ArrayList<>();
		DBConnection conn = DBConnectionFactory.getDBConnection();
		
		// fetch all favorite item ids
		Set<String> favoriteItems = conn.getFavoriteItemIds(userId);
		
		// fetch all categories of favorite items & sort
		Map<String, Integer> allCategories = new HashMap<>();
		for (String item : favoriteItems) {
			Set<String> categories = conn.getCategories(item);
			for (String category : categories) {
				if (!allCategories.containsKey(category)) {
					allCategories.put(category, 1);
				} else {
					allCategories.put(category, allCategories.get(category) + 1);
				}
			}
		}
		
		// sort categories
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
				return Integer.compare(e1.getValue(), e2.getValue());
			}
		});
		
		// fetch all items by categories except for those have already been visited or favorited
		Set<Item> visitedItems = new HashSet<>();
		for (Entry<String, Integer> category : categoryList) {
			List<Item> items = conn.searchItems(lat, lon, category.getKey());
			List<Item> filteredItems = new ArrayList<>();
			
			for (Item item : items) {
				if (!favoriteItems.contains(item.getItemId()) && !visitedItems.contains(item)) {
					filteredItems.add(item);
				}
			}
			
			// sort by distance
			Collections.sort(filteredItems, new Comparator<Item>() {
				public int compare(Item i1, Item i2) {
					return Double.compare(i1.getDistance(), i2.getDistance());
				}
			});
			
			visitedItems.addAll(items);
			recommendItems.addAll(filteredItems);
		}
		
		return recommendItems;
	}
}
