package db.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

/*
 * create new new collections for mongoDb
 */
public class MongoDBTableCreation {
	public static void main(String[] args)  {
		
		// connect to mongodb
		MongoClient mongoClient = new MongoClient();
		MongoDatabase mongodb = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
		
		// remove old collections
		mongodb.getCollection("items").drop();
		mongodb.getCollection("users").drop();
		
		// create index & add new collections
		// new Document("user_id", 1) <=> {"user_id":1}   
		// 1 stands for ascend order; -1 stands for descend order
		    
		IndexOptions indexOptions = new IndexOptions().unique(true);
		mongodb.getCollection("users").createIndex(new Document("user_id", 1), indexOptions);
		mongodb.getCollection("items").createIndex(new Document("item_id", 1), indexOptions);
		
		/* insert test data
		  {
	      	User_id: 1111,
	      	Password: 3229c1097c00d497a0fd282d586be050,
	      	First_name: Wayne,
	      	Last_name: Ding,
	      }
	    */
		mongodb.getCollection("users").insertOne(new Document().append("first_name", "Wayne").append("last_name", "Ding")
				.append("user_id", "1111").append("password", "3229c1097c00d497a0fd282d586be050"));

		mongoClient.close();
		
		System.out.println("Import is done successfully.");
	}
}
