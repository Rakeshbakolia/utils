package com.techi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransformUtil {

    private static final Logger log = LoggerFactory.getLogger(TransformUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param arrayOfStrings - <strong>Array</strong> of {@link String}
     * @return {@link List} of {@link Integer}
     */
    public static List<Integer> stringToNumberList(String[] arrayOfStrings) {
        List<Integer> results = null;
        if (arrayOfStrings != null) {
            results = new ArrayList<Integer>();
            for (int i = 0; i < arrayOfStrings.length; i++) {
                try {
                    results.add(Integer.parseInt(arrayOfStrings[i]));
                } catch (NumberFormatException nfe) {
                    results.add(null);
                }
            }
        }
        return results;
    }

    /**
     * Returns a {@link List} of {@link Integer} from a {@link String} of numbers separated by
     * <strong>comma</strong>, after removing specific characters like [], before splitting by
     * <strong>comma</strong>
     *
     * @param commaSeparatedNumbersString - {@link String}
     * @return {@link List} of {@link Integer}
     */
    public static List<Integer> toArray(String commaSeparatedNumbersString) {
        List<Integer> results = null;
        commaSeparatedNumbersString = removeExtraCharactersForNumber(commaSeparatedNumbersString,
                false);
        if (commaSeparatedNumbersString != null) {
            results = stringToNumberList(commaSeparatedNumbersString.split(","));
        }
        return results;
    }

    /**
     * Returns a {@link List} of {@link Integer} without Null from a {@link String} of numbers
     * separated by <strong>comma</strong>, after removing specific characters like [], before
     * splitting by <strong>comma</strong>
     *
     * @param commaSeparatedNumbersString
     * @return
     */
    public static List<Integer> toArrayWithoutNull(String commaSeparatedNumbersString) {
        List<Integer> results = toArray(commaSeparatedNumbersString);
        if (results != null) {
            results.removeIf(Objects::isNull);
        }
        return results;
    }

    /**
     * Removes specific characters like [] including commas
     *
     * @param fullStringNumber - {@link String}
     * @return {@link String} - reducedString
     */
    public static String removeExtraCharactersForNumber(String fullStringNumber) {
        return removeExtraCharactersForNumber(fullStringNumber, true);
    }

    /**
     * Returns after removing specific characters like []
     *
     * @param fullStringNumber - {@link String}
     * @param removeCommas     - {@link boolean} - <strong>true</strong> if commas also are to be removed,
     *                         else <strong>false</strong>
     * @return {@link String} - reducedString
     */
    public static String removeExtraCharactersForNumber(String fullStringNumber,
                                                        boolean removeCommas) {
        String reducedString = null;
        if (fullStringNumber != null) {
            reducedString = fullStringNumber.replaceAll("[\\[\\]\\s]", "");
            if (removeCommas) {
                reducedString = reducedString.replaceAll(",", "");
            }
        }
        return reducedString;
    }

    /**
     * Returns an <strong>Array</strong> of {@link String Strings} from a {@link Collection} of
     * {@link String Strings}
     *
     * @param collectionOfStrings - {@link Collection} of {@link String Strings}
     * @return <strong>Array</strong> of {@link String Strings}
     */
    public static String[] collectionOfStringsToArrayOfStrings(
            Collection<String> collectionOfStrings) {
        String[] arrayOfStrings = null;
        if (collectionOfStrings != null) {
            arrayOfStrings = collectionOfStrings.stream().toArray(String[]::new);
        }
        return arrayOfStrings;
    }

    /**
     * @param listOfStrings - {@link List} of {@link String}
     * @return {@link List} of {@link Integer}
     */
    public static List<Integer> stringToNumberList(List<String> listOfStrings) {
        List<Integer> results = null;
        if (listOfStrings != null) {
            results = stringToNumberList(collectionOfStringsToArrayOfStrings(listOfStrings));
        }
        return results;
    }

    /**
     * @param setOfStrings - {@link Set} of {@link String}
     * @return {@link List} of {@link Integer}
     */
    public static List<Integer> stringToNumberList(Set<String> setOfStrings) {
        List<Integer> results = null;
        if (setOfStrings != null) {
            results = stringToNumberList(collectionOfStringsToArrayOfStrings(setOfStrings));
        }
        return results;
    }

    /**
     * @param list                    {@link List} of {@link T}
     * @param batchSize               - {@link int}
     * @param fillValue               - {@link T}
     * @param ascendingfillBatchSizes - {@link int[]}
     * @return {@link List}<{@link List}<{@link T}>> - {@link List} of {@link List} of {@link T}, in
     * which every batch will have <strong>batchSize</strong> elements, the last one might
     * have less than <strong>batchSize</strong> or if {@link T fillValue} is not NULL, it
     * fills with it till the nearest limit in <strong>ascendingfillBatchSizes</strong> if
     * it's NOT NULL or <strong>batchSize</strong> otherwise
     */
    public static <T> List<List<T>> inBatches(List<T> list, int batchSize, T fillValue,
                                              int[] ascendingfillBatchSizes) {
        if (list == null) {
            return null;
        }
        if (batchSize < 1) {
            batchSize = 100;
        }
        List<List<T>> batches = new ArrayList<List<T>>();
        int start = 0;
        while (start < list.size()) {
            int end = Math.min(start + batchSize, list.size());
            batches.add(list.subList(start, end));
            start = end;
        }
        if (fillValue != null && batches.size() > 0) {
            List<T> lastBatch = batches.get(batches.size() - 1);
            if (ascendingfillBatchSizes == null || ascendingfillBatchSizes.length == 0) {
                ascendingfillBatchSizes = new int[]{batchSize};
            }
            int lastBatchSize = lastBatch.size();
            for (int j = 0; j < ascendingfillBatchSizes.length; ++j) {
                int fillBatchSize = ascendingfillBatchSizes[j];
                if (lastBatchSize == fillBatchSize) {
                    break;
                } else if (lastBatchSize < fillBatchSize) {
                    List<T> fillList = new ArrayList<T>();
                    int remainingSize = fillBatchSize - lastBatchSize;
                    for (int i = 0; i < remainingSize; ++i) {
                        fillList.add(fillValue);
                    }
                    List<T> filledLastBatch = new ArrayList<T>();
                    filledLastBatch.addAll(lastBatch);
                    filledLastBatch.addAll(fillList);
                    batches.set(batches.size() - 1, filledLastBatch);
                    break;
                }
            }
        }
        return batches;
    }

    public static <T> List<List<T>> inBatches(List<T> list, int batchSize, T fillValue) {
        return inBatches(list, batchSize, fillValue, null);
    }

    public static <T> List<List<T>> inBatches(List<T> list, int batchSize) {
        return inBatches(list, batchSize, null);
    }

    /**
     * @param list {@link List} of {@link T}
     * @return {@link List}<{@link List}<{@link T}>> - {@link List} of {@link List} of {@link T}, in
     * which every batch will have <strong>100</strong> elements, the last one might have less
     * than <strong>100</strong> elements
     */
    public static <T> List<List<T>> inBatches(List<T> list) {
        return inBatches(list, 100);
    }

    public static <T> List<List<T>> inBatches(List<T> list, T fillValue) {
        return inBatches(list, 100, fillValue);
    }

    /**
     * Returns a JSON {@link String} from the <strong>obj</strong> provided using {@link ObjectMapper}
     *
     * @param obj - {@link Object}
     * @return {@link String} - JSON string
     */
    public static String toJson(Object obj) {
        try {
            if (obj != null) {
                return objectMapper.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            log.error("Error in toJson(), obj: " + obj + " ; Exception: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns the parsed {@link Object} from the {@link String jsonString} provided using
     * {@link ObjectMapper} - will need a type cast
     *
     * @param jsonString - {@link String}
     * @param valueType  - {@link Class}
     * @return {@link Object}
     */
    public static <T> T fromJson(String jsonString, Class<T> valueType) {
        try {
            if (jsonString != null) {
                return objectMapper.readValue(jsonString, valueType);
            }
        } catch (Exception e) {
            log.error(
                    "Error in fromJson(), jsonString: " + jsonString + " ; Exception: " + e.getMessage(), e);
        }
        return null;
    }

    public static <T> T fromObject(Object fromValue, Class<T> valueType) {
        try {
            if (fromValue != null) {
                return objectMapper.convertValue(fromValue, valueType);
            }
        } catch (Exception e) {
            log.error(
                    "Error in fromJson(), fromValue: " + fromValue + " ; Exception: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param original - <{@link T} extends {@link Serializable}>
     * @return {@link T} - a copy of the original with new reference address
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(T original) throws IOException {
        if (original == null) {
            return null;
        }
        T copy = null;
        FastByteArrayOutputStream fbos = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            fbos = new FastByteArrayOutputStream();
            out = new ObjectOutputStream(fbos);
            out.writeObject(original);
            out.flush();
            out.close();
            in = new ObjectInputStream(fbos.getInputStream());
            Object obj = in.readObject();
            copy = (T) obj;
            in.close();
            fbos.close();
        } catch (Exception e) {
            log.error("Error in deepCopy for {}, Exception: {}", original.getClass().getName(),
                    e.getMessage(), e);
        } finally {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (fbos != null)
                fbos.close();
        }
        return copy;
    }

    /**
     * return mapping of given list according to given function
     *
     * @param list1 - {@link List} of {@link T}
     * @param fun   - {@link Function} of {@link T} and {@link U}
     * @return {@link List} of {@link U}
     */
    public static <T, U> List<U> map(List<T> list1, Function<T, U> fun) {
        if (CollectionUtils.isEmpty(list1)) {
            return null;
        }
        List<U> list2 = list1.stream().map(fun).collect(Collectors.toList());
        return list2;
    }

    /**
     * Zips two array lists into one using passed bifunction
     *
     * @param list1 - {@link List}
     * @param list2 - {@link List}
     * @param fun   - {@link BiFunction}
     * @return {@link List}
     */
    public static <T, U, V> List<V> zip(List<T> list1, List<U> list2, BiFunction<T, U, V> fun) {
        if (list1.size() != list2.size()) {
            return null;
        }
        List<V> list3 = new ArrayList<V>();
        for (int index = 0; index < list1.size(); index++) {
            T elem1 = list1.get(index);
            U elem2 = list2.get(index);
            V elem3 = fun.apply(elem1, elem2);
            list3.add(elem3);
        }
        return list3;
    }

    /**
     * Groups a list into map
     *
     * @param list     - {@link List}<{@link U}>
     * @param fun to get key - {@link Function} with input type of {@link T} and output type of
     *                 {@link U}
     * @return {@link Map}<{@link T}, {@link List}<{@link U}>>
     */
    public static <T, U> Map<T, List<U>> group(List<U> list, Function<U, T> fun) {
        Map<T, List<U>> map = new HashMap<T, List<U>>();
        list.forEach(item -> {
            T key = fun.apply(item);
            List<U> values = map.get(key);
            if (values == null) {
                values = new ArrayList<U>();
            }
            values.add(item);
            map.put(key, values);
        });
        return map;
    }

    public static <T1, T2, U> Map<T1, List<T2>> group(Collection<U> list, Function<U, T1> keyFun, Function<U, T2> valueFun) {
        Map<T1, List<T2>> map = new HashMap<T1, List<T2>>();
        list.forEach(item -> {
            T1 key = keyFun.apply(item);
            List<T2> values = map.get(key);
            if (CollectionUtils.isEmpty(values)) {
                values = new ArrayList<T2>();
            }
            T2 value = valueFun.apply(item);
            values.add(value);
            map.put(key, values);
        });
        return map;
    }

    /**
     * Indexes a list into map
     *
     * @param list     - {@link List}<{@link U}>
     * @param fun to get key - {@link Function} with input type of {@link T} and output type of
     *                 {@link U}
     * @return {@link Map}<{@link T}, {@link U}>
     */
    public static <T, U> Map<T, U> index(Collection<U> list, Function<U, T> fun) {
        Map<T, U> map = new HashMap<T, U>();
        list.forEach(item -> {
            T key = fun.apply(item);
            map.put(key, item);
        });
        return map;
    }

    /**
     * Indexes a list into map
     *
     * @param list     - {@link List}<{@link U}>
     * @param keyFun to get key - {@link Function} with input type of {@link T} and output type of
     *                 {@link U}
     * @param valFun to get value - {@link Function} with input type of {@link T} and output type of
     *      *                 {@link V}
     * @return {@link Map}<{@link U}, {@link V}>
     */
    public static<T, U, V> Map<U, V> index(List<T> list, Function<T, U> keyFun, Function<T, V> valFun) {
        Map<U, V> map = new HashMap<>();
        list.forEach(item -> {
            U key = keyFun.apply(item);
            if(key == null) {
                return;
            }
            V value = valFun.apply(item);
            map.put(key, value);
        });
        return map;
    }




    public static boolean isTimeBetweenInclusive(Long timeToCheck, Long fromTime, Long toTime) {
        if (timeToCheck >= fromTime && timeToCheck <= toTime) {
            return true;
        }
        return false;
    }

    public static <T> void sortComparator(List<T> list, Comparator<T> comparator, String order) {
        if ("desc".equalsIgnoreCase(order)) {
            list.sort(comparator.reversed());
        } else {
            list.sort(comparator);
        }
    }

    public static LocalDate convertEpochSecToLocalDate(Long epochSec) {
        if (epochSec != null) {
            return Instant.ofEpochSecond(epochSec).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    public static String convertEpochSecToLocalDateTime(Long time) {
        if (time != null) {
            long val = time * 1000;
            Date date = new Date(val);
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMM, yyyy, hh:mm a");
            return df2.format(date);
        } else {
            return "";
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}

