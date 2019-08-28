package com.solr.template;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSolrTemplate {

    @Autowired
    private SolrTemplate solrTemplate;


    @Test
    public void test01() {
        //构建对象
        TbItem item = new TbItem();
        item.setId(1L);
        item.setTitle("三星 Note手机");
        item.setBrand("三星");
        item.setPrice(new BigDecimal("29999"));
        item.setSeller("三星专卖南山分部");
        item.setImage("http://www.baidu.com/images/a.jpg");
        item.setGoodsId(1L);
        item.setCategory("手机类");
        //添加对象
        solrTemplate.saveBean(item);
        //提交
        solrTemplate.commit();
    }

    @Test
    /**
     * 查询一条数据
     */
    public void testFindOne(){
        TbItem tbItem = solrTemplate.getById(1L, TbItem.class);
        System.out.println("tbItem = " + tbItem);
    }

    @Test
    /**
     * 修改商品
     */
    public void test02() {
        //根据商品id查询出一件商品
        TbItem item = solrTemplate.getById(1L, TbItem.class);
        //设置修改的值
        item.setTitle("华为Meta 30");
        //执行修改方法
        solrTemplate.saveBean(item);
        //提交
        solrTemplate.commit();
    }

    @Test
    public void test03() {
        //定义要添加的集合
        List<TbItem> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TbItem item = new TbItem();
            item.setId(new Long(i + ""));
            item.setTitle("三星 Note手机" + i);
            item.setBrand("三星");
            item.setPrice(new BigDecimal("29999" + i));
            item.setSeller("三星专卖南山分部" + i);
            item.setImage("http://www.baidu.com/images/a.jpg");
            item.setGoodsId(1L);
            item.setCategory("手机类");
            list.add(item);
        }
        //执行添加
        solrTemplate.saveBeans(list);
        //提交
        solrTemplate.commit();
    }

    @Test
    /**
     * 分页查询
     */
    public void test04() {
        //构造查询对象
        Query query = new SimpleQuery("*:*");
        query.setOffset(0);
        query.setRows(10);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = tbItems.getContent();
        for (TbItem item : content) {
            System.out.println("item = " + item);
        }
    }
}
