-- ============================================================
-- DATA.SQL (Postgres / schema public)
-- Insert ONLY if the table is void
-- ============================================================


-- ============================================================
-- 1) OFFICES
-- ============================================================
INSERT INTO public.offices ("name") VALUES
    ('milan'),
    ('rome-hq'),
    ('turin'),
    ('naples'),
    ('florence'),
    ('bologna'),
    ('venice'),
    ('genoa'),
    ('palermo'),
    ('cagliari'),
    ('verona'),
    ('padua'),
    ('parma'),
    ('trieste'),
    ('brescia'),
    ('remote-north'),
    ('remote-south'),
    ('warehouse-east'),
    ('warehouse-west'),
    ('training-center')

ON CONFLICT (id_office) DO NOTHING;


-- ============================================================
-- 2) ASSET TYPES
-- ============================================================
INSERT INTO public.asset_types (asset_type_name, asset_type_description) VALUES
     ('laptop',         'portable-computer'),
     ('monitor',        'external-display'),
     ('desktop',        'fixed-workstation'),
     ('printer',        'office-printer'),
     ('docking-station', NULL),
     ('keyboard',       'input-device'),
     ('mouse',           NULL),
     ('headset',        'audio-device'),
     ('router',         'network-device'),
     ('switch',          NULL),
     ('tablet',         'portable-tablet'),
     ('phone',           NULL),
     ('scanner',        'document-scanner'),
     ('ups',            'power-backup'),
     ('projector',       NULL),
     ('webcam',         'video-device'),
     ('speaker',         NULL),
     ('server',         'datacenter-server'),
     ('nas',             NULL),
     ('badge-reader',   'security-device')
ON CONFLICT (asset_type_name) DO NOTHING;

-- ============================================================
-- 3) ASSETS
--    (resolve FK: asset_type_name -> id_asset_type, office name -> id_office)
-- ============================================================
WITH seed_assets(purchase_date, serial_number, asset_type_name, office_name) AS (
    VALUES
        (DATE '2025-02-10', 'LAP-DEV-001', 'laptop',         'milan'),
        (DATE '2025-03-05', 'LAP-DEV-002', 'laptop',         'rome-hq'),
        (DATE '2024-11-18', 'MON-OPS-001', 'monitor',        'turin'),
        (DATE '2024-07-22', 'DES-FIN-001', 'desktop',        'naples'),
        (NULL,              'PRN-HR-001',  'printer',        'florence'),
        (DATE '2025-01-15', 'DOC-IT-001',  'docking-station','bologna'),
        (DATE '2023-12-01', 'KBD-OPS-001', 'keyboard',       'venice'),
        (NULL,              'MOU-OPS-001', 'mouse',          'genoa'),
        (DATE '2025-05-09', 'HDS-SLS-001', 'headset',        'palermo'),
        (NULL,              'RTR-NET-001', 'router',         'cagliari'),
        (DATE '2024-02-28', 'SWT-NET-001', 'switch',         'verona'),
        (DATE '2025-06-30', 'TAB-MKT-001', 'tablet',         'padua'),
        (NULL,              'PHN-MKT-001', 'phone',          'parma'),
        (DATE '2024-10-10', 'SCN-ADM-001', 'scanner',        'trieste'),
        (NULL,              'UPS-ADM-001', 'ups',            'brescia'),
        (DATE '2024-08-08', 'PRO-TRN-001', 'projector',      'milan'),
        (DATE '2025-09-14', 'WBC-DEV-001', 'webcam',         'rome-hq'),
        (NULL,              'SPK-SLS-001', 'speaker',        'turin'),
        (DATE '2023-06-01', 'SRV-DC-001',  'server',         'naples'),
        (NULL,              'NAS-DC-001',  'nas',            'florence')
)
INSERT INTO public.assets (purchase_date, serial_number, id_asset_type, id_office)
SELECT
    s.purchase_date,
    s.serial_number,
    at.id_asset_type,
    o.id_office
FROM seed_assets s
         JOIN public.asset_types at
ON at.asset_type_name = s.asset_type_name
    JOIN public.offices o
    ON o."name" = s.office_name
    ON CONFLICT (serial_number) DO NOTHING;


-- ============================================================
-- 4) SOFTWARE LICENSES
-- ============================================================
INSERT INTO public.software_licenses (software_name, expiration_date, max_installations) VALUES
     ('adobe-acrobat-pro',        DATE '2026-01-20',  3),
     ('jetbrains-datagrip',       DATE '2026-01-25', 10),
     ('microsoft-office-365',     DATE '2026-02-05', NULL),
     ('windows-11-pro',           DATE '2026-02-12', 25),
     ('intellij-idea-ultimate',   DATE '2026-02-10',  5),
     ('visual-studio-pro',        DATE '2026-01-18',  8),
     ('docker-desktop',           DATE '2026-02-01', NULL),
     ('postgresql-client',        DATE '2026-01-30', 50),
     ('slack-standard',           DATE '2026-02-08',100),
     ('zoom-pro',                 DATE '2026-02-11', 20),
     ('figma-professional',       DATE '2026-03-15', NULL),
     ('jira-software',            DATE '2026-04-01', 50),
     ('confluence-standard',      DATE '2026-06-30', 50),
     ('github-enterprise',        DATE '2026-09-30',200),
     ('aws-console-access',       DATE '2026-12-31', NULL),
     ('autocad',                  DATE '2027-01-31',  2),
     ('adobe-photoshop',          DATE '2026-08-15',  4),
     ('adobe-illustrator',        DATE '2026-08-20',  4),
     ('vmware-workstation',       DATE '2026-05-20', NULL),
     ('kaspersky-endpoint',       DATE '2026-07-10', 30)
ON CONFLICT (software_name) DO NOTHING;


-- ============================================================
-- 5) ASSETS_LICENSES (JOIN TABLE)
--    (resolve FK by serial_number + software_name)
-- ============================================================
WITH seed_links(software_name, serial_number) AS (
    VALUES
        ('jetbrains-datagrip',     'LAP-DEV-001'),
        ('jetbrains-datagrip',     'LAP-DEV-002'),
        ('jetbrains-datagrip',     'DOC-IT-001'),
        ('intellij-idea-ultimate', 'LAP-DEV-001'),
        ('intellij-idea-ultimate', 'LAP-DEV-002'),
        ('microsoft-office-365',   'LAP-DEV-002'),
        ('microsoft-office-365',   'TAB-MKT-001'),
        ('windows-11-pro',         'LAP-DEV-002'),
        ('adobe-acrobat-pro',      'MON-OPS-001'),
        ('adobe-acrobat-pro',      'SCN-ADM-001'),
        ('docker-desktop',         'DOC-IT-001'),
        ('postgresql-client',      'DOC-IT-001'),
        ('slack-standard',         'TAB-MKT-001'),
        ('slack-standard',         'SPK-SLS-001'),
        ('zoom-pro',               'PRO-TRN-001'),
        ('jira-software',          'DES-FIN-001'),
        ('autocad',                'DES-FIN-001'),
        ('github-enterprise',      'SRV-DC-001'),
        ('vmware-workstation',     'SRV-DC-001'),
        ('kaspersky-endpoint',     'NAS-DC-001')
)
INSERT INTO public.assets_licenses (license_id, asset_id)
SELECT
    l.id_software_license AS license_id,
    a.id_asset            AS asset_id
FROM seed_links s
         JOIN public.software_licenses l
              ON l.software_name = s.software_name
         JOIN public.assets a
              ON a.serial_number = s.serial_number
ON CONFLICT DO NOTHING;

