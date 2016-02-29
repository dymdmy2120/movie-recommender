
package com.wx.movie.rec.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

/**
 * Date:     2016年2月27日 下午2:03:51
 * @author   dynamo
 * @version
 * @see
 */
/**
 * @author dynamo
 * @version
 */
public class SortMap {
  /**
   * 对Map 根据Value进行从大到小排序，并且排序完后取出前面topCount
   *
   * @param map
   * @param topCount
   * @return
   */
  public static Map<String, Double> sortedMapByValue(Map<String, Double> map, int topCount) {
    List<Map.Entry<String, Double>> lists =
        new ArrayList<Map.Entry<String, Double>>(map.entrySet());
    Collections.sort(lists, new Comparator<Map.Entry<String, Double>>() {
      @Override
      public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
        return o1.getValue().compareTo(o2.getValue());
      }
    });
    Map<String, Double> sortedMap = Maps.newLinkedHashMap();
    int maxSize = Math.min(topCount, lists.size());
    for (int i = 0; i < maxSize; i++) {
      Map.Entry<String, Double> entry = lists.get(i);
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }
}
