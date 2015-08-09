package daogenerator.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {
    public static void main(String[] args) throws Exception {
        String pkg = "com.william.mangoreader";
        String src = "app/src/main/java";
        Schema schema = new Schema(1000, pkg + ".daogen");

        addMyLibraryManga(schema);
        new DaoGenerator().generateAll(schema, src);
    }

    private static void addMyLibraryManga(Schema schema) {
        Entity note = schema.addEntity("MyLibraryMangaCard");
        note.addIdProperty();
        note.addStringProperty("title").notNull();
    }
}
