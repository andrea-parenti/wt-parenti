package it.polimi.wt.parenti.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.wt.parenti.utils.enumerations.ExamResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utility {
    private static Gson jsonParser = null;

    private Utility() {}

    public static final Gson getJsonParser() {
        if (jsonParser == null) {
            jsonParser = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
        }
        return jsonParser;
    }

    public static final List<String> initializeOptions() {
        var results = Arrays.stream(ExamResult.values())
                .filter(r -> r != ExamResult.PASSED)
                .map(ExamResult::displayName);
        var grades = IntStream.rangeClosed(18, 30).boxed()
                .map(i -> i.toString());
        var laude = Stream.of("30L");
        var options = Stream.of(results, grades, laude)
                .flatMap(Function.identity())
                .collect(Collectors.toList());
        return options;
    }

    public static final void writeHttpResponse(HttpServletResponse response, int sc, String mime, String content) throws IOException {
        response.setStatus(sc);
        response.setContentType(mime);
        response.getWriter().println(content);
    }
}
