
package com.wx.movie.rec.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wx.movie.rec.similarity.common.FinalSilityCommonService;
import com.wx.movie.rec.similarity.pojo.UserActionProportion;

/**
 * Date:     2016年3月8日 下午4:44:04
 * @author   dynamo
 * @version
 * @see
 */
/**
 * @author dynamo
 * @version
 */
public class LoadConfigFileUtil implements InitializingBean{
  @Value("${user.action.json}")
  private String userActionJson;

  private static List<UserActionProportion> userActionProportions;
  private TypeReference<List<UserActionProportion>> tr;
  private Logger logger = LoggerFactory.getLogger(FinalSilityCommonService.class);

  public static List<UserActionProportion> getActionProportions() {
    return userActionProportions;
  }
  @PostConstruct
  public void init() {
    tr = new TypeReference<List<UserActionProportion>>() {};
  }
  @Override
  public void afterPropertiesSet() throws Exception {
    FileInputStream fis = null;
    try {
      // 得到classpath目录路径
      String path = this.getClass().getResource("/").toURI().getPath();
      path = path + userActionJson;
      logger.info("配置用户操作和对应比例路径：" + path);
      File jsonFile = new File(path);
      if (!jsonFile.exists()) {
        jsonFile = new File(userActionJson);
      }
      fis = new FileInputStream(jsonFile);
      Long filelength = jsonFile.length();
      byte[] fileContent = new byte[filelength.intValue()];
      fis.read(fileContent);
      String actionJson = new String(fileContent, "UTF-8");
      userActionProportions = JsonMapperUtil.getInstance().fromJson(actionJson, tr);
      logger.info("获取用户行为操作：{}", actionJson);
    } catch (Exception e) {
      logger.error("parse user_action.json fail", e);
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
  }
}
