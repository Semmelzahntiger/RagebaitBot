package com.tree3.util;

import com.google.gson.Gson;
import com.tree3.json.LaTeXInterpreted;

public class GSONSerializer {
    public static final Gson gson = new Gson();

    public static LaTeXInterpreted getInterpreted(String json) {
        return gson.fromJson(json, LaTeXInterpreted.class);
    }

}
