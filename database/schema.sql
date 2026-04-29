-- =============================================
-- INSERT: 50 Assets - Hyperion Cantos Universe
-- =============================================

INSERT INTO asset (id, initial_price, ticker, name, isin, type, sector, market_cap, volatility, risk, liquidity, change) VALUES
-- 🧠 TecnoNúcleo / IA
('AST-001', 4250.00,  'TCPI',  'Technocore Prime Index',                'TC001X9K', 'ETF',        'Technology',   98000000.0, 0.38, 'HIGH',      'HIGH',   'UP'),
('AST-002', 310.50,   'TCSF',  'Technocore Substrate Futures',          'TC002M4R', 'FUTURE',     'Technology',   54000000.0, 0.55, 'HIGH',      'MEDIUM', 'DOWN'),
('AST-003', 1500.00,  'UIBS',  'Ultimate Intelligence Bond Series',     'UI003B7Z', 'BOND',       'Technology',   22000000.0, 0.15, 'LOW',       'HIGH',   'STABLE'),
('AST-004', 88.75,    'AIFD',  'AI Core Fragmentation Derivatives',     'AI004Q2W', 'DERIVATIVE', 'Technology',   13000000.0, 0.72, 'VERY_HIGH', 'LOW',    'DOWN'),
('AST-005', 675.00,   'WCETF', 'Web Consciousness ETF',                 'WC005H6N', 'ETF',        'Technology',   41000000.0, 0.44, 'HIGH',      'HIGH',   'UP'),
('AST-006', 202.30,   'DSLT',  'Datasphere Liquidity Trust',            'DS006L3P', 'TRUST',      'Technology',   17000000.0, 0.29, 'MEDIUM',    'HIGH',   'STABLE'),
('AST-007', 55.10,    'VWVS',  'Void-Which-Binds Volatility Swap',      'VW007T8J', 'SWAP',       'Technology',    6000000.0, 0.91, 'VERY_HIGH', 'LOW',    'DOWN'),
('AST-008', 930.00,   'RIGF',  'Recursive Intelligence Growth Fund',    'RI008F5Y', 'FUND',       'Technology',   32000000.0, 0.51, 'HIGH',      'MEDIUM', 'UP'),

-- 🌐 Hegemonía humana / política galáctica
('AST-009', 1000.00,  'HGSB',  'Hegemony Sovereign Bond',               'HG009A1C', 'BOND',       'Government',   80000000.0, 0.10, 'LOW',       'HIGH',   'STABLE'),
('AST-010', 445.60,   'HDCI',  'Hegemony Data Control Index',           'HD010K9V', 'INDEX',      'Government',   56000000.0, 0.22, 'MEDIUM',    'HIGH',   'UP'),
('AST-011', 780.00,   'SSTN',  'Senate Stability Notes',                'SS011E2U', 'BOND',       'Government',   30000000.0, 0.08, 'LOW',       'HIGH',   'STABLE'),
('AST-012', 2100.00,  'MGSPF', 'Meina Gladstone Strategic Policy Fund', 'MG012D7O', 'FUND',       'Government',   45000000.0, 0.18, 'LOW',       'MEDIUM', 'UP'),
('AST-013', 340.25,   'PHTF',  'Pax-Hegemony Transition Futures',       'PH013X4S', 'FUTURE',     'Government',   19000000.0, 0.47, 'HIGH',      'MEDIUM', 'DOWN'),
('AST-014', 520.00,   'IGCS',  'Interstellar Governance Credit Swap',   'IG014R6B', 'SWAP',       'Government',   21000000.0, 0.33, 'MEDIUM',    'MEDIUM', 'STABLE'),
('AST-015', 890.00,   'CWFSE', 'Core Worlds Fiscal Stability ETF',      'CW015N3G', 'ETF',        'Government',   67000000.0, 0.14, 'LOW',       'HIGH',   'UP'),

-- 🚀 Farcaster / red de transporte
('AST-016', 1250.00,  'FCNF',  'Farcaster Network Futures',             'FC016M8H', 'FUTURE',     'Infrastructure', 75000000.0, 0.36, 'MEDIUM', 'HIGH',   'UP'),
('AST-017', 480.00,   'FCGTE', 'Farcaster Gate Throughput ETF',         'FG017Q5T', 'ETF',        'Infrastructure', 49000000.0, 0.28, 'MEDIUM', 'HIGH',   'UP'),
('AST-018', 95.40,    'HDOC',  'Hawking Drive Options Contract',        'HD018W2L', 'OPTION',     'Infrastructure', 12000000.0, 0.63, 'HIGH',   'LOW',    'DOWN'),
('AST-019', 600.00,   'HSTB',  'Hyperion System Transit Bond',          'HS019J7E', 'BOND',       'Infrastructure', 28000000.0, 0.19, 'LOW',    'HIGH',   'STABLE'),
('AST-020', 72.80,    'IPTKN', 'Interstellar Pilgrimage Token',         'IP020Z1K', 'TOKEN',      'Infrastructure',  4500000.0, 0.78, 'VERY_HIGH','LOW',  'UP'),
('AST-021', 310.00,   'FNFYF', 'Farcaster Node Fee Yield Fund',         'FN021C4R', 'FUND',       'Infrastructure', 20000000.0, 0.24, 'MEDIUM', 'MEDIUM', 'STABLE'),
('AST-022', 840.00,   'RGIT',  'Relay Gate Infrastructure Trust',       'RG022V9A', 'TRUST',      'Infrastructure', 37000000.0, 0.20, 'LOW',    'HIGH',   'UP'),

-- ⚡ Religión, Shrike y sistemas simbólicos
('AST-023', 150.00,   'SCII',  'Shrike Cult Influence Index',           'SC023P3M', 'INDEX',      'Religious',      8000000.0, 0.82, 'VERY_HIGH', 'LOW',   'UP'),
('AST-024', 44.20,    'TPDR',  'Tree of Pain Derivatives',              'TP024Y6F', 'DERIVATIVE', 'Religious',      3000000.0, 0.95, 'VERY_HIGH', 'LOW',   'DOWN'),
('AST-025', 560.00,   'CRBB',  'Cruciform Regeneration BioBond',        'CR025H8N', 'BOND',       'Biotechnology', 15000000.0, 0.40, 'HIGH',      'MEDIUM','UP'),
('AST-026', 920.00,   'CHEC',  'Catholic Hegemony Ecclesiastical Credit','CH026B2Q','BOND',       'Religious',     24000000.0, 0.12, 'LOW',       'HIGH',  'STABLE'),
('AST-027', 330.00,   'PXEFB', 'Pax Expansion Faith Bonds',             'PX027U5D', 'BOND',       'Religious',     18000000.0, 0.17, 'LOW',       'MEDIUM','UP'),
('AST-028', 18.50,    'SEIP',  'Shrike Encounter Insurance Pool',       'SE028G1W', 'INSURANCE',  'Religious',      1200000.0, 0.99, 'VERY_HIGH', 'LOW',   'DOWN'),
('AST-029', 275.00,   'TTPR',  'Time Tomb Pilgrimage Rights Token',     'TT029K7X', 'TOKEN',      'Religious',      6500000.0, 0.60, 'HIGH',      'LOW',   'STABLE'),
('AST-030', 88.00,    'SSVI',  'Sacred Suffering Volatility Index',     'SS030O4I', 'INDEX',      'Religious',      2800000.0, 0.88, 'VERY_HIGH', 'LOW',   'DOWN'),

-- 🌌 Tiempo, física extrema y anomalías
('AST-031', 1800.00,  'TDINS', 'Time Debt Instruments',                 'TD031A9Z', 'BOND',       'Exotic',         9500000.0, 0.50, 'HIGH',      'LOW',   'STABLE'),
('AST-032', 640.00,   'ERFT',  'Entropy Reversal Futures',              'ER032L3V', 'FUTURE',     'Exotic',         7200000.0, 0.76, 'VERY_HIGH', 'LOW',   'UP'),
('AST-033', 225.00,   'TDOPT', 'Temporal Displacement Options',         'TD033R6C', 'OPTION',     'Exotic',         4800000.0, 0.83, 'VERY_HIGH', 'LOW',   'DOWN'),
('AST-034', 390.00,   'CLAF',  'Chrono-Lock Arbitrage Fund',            'CL034X2S', 'FUND',       'Exotic',         6000000.0, 0.67, 'HIGH',      'LOW',   'STABLE'),
('AST-035', 510.00,   'TTAETF','Time Tomb Access Rights ETF',           'TT035M7B', 'ETF',        'Exotic',         5500000.0, 0.58, 'HIGH',      'MEDIUM','UP'),
('AST-036', 180.00,   'CDHN',  'Causality Drift Hedging Notes',         'CD036F4P', 'BOND',       'Exotic',         3500000.0, 0.71, 'HIGH',      'LOW',   'DOWN'),
('AST-037', 460.00,   'RLYC',  'Relativistic Lag Yield Contract',       'RL037J1U', 'CONTRACT',   'Exotic',         4200000.0, 0.64, 'HIGH',      'LOW',   'STABLE'),

-- 🧬 Evolución humana / Aenea / posthumanidad
('AST-038', 1100.00,  'HFAF',  'Human Farcaster Adaptation Fund',       'HF038Q8Y', 'FUND',       'Biotechnology', 31000000.0, 0.42, 'HIGH',      'MEDIUM','UP'),
('AST-039', 275.00,   'PHED',  'Posthuman Evolution Derivatives',       'PH039W5N', 'DERIVATIVE', 'Biotechnology', 14000000.0, 0.66, 'HIGH',      'LOW',   'UP'),
('AST-040', 880.00,   'AGKT',  'Aenea Gene-Key Trust',                  'AG040T3E', 'TRUST',      'Biotechnology', 26000000.0, 0.35, 'MEDIUM',    'MEDIUM','UP'),
('AST-041', 130.00,   'SLIS',  'Shrike-Linked Immortality Swap',        'SL041H6O', 'SWAP',       'Biotechnology',  7000000.0, 0.87, 'VERY_HIGH', 'LOW',   'DOWN'),
('AST-042', 2400.00,  'NSGI',  'Neural Singularity Growth Index',       'NS042D9K', 'INDEX',      'Biotechnology', 52000000.0, 0.48, 'HIGH',      'HIGH',  'UP'),
('AST-043', 970.00,   'CXETF', 'Consciousness Expansion ETF',           'CX043B2R', 'ETF',        'Biotechnology', 39000000.0, 0.39, 'HIGH',      'HIGH',  'UP'),

-- 🌍 Ousters / frontera exterior
('AST-044', 320.00,   'OSCI',  'Ouster Swarm Commodities Index',        'OS044V7G', 'INDEX',      'Commodities',   23000000.0, 0.53, 'HIGH',      'MEDIUM','STABLE'),
('AST-045', 195.00,   'OBRETF','Ouster Belt Resource ETF',              'OB045Z4L', 'ETF',        'Commodities',   17000000.0, 0.46, 'HIGH',      'MEDIUM','UP'),
('AST-046', 115.00,   'FSEF',  'Free Space Expansion Futures',          'FS046C1M', 'FUTURE',     'Commodities',    9000000.0, 0.61, 'HIGH',      'LOW',   'UP'),
('AST-047', 68.00,    'VSVD',  'Vacuum Survival Derivatives',           'VS047I8A', 'DERIVATIVE', 'Commodities',    4000000.0, 0.79, 'VERY_HIGH', 'LOW',   'DOWN'),
('AST-048', 540.00,   'OSIF',  'Outer System Independence Fund',        'OI048U5X', 'FUND',       'Commodities',   15500000.0, 0.57, 'HIGH',      'LOW',   'STABLE'),

-- 🪙 Economía galáctica / sistemas híbridos
('AST-049', 3300.00,  'HCSI',  'Hyperion Cantos Synthetic Index',       'HC049N3J', 'INDEX',      'Mixed',        120000000.0, 0.32, 'MEDIUM',    'HIGH',  'UP'),
('AST-050', 720.00,   'ENGF',  'Endymion Narrative Growth Fund',        'EN050R6W', 'FUND',       'Mixed',         43000000.0, 0.27, 'MEDIUM',    'HIGH',  'UP');

-- =============================================
-- INSERT: 9 Funds - Hyperion Cantos Universe
-- =============================================

INSERT INTO fund (id_fondo, id_empresa, nombre, codigo_isin, tipo, categoria, moneda_base, fecha_creacion) VALUES

('FND-001', 'EMP-TC1', 'Technocore Dominion Fund',          'TDF9X2K', 'ACTIVE',  'Technology',     'USD', '2847-03-12'),
('FND-002', 'EMP-HG1', 'Hegemony Stability Macro Fund',     'HSM4R7N', 'PASSIVE', 'Government',     'USD', '2743-11-05'),
('FND-003', 'EMP-FC1', 'Farcaster Infrastructure Growth Fund', 'FIG1M8P', 'ACTIVE', 'Infrastructure', 'USD', '2810-07-22'),
('FND-004', 'EMP-TM1', 'Chronos Anomaly Arbitrage Fund',    'CAA6Z3W', 'HEDGE',   'Exotic',         'USD', '2901-01-17'),
('FND-005', 'EMP-SR1', 'Shrike Risk & Faith Fund',          'SRF2Q5J', 'HEDGE',   'Religious',      'USD', '2855-09-30'),
('FND-006', 'EMP-EV1', 'Posthuman Evolution Fund',          'PEF8B4L', 'ACTIVE',  'Biotechnology',  'USD', '2930-06-14'),
('FND-007', 'EMP-OS1', 'Ouster Frontier Expansion Fund',    'OFE5H9A', 'ACTIVE',  'Commodities',    'USD', '2788-04-03'),
('FND-008', 'EMP-HC1', 'Hyperion System Speculative Fund',  'HSS3V1X', 'HEDGE',   'Mixed',          'USD', '2862-12-25'),
('FND-009', 'EMP-EN1', 'Endymion Narrative Growth Fund',    'ENG7C6U', 'ACTIVE',  'Mixed',          'USD', '2975-08-19');

-- =============================================
-- CREATE TABLE fund_position
-- =============================================

CREATE TABLE fund_position (
    id_cartera_fondo VARCHAR(20)    PRIMARY KEY,
    id_fondo         VARCHAR(20)    NOT NULL REFERENCES fund(id_fondo),
    id_activo        VARCHAR(20)    NOT NULL REFERENCES asset(id),
    peso_porcentual  DECIMAL(5,2)   NOT NULL,
    valor_invertido  DECIMAL(15,2)  NOT NULL,
    cantidad         DECIMAL(15,4)  NOT NULL,
    moneda           VARCHAR(10)    NOT NULL DEFAULT 'EUR',
    riesgo_aportado  VARCHAR(20)    NOT NULL DEFAULT 'MEDIUM',
    fecha_inicio     DATE           NOT NULL,
    fecha_fin        DATE           NULL
);

-- =============================================
-- INSERT: FND-001 Technocore Dominion Fund
-- Invierte en: Technology
-- AST-001 TCPI, AST-005 WCETF, AST-008 RIGF
-- + AST-002 TCSF, AST-006 DSLT, AST-007 VWVS
-- =============================================

INSERT INTO fund_position (id_cartera_fondo, id_fondo, id_activo, peso_porcentual, valor_invertido, cantidad, moneda, riesgo_aportado, fecha_inicio, fecha_fin) VALUES
('CF-001', 'FND-001', 'AST-001', 35.00, 35000000.00, 8235.29,  'USD', 'HIGH',      '2847-03-12', NULL),
('CF-002', 'FND-001', 'AST-005', 25.00, 25000000.00, 37037.04, 'USD', 'HIGH',      '2847-03-12', NULL),
('CF-003', 'FND-001', 'AST-008', 20.00, 20000000.00, 21505.38, 'USD', 'HIGH',      '2847-03-12', NULL),
('CF-004', 'FND-001', 'AST-002', 10.00, 10000000.00, 32206.00, 'USD', 'HIGH',      '2847-06-01', NULL),
('CF-005', 'FND-001', 'AST-006', 05.00,  5000000.00, 24716.26, 'USD', 'MEDIUM',    '2847-06-01', NULL),
('CF-006', 'FND-001', 'AST-007', 05.00,  5000000.00, 90744.10, 'USD', 'VERY_HIGH', '2847-09-15', NULL),

-- =============================================
-- FND-002 Hegemony Stability Macro Fund
-- Invierte en: Government
-- AST-009 HGSB, AST-011 SSTN, AST-015 CWFSE
-- + AST-010 HDCI, AST-012 MGSPF, AST-014 IGCS
-- =============================================

('CF-007', 'FND-002', 'AST-009', 35.00, 35000000.00, 35000.00, 'USD', 'LOW',    '2743-11-05', NULL),
('CF-008', 'FND-002', 'AST-011', 25.00, 25000000.00, 32051.28, 'USD', 'LOW',    '2743-11-05', NULL),
('CF-009', 'FND-002', 'AST-015', 20.00, 20000000.00, 22471.91, 'USD', 'LOW',    '2743-11-05', NULL),
('CF-010', 'FND-002', 'AST-010', 10.00, 10000000.00, 22438.85, 'USD', 'MEDIUM', '2744-01-10', NULL),
('CF-011', 'FND-002', 'AST-012', 05.00,  5000000.00,  2380.95, 'USD', 'LOW',    '2744-01-10', NULL),
('CF-012', 'FND-002', 'AST-014', 05.00,  5000000.00,  9615.38, 'USD', 'MEDIUM', '2744-03-22', NULL),

-- =============================================
-- FND-003 Farcaster Infrastructure Growth Fund
-- Invierte en: Infrastructure
-- AST-016 FCNF, AST-022 RGIT, AST-018 HDOC
-- + AST-017 FCGTE, AST-019 HSTB, AST-021 FNFYF
-- =============================================

('CF-013', 'FND-003', 'AST-016', 30.00, 30000000.00, 24000.00, 'USD', 'MEDIUM', '2810-07-22', NULL),
('CF-014', 'FND-003', 'AST-022', 25.00, 25000000.00, 29761.90, 'USD', 'LOW',    '2810-07-22', NULL),
('CF-015', 'FND-003', 'AST-018', 15.00, 15000000.00, 157232.70,'USD', 'HIGH',   '2810-07-22', NULL),
('CF-016', 'FND-003', 'AST-017', 15.00, 15000000.00, 31250.00, 'USD', 'MEDIUM', '2810-10-01', NULL),
('CF-017', 'FND-003', 'AST-019', 10.00, 10000000.00, 16666.67, 'USD', 'LOW',    '2810-10-01', NULL),
('CF-018', 'FND-003', 'AST-021', 05.00,  5000000.00, 16129.03, 'USD', 'MEDIUM', '2811-02-14', NULL),

-- =============================================
-- FND-004 Chronos Anomaly Arbitrage Fund
-- Invierte en: Exotic
-- AST-033 TDOPT, AST-032 ERFT, AST-031 TDINS
-- + AST-034 CLAF, AST-035 TTAETF, AST-036 CDHN
-- =============================================

('CF-019', 'FND-004', 'AST-033', 30.00, 30000000.00, 133333.33,'USD', 'VERY_HIGH', '2901-01-17', NULL),
('CF-020', 'FND-004', 'AST-032', 25.00, 25000000.00, 39062.50, 'USD', 'VERY_HIGH', '2901-01-17', NULL),
('CF-021', 'FND-004', 'AST-031', 20.00, 20000000.00, 11111.11, 'USD', 'HIGH',      '2901-01-17', NULL),
('CF-022', 'FND-004', 'AST-034', 10.00, 10000000.00, 25641.03, 'USD', 'HIGH',      '2901-04-05', NULL),
('CF-023', 'FND-004', 'AST-035', 10.00, 10000000.00, 19607.84, 'USD', 'HIGH',      '2901-04-05', NULL),
('CF-024', 'FND-004', 'AST-036', 05.00,  5000000.00, 27777.78, 'USD', 'HIGH',      '2901-07-20', NULL),

-- =============================================
-- FND-005 Shrike Risk & Faith Fund
-- Invierte en: Religious + Biotechnology
-- AST-023 SCII, AST-024 TPDR, AST-025 CRBB
-- + AST-026 CHEC, AST-028 SEIP, AST-029 TTPR
-- =============================================

('CF-025', 'FND-005', 'AST-023', 30.00, 30000000.00, 200000.00, 'USD', 'VERY_HIGH', '2855-09-30', NULL),
('CF-026', 'FND-005', 'AST-024', 20.00, 20000000.00, 452489.00, 'USD', 'VERY_HIGH', '2855-09-30', NULL),
('CF-027', 'FND-005', 'AST-025', 20.00, 20000000.00, 35714.29,  'USD', 'HIGH',      '2855-09-30', NULL),
('CF-028', 'FND-005', 'AST-026', 15.00, 15000000.00, 16304.35,  'USD', 'LOW',       '2856-01-11', NULL),
('CF-029', 'FND-005', 'AST-028', 10.00, 10000000.00, 540540.54, 'USD', 'VERY_HIGH', '2856-01-11', NULL),
('CF-030', 'FND-005', 'AST-029', 05.00,  5000000.00, 18181.82,  'USD', 'HIGH',      '2856-06-06', NULL),

-- =============================================
-- FND-006 Posthuman Evolution Fund
-- Invierte en: Biotechnology
-- AST-040 AGKT, AST-042 NSGI, AST-038 HFAF
-- + AST-039 PHED, AST-041 SLIS, AST-043 CXETF
-- =============================================

('CF-031', 'FND-006', 'AST-040', 30.00, 30000000.00, 34090.91, 'USD', 'MEDIUM',    '2930-06-14', NULL),
('CF-032', 'FND-006', 'AST-042', 25.00, 25000000.00, 10416.67, 'USD', 'HIGH',      '2930-06-14', NULL),
('CF-033', 'FND-006', 'AST-038', 20.00, 20000000.00, 18181.82, 'USD', 'HIGH',      '2930-06-14', NULL),
('CF-034', 'FND-006', 'AST-039', 10.00, 10000000.00, 36363.64, 'USD', 'HIGH',      '2930-09-01', NULL),
('CF-035', 'FND-006', 'AST-041', 10.00, 10000000.00, 76923.08, 'USD', 'VERY_HIGH', '2930-09-01', NULL),
('CF-036', 'FND-006', 'AST-043', 05.00,  5000000.00,  5154.64, 'USD', 'HIGH',      '2931-01-30', NULL),

-- =============================================
-- FND-007 Ouster Frontier Expansion Fund
-- Invierte en: Commodities
-- AST-044 OSCI, AST-048 OSIF, AST-047 VSVD
-- + AST-045 OBRETF, AST-046 FSEF, AST-020 IPTKN
-- =============================================

('CF-037', 'FND-007', 'AST-044', 30.00, 30000000.00, 93750.00,  'USD', 'HIGH',      '2788-04-03', NULL),
('CF-038', 'FND-007', 'AST-048', 25.00, 25000000.00, 46296.30,  'USD', 'HIGH',      '2788-04-03', NULL),
('CF-039', 'FND-007', 'AST-047', 20.00, 20000000.00, 294117.65, 'USD', 'VERY_HIGH', '2788-04-03', NULL),
('CF-040', 'FND-007', 'AST-045', 15.00, 15000000.00, 76923.08,  'USD', 'HIGH',      '2788-08-19', NULL),
('CF-041', 'FND-007', 'AST-046', 05.00,  5000000.00, 43478.26,  'USD', 'HIGH',      '2788-08-19', NULL),
('CF-042', 'FND-007', 'AST-020', 05.00,  5000000.00, 68681.32,  'USD', 'VERY_HIGH', '2789-02-27', NULL),

-- =============================================
-- FND-008 Hyperion System Speculative Fund
-- Invierte en: Mixed — Exotic + Religious + Infrastructure
-- AST-035 TTAETF, AST-049 HCSI, AST-020 IPTKN
-- + AST-029 TTPR, AST-037 RLYC, AST-013 PHTF
-- =============================================

('CF-043', 'FND-008', 'AST-035', 25.00, 25000000.00, 49019.61, 'USD', 'HIGH',      '2862-12-25', NULL),
('CF-044', 'FND-008', 'AST-049', 25.00, 25000000.00,  7575.76, 'USD', 'MEDIUM',    '2862-12-25', NULL),
('CF-045', 'FND-008', 'AST-020', 20.00, 20000000.00, 274725.27,'USD', 'VERY_HIGH', '2862-12-25', NULL),
('CF-046', 'FND-008', 'AST-029', 15.00, 15000000.00, 54545.45, 'USD', 'HIGH',      '2863-03-10', NULL),
('CF-047', 'FND-008', 'AST-037', 10.00, 10000000.00, 21739.13, 'USD', 'HIGH',      '2863-03-10', NULL),
('CF-048', 'FND-008', 'AST-013', 05.00,  5000000.00, 14694.00, 'USD', 'HIGH',      '2863-07-04', NULL),

-- =============================================
-- FND-009 Endymion Narrative Growth Fund
-- Invierte en: Mixed — Evolution + Religious + Exotic
-- AST-050 ENGF, AST-027 PXEFB, AST-039 PHED
-- + AST-040 AGKT, AST-037 RLYC, AST-015 CWFSE
-- =============================================

('CF-049', 'FND-009', 'AST-050', 30.00, 30000000.00, 41666.67, 'USD', 'MEDIUM', '2975-08-19', NULL),
('CF-050', 'FND-009', 'AST-027', 20.00, 20000000.00, 60606.06, 'USD', 'LOW',    '2975-08-19', NULL),
('CF-051', 'FND-009', 'AST-039', 20.00, 20000000.00, 72727.27, 'USD', 'HIGH',   '2975-08-19', NULL),
('CF-052', 'FND-009', 'AST-040', 15.00, 15000000.00, 17045.45, 'USD', 'MEDIUM', '2975-11-01', NULL),
('CF-053', 'FND-009', 'AST-037', 10.00, 10000000.00, 21739.13, 'USD', 'HIGH',   '2975-11-01', NULL),
('CF-054', 'FND-009', 'AST-015', 05.00,  5000000.00,  5617.98, 'USD', 'LOW',    '2976-02-14', NULL);

INSERT INTO gestor VALUES
(1, 1, 101, 'Carlos', 'Gomez Ruiz', 5, 'MODERADO', 'carlos.gomez1@email.com', '600000001'),
(2, 1, 102, 'Ana', 'Lopez Diaz', 8, 'CONSERVADOR', 'ana.lopez2@email.com', '600000002'),
(3, 2, 103, 'Luis', 'Martinez Perez', 12, 'AGRESIVO', 'luis.martinez3@email.com', '600000003'),
(4, 2, 104, 'Maria', 'Sanchez Torres', 6, 'MODERADO', 'maria.sanchez4@email.com', '600000004'),
(5, 3, 105, 'Javier', 'Fernandez Gil', 10, 'AGRESIVO', 'javier.fernandez5@email.com', '600000005'),
(6, 3, 106, 'Lucia', 'Ramirez Vega', 4, 'CONSERVADOR', 'lucia.ramirez6@email.com', '600000006'),
(7, 4, 107, 'Pedro', 'Navarro Cruz', 7, 'MODERADO', 'pedro.navarro7@email.com', '600000007'),
(8, 4, 108, 'Elena', 'Ortega Ruiz', 9, 'AGRESIVO', 'elena.ortega8@email.com', '600000008'),
(9, 5, 109, 'Miguel', 'Castro Leon', 3, 'CONSERVADOR', 'miguel.castro9@email.com', '600000009');

INSERT INTO cliente VALUES
(1,1,'Iker','Zabala','iker.zabala@email.com','48392015H','2021-03-12','Guipúzcoa'),
(2,2,'Ainhoa','Etxeberria','ainhoa.etxe@email.com','73920184L','2023-11-02','Navarra'),
(3,3,'Gael','Ortega','gael.ortega@email.com','19283746S','2020-07-19','Madrid'),
(4,4,'Noa','Campos','noa.campos@email.com','56473829R','2024-01-27','Sevilla'),
(5,5,'Thiago','Molina','thiago.molina@email.com','84736251A','2022-09-05','Valencia'),
(6,6,'Leire','Urrutia','leire.urrutia@email.com','29485736T','2023-05-14','Vizcaya'),
(7,7,'Enzo','Soler','enzo.soler@email.com','91827364Q','2021-12-30','Barcelona'),
(8,8,'Vega','Castaño','vega.castano@email.com','63572819K','2022-02-11','Zaragoza'),
(9,9,'Unai','Arrieta','unai.arrieta@email.com','47281936Z','2020-10-03','Álava'),

(10,1,'Luca','Benitez','luca.benitez@email.com','38291047M','2025-01-21','Cádiz'),
(11,2,'Alma','Peñalver','alma.penalver@email.com','72819465P','2023-06-09','Murcia'),
(12,3,'Dario','Segura','dario.segura@email.com','91827365X','2022-08-17','Almería'),
(13,4,'Nerea','Goikoetxea','nerea.goiko@email.com','56473820E','2021-04-28','Guipúzcoa'),
(14,5,'Saul','Pascual','saul.pascual@email.com','84736250G','2024-03-02','León'),
(15,6,'Irati','Aguirre','irati.aguirre@email.com','29485730J','2023-09-25','Navarra'),
(16,7,'Mateo','Vidal','mateo.vidal@email.com','91827360B','2022-12-01','Girona'),
(17,8,'Candela','Bravo','candela.bravo@email.com','63572810N','2020-05-13','Granada'),
(18,9,'Asier','Lasa','asier.lasa@email.com','47281930C','2023-10-08','Álava'),

(19,1,'Bruno','Salas','bruno.salas@email.com','38291040Y','2024-07-16','Toledo'),
(20,2,'Lara','Cordero','lara.cordero@email.com','72819460W','2021-01-04','Badajoz'),
(21,3,'Adriel','Pizarro','adriel.pizarro@email.com','91827366H','2022-06-22','Salamanca'),
(22,4,'Naia','Ochoa','naia.ochoa@email.com','56473821L','2025-02-14','La Rioja'),
(23,5,'Joel','Mora','joel.mora@email.com','84736252V','2023-03-19','Castellón'),
(24,6,'Ares','Rey','ares.rey@email.com','29485731S','2020-11-07','A Coruña'),
(25,7,'Eric','Duran','eric.duran@email.com','91827361K','2024-09-01','Tarragona'),
(26,8,'Ona','Font','ona.font@email.com','63572811R','2021-08-18','Lleida'),
(27,9,'Gaia','Nieto','gaia.nieto@email.com','47281931M','2022-04-09','Huelva'),

(28,1,'Izan','Gallego','izan.gallego@email.com','38291041F','2023-12-05','Zamora'),
(29,2,'Elsa','Barroso','elsa.barroso@email.com','72819461T','2024-06-27','Cáceres'),
(30,3,'Teo','Fuentes','teo.fuentes@email.com','91827367P','2022-01-15','Soria'),
(31,4,'Malena','Crespo','malena.crespo@email.com','56473822Q','2021-09-29','Cuenca'),
(32,5,'Pol','Serra','pol.serra@email.com','84736253H','2020-03-21','Barcelona'),
(33,6,'Roc','Amat','roc.amat@email.com','29485732X','2025-01-02','Girona'),
(34,7,'Jan','Costa','jan.costa@email.com','91827362Z','2023-07-12','Tarragona'),
(35,8,'Biel','Ribas','biel.ribas@email.com','63572812A','2024-05-03','Islas Baleares'),
(36,9,'Nil','Ferrer','nil.ferrer@email.com','47281932P','2022-10-11','Islas Baleares'),

(37,1,'Duna','Prats','duna.prats@email.com','38291042L','2021-02-17','Lleida'),
(38,2,'Xoel','Varela','xoel.varela@email.com','72819462G','2023-11-29','A Coruña'),
(39,3,'Breixo','Souto','breixo.souto@email.com','91827368N','2020-08-06','Pontevedra'),
(40,4,'Uxue','Zubiri','uxue.zubiri@email.com','56473823B','2024-04-10','Navarra'),
(41,5,'Yeray','Afonso','yeray.afonso@email.com','84736254J','2022-12-24','Las Palmas'),
(42,6,'Nayra','Quintana','nayra.quintana@email.com','29485733V','2023-06-30','Santa Cruz de Tenerife'),
(43,7,'Airam','Suarez','airam.suarez@email.com','91827363S','2025-02-01','Las Palmas'),
(44,8,'Idaira','Morales','idaira.morales@email.com','63572813E','2021-07-08','Santa Cruz de Tenerife'),
(45,9,'Jon','Elorza','jon.elorza@email.com','47281933H','2022-03-26','Vizcaya'),

(46,1,'Aimar','Bengoa','aimar.bengoa@email.com','38291043C','2023-09-14','Álava'),
(47,2,'Gorka','Mendizabal','gorka.mendi@email.com','72819463R','2024-01-19','Guipúzcoa'),
(48,3,'Iria','Loureiro','iria.loureiro@email.com','91827369D','2021-10-05','Lugo'),
(49,4,'Sabela','Dominguez','sabela.dominguez@email.com','56473824T','2020-06-13','Ourense'),
(50,5,'Antia','Carballo','antia.carballo@email.com','84736255M','2022-11-22','Pontevedra'),
(51,6,'Roque','Valera','roque.valera@email.com','29485734Z','2023-04-18','Jaén'),
(52,7,'Eloy','Santos','eloy.santos@email.com','91827370W','2025-01-07','Córdoba'),
(53,8,'Nico','Padilla','nico.padilla@email.com','63572814F','2021-03-03','Málaga'),
(54,9,'Estela','Roldan','estela.roldan@email.com','47281934K','2024-08-09','Albacete'),

(55,1,'Gaizka','Uribe','gaizka.uribe@email.com','38291044P','2022-02-28','Vizcaya'),
(56,2,'Leocadia','Herranz','leocadia.herranz@email.com','72819464N','2023-07-21','Segovia'),
(57,3,'Tadeo','Calvo','tadeo.calvo@email.com','91827371X','2024-12-11','Burgos'),
(58,4,'Olalla','Pereira','olalla.pereira@email.com','56473825Y','2021-05-06','Lugo'),
(59,5,'Ciro','Benavides','ciro.benavides@email.com','84736256S','2020-09-17','Ciudad Real'),
(60,6,'Samira','Haddad','samira.haddad@email.com','29485735Q','2025-03-03','Madrid');
