ALTER TABLE sites 
  MODIFY cms ENUM(
    'WORDPRESS',
    'SHOPIFY',
    'DRUPAL',
    'PRESTASHOP',
    'MAGENTO',
    'JOOMLA',
    'WIX',
    'CUSTOM',
    'OTHER'
  ) NOT NULL DEFAULT 'CUSTOM';
