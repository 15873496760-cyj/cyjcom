package com.zelin.text;


import com.zelin.util.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSolrTmplate {

    @Autowired
    private SolrUtil solrUtil;

    @Test
    public void test01() {
        solrUtil.importItemData();
    }
}
