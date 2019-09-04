package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodIds) {
        System.out.println("goodIds = " + goodIds);
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map data = new HashMap();
            //加载商品数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodIds);
            data.put("goods", tbGoods);

            //加载商品扩展属性
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodIds);
            data.put("goodsDesc", tbGoodsDesc);

            //商品分类
            String category1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

            //得到sku列表
            List<TbItem> tbItems = getitemList(goodIds);
            data.put("itemList", tbItems);


            data.put("category1", category1);
            data.put("category2", category2);
            data.put("category3", category3);
            //定义输出流对象
            Writer out = new FileWriter(pagedir + goodIds + ".html");
            template.process(data, out);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<TbItem> getitemList(Long id) {
        //创建查询对象
        TbItemExample example=new TbItemExample();
        //创建查询条件对象
        TbItemExample.Criteria criteria = example.createCriteria();
        //添加条件
        criteria.andStatusEqualTo("1");
        criteria.andGoodsIdEqualTo(id);
        example.setOrderByClause("is_default desc");
        //得到结果集合
        List<TbItem> itemList = itemMapper.selectByExample(example);
        return itemList;
    }
}
