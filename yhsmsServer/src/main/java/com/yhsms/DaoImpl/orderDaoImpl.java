package com.yhsms.DaoImpl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.yhsms.Dao.orderDao;
import com.yhsms.domain.Orders;
import com.yhsms.util.DBUtil;

public class orderDaoImpl implements orderDao {

	private DBUtil db;

	//获取系统时间
	public String sysdate(){
		this.db=new DBUtil();
		String sql="select sysdate from dual";
		try {
			ResultSet rs = this.db.query(sql);
			if(rs.next()){				
				return rs.getString("sysdate");   
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;

	}

	//添加订单
	@Override//若无会员卡，则输入0
	public String addorders(int caid,int mid,int num) {
		this.db=new DBUtil();
		String sql3="select mprice from menum where mid="+mid;
		String sql4="select canote from card where caid="+caid;
		try {
			ResultSet rs3 = this.db.query(sql3);
			ResultSet rs4 = this.db.query(sql4);
			if(rs3.next() && rs4.next()){
				if(rs4.getString("canote")==null ){
					String sql5="insert into oitem values(seq_orders.nextval,"+caid+","+mid+","+num+",?,'','')";
					int i = this.db.update(sql5,rs3.getDouble("mprice")*num);
					if(i>0){
						return "下单成功";
					}
				}
				return "该卡已被"+rs4.getString("canote");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "下单失败";
		}finally{
			this.db.close();
		}
		return "下单失败";

	}


	//根据卡号显示所有订单
	@Override
	public Map<Integer,String> selectordersBycaid(int caid) {
		Map<Integer,String> map =new HashMap<Integer,String>();
		this.db = new DBUtil();
		String sql="select * from oitem where caid="+caid+" and (onote is null or onote ='已结账') order by otime";
		try {
			ResultSet rs = this.db.query(sql);
			System.out.println("编号"+"\t"+"菜品编号"+"\t"+"数量"+"\t"+"金额"+"\t"+"备注"+"\t"+"下单时间");
			while(rs.next()){
				map.put(rs.getInt("oid"),rs.getInt("mid")+"\t"+rs.getInt("num")+"\t"+rs.getDouble("oprice")+"\t"+rs.getString("onote")+"\t"+rs.getString("otime"));
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

	//根据卡号显示当前订单
	@Override
	public Map<Integer,String> selectnoworder(int caid) {
		Map<Integer,String> map =new HashMap<Integer,String>();
		this.db = new DBUtil();
		String sql="select o.oid,o.mid,m.mname,o.num,m.mprice,o.oprice from oitem o,menum m"+
				   " where o.caid="+caid+"and m.mid=o.mid and onote is null";
		try {
			ResultSet rs = this.db.query(sql);
			System.out.println("编号"+"\t"+"菜品编号"+"\t"+"菜名"+"\t"+"数量"+"\t"+"单价"+"\t"+"金额");
			while(rs.next()){
				map.put(rs.getInt("oid"),rs.getInt("mid")+"\t"+rs.getString("mname")+"\t"+rs.getInt("num")+"\t"+rs.getDouble("mprice")+"\t"+rs.getDouble("oprice"));
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

	//员工下单
	@Override
	public String emporder(int eid,int caid,int mid,int num) {
		this.db=new DBUtil();
		String sql1="select mprice from menum where mid="+mid;
		String sql2="select canote from card where caid="+caid;
		String sql="select seq_orders.nextval from dual";
		try {
			ResultSet rs = this.db.query(sql);
			ResultSet rs1 = this.db.query(sql1);
			ResultSet rs2 = this.db.query(sql2);
			if(rs1.next() && rs2.next() && rs.next()){
				if(rs2.getString("canote")==null ){
					String sql3="insert into oitem values(?,"+caid+","+mid+","+num+",?,'','')";
					String sql4="insert into Orders values(?,"+eid+",'')";
					int i = this.db.update(sql3,rs.getInt("nextval"),rs1.getDouble("mprice")*num);
					int n = this.db.update(sql4,rs.getInt("nextval"));
					if(i>0 && n>0){
						return "下单成功";
					}
				}
				return "该卡已被"+rs2.getString("canote");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "下单失败";
		}finally{
			this.db.close();
		}
		return "下单失败";

	}


	//根据员工号显示所有订单
	@Override
	public Map<Integer,String> selectorderbyeid(int eid) {
		Map<Integer,String> map =new HashMap<Integer,String>();
		this.db = new DBUtil();
		String str="select oid from orders where eid="+eid;

		try {
			ResultSet r = this.db.query(str);
			System.out.println("编号"+"\t"+"菜品编号"+"\t"+"数量"+"\t"+"金额"+"\t"+"备注"+"\t"+"下单时间");
			while(r.next()){
				String sql="select * from oitem where oid="+r.getInt("oid")+" and onote = '已结账'";
				ResultSet rs = this.db.query(sql);

				while(rs.next()){
					map.put(rs.getInt("oid"),rs.getInt("mid")+"\t"+rs.getInt("num")+"\t"+rs.getDouble("oprice")+"\t"+rs.getString("onote")+"\t"+rs.getString("otime"));
				}

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

	//删除订单
	@Override
	public String deleteorder(int oid) {
		this.db = new DBUtil();
		String sql="update oitem set onote='已删除' where oid="+oid ;

		try {
			int i = this.db.update(sql);
			if(i>0){
				return "删除成功"; 

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "删除失败"; 
		}finally{
			this.db.close();
		}
		return null;
	}

	//修改订单
	@Override
	public String updateorder(int caid,int mid,int num ) {
		this.db =new DBUtil();
		String sql="update oitem set num="+num+" where caid="+caid+" and mid="+mid+" and onote is null";
		try {
			int i = this.db.update(sql);
			if(i>0){
				String s="select mprice from menum where mid="+mid;
				ResultSet rs = this.db.query(s);
				if(rs.next()){
					String str="update oitem set oprice="+rs.getDouble("mprice")*num+" where caid="+caid+" and mid="+mid;
					int n = this.db.update(str);
					if(n>0){
						return "修改成功";
					}

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "修改失败";
		}finally{
			this.db.close();
		}
		return "修改失败";
	}

	//结账
	@Override
	public double jiezhang(int caid) {
		this.db = new DBUtil();
		String sql="select sum(oprice) from oitem where caid="+caid+" and onote is null";
		try {
			ResultSet rs = this.db.query(sql);
			while(rs.next()){
				String date = this.sysdate();
				//String date = "2019-01-01 11:11:11";
				String s="update oitem set otime=to_date('"+date+"','yyyy-mm-dd hh24:mi:ss') where caid="+caid+" and onote is null";

				int i = this.db.update(s);
				if(i>0){
					String str="update oitem set onote='已结账' where caid="+caid+" and otime is not null";
					int n = this.db.update(str);
					if(n>0){
						return rs.getDouble("sum(oprice)");
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			this.db.close();
		}
		return 0;
	}

	//查看月账单
	@Override
	public Map<Integer, String> month(int date) {
		Map<Integer, String> map=new  HashMap<Integer, String>();
		this.db=new DBUtil();
		String sql="select o.mid,m.mname,count(o.num) from oitem o,menum m where m.mid=o.mid and o.onote='已结账'"
				+ "and months_between(add_months(to_date('"+date+"','mm'),1),to_date('"+date+"','mm'))>0 group by o.mid,m.mname ";
		try {
			ResultSet rs = this.db.query(sql);
			System.out.println("菜品编号"+"\t"+"菜名"+"\t"+"销量");
			while(rs.next()){
				map.put(rs.getInt("mid"), rs.getString("mname")+"\t"+rs.getInt("count(o.num)"));
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
	//查看员工月账单
	@Override
	public Map<Integer, String> empmonth(int eid,int date) {
		Map<Integer, String> map=new  HashMap<Integer, String>();
		this.db=new DBUtil();
		String sql="select o.mid,m.mname,count(o.num) from oitem o,menum m,orders ors where m.mid=o.mid and o.onote='已结账' and ors.eid="+eid
				+ "and months_between(add_months(to_date('"+date+"','mm'),1),to_date('"+date+"','mm'))>0 group by o.mid,m.mname ";
		try {
			ResultSet rs = this.db.query(sql);
			System.out.println("员工编号"+"\t"+"菜名"+"\t"+"销量");
			while(rs.next()){
				map.put(rs.getInt("mid"), rs.getString("mname")+"\t"+rs.getInt("count(o.num)"));
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

//	//员工结账打印小票
//	@Override
//	public Map<Integer,String> xiaopiao(int caid){
//		this.db = new DBUtil();
//		Map<Integer, String> map=new  HashMap<Integer, String>();
//		String oitem="select o.oid,o.mid,m.mname,o.num,m.mprice,o.oprice from oitem o,menum m"+
//				" where o.caid="+caid+"and m.mid=o.mid and onote is null";
//		try {
//			ResultSet r =this. db.query(oitem);
//			while(r.next()){
//				map.put(r.getInt("oid"), r.getInt("mid")+"\t"+r.getString("mname")+"\t"+r.getInt("num")+"\t"+r.getDouble("mprice")+"\t"+r.getDouble("oprice"));
//
//					}
//				return map;
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}finally{
//			this. db.close();
//		}

	}



