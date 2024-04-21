
# Project Title

This Repository is a collection of diffrent type of conversions.  
     1. DateTime conversions  
     2. Object conversions

## DateTime conversion methods
- String getTimeString(Long epocInSec)
- Long getDayEpochEndTimeFromEpochTime(@NonNull Long epoc)
- String getDateAndTimeString(@NonNull Long epoc)
- long getDiffInDays(@NonNull Long fromTime, @NonNull Long toTime)
- String convertToTime(@NonNull Long timeDiff)
- List<Long> getDayStartEpoch(@NonNull Long firstDayStart, @NonNull Long lastDayEnd)
- LocalDateTime dateToLocalDateTime(@NonNull Date date, String zone)
- Date localDateTimeToDate(@NonNull LocalDateTime startOfDay, String zone)
- Long getEndOfDayEpocInSec(Long startTimeInSec, String zone) 
- Long getStartOfDayEpocInSec(Long endTimeInSec, String zone)
- List<TimeData> getDayRangeEpoch(Long firstDayStart, Long lastDayEnd, String zone)
- Long secondsAtToday12AM(String zone)
- Long secondsAtTomorrow12AM(String zone)
- Long secondsAtYesterday12AM(String zone) 
- List<TimeData> getAnyEpochTimeRangeInEpochDayWiseRange(@NonNull Long startTime, @NonNull Long endTime, @NonNull String zone)
- Long getStartTimeOfMonth(String zone)
- String getMonthForInt(int num)
- LocalDateTime getLocalDateTimeFromEpoch(Long epochTime, String zone)
- Date convertStringToDate(String date, String pattern)
- Date getDateFromEpochInSec(Long epochTime)
- String ymdDateFormat(Date time, String zone)

## Object conversion methods
- List<Integer> stringToNumberList(String[] arrayOfStrings)
- List<Integer> toArray(String commaSeparatedNumbersString)
- List<Integer> toArrayWithoutNull(String commaSeparatedNumbersString)
- String removeExtraCharactersForNumber(String fullStringNumber)
- String removeExtraCharactersForNumber(String fullStringNumber, boolean removeCommas)
- String[] collectionOfStringsToArrayOfStrings(Collection<String> collectionOfStrings)
- List<Integer> stringToNumberList(List<String> listOfStrings)
- List<Integer> stringToNumberList(Set<String> setOfStrings)
- <T> List<List<T>> inBatches(List<T> list, int batchSize, T fillValue, int[] ascendingfillBatchSizes)
- <T> List<List<T>> inBatches(List<T> list, int batchSize, T fillValue)
- <T> List<List<T>> inBatches(List<T> list, int batchSize)
- <T> List<List<T>> inBatches(List<T> list)
- <T> List<List<T>> inBatches(List<T> list, T fillValue)
- String toJson(Object obj)
- <T> T fromJson(String jsonString, Class<T> valueType)
- <T> T fromObject(Object fromValue, Class<T> valueType)
- <T extends Serializable> T deepCopy(T original)
- <T, U> List<U> map(List<T> list1, Function<T, U> fun)
- <T, U, V> List<V> zip(List<T> list1, List<U> list2, BiFunction<T, U, V> fun)
- <T, U> Map<T, List<U>> group(List<U> list, Function<U, T> fun)
- <T1, T2, U> Map<T1, List<T2>> group(Collection<U> list, Function<U, T1> keyFun, Function<U, T2> valueFun)
- <T, U> Map<T, U> index(Collection<U> list, Function<U, T> fun)
- <T, U, V> Map<U, V> index(List<T> list, Function<T, U> keyFun, Function<T, V> valFun)

## Authors

- [github](https://github.com/Rakeshbakolia)
- [linkedin](https://www.linkedin.com/in/rakesh-bakolia-8b9842144/)
