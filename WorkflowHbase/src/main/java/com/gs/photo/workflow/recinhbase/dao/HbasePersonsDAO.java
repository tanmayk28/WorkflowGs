package com.gs.photo.workflow.recinhbase.dao;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Connection;
import org.springframework.stereotype.Component;

import com.gs.photo.common.workflow.hbase.dao.AbstractHbasePersonsDAO;
import com.workflow.model.HbasePersons;

@Component
public class HbasePersonsDAO extends AbstractHbasePersonsDAO implements IHbasePersonsDAO {

    public HbasePersonsDAO(
        Connection connection,
        String nameSpace
    ) { super(connection,
        nameSpace); }

    @Override
    public void truncate() throws IOException { super.truncate(this.getHbaseDataInformation()); }

    @Override
    public void delete(HbasePersons hbaseData, String family, String column) { // TODO Auto-generated method stub
    }

}
