package org.example.paneljavafx.dao.impl;

import org.example.paneljavafx.dao.FundPositionDAO;
import org.example.paneljavafx.database.DatabaseConnection;
import org.example.paneljavafx.model.FundPosition;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FundPositionImpl implements FundPositionDAO {

    @Override
    public void save(FundPosition fp) {
        String sql = """
            INSERT INTO fund_position (
                id_fund_position, id_fund, id_asset,
                peso_porcentual, invested_value, quantity,
                currency, added_risk, start_date, finish_date
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fp.getIdFundPosition());
            stmt.setString(2, fp.getIdFund());
            stmt.setString(3, fp.getIdAsset());
            stmt.setDouble(4, fp.getPesoPorcentual());
            stmt.setDouble(5, fp.getInvestedValue());
            stmt.setDouble(6, fp.getQuantity());
            stmt.setString(7, fp.getCurrency());
            stmt.setString(8, fp.getAddedRisk());
            stmt.setDate(9, Date.valueOf(fp.getStartDate()));
            stmt.setDate(10, fp.getFinishDate() != null
                    ? Date.valueOf(fp.getFinishDate()) : null);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando FundPosition", e);
        }
    }

    @Override
    public List<FundPosition> findAll() {
        List<FundPosition> list = new ArrayList<>();
        String sql = "SELECT * FROM fund_position";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo fund positions", e);
        }

        return list;
    }

    @Override
    public Optional<FundPosition> findById(String id) {
        String sql = "SELECT * FROM fund_position WHERE id_fund_position = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(map(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<FundPosition> findByFundId(String fundId) {
        return findByField("id_fund", fundId);
    }

    @Override
    public List<FundPosition> findByAssetId(String assetId) {
        return findByField("id_asset", assetId);
    }

    @Override
    public boolean update(String id, FundPosition fp) {
        String sql = """
            UPDATE fund_position SET
                id_fund=?, id_asset=?,
                peso_porcentual=?, invested_value=?, quantity=?,
                currency=?, added_risk=?, start_date=?, finish_date=?
            WHERE id_fund_position=?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fp.getIdFund());
            stmt.setString(2, fp.getIdAsset());
            stmt.setDouble(3, fp.getPesoPorcentual());
            stmt.setDouble(4, fp.getInvestedValue());
            stmt.setDouble(5, fp.getQuantity());
            stmt.setString(6, fp.getCurrency());
            stmt.setString(7, fp.getAddedRisk());
            stmt.setDate(8, Date.valueOf(fp.getStartDate()));
            stmt.setDate(9, fp.getFinishDate() != null
                    ? Date.valueOf(fp.getFinishDate()) : null);
            stmt.setString(10, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM fund_position WHERE id_fund_position = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // HELPERS
    // =========================
    private List<FundPosition> findByField(String column, String value) {
        List<FundPosition> list = new ArrayList<>();
        String sql = "SELECT * FROM fund_position WHERE " + column + " = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private FundPosition map(ResultSet rs) throws SQLException {
        FundPosition fp = new FundPosition();

        fp.setIdFundPosition(rs.getString("id_fund_position"));
        fp.setIdFund(rs.getString("id_fund"));
        fp.setIdAsset(rs.getString("id_asset"));
        fp.setPesoPorcentual(rs.getDouble("peso_porcentual"));
        fp.setInvestedValue(rs.getDouble("invested_value"));
        fp.setQuantity(rs.getDouble("quantity"));
        fp.setCurrency(rs.getString("currency"));
        fp.setAddedRisk(rs.getString("added_risk"));
        fp.setStartDate(rs.getDate("start_date").toLocalDate());

        Date finishDate = rs.getDate("finish_date");
        if (finishDate != null) fp.setFinishDate(finishDate.toLocalDate());

        return fp;
    }
}