-- Sites pour Acme Corp (id=1)
INSERT INTO sites(client_id, name, url, environment, type, cms, hosting_provider, status)
VALUES
(1, 'Acme Main Website', 'https://www.acme.com', 'PROD', 'WEBSITE', 'WORDPRESS', 'OVH', 'ACTIVE'),
(1, 'Acme Shop', 'https://shop.acme.com', 'PROD', 'SHOP', 'SHOPIFY', 'Shopify', 'ACTIVE');

-- Sites pour Globex (id=2)
INSERT INTO sites(client_id, name, url, environment, type, cms, hosting_provider, status)
VALUES
(2, 'Globex Landing', 'https://www.globex.com', 'PROD', 'WEBSITE', 'CUSTOM', 'AWS', 'ACTIVE'),
(2, 'Globex API', 'https://api.globex.com', 'PROD', 'API', 'CUSTOM', 'AWS', 'ACTIVE');

-- Sites pour Initech (id=3)
INSERT INTO sites(client_id, name, url, environment, type, cms, hosting_provider, status)
VALUES
(3, 'Initech Portal', 'https://portal.initech.com', 'STAGING', 'APP', 'DRUPAL', 'GCP', 'ACTIVE');
