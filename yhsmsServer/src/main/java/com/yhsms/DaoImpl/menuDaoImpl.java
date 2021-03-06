package com.yhsms.DaoImpl;

import java.awt.Menu;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yhsms.Dao.menuDao;
import com.yhsms.domain.Menum;
import com.yhsms.domain.Menutype;
import com.yhsms.util.DBUtil;

public class menuDaoImpl implements menuDao {
	
	private DBUtil db;

	

	//添加菜品
	@Override
	public boolean addmenu(Menum m,int mtid) {
		//Menutype mt = new Menutype(mtid);
		
		this.db = new DBUtil();
		String sql="insert into menum values(?,?,?,?,?) ";
		try {
			int i = this.db.update(sql,m.getMid(),m.getMname(),m.getMprice(),mtid,m.getMnote());
			return i>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			this.db.close();
		}
	}


	//客户查询所有菜
	@Override
	public Map<Integer,String> userseletemenu(int mtid) {
		Map<Integer,String> map = new HashMap<Integer,String>();
		this.db = new DBUtil();
		String sql="select * from menum where mtid="+mtid+"and mnote is null or mnote='特色菜'";
		try {
			ResultSet rs = this.db.query(sql);
			System.out.println("菜品编号"+"\t"+"菜名"+"\t"+"单价");
			while(rs.next()){
				
				map.put(rs.getInt("mid"), rs.getString("mname")+"\t"+rs.getDouble("mprice"));
			}
			return map;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally{
			this.db.close();
		}
	
	}
	
	//经理查询所有的菜
	@Override
	public Map<Integer,String> empseletemenu(){
		Map<Integer,String> map = new HashMap<Integer,String>();
		this.db = new DBUtil();
		String sql="select * from menum ";
		try {
			ResultSet rs = this.db.query(sql);
			System.out.println("菜品编号"+"\t"+"菜名"+"\t"+"单价"+"\t"+"所属类型"+"\t"+"备注");
			while(rs.next()){
				
				map.put(rs.getInt("mid"), rs.getString("mname")+"\t"+rs.getDouble("mprice")+"\t"+rs.getInt("mtid")+"\t"+rs.getString("mnote"));
			}
			return map;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally{
			this.db.close();
		}
		
	}

	//删除菜
	@Override
	public boolean deletemenu(int mid) {
		this.db = new DBUtil();
		String sql="delete from menum where mid="+mid;
		try {
			int i = this.db.update(sql);
			return i>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			this.db.close();
		}
	}

	//修改菜的价钱
	@Override
	public boolean updatemenu(int mid, double price) {
		this.db = new DBUtil();
		String sql="update menum set mprice ="+price+"where mid="+mid;
		try {
			int i = this.db.update(sql);
			return i>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			this.db.close();
		}
		
	}

	//设置特价菜
	@Override
	public boolean setspecial(int mid) {
		this.db = new DBUtil();
		String sql="update menum set mnote ='特价菜'  where mid="+mid;
		try {
			int i = this.db.update(sql);
			return i>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			this.db.close();
		}
		
	}

	//显示特价菜
	@Override
	public Map<Integer, String> selectspecial() {
		Map<Integer,String> map = new HashMap<Integer,String>();
		this.db = new DBUtil();
		String sql="select * from menum where mnote='特价菜'";
		try {
			ResultSet rs = this.db.query(sql);
			System.out.println("菜品编号"+"\t"+"菜名"+"\t"+"单价");
			while(rs.next()){
				
				map.put(rs.getInt("mid"), rs.getString("mname")+"\t"+rs.getDouble("mprice"));
			}
			return map;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally{
			this.db.close();
		}
	
	}

	
	

	
}
