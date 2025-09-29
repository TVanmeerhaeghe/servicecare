INSERT INTO clients(name, legal_name, siret, vat_number, contact_first_name, contact_last_name,
                    contact_email, contact_phone, billing_email, technical_email, website_url,
                    address_line1, postal_code, city, country_code, currency_code, status)
VALUES
('Acme Corp', 'Acme Corporation France', '12345678900012', 'FR123456789',
 'Alice', 'Martin', 'alice.martin@acme.com', '+33142000000',
 'billing@acme.com', 'tech@acme.com', 'https://www.acme.com',
 '12 rue de la Paix', '75002', 'Paris', 'FR', 'EUR', 'ACTIVE'),

('Globex', 'Globex International', '98765432100045', 'FR987654321',
 'Hugo', 'Dupont', 'hugo.dupont@globex.com', '+33156000000',
 'billing@globex.com', 'tech@globex.com', 'https://www.globex.com',
 '25 avenue Victor Hugo', '75116', 'Paris', 'FR', 'EUR', 'ACTIVE'),

('Initech', 'Initech Europe SARL', '11223344556677', 'FR112233445',
 'Sophie', 'Lemoine', 'sophie.lemoine@initech.com', '+33472000000',
 'billing@initech.com', 'tech@initech.com', 'https://www.initech.com',
 '5 rue de la République', '69002', 'Lyon', 'FR', 'EUR', 'LEAD');
