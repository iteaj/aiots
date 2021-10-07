package com.iteaj.quartz;

import org.apache.ibatis.type.*;
import org.quartz.JobDataMap;

import java.io.*;
import java.sql.*;

@MappedJdbcTypes(value = {JdbcType.BLOB})
@MappedTypes(value = {JobDataMap.class})
public class JobDataMapTypeHandle extends BaseTypeHandler<JobDataMap> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, JobDataMap jobDataMap, JdbcType jdbcType) throws SQLException {
        if(jobDataMap != null) {
            try(final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);) {

                objectOutputStream.writeObject(jobDataMap);
                final byte[] bytes = outputStream.toByteArray();
                preparedStatement.setBlob(i, new ByteArrayInputStream(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            preparedStatement.setBlob(i, (InputStream) null);
        }
    }

    @Override
    public JobDataMap getNullableResult(ResultSet resultSet, String s) throws SQLException {
        final Blob blob = resultSet.getBlob(s);
        if(blob != null) {
            try {
                ObjectInputStream stream = new ObjectInputStream(blob.getBinaryStream());
                return (JobDataMap) stream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public JobDataMap getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return null;
    }

    @Override
    public JobDataMap getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return null;
    }
}
