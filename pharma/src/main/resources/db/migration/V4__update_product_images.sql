-- Populate the missing image_url field in the product table using the product_id
UPDATE product SET image_url = CONCAT('/images/products/', product_id, '.png') WHERE image_url IS NULL;
