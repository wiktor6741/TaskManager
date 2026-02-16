package dao;

import java.sql.Connection;

public class TaskDAO {
    private final Connection conn;

    public TaskDAO(Connection conn){
        this.conn = conn;
    }


}

