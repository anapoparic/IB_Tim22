INSERT INTO address (country, city, postal_code, street_and_number, latitude, longitude, active)
VALUES
    ('United Kingdom', 'London', 'W1J 9BR', '150 Piccadilly', 51.5074, -0.1278, true);

INSERT INTO public.authority (role) VALUES
                                        ('ADMIN'),
                                        ('GUEST'),
                                        ('HOST');

INSERT INTO photo (url, caption, width, height, active)
VALUES
    ('images/us1.png', 'admin', 264, 264, true),
    ('images/us2.png', 'host', 488, 511, true),
    ('images/us3.png', 'host', 980, 980, true),
    ('images/us4.png', 'guest', 535, 466, true),
    ('images/us5.png', 'guest', 500, 500, true),
    ('images/us6.png', 'guest', 220, 230, true),
    ('images/us7.png', 'guest', 524, 476,true);

INSERT INTO users (first_name, last_name, address_id, phone, email, password, is_blocked, verified, photo_id, last_password_reset_date, active, role)
    VALUES ('Jovan', 'Jovanović', 1, '123456789', 'jovan.jovanovic@example.com', 'jovanpass', false, true, 1, '2023-01-01 10:00:00', true, 'ADMIN');
