package com.shyl.msc.b2b.order.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IDatagramDao;
import com.shyl.msc.b2b.order.entity.Datagram;
@Repository
public class DatagramDao extends BaseDao<Datagram, Long> implements IDatagramDao {

}
