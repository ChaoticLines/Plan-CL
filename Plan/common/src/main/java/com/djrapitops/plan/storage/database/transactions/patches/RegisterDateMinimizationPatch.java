package com.djrapitops.plan.storage.database.transactions.patches;

import com.djrapitops.plan.storage.database.queries.Query;
import com.djrapitops.plan.storage.database.queries.QueryAllStatement;
import com.djrapitops.plan.storage.database.sql.tables.UserInfoTable;
import com.djrapitops.plan.storage.database.sql.tables.UsersTable;
import com.djrapitops.plan.storage.database.transactions.ExecBatchStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.djrapitops.plan.storage.database.sql.parsing.Sql.*;

public class RegisterDateMinimizationPatch extends Patch {

    private Map<UUID, Long> registerDates;

    @Override
    public boolean hasBeenApplied() {
        registerDates = query(fetchSmallestServerRegisterDates());
        return registerDates.isEmpty();
    }

    private Query<Map<UUID, Long>> fetchSmallestServerRegisterDates() {
        String sql = SELECT + "u1.uuid,u1." + UsersTable.REGISTERED + ",min_registered" + FROM + '(' +
                SELECT + UserInfoTable.USER_UUID + ',' +
                "MIN(" + UserInfoTable.REGISTERED + ") as min_registered" +
                FROM + UserInfoTable.TABLE_NAME +
                GROUP_BY + UserInfoTable.USER_UUID + ") u2" +
                INNER_JOIN + UsersTable.TABLE_NAME + " u1 on u1.uuid=u2.uuid" +
                WHERE + "u1." + UsersTable.REGISTERED + ">min_registered";

        return new QueryAllStatement<Map<UUID, Long>>(sql, 500) {
            @Override
            public Map<UUID, Long> processResults(ResultSet set) throws SQLException {
                Map<UUID, Long> registerDates = new HashMap<>();
                while (set.next()) {
                    UUID playerUUID = UUID.fromString(set.getString(1));
                    long newRegisterDate = set.getLong("min_registered");
                    registerDates.put(playerUUID, newRegisterDate);
                }
                return registerDates;
            }
        };

    }

    @Override
    protected void applyPatch() {
        if (registerDates.isEmpty()) return;

        String sql = "UPDATE " + UsersTable.TABLE_NAME + " SET " + UsersTable.REGISTERED + "=?" +
                WHERE + UsersTable.USER_UUID + "=?" +
                AND + UsersTable.REGISTERED + ">?";

        execute(new ExecBatchStatement(sql) {
            @Override
            public void prepare(PreparedStatement statement) throws SQLException {
                for (Map.Entry<UUID, Long> entry : registerDates.entrySet()) {
                    UUID playerUUID = entry.getKey();
                    Long registerDate = entry.getValue();
                    statement.setLong(1, registerDate);
                    statement.setString(2, playerUUID.toString());
                    statement.setLong(3, registerDate);
                    statement.addBatch();
                }

            }
        });
    }
}
