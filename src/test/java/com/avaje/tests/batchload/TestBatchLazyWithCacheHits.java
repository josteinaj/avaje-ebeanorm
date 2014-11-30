package com.avaje.tests.batchload;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.cache.ServerCacheManager;
import com.avaje.tests.model.basic.UUOne;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestBatchLazyWithCacheHits extends BaseTestCase {

  private UUOne insert(String name) {
    UUOne one = new UUOne();
    one.setName("testBLWCH"+name);
    Ebean.save(one);
    return one;
  }

  @Test
  public void testOnCacheHit() {

    ArrayList<UUOne> inserted = new ArrayList<UUOne>();
    String[] names = "A,B,C,D,E,F,G,H,I,J".split(",");
    for (int i = 0; i < names.length; i++) {
      inserted.add(insert(names[i]));
    }
    
    ServerCacheManager serverCacheManager = Ebean.getServerCacheManager();
    serverCacheManager.setCaching(UUOne.class, true);
    
    UUOne b = Ebean.find(UUOne.class)
      .setId(inserted.get(1).getId())
      .setUseCache(true)
      .findUnique();

    Assert.assertNotNull(b);
    
    UUOne b2 = Ebean.find(UUOne.class)
        .where().idEq(inserted.get(1).getId())
        .setUseCache(true)
        .findUnique();
    
    Assert.assertNotNull(b2);
    
    UUOne c = Ebean.find(UUOne.class)
        .where().idEq(inserted.get(2).getId())
        .setUseCache(true)
        .findUnique();
    
    Assert.assertNotNull(c);
    
    UUOne c2 = Ebean.find(UUOne.class)
        .where().idEq(inserted.get(2).getId())
        .setUseCache(true)
        .findUnique();
    
    Assert.assertNotNull(c2);

    List<UUOne> list = Ebean.find(UUOne.class)
        //.setDefaultLazyLoadBatchSize(5)
        .setUseCache(true)
        .select("id")
        .where().startsWith("name", "testBLWCH")
        .order("name")
        .findList();

   int count0 = Ebean.createSqlQuery("select t0.id c0 from uuone t0 where t0.name like ? order by t0.name").setParameter(1,"testBLWCH%").findList().size();
   int count1 = Ebean.createSqlQuery("select t0.id c0 from uuone t0 where t0.name like ? escape'x' order by t0.name").setParameter(1,"testBLWCH%").findList().size();
   int count2 = Ebean.createSqlQuery("select t0.id c0 from uuone t0 where t0.name like ? escape'/'  order by t0.name").setParameter(1,"testBLWCH%").findList().size();
   int count3 = Ebean.createSqlQuery("select t0.id c0 from uuone t0 where t0.name like ? escape''  order by t0.name").setParameter(1,"testBLWCH%").findList().size();
    System.out.println("count0:" + count0 + " count1:" + count1 + " count2:" + count2 + " count3:" + count3 + "  List:" + list);
   for (UUOne uuOne : list) {
     System.out.println(uuOne.getName());
   }
   list.get(0).getName();

  }

}
