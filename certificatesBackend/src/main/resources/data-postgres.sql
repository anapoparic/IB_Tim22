INSERT INTO certificate (valid_from, valid_to, alias, issuer_alias, is_revoked, reason, template, common_name, organization, owner_email, active)
VALUES
    ('2023-01-01 10:00:00', '2024-01-01 10:00:00', 'Alias1', 'Alias1', false, NULL, 0, 'CommonName1', 'Organization1', 'email1@example.com', true),
    ('2023-01-01 10:00:00', '2024-01-01 10:00:00', 'Alias2', 'Alias1', false, NULL, 1, 'CommonName2', 'Organization2', 'email2@example.com', true),
    ('2023-01-01 10:00:00', '2024-01-01 10:00:00', 'Alias3', 'Alias2', false, NULL, 3, 'CommonName3', 'Organization3', 'email3@example.com', true);

INSERT INTO requests (common_name, first_name, last_name, organization, unit, country, email, uid, active)
VALUES
    ('CommonName1', 'John', 'Doe', 'Organization1', 'Unit1', 'Country1', 'email1@example.com', 'uid1', true),
    ('CommonName2', 'Jane', 'Smith', 'Organization2', 'Unit2', 'Country2', 'email2@example.com', 'uid2', true),
    ('CommonName3', 'Mike', 'Johnson', 'Organization3', 'Unit3', 'Country3', 'email3@example.com', 'uid3', false);
