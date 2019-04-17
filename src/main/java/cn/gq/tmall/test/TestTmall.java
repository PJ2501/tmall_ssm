package cn.gq.tmall.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestTmall {

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tmall_ssm?useUnicode=true&characterEncoding=utf8",
                "root", "admin");
        Statement statement = connection.createStatement();){
            for (int i = 1;i <= 10;i++){
                String sqlFormat = "insert into category values (null, '测试分类%d')";
                String sql = String.format(sqlFormat,i);
                statement.execute(sql);
            }
            System.out.println("已成功创建十条数据");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
