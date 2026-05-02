package org.example.paneljavafx.service;

import lombok.Getter;
import org.example.paneljavafx.model.Asset;
import org.example.paneljavafx.model.Fund;
import org.example.paneljavafx.model.FundAssetPosition;

import java.util.List;

public class AdminService {

    private final FundService  fundService;
    private final AssetService assetService;

    @Getter
    private List<FundAssetPosition> cachedPositions;

    public AdminService(FundService fundService, AssetService assetService) {
        this.fundService  = fundService;
        this.assetService = assetService;
    }

    public void initialize(List<FundAssetPosition> allPositions) {
        this.cachedPositions = allPositions;
        fundService.load();
        assetService.load();
    }

    public List<FundAssetPosition> getPositionsForFund(Fund fund) {
        return fundService.getPositionsByFund(cachedPositions, fund.getFundId());
    }

    public FundViewContext prepareFundView(Fund fund) {
        List<FundAssetPosition> positions = getPositionsForFund(fund);
        return new FundViewContext(fund, positions);
    }

    public AssetViewContext prepareAssetView(Asset asset) {
        return new AssetViewContext(asset, cachedPositions);
    }

    public record FundViewContext(
            Fund fund,
            List<FundAssetPosition> positions
    ) {}

    public record AssetViewContext(
            Asset asset,
            List<FundAssetPosition> positions
    ) {}
}