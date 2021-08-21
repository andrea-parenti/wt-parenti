package it.polimi.wt.parenti.utils;

import it.polimi.wt.parenti.utils.enumerations.ExamResult;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Constants {
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
}
