package main;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import de.svenkubiak.embeddedmongodb.EmbeddedMongo;
import fr.javatic.mongo.jacksonCodec.JacksonCodecProvider;
import fr.javatic.mongo.jacksonCodec.ObjectMapperFactory;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.converters.Converters;

public class FakeMongoClient {

    public Morphia getMorphia() {
        return morphia;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    private Morphia morphia;
    private Datastore datastore;

    public static final String HOST_NAME = "localhost";
    public static final String DATABASE_NAME = "faked_database";
    public static final int DATABASE_PORT = 28018;


    public String getMongoUri(){
        return String.format("mongodb://%s:%s",HOST_NAME,  DATABASE_PORT);
    }

    public String getMongoHostAddress(){
        return String.format("%s:%s", HOST_NAME, DATABASE_PORT);
    }


    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    private MongoDatabase mongoDatabase;

    public MongoClient getMongo() {
        return mongo;
    }

    private MongoClient mongo;

    public FakeMongoClient(){

        EmbeddedMongo.DB.port(DATABASE_PORT).start();

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(new JacksonCodecProvider(ObjectMapperFactory.createObjectMapper())));


        MongoClientOptions settings = MongoClientOptions.builder()
                .codecRegistry(codecRegistry).build();

        mongo = new MongoClient(getMongoHostAddress() ,settings);

//        mongo = EmbeddedMongo.DB.getMongoClient();

        morphia = new Morphia();

        Converters converters = morphia.getMapper().getConverters();

//        converters.addConverter(ZonedDateTimeConverter.class);
//        converters.addConverter(InstantConverter.class);
//        converters.addConverter(IntEnumTypeConverter.class);
//        converters.addConverter(OptionalConverter.class);


        mongoDatabase = mongo.getDatabase(DATABASE_NAME);

        datastore = morphia.createDatastore(mongo, DATABASE_NAME);


    }

    public void dropAllCollections() {

        mongoDatabase.drop();

    }

    public void clearAllCollections() {

        mongoDatabase.listCollectionNames().spliterator().forEachRemaining(s -> {
            mongoDatabase.getCollection(s).deleteMany(new BsonDocument());
        });

    }

    public void stopMongo(){

        EmbeddedMongo.DB.stop();

    }


}