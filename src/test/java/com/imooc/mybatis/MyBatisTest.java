package com.imooc.mybatis;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.imooc.mybatis.dto.GoodsDTO;
import com.imooc.mybatis.entity.Goods;
import com.imooc.mybatis.entity.GoodsDetail;
import com.imooc.mybatis.utils.MyBatisUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.*;

/**
 * Junit 对SqlSessionFactory 进行单元测试
 */
public class MyBatisTest {
    @Test
    public void testSqlSessionFactory() {
        SqlSession sqlSession = null;
        try {
            //利用Reader加载classpath下的mybatis-config.xml核心配置文件
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            //初始化SqlSessionFactory对象，同时解析mybatis-config.xml文件
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            System.out.println("SessionFactory加载成功");
            //创建SqlSession对象，SqlSession是JDBC的拓展类，用于与数据库交互
            sqlSession = sqlSessionFactory.openSession();
            Connection conn = sqlSession.getConnection();
            System.out.println(conn);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sqlSession != null) {
                //如果type="POOLED"，代表使用连接池，close则是将连接回收到连接池中
                //如果type="UNPOOLED",代表直连，close则会调用Connection.close()方法关闭连接
                sqlSession.close();
            }
        }
    }

    @Test
    public void testMyBatisUtils() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Connection conn = sqlSession.getConnection();
            System.out.println(conn);
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectAll() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
//            Connection conn=sqlSession.getConnection();
            List<Goods> list = sqlSession.selectList("goods.selectAll");
            for (Goods goods : list) {
                System.out.println(goods.getTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectById() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1603);
            System.out.println(goods.getTitle());
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectByPriceRange() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Map param = new HashMap();
            param.put("min", 100);
            param.put("max", 500);
            param.put("limit", 10);
            List<Goods> list = sqlSession.selectList("goods.selectByPriceRange", param);
            for (Goods goods : list) {
                System.out.println(goods.getTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectGoodsDTO() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            List<GoodsDTO> list = sqlSession.selectList("goods.selectGoodsDTO");
            for (GoodsDTO goodsDTO : list) {
                System.out.println(goodsDTO.getGoods().getTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testInsert() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = new Goods();
            goods.setTitle("测试商品");
            goods.setSubTitle("测试子标题");
            goods.setOriginalCost(200f);
            goods.setCurrentPrice(100f);
            goods.setDiscount(0.5f);
            goods.setIsFreeDelivery(1);
            goods.setCategoryId(43);
            //insert()方法返回值代表本次成功插入的记录总数
//            int num = sqlSession.insert("goods.insert", goods);
            int num = sqlSession.insert("goods.insert2", goods);
            // selectKey 标签时通用方案，适用于所有数据库，但编写麻烦
            // userGeneratedKeys属性只支持“自增主键”数据库，使用简单,例如Oracle则必须使用selectKey
            sqlSession.commit();
            System.out.println(num);
            System.out.println(goods.getGoodsId());
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 2676);
            goods.setTitle("更新测试商品");
//            goods.setSubTitle("测试子标题");
//            goods.setOriginalCost(200f);
//            goods.setCurrentPrice(100f);
//            goods.setDiscount(0.5f);
//            goods.setIsFreeDelivery(1);
//            goods.setCategoryId(43);
            int num = sqlSession.update("goods.update", goods);
            System.out.println(num);
            sqlSession.commit();
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testDelete() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            int num = sqlSession.delete("goods.delete", 2676);
            System.out.println(num);
            sqlSession.commit();
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectByTitle() {
        //#{title}使用预编译 ,可以有效解决sql注入的问题
        //${title}使用原文传值
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Map param = new HashMap();
            param.put("title", "'测试商品'");
            List<Goods> list = sqlSession.selectList("goods.selectByTitle", param);
            for (Goods goods : list) {
                System.out.println(goods.getTitle() + ":" + goods.getSubTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testDynamicSQL() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Map param = new HashMap();
            param.put("categoryId", 44);
            param.put("currentPrice", 500);
            //查询条件
            List<Goods> list = sqlSession.selectList("goods.dynamicSQL", param);
            for (Goods goods : list) {
                System.out.println(goods.getTitle() + ":" + goods.getSubTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testLv1Cache() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1603);
            Goods goods1 = sqlSession.selectOne("goods.selectById", 1603);
            System.out.println(goods.hashCode() + " : " + goods1.hashCode());
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }

        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1603);
            sqlSession.commit();//commit 提交时对该namespace缓存强制清空
            Goods goods1 = sqlSession.selectOne("goods.selectById", 1603);
            System.out.println(goods.hashCode() + " : " + goods1.hashCode());
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testLv2Cache() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1603);
            System.out.println(goods.hashCode());
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }

        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1603);
            System.out.println(goods.hashCode());
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testManyToOne() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            List<GoodsDetail> list = sqlSession.selectList("goodsDetail.selectManyToOne");
            for (GoodsDetail goodsDetail : list) {
                System.out.println(goodsDetail.getGdPicUrl() + ":" + goodsDetail.getGoods().getTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectPage() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            //页码从 1 开始
            PageHelper.startPage(1, 10);
            Page<Goods> page = (Page) sqlSession.selectList("goods.selectPage");
            System.out.println("总页数：" + page.getPages());
            System.out.println("总记录数：" + page.getTotal());
            System.out.println("开始行号：" + page.getStartRow());
            System.out.println("结束行号：" + page.getEndRow());
            System.out.println("当前页码：" + page.getPageNum());
            List<Goods> data = page.getResult();//当前页数据
            for (Goods goods : data) {
                System.out.println(goods.getGoodsId() + ":" + goods.getTitle());
            }
            System.out.println();
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testBatchInsert() throws Exception {
        SqlSession sqlSession = null;
        try {
            long st = new Date().getTime();
            sqlSession = MyBatisUtils.openSession();
            List list = new ArrayList();
            for (int i = 0; i < 10000; i++) {
                Goods goods = new Goods();
                goods.setGoodsId(i);
                goods.setTitle("测试商品");
                goods.setSubTitle("测试子标题");
                goods.setOriginalCost(200f);
                goods.setCurrentPrice(100f);
                goods.setDiscount(0.5f);
                goods.setIsFreeDelivery(1);
                goods.setCategoryId(43);
                list.add(goods);
            }
            sqlSession.insert("goods.batchInsert", list);
            sqlSession.commit();
            long et = new Date().getTime();
            System.out.println("执行时间：" + (et - st) + "毫秒");
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testBatchDelete() throws Exception {
        SqlSession sqlSession = null;
        try {
            long st = new Date().getTime();
            sqlSession = MyBatisUtils.openSession();
            List list = new ArrayList();
            list.add(1920);
            list.add(1921);
            list.add(1922);
            sqlSession.delete("goods.batchDelete", list);
            sqlSession.commit();//提交事务数据
            long et = new Date().getTime();
            System.out.println("执行时间：" + (et - st) + "毫秒");
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }
}
