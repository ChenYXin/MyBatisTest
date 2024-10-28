package com.imooc.mybatis;

import com.imooc.mybatis.dto.GoodsDTO;
import com.imooc.mybatis.entity.Goods;
import com.imooc.mybatis.utils.MyBatisUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
