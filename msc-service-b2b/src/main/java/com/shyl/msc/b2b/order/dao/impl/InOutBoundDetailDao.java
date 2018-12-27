package com.shyl.msc.b2b.order.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IInOutBoundDetailDao;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
/**
 * 出入库明细DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class InOutBoundDetailDao extends BaseDao<InOutBoundDetail, Long> implements IInOutBoundDetailDao {

	@Override
	public InOutBoundDetail getByDeliveryOrderDetailCode(String code) {
		String hql = "from InOutBoundDetail d where d.deliveryOrderDetailCode=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<InOutBoundDetail> listByIsPass(int isPass) {
		String hql = "from InOutBoundDetail d where d.isPass=?";
		return super.listByHql(hql, null, isPass);
	}

	@Override
	public List<InOutBoundDetail> listByInOutBound(Long id) {
		String hql = "from InOutBoundDetail d where d.inOutBound.id=?";
		return super.listByHql(hql, null, id);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE ,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.unitName,b.ISGPOPURCHASE,b.AUTHORIZENO, "
				+ " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT, "
				+ " sum(case when po.gpoCode is null then 0 else p.goodsSum end) as GPOGOODSSUM,max(p.price) as price "
				+ " from t_order_inoutbound_detail p"
				+ " left join t_order_inoutbound po on p.INOUTBOUNDID=po.ID "
				+"  left join t_dm_product b on p.productCode=b.code "
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.unitName, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable) {
		String sql = "select p.PRODUCTCODE,p.PRODUCTNAME,p.DOSAGEFORMNAME,p.unit as UNITNAME,"
				+ " p.MODEL,p.PACKDESC,p.PRODUCERNAME,p.goodsNum as NUM,p.goodsSum as AMT,"
				+ " p.CODE,p.BATCHCODE,p.BATCHDATE,p.EXPIRYDATE,po.VENDORNAME,po.HOSPITALNAME,po.code as INOUTBOUNDCODE"
				+ " from t_order_inoutbound_detail p"
				+ " left join t_order_inoutbound po on p.INOUTBOUNDID=po.ID";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE ,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.unitName,b.ISGPOPURCHASE,b.AUTHORIZENO,"
				+ " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT, "
				+ " sum(case when po.gpoCode is null then 0 else p.goodsSum end) as GPOGOODSSUM,max(p.price) as price "
				+ " from t_order_inoutbound_detail p"
				+ " left join t_order_inoutbound po on p.INOUTBOUNDID=po.ID "
				+"  left join t_dm_product b on p.productCode=b.code "
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.unitName, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.listBySql2(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String,Object>> listGpoReport(String year, String month, int maxsize) {
		//判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		//默認日期查詢條件
		String dateQuerySQL = " and to_char(b.orderDate,'yyyy') = '" + year+"'";
		//如果有日期的情況日期條件處理
		if (!StringUtils.isEmpty(month)) {
			dateQuerySQL = " and to_char(b.orderDate,'yyyyMM') = '" + year + month+"'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select z.fullname as name,round(a.sumje*100/(nvl(a.sumje,0)+nvl(b.sumje,0))) as value from t_set_hospital z");
		sql.append(" inner join (select b.hospitalcode,sum(a.goodssum) as sumje from  t_order_purchaseorder_detail a ");
		sql.append(" left join t_order_purchaseorder b on a.purchaseorderid=b.id  ");
		sql.append(" where b.gpocode is not null  ");
		sql.append(dateQuerySQL);
		sql.append(" group by b.hospitalcode) a on z.code=a.hospitalcode ");
		sql.append(" left join (select hospitalcode,sum(a.goodssum) as sumje from t_order_inoutbound_detail a ");
		sql.append(" left join t_order_inoutbound b on a.inoutboundid=b.id ");
		sql.append(" where b.gpocode is  null");
		sql.append(dateQuerySQL);
		sql.append(" group by b.hospitalcode) b  on z.code=b.hospitalcode   ");
		
		return super.limitBySql(sql.toString(), maxsize, null, Map.class);
	}
	

	@Override
	public List<Map<String, Object>> listGpoReportByRegion(String year, String month, String treepath, int maxsize) {
		//判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		//默認日期查詢條件
		String dateQuerySQL = " and to_char(b.orderDate,'yyyy') = '" + year+"'";
		//如果有日期的情況日期條件處理
		if (!StringUtils.isEmpty(month)) {
			dateQuerySQL = " and to_char(b.orderDate,'yyyyMM') = '" + year + month+"'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select z.name,round(a.sumje*100/(nvl(a.sumje,0)+nvl(b.sumje,0))) as value  from ");
		sql.append(" t_set_regioncode z inner join ");
		sql.append(" (select regioncode,sum(a.goodssum) as sumje  ");
		sql.append(" from  t_order_purchaseorder_detail a,t_order_purchaseorder b,t_set_hospital c ");
		sql.append(" where a.purchaseorderid=b.id and b.hospitalcode=c.code and b.gpocode is not null ");
		sql.append(dateQuerySQL);
		sql.append(" group by c.regioncode) a on  z.id=a.regioncode ");
		sql.append(" left join  (select regioncode,sum(a.goodssum) as sumje  ");
		sql.append(" from t_order_inoutbound_detail a,t_order_inoutbound b,t_set_hospital c ");
		sql.append(" where  a.inoutboundid=b.id and b.hospitalcode=c.code and b.gpocode is null ");
		sql.append(dateQuerySQL);
		sql.append(" group by c.regioncode) b on  z.id=b.regioncode ");
		//如果区域书为空
		if (!StringUtils.isEmpty(treepath)) {
			sql.append(" and z.treepath like '").append(treepath).append("'||'%'");
		}
		sql.append(" order by z.id desc");
		return super.limitBySql(sql.toString(), maxsize, null, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(PageRequest pageable) {
		String sql = "select b.hospitalCode,max(b.hospitalName) as hospitalName,sum(d.goodssum) as goodssum,sum(case when p.isGPOPurchase = 1 then d.goodssum else 0 end) as gpoGoodsSum, sum(case when p.isGPOPurchase = 0 then d.goodssum else 0 end ) as otherGoodsSum "
				+ " from t_order_inoutbound_detail d, t_dm_product p,t_order_inoutbound b where d.productcode = p.code(+) and inOutBoundId=b.id group by b.hospitalcode" ;
		return this.findBySql(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> queryByAll(PageRequest pageable) {
		String sql = "select b.hospitalCode,max(b.hospitalName) as hospitalName,sum(d.goodssum) as goodssum,sum(case when p.isGPOPurchase = 1 then d.goodssum else 0 end) as gpoGoodsSum, sum(case when p.isGPOPurchase = 0 then d.goodssum else 0 end ) as otherGoodsSum "
				+ "  from t_order_inoutbound_detail d, t_dm_product p,t_order_inoutbound b where d.productcode = p.code(+) and inOutBoundId=b.id group by b.hospitalcode" ;
		return this.listBySql2(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> queryInOutBoundDetail(PageRequest page, String beginMonth, String endMonth) {
		String sql = "select d.productCode as productCode,sum(d.goodsNum) as purcahseNum,sum(d.goodsSum) as purcahseSum, "
		+" sum(case when l.Isgpopurchase = 1 then d.goodsNum else 0 end) as gpoPurcahseNum,sum(case when l.Isgpopurchase = 1 then d.goodsSum else 0 end) as gpoPurcahseSum, "
		+" sum(case when l.Isgpopurchase = 0 then d.goodsNum else 0 end) as notGpoPurcahseNum,sum(case when l.Isgpopurchase = 0 then d.goodsSum else 0 end) as notGpoPurcahseSum "
		+" from t_order_inoutbound_detail d, t_order_inoutbound b, t_dm_product l where d.inoutboundid = b.id and d.productcode = l.code(+) ";
		if(!StringUtils.isEmpty(beginMonth)&&!StringUtils.isEmpty(endMonth)){
			sql+=" and to_char(d.orderdate, 'yyyy-mm') >= ? and to_char(d.orderdate, 'yyyy-mm') <= ?";
		}
		sql+=" group by d.productCode";
		return super.listBySql2(sql, page, Map.class,beginMonth,endMonth);
	}

	@Override
	public DataGrid<InOutBoundDetail> queryByCode(PageRequest pageable) {
		String sql = "select t.* from t_order_inoutbound_detail t left join t_order_inoutbound op on t.inOutBoundId"
				+" =op.id left join t_dm_product p on t.productcode=p.code";
		return super.findBySql(sql,pageable,Map.class);

	}
}
