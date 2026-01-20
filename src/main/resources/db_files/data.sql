-- ============================================================
-- DATA.SQL (Postgres / schema public)
-- Insert ONLY if the table is void
-- ============================================================

INSERT INTO public.offices (id_office, "name") VALUES
   ( 1, 'milan'),
   ( 2, 'rome-hq'),
   ( 3, 'turin'),
   ( 4, 'naples'),
   ( 5, 'florence'),
   ( 6, 'bologna'),
   ( 7, 'venice'),
   ( 8, 'genoa'),
   ( 9, 'palermo'),
   (10, 'cagliari'),
   (11, 'verona'),
   (12, 'padua'),
   (13, 'parma'),
   (14, 'trieste'),
   (15, 'brescia'),
   (16, 'remote-north'),
   (17, 'remote-south'),
   (18, 'warehouse-east'),
   (19, 'warehouse-west'),
   (20, 'training-center')
ON CONFLICT (id_office) DO NOTHING;


INSERT INTO public.asset_types (id_asset_type, asset_type_description, asset_type_name) VALUES
    ( 1, 'portable-computer',   'laptop'),
    ( 2, 'external-display',    'monitor'),
    ( 3, 'fixed-workstation',   'desktop'),
    ( 4, 'office-printer',      'printer'),
    ( 5, NULL,                  'docking-station'),
    ( 6, 'input-device',        'keyboard'),
    ( 7, NULL,                  'mouse'),
    ( 8, 'audio-device',        'headset'),
    ( 9, 'network-device',      'router'),
    (10, NULL,                  'switch'),
    (11, 'portable-tablet',     'tablet'),
    (12, NULL,                  'phone'),
    (13, 'document-scanner',    'scanner'),
    (14, 'power-backup',        'ups'),
    (15, NULL,                  'projector'),
    (16, 'video-device',        'webcam'),
    (17, NULL,                  'speaker'),
    (18, 'datacenter-server',   'server'),
    (19, NULL,                  'nas'),
    (20, 'security-device',     'badge-reader')
ON CONFLICT (id_asset_type) DO NOTHING;

INSERT INTO public.assets (id_asset, purchase_date, serial_number, id_asset_type, id_office) VALUES
    ( 1, DATE '2025-02-10', 'LAP-DEV-001',  1,  1),
    ( 2, DATE '2025-03-05', 'LAP-DEV-002',  1,  2),
    ( 3, DATE '2024-11-18', 'MON-OPS-001',  2,  3),
    ( 4, DATE '2024-07-22', 'DES-FIN-001',  3,  4),
    ( 5, NULL,              'PRN-HR-001',   4,  5),
    ( 6, DATE '2025-01-15', 'DOC-IT-001',   5,  6),
    ( 7, DATE '2023-12-01', 'KBD-OPS-001',  6,  7),
    ( 8, NULL,              'MOU-OPS-001',  7,  8),
    ( 9, DATE '2025-05-09', 'HDS-SLS-001',  8,  9),
    (10, NULL,              'RTR-NET-001',  9, 10),
    (11, DATE '2024-02-28', 'SWT-NET-001', 10, 11),
    (12, DATE '2025-06-30', 'TAB-MKT-001', 11, 12),
    (13, NULL,              'PHN-MKT-001', 12, 13),
    (14, DATE '2024-10-10', 'SCN-ADM-001', 13, 14),
    (15, NULL,              'UPS-ADM-001', 14, 15),
    (16, DATE '2024-08-08', 'PRO-TRN-001', 15,  1),
    (17, DATE '2025-09-14', 'WBC-DEV-001', 16,  2),
    (18, NULL,              'SPK-SLS-001', 17,  3),
    (19, DATE '2023-06-01', 'SRV-DC-001',  18,  4),
    (20, NULL,              'NAS-DC-001',  19,  5)
ON CONFLICT (id_asset) DO NOTHING;

INSERT INTO public.software_licenses (id_software_license, expiration_date, max_installations, software_name) VALUES
    ( 1, DATE '2026-01-20',  3,   'adobe-acrobat-pro'),
    ( 2, DATE '2026-01-25', 10,   'jetbrains-datagrip'),
    ( 3, DATE '2026-02-05', NULL, 'microsoft-office-365'),
    ( 4, DATE '2026-02-12', 25,   'windows-11-pro'),
    ( 5, DATE '2026-02-10',  5,   'intellij-idea-ultimate'),
    ( 6, DATE '2026-01-18',  8,   'visual-studio-pro'),
    ( 7, DATE '2026-02-01', NULL, 'docker-desktop'),
    ( 8, DATE '2026-01-30', 50,   'postgresql-client'),
    ( 9, DATE '2026-02-08', 100,  'slack-standard'),
    (10, DATE '2026-02-11', 20,   'zoom-pro'),
    (11, DATE '2026-03-15', NULL, 'figma-professional'),
    (12, DATE '2026-04-01', 50,   'jira-software'),
    (13, DATE '2026-06-30', 50,   'confluence-standard'),
    (14, DATE '2026-09-30', 200,  'github-enterprise'),
    (15, DATE '2026-12-31', NULL, 'aws-console-access'),
    (16, DATE '2027-01-31',  2,   'autocad'),
    (17, DATE '2026-08-15',  4,   'adobe-photoshop'),
    (18, DATE '2026-08-20',  4,   'adobe-illustrator'),
    (19, DATE '2026-05-20', NULL, 'vmware-workstation'),
    (20, DATE '2026-07-10', 30,   'kaspersky-endpoint')
ON CONFLICT (id_software_license) DO NOTHING;


INSERT INTO public.assets_licenses (license_id, asset_id) VALUES
    ( 2,  1),
    ( 2,  2),
    ( 2,  6),
    ( 5,  1),
    ( 5,  2),
    ( 3,  2),
    ( 3, 12),
    ( 4,  2),
    ( 1,  3),
    ( 1, 14),
    ( 7,  6),
    ( 8,  6),
    ( 9, 12),
    ( 9, 18),
    (10, 16),
    (12,  4),
    (16,  4),
    (14, 19),
    (19, 19),
    (20, 20)
ON CONFLICT DO NOTHING;
