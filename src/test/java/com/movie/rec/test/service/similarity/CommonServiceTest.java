package com.movie.rec.test.service.similarity;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.movie.rec.test.service.BaseTest;
import com.wx.movie.rec.similarity.common.ProSilityCommonService;

/**
 * Date:     2016年2月23日 下午7:58:07 <br/>
 * @author   dynamo
 */
public class CommonServiceTest extends  BaseTest{
  @Autowired
private ProSilityCommonService commService;
  
  @Test
  public void testHandlerUserActionData(){
    Map<String,Map<String,Double> > map = Maps.newHashMap();
    Map<String,Set<String>> actionMap = Maps.newHashMap();
    Set<String> set1 = Sets.newHashSet();
    set1.add("M1");
    set1.add("M2");
    Set<String> set2 = Sets.newHashSet();
    set2.add("M2");
    set2.add("M3");
    Set<String> set3 = Sets.newHashSet();
    set3.add("M2");
    set3.add("M3");
    set3.add("M1");
    Set<String> set4 = Sets.newHashSet();
    set4.add("M3");
    set4.add("M1");
    actionMap.put("U1", set1);
    actionMap.put("U2", set2);
    actionMap.put("U3", set3);
    actionMap.put("U4", set4);
   // commService.handleUserActionData(map, "", actionMap, "");
    System.out.println(map);
  }
}
