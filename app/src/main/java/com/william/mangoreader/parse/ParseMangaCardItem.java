package com.william.mangoreader.parse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.william.mangoreader.model.MangaCardItem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ParseMangaCardItem {
    public static final String MANGA_EDEN_BASE_URL = "https://www.mangaeden.com/api/list/0/";
    public static final ObjectMapper mapper = new ObjectMapper(); // create once, reuse
    public static final JsonFactory factory = new JsonFactory();


    static public ArrayList<MangaCardItem> parseMangaEden(int page, int quantity) throws IOException {

        String params = "?p=" + page + "&l=" + quantity;
        String query = MANGA_EDEN_BASE_URL + params;
        URL requestUrl = new URL(query);
        JsonParser parser = factory.createParser(requestUrl);
        JsonNode root = mapper.readTree(requestUrl);

        ArrayList<MangaCardItem> mangaCardModels = new ArrayList<>();

        ArrayNode mangas = (ArrayNode) root.get("manga");
        for (int i = 0; i < mangas.size(); i++){
            JsonNode m = mangas.get(i);
            MangaCardItem item = mapper.treeToValue(m, MangaCardItem.class);
            mangaCardModels.add(item);
        }

        return mangaCardModels;
    }
//    public static void main(String[] args){
//        try {
//            parseMangaEden(0,25);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
