package db.mongodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBConnection implements DBConnection {
	
	private MongoClient mongoClient;
	private MongoDatabase mongoDb;
	
	/*
	 * connect to local mongodb
	 */
	public MongoDBConnection() {
		mongoClient = new MongoClient();
		mongoDb = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}

	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (mongoDb == null) {
			return;
		}
		
		Document query1 = new Document("user_id", userId);
		Document query2 = new Document("$push", new Document("favorite", new Document("$each", itemIds)));
		mongoDb.getCollection("users").updateOne(query1, query2);	
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (mongoDb == null) {
			return;
		}
		
		Document query1 = new Document("user_id", userId);
		Document query2 = new Document("$pullAll", new Document("favorite", itemIds));
		mongoDb.getCollection("users").updateOne(query1, query2);
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (mongoDb == null) {
			return null;
		}
		Set<String> favoriteItemIds = new HashSet<>();
		FindIterable<Document> iterable = mongoDb.getCollection("users").find(eq("user_id", userId));
		if (iterable.first().containsKey("favorite")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("favorite");
			favoriteItemIds.addAll(list);
		}
		return favoriteItemIds;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (mongoDb == null) {
			return null;
		}
		
		Set<String> itemIds = getFavoriteItemIds(userId);
		Set<Item> favoriteItems = new HashSet<>();
		for (String itemId : itemIds) {
			FindIterable<Document> iterable = mongoDb.getCollection("items").find(eq("item_id", itemId));
			if (iterable.first() != null) {
				Document doc = iterable.first();
				
				ItemBuilder builder = new ItemBuilder();
				builder.setItemId(doc.getString("item_id"));
				builder.setName(doc.getString("name"));
				builder.setRating(doc.getDouble("rating"));
				builder.setAddress(doc.getString("address"));
				builder.setImageUrl(doc.getString("image_url"));
				builder.setUrl(doc.getString("url"));
				builder.setDistance(doc.getDouble("distance"));
				builder.setCategories(getCategories(itemId));

				favoriteItems.add(builder.build());
			}
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (mongoDb == null) {
			return null;
		}
		
		Set<String> categories = new HashSet<>();
		FindIterable<Document> iterable = mongoDb.getCollection("items").find(eq("item_id", itemId));
		
		if (iterable.first() != null) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("categories");
			if (list != null) {
				categories.addAll(list);
			}
		}
		
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI ticketMasterAPI = new TicketMasterAPI();
		List<Item> items = ticketMasterAPI.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}
		
		return items;
	}

	@Override
	public void saveItem(Item item) {
		if (mongoDb == null) {
			return;
		}
		// Document filter = eq("item_id", item.getItemId());
		FindIterable<Document> iterable = mongoDb.getCollection("items").find(eq("item_id", item.getItemId()));
		if (iterable.first() == null) {
			mongoDb.getCollection("items").insertOne(new Document().append("item_id", item.getItemId()).append("name", item.getName())
							.append("rating", item.getRating()).append("address", item.getAddress())
							.append("image_url", item.getImageUrl()).append("url", item.getUrl())
							.append("categories", item.getCategories()).append("distance", item.getDistance()));
		}
	}

	@Override
	public String getFullname(String userId) {
		if (mongoDb == null) {
			return null;
		}
		
		FindIterable<Document> iterable = mongoDb.getCollection("users").find(eq("user_id", userId));
		Document document = iterable.first();
		String firstName = document.getString("first_name");
		String lastName = document.getString("last_name");
		return firstName + " " + lastName;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if (mongoDb == null) {
			return false;
		}
		
		FindIterable<Document> iterable = mongoDb.getCollection("users").find(eq("user_id", userId));
		return (iterable.first() != null) && (iterable.first().getString("password").equals(password));
	}

}
