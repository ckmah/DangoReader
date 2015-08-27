package daogenerator.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {
    public static void main(String[] args) throws Exception {
        String pkg = "com.william.mangoreader";
        String src = "app/src/main/java";
        Schema schema = new Schema(1000, pkg + ".daogen");

        addUserLibraryManga(schema);
        new DaoGenerator().generateAll(schema, src);
    }

    private static void addUserLibraryManga(Schema schema) {
        Entity userLibraryManga = schema.addEntity("UserLibraryManga");
        userLibraryManga.addIdProperty();
        userLibraryManga.addStringProperty("tabs"); //for which tab/tabs
        userLibraryManga.addStringProperty("title").notNull();
        userLibraryManga.addStringProperty("imageURL");
        userLibraryManga.addStringProperty("status");
        userLibraryManga.addLongProperty("lastChapterDate");
        userLibraryManga.addIntProperty("hits");
    }
}

