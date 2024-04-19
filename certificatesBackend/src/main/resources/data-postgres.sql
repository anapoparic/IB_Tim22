INSERT INTO address (country, city, postal_code, street_and_number, latitude, longitude, active)
VALUES
    ('United Kingdom', 'London', 'W1J 9BR', '150 Piccadilly', 51.5074, -0.1278, true);



INSERT INTO photo (url, caption, width, height, active)
VALUES
    ('images/us1.png', 'admin', 264, 264, true);

INSERT INTO users (first_name, last_name, address_id, phone, email, password, is_blocked, verified, photo_id, last_password_reset_date, active, role)
VALUES
    ('Jovan', 'Jovanović', 1, 123456789, 'jovan.jovanovic@example.com', 'jovanpass', false, true, 1, '2023-01-01 10:00:00',true, 'ADMIN');