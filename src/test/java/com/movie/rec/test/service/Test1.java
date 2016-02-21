package com.movie.rec.test.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Stopwatch;
import com.wx.movie.rec.common.enums.Constant;
import com.wx.movie.rec.common.enums.RedisKey;
import com.wx.movie.rec.common.util.JsonMapperUtil;
import com.wx.movie.rec.redis.RedisUtils;
import com.wx.movie.rec.similarity.pojo.UserActionProportion;

/**
 * Date: 2016年2月16日 下午8:22:42 <br/>
 * 
 * @author dynamo
 */
public class Test1 extends BaseTest implements InitializingBean {
  @Autowired
  private RedisUtils redisUtils;

  @Value("${user.action.json}")
  private String userActionJson;

  private List<UserActionProportion> userActionProportion;
  private TypeReference<List<UserActionProportion>> tr;
  private Logger logger = LoggerFactory.getLogger(Test1.class);

  private String[] methodArray = {Constant.BSE_USE, Constant.BSE_MOVIE};

  @Test
  public void test() {
    Stopwatch timer = Stopwatch.createStarted();
    String rootPath = "/home/dynamo/";
    for (String method : methodArray) {
     String dir = rootPath+method;
      for (UserActionProportion userActionPro : userActionProportion) {
        String rKey =
            String.format(RedisKey.USER_ACTION_SIMILARITY, userActionPro.getAction(), method);
        Map<String, Map> map = redisUtils.hgetAll(rKey, Map.class);
        String result =
            String.format("Get Similarity From Cache ,UserAction is %s,Base On %s, Value is %s",
                userActionPro.getAction(), method, JsonMapperUtil.getInstance().toJson(map));
        String directory = dir +"/"+userActionPro.getAction();
        writeFile(directory,result);
      }
    }
    System.out.println("taket total time is {}"+timer.stop());
  }

  @PostConstruct
  public void init() {
    System.out.println(redisUtils.exists(String.format(RedisKey.COUNT_SIMILARITY, Constant.BSE_USE)));
    if(redisUtils.exists(String.format(RedisKey.COUNT_SIMILARITY, Constant.BSE_USE))){
    int count = redisUtils.getInt(String.format(RedisKey.COUNT_SIMILARITY, Constant.BSE_USE));
    System.out.println("count"+count);
    }
    
    tr = new TypeReference<List<UserActionProportion>>() {};
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    FileInputStream fis = null;
    try {
      // 得到classpath目录路径
      String path = this.getClass().getResource("/").toURI().getPath();
      path = path + userActionJson;
      logger.info("配置用户操作文件路径：" + path);
      File jsonFile = new File(path);
      if (!jsonFile.exists()) {
        jsonFile = new File(userActionJson);
      }
      fis = new FileInputStream(jsonFile);
      Long filelength = jsonFile.length();
      byte[] fileContent = new byte[filelength.intValue()];
      fis.read(fileContent);
      String actionJson = new String(fileContent, "UTF-8");
      userActionProportion = JsonMapperUtil.getInstance().fromJson(actionJson, tr);
      logger.info("获取用户行为操作：{}", actionJson);
    } catch (Exception e) {
      logger.error("parse user_action.json fail", e);
    } finally {
      if (fis != null) fis.close();
    }
  }
  
  private void writeFile(String filePath,String content){
    try {
      File file = new File(filePath);
      if(!file.exists() &&! file.isDirectory()){
        file.mkdirs();
      }
      filePath = filePath+"/data.txt";
    FileWriter fw = new FileWriter(filePath);
 
      fw.write(content);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
