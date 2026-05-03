package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.FundAssetPositionDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.FundAssetPosition;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FundAssetPositionImpl implements FundAssetPositionDAO {

    @Override
    public List<FundAssetPosition> findAll() {
        List<FundAssetPosition> list = new ArrayList<>();
        String sql = "SELECT * FROM fund_position_asset";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo fund positions", e);
        }

        return list;
    }

    private FundAssetPosition map(ResultSet rs) throws SQLException {
        FundAssetPosition fp = new FundAssetPosition();

        fp.setIdFundPosition(rs.getString("id"));
        fp.setIdFund(rs.getString("id_fund"));
        fp.setIdAsset(rs.getString("id_asset"));
        fp.setPortfolioWeight(rs.getDouble("percentual_weight"));
        fp.setInvestedValue(rs.getDouble("invested_value"));
        fp.setQuantity(rs.getDouble("quantity"));
        fp.setCurrency(rs.getString("currency"));
        fp.setAddedRisk(rs.getString("added_risk"));
        fp.setOpenedDate(rs.getDate("start_date").toLocalDate());

        Date finishDate = rs.getDate("finish_date");
        if (finishDate != null) fp.setClosedDate(finishDate.toLocalDate());

        return fp;
    }
}