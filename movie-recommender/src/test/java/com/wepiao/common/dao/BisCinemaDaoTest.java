package com.wepiao.common.dao;

import com.wepiao.common.entity.BisCinema;
import com.wepiao.common.entity.BisServerIdCount;
import com.wepiao.common.utils.JsonMapper;
import com.wepiao.goods.service.BaseTest;
import java.util.Date;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:config-property.xml","classpath*:/config-mongo.xml"})
public class BisCinemaDaoTest {

    @Autowired
    private BisCinemaDao bisCinemaDao;


    @Ignore
    @Test
    public void testFindByBisId(){
        List<BisCinema> list = bisCinemaDao.findByBisid("545893212bc3abad3154fcb0");
        System.out.println();
        assertTrue(list.size() > 0);
    }

    @Test
    public void testFindOne(){
        BisCinema bc = bisCinemaDao.findOne("20002712", "54604a3faf2b983fbbbd6a5i");
        System.out.println(null != bc);
    }
}
