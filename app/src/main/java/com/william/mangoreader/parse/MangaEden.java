package com.william.mangoreader.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.william.mangoreader.model.MangaCardItem;

import java.io.IOException;
import java.util.ArrayList;

public class MangaEden {

    public static final String MANGAEDEN_PREFIX = "https://www.mangaeden.com/api/list/0/?p=";
    public static final String MANGAEDEN_SUFFIX = "&l=25";

    public static final String MANGAEDEN_IMAGE_CDN = "https://cdn.mangaeden.com/mangasimg/98x/"; //TODO figure out

    public static final ObjectMapper mapper = new ObjectMapper(); // create once, reuse

    static public ArrayList<MangaCardItem> parseMangaEdenResponse(String jsonString) {

        ArrayList<MangaCardItem> mangaCardModels = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(jsonString);
            ArrayNode mangas = (ArrayNode) root.get("manga");

            for (int i = 0; i < mangas.size(); i++) {
                JsonNode m = mangas.get(i);
                MangaCardItem item = mapper.treeToValue(m, MangaCardItem.class);
                mangaCardModels.add(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mangaCardModels;
    }
}
