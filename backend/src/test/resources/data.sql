INSERT INTO address (country, city, postal_code, street_and_number, latitude, longitude, active)
VALUES
    ('United Kingdom', 'London', 'W1J 9BR', '150 Piccadilly', 51.5074, -0.1278, true),
    ('France', 'Paris', '75008', '10 Place de la Concorde', 48.8656, 2.3111, true),
    ('Germany', 'Berlin', '10117', 'Unter den Linden 77', 52.5162, 13.3761, true),
    ('Russia', 'St. Petersburg', '', 'Mikhailovskaya Ulitsa 1/7', 59.9343, 30.3351, true),
    ('Italy', 'Rome', '00187', 'Via del Babuino, 9', 41.9062, 12.4784, true),
    ('Switzerland', 'St. Moritz', '7500', 'Via Serlas 27', 46.4907, 9.8350, true),
    ('Spain', 'Barcelona', '08007', 'Passeig de Gràcia, 38-40', 41.3925, 2.1657, true),
    ('United Kingdom', 'London', 'W1K 4HR', 'Brook Street, Mayfair', 51.5094, -0.1493, true),
    ('Italy', 'Venice', '30122', 'Riva degli Schiavoni, 4196', 45.4336, 12.3433, true),
    ('Italy', 'Rome', '00187', 'Via Vittorio Veneto, 125', 41.9064, 12.4881, true);

INSERT INTO photo (url, caption, width, height, active)
VALUES
    ('images/usx.jpg', 'Description1', 264, 264, true),
    ('images/usx.jpg', 'Description2', 488, 511, true),
    ('images/usx.jpg', 'Description3', 980, 980, true),
    ('images/usx.jpg', 'Description4', 535, 466, true),
    ('images/usx.jpg', 'Description5', 500, 500, true),
    ('images/usx.jpg', 'Description6', 220, 230, true),
    ('images/usx.jpg', 'Description7', 524, 476,true),
    ('images/acc1.jpg', 'No profile picture', 640, 960,true);


INSERT INTO users (first_name, last_name, address_id, phone, email, password, is_blocked, verified, photo_id, last_password_reset_date, notification_enable, accommodation_rating_notification_enabled, average_rating, cancellation_notification_enabled, host_rating_notification_enabled, reservation_created_notification_enabled, active, role)
VALUES
    ('Jovan', 'Jovanović', 1, 123456789, 'jovan.jovanovic@example.com', 'jovanpass', false, true, 1, '2023-01-01 10:00:00', null, null, null, null, null, null,true, 'ADMIN'),
    ('Ana', 'Anić', 2, 987654321, 'ana.anic@example.com', 'anapass', false, true, 2, '2023-02-01 12:30:00', null, true, 4.8, true, true, false,true, 'HOST'),
    ('Milica', 'Milosavljević', 3, 987654322, 'milica.milosavljevic@example.com', 'milicinapass', false, true, 3, '2023-03-01 15:45:00', null, true, 5.0, false, true, false,true, 'HOST'),
    ('Marko', 'Marković', 4, 0631234567, 'marko.markovic@example.com', 'markopass', false, true, 4, '2023-04-01 18:20:00', true, null, null, null, null, null,true, 'GUEST'),
    ('Jovana', 'Jovanović', 5, 0649876543, 'jovana.jovanovic@example.com', 'jovanapass', false, true, 5, '2023-04-01 18:20:00', true, null, null, null, null, null,true, 'GUEST'),
    ('Nenad', 'Nenadić', 6, 0658765432, 'nenad.nenadic@example.com', 'nenadpass', false, true, 6, '2023-05-01 21:10:00', true, null, null, null, null, null,true, 'GUEST'),
    ('Mila', 'Milićević', 7, 0661122334, 'mila.milicevic@example.com', 'milinpass', false, true, 7, '2022-06-01 09:30:00', false, null, null, null, null, null,true, 'GUEST');

-- INSERT INTO users_authority (user_id, authority_id)
-- VALUES
--     (1, 1),
--     (2, 3),
--     (3, 3),
--     (4, 2),
--     (5, 2),
--     (6, 2),
--     (7, 2);
ALTER TABLE accommodation
ALTER COLUMN description TYPE VARCHAR(1000);

INSERT INTO accommodation (name, description, address_id, price, min_guests, max_guests, cancellation_deadline, automatic_reservation_acceptance, status, price_type, type, average_rating, host_id, active)
VALUES
    ('OceanViewEscape', 'Indulge in panoramic ocean views from this luxurious coastal retreat. The three-bedroom villa features contemporary design, a fully equipped gourmet kitchen, and a private terrace for soaking in the sea breeze. Enjoy access to resort-style amenities, including a beachfront pool and spa. Ideal for a tranquil getaway or entertaining guests.', 1, 200.0, 2, 4, 7, true, 'CREATED', 'PER_NIGHT', 'HOTEL', 3.8, 2, true),
    ('Sky High Suite', 'Elevate your stay in the luxurious Sky High Suite, situated on the top floors of a landmark skyscraper. This opulent three-bedroom suite offers panoramic city views through floor-to-ceiling windows, a private chef''s kitchen, and a lavish entertainment lounge. Indulge in the epitome of sophistication and convenience in the heart of the urban skyline.', 2, 50.0, 1, 3, 3, true, 'ACTIVE', 'PER_GUEST', 'HOSTEL', 4.0, 2, true),
    ('City Lights Penthouse', 'Experience the epitome of urban living in this breathtaking penthouse with floor-to-ceiling windows showcasing city lights. Boasting four bedrooms and a stylish modern interior, this high-rise oasis offers a fully stocked bar, entertainment lounge, and a private rooftop terrace with skyline views. Elevate your stay in the heart of the city.', 3, 300.0, 4, 8, 14, false, 'CHANGED', 'PER_NIGHT', 'VILLA', 5.0, 2, true),
    ('Alpine Retreat Chalet', 'Nestled in the serene alpine landscape, this chalet offers a cozy escape with log cabin charm. Featuring two bedrooms, a wood-burning fireplace, and a hot tub on the deck, it''s an idyllic setting for a mountain retreat. Enjoy direct access to hiking trails and ski slopes for year-round outdoor adventures.', 4, 80.0, 1, 2, 2, false, 'ACTIVE', 'PER_NIGHT', 'APARTMENT', 4.5, 3, true),
    ('Urban Retreat Suite', 'Welcome to your new urban oasis! This stylish two-bedroom apartment boasts modern elegance with an open-concept design, featuring sleek hardwood floors and large windows that flood the space with natural light. The kitchen is a culinary enthusiast''s dream, equipped with state-of-the-art stainless steel appliances and granite countertops. Unwind in the spacious living room, perfect for entertaining or cozy nights in, and retreat to the master bedroom with its en-suite bathroom for the ultimate in comfort. Located in a vibrant neighborhood, this apartment offers not just a home but a lifestyle, with trendy shops, restaurants, and parks just steps away.', 5, 500.0, 2, 6, 14, true, 'ACTIVE', 'PER_NIGHT', 'RESORT', 7.5, 2, true),
    ('Historic Inn Elegance', 'Step back in time at this meticulously restored historic inn. Each room tells a story with antique furnishings and period decor. Indulge in a continental breakfast in the charming courtyard and explore nearby landmarks. Immerse yourself in the elegance of a bygone era while enjoying modern comforts.', 6, 120.0, 2, 4, 7, false, 'CHANGED', 'PER_NIGHT', 'APARTMENT', 4.8, 3, true),
    ('Skyline View Loft', 'Perched atop a skyscraper, this contemporary loft offers unparalleled skyline views. The one-bedroom space features minimalist aesthetics, a fully equipped kitchen, and a spacious living area. Enjoy the luxury of a private balcony overlooking the city lights for a truly cosmopolitan experience.', 7, 450.0, 6, 10, 14, false, 'ACTIVE', 'PER_NIGHT', 'VILLA', 3.0, 2, true),
    ('Tranquil Cottage Retreat', 'Escape to the countryside in this charming cottage surrounded by lush gardens. The two-bedroom retreat features a rustic fireplace, a fully equipped kitchen, and a private patio for enjoying morning coffee or evening stargazing. Immerse yourself in the tranquility of nature while being just a short drive from local vineyards and hiking trails.', 8, 300.0, 2, 4, 7, true, 'ACTIVE', 'PER_NIGHT', 'HOTEL', 3.8, 3, true),
    ('Beachfront Bungalow Paradise', 'Experience paradise at this beachfront bungalow with direct access to the sandy shores. The tropical-themed two-bedroom haven offers a sun-soaked deck, hammocks for lazy afternoons, and stunning sunset views. Embrace the laid-back coastal lifestyle with surf lessons, beach picnics, and seaside relaxation.', 9, 70.0, 1, 3, 3, true, 'ACTIVE', 'PER_NIGHT', 'VILLA', 2.4, 2, true),
    ('Riverside Retreat Cabin', 'Unplug and unwind in this secluded cabin nestled by a serene river. The one-bedroom hideaway features a woodsy interior, a riverside deck for fishing or lounging, and a cozy fireplace. Disconnect from the hustle and bustle, and reconnect with nature in this peaceful riverside retreat.', 10, 90.0, 2, 4, 5, true, 'ACTIVE', 'PER_NIGHT', 'APARTMENT', 4.7, 3, true);

INSERT INTO photo (url, caption, width, height, active, accommodation_id)
VALUES
    ('images/acc1.jpg', 'Description1', 264, 264, true, 1),
    ('images/acc12.jpg', 'Description2', 488, 511, true, 3),
    ('images/acc11.jpg', 'Description3', 980, 980, true, 5),
    ('images/acc10.jpg', 'Description4', 535, 466, true, 6),
    ('images/acc9.jpg', 'Description5', 500, 500, true, 4),
    ('images/acc8.jpg', 'Description6', 220, 230, true, 10),
    ('images/acc7.jpg', 'Description7', 524, 476,true, 7),
    ('images/acc6.jpg', 'No profile picture', 640, 960,true, 9),
    ('images/acc5.jpg', 'No profile picture', 640, 960,true, 8),
    ('images/acc2.png', 'No profile picture', 640, 960,true, 2),
    ('images/acc3.png', 'No profile picture', 640, 960,true, 5),
    ('images/acc4.jpg', 'No profile picture', 640, 960,true, 5);


INSERT INTO amenities (accommodation_id, amenities)
VALUES
    (1, 'FREE_WIFI'),
    (1, 'NON_SMOKING_ROOMS'),
    (1, 'PARKING'),
    (2, 'RESTAURANT'),
    (2, 'SWIMMING_POOL'),
    (3, 'FREE_WIFI'),
    (3, 'FITNESS_CENTRE'),
    (4, 'NON_SMOKING_ROOMS'),
    (5, 'PARKING'),
    (5, 'RESTAURANT'),
    (6, 'FREE_WIFI'),
    (6, 'NON_SMOKING_ROOMS'),
    (7, 'PARKING'),
    (8, 'RESTAURANT'),
    (8, 'SWIMMING_POOL'),
    (9, 'FREE_WIFI'),
    (9, 'FITNESS_CENTRE'),
    (10, 'NON_SMOKING_ROOMS'),
    (10, 'PARKING'),
    (10, 'RESTAURANT');


INSERT INTO date_range (accommodation_id, start_date, end_date)
VALUES
    (1, '2024-01-01 13:00:00.000000', '2024-01-10 13:00:00.000000'),
    (1, '2024-01-15 13:00:00.000000', '2024-02-20 13:00:00.000000'),
    (2, '2024-01-05 13:00:00.000000', '2024-01-12 13:00:00.000000'),
    (2, '2024-01-18 13:00:00.000000', '2024-01-25 13:00:00.000000'),
    (3, '2024-01-08 13:00:00.000000', '2024-01-14 13:00:00.000000'),
    (3, '2024-01-21 13:00:00.000000', '2024-05-28 13:00:00.000000'),
    (4, '2023-12-20 13:00:00.000000', '2025-01-09 13:00:00.000000'),
    (5, '2023-12-18 13:00:00.000000', '2024-03-24 13:00:00.000000'),
    (5, '2024-04-25 13:00:00.000000', '2024-04-28 13:00:00.000000'),
    (6, '2024-01-03 13:00:00.000000', '2024-01-11 13:00:00.000000'),
    (7, '2024-01-07 13:00:00.000000', '2024-12-14 13:00:00.000000'),
    (8, '2024-01-14 13:00:00.000000', '2024-01-21 13:00:00.000000'),
    (8, '2024-01-28 13:00:00.000000', '2024-12-04 13:00:00.000000'),
    (9, '2024-01-10 13:00:00.000000', '2024-01-17 13:00:00.000000'),
    (9, '2024-01-22 13:00:00.000000', '2024-12-29 13:00:00.000000'),
    (10, '2023-12-05 13:00:00.000000', '2024-01-12 13:00:00.000000'),
    (10, '2024-01-15 13:00:00.000000', '2024-10-26 13:00:00.000000');

INSERT INTO price_change (accommodation_id, change_date, new_price)
VALUES
    (1, '2024-01-05 13:00:00.000000', 120.0),
    (1, '2024-02-15 13:00:00.000000', 130.0),
    (2, '2024-01-08 13:00:00.000000', 150.0),
    (2, '2024-01-20 13:00:00.000000', 160.0),
    (3, '2024-01-14 13:00:00.000000', 100.0),
    (3, '2024-02-28 13:00:00.000000', 110.0),
    (4, '2024-02-08 13:00:00.000000', 180.0),
    (5, '2024-01-20 13:00:00.000000', 200.0),
    (5, '2024-02-26 13:00:00.000000', 220.0),
    (6, '2024-01-12 13:00:00.000000', 130.0),
    (7, '2024-01-15 13:00:00.000000', 90.0),
    (8, '2024-01-25 13:00:00.000000', 170.0),
    (8, '2024-02-05 13:00:00.000000', 190.0),
    (9, '2024-01-20 13:00:00.000000', 120.0),
    (9, '2024-02-29 13:00:00.000000', 130.0),
    (10, '2024-01-18 13:00:00.000000', 110.0),
    (10, '2024-01-28 13:00:00.000000', 120.0);

INSERT INTO guest_favourite (guest_id, accommodation_id)
VALUES
    (4, 1),
    (4, 3),
    (4, 5),
    (4, 7),
    (5, 2),
    (5, 4),
    (5, 6),
    (6, 1),
    (6, 3),
    (7, 2),
    (7, 4),
    (7, 6),
    (7, 8),
    (4, 9),
    (5, 8),
    (6, 10),
    (7, 9);


INSERT INTO user_report (reason, reported_user_id, status)
VALUES
    ('Inappropriate content', 2, true),
    ('Hate speech', 4, true),
    ('Other', 7, false);

INSERT INTO review (guest_id, review, comment, date, host_id, accommodation_id, type, is_review_active, approved)
VALUES
    (4, 4, 'Great experience! Highly recommended.', '2023-12-01T10:30:00', null, 1, 'ACCOMMODATION', true, false),
    (5, 3, 'Good place, but could be better.', '2023-12-02T11:45:00', null, 2, 'ACCOMMODATION', true, true),
    (6, 5, 'Amazing host and accommodation!', '2023-12-03T09:15:00', 2, null, 'HOST', true,false),
    (7, 2, 'Not satisfied with the service.', '2023-12-04T08:00:00', null, 4, 'ACCOMMODATION', true,false),
    (4, 4, 'Excellent stay, would come back!', '2023-12-05T14:20:00', null, 5, 'ACCOMMODATION', true,false),
    (5, 2, 'Disappointing experience.', '2023-12-06T16:30:00', 3, null, 'HOST', true, false),
    (6, 5, 'Host was very helpful and friendly.', '2023-12-07T12:10:00', null, 7, 'ACCOMMODATION', true, false),
    (7, 3, 'Average place, nothing special.', '2023-12-08T13:45:00', null, 8, 'ACCOMMODATION', true, false),
    (4, 4, 'Lovely accommodation with a great view.', '2023-12-09T18:00:00', 2, null, 'HOST', true, false),
    (5, 1, 'Terrible experience, do not recommend.', '2023-12-10T22:00:00',null, 10, 'ACCOMMODATION', true, false),
    (5, 3, 'Good place, but could be better.', '2023-12-02T11:45:00', null, 2, 'ACCOMMODATION', true, true),
    (5, 3, 'Good place, but could be better.', '2023-12-02T11:45:00', null, 2, 'ACCOMMODATION', true, true),
    (5, 3, 'Good place, but could be better.', '2023-12-02T11:45:00', null, 2, 'ACCOMMODATION', true, true)
;


INSERT INTO review_report (reason, reported_review_id, status)
VALUES
    ('Inappropriate content', 1, true),
    ('False information', 2, true),
    ('Abusive language', 3, false),
    ('Not related to the review', 4, true),
    ('Spam', 5, true),
    ('Unfair criticism', 6, false),
    ('Not a genuine review', 7, true),
    ('Violation of community guidelines', 8, false),
    ('Slanderous content', 9, true),
    ('Privacy concerns', 10, true);



INSERT INTO reservation (created_time, start_date, end_date, total_price, guests_number, accommodation_id, guest_id, status, active)
VALUES
    ('2023-12-10 08:00:00', '2024-01-05 13:00:00.000000', '2024-01-10 13:00:00.000000', 300.00, 2, 1, 4, 'CREATED', true),
    ('2023-12-11 09:30:00', '2024-02-15 13:00:00.000000', '2024-02-22 13:00:00.000000', 450.00, 3, 2, 5, 'CREATED', true),
    ('2023-12-12 10:45:00', '2024-03-20 13:00:00.000000', '2024-03-25 13:00:00.000000', 200.00, 1, 3, 6, 'REJECTED', true),
    ('2023-12-13 12:15:00', '2024-04-05 13:00:00.000000', '2024-04-15 13:00:00.000000', 600.00, 4, 4, 7, 'ACCEPTED', true),
    ('2023-12-14 14:00:00', '2024-05-12 13:00:00.000000', '2024-05-20 13:00:00.000000', 350.00, 2, 5, 4, 'CREATED', true),
    ('2023-12-15 16:30:00', '2024-06-18 13:00:00.000000', '2024-06-25 13:00:00.000000', 700.00, 3, 6, 5, 'CREATED', true),
    ('2023-03-16 18:20:00', '2023-07-10 13:00:00.000000', '2023-07-15 13:00:00.000000', 250.00, 1, 7, 6, 'COMPLETED', true),
    ('2023-12-17 20:45:00', '2024-08-22 13:00:00.000000', '2024-08-30 13:00:00.000000', 500.00, 2, 8, 7, 'REJECTED', true),
    ('2022-12-18 22:10:00', '2023-09-05 13:00:00.000000', '2023-09-15 13:00:00.000000', 400.00, 3, 9, 4, 'COMPLETED', true),
    ('2023-12-19 23:55:00', '2024-10-12 13:00:00.000000', '2024-10-20 13:00:00.000000', 800.00, 4, 10, 5, 'REJECTED', true),
    ('2023-12-20 01:30:00', '2024-11-18 13:00:00.000000', '2024-11-25 13:00:00.000000', 300.00, 2, 1, 6, 'CANCELLED', true),
    ('2023-12-10 03:40:00', '2023-12-15 13:00:00.000000', '2023-01-02 13:00:00.000000', 600.00, 3, 2, 7, 'COMPLETED', true),
    ('2023-12-22 05:25:00', '2025-01-20 13:00:00.000000', '2025-02-01 13:00:00.000000', 350.00, 2, 3, 4, 'CREATED', true),
    ('2023-12-23 07:15:00', '2024-02-15 13:00:00.000000', '2024-02-22 13:00:00.000000', 450.00, 3, 4, 5, 'CREATED', true),
    ('2022-12-24 09:00:00', '2023-03-20 13:00:00.000000', '2023-03-25 13:00:00.000000', 200.00, 1, 5, 6, 'COMPLETED', true),
    ('2023-12-25 11:30:00', '2025-04-05 13:00:00.000000', '2025-04-15 13:00:00.000000', 700.00, 4, 6, 7, 'ACCEPTED', true),
    ('2023-12-26 13:15:00', '2025-05-12 13:00:00.000000', '2025-05-20 13:00:00.000000', 350.00, 2, 7, 4, 'CANCELLED', true),
    ('2023-12-02 15:45:00', '2023-12-18 13:00:00.000000', '2023-12-25 13:00:00.000000', 800.00, 3, 8, 5, 'COMPLETED', true),
    ('2023-05-28 17:30:00', '2023-07-10 13:00:00.000000', '2023-07-15 13:00:00.000000', 250.00, 1, 9, 6, 'COMPLETED', true),
    ('2023-12-29 19:20:00', '2025-08-22 13:00:00.000000', '2025-08-30 13:00:00.000000', 500.00, 2, 10, 7, 'ACCEPTED', true);


INSERT INTO notification (from_user_id, to_user_id, title, message, timestamp, type, active)
VALUES
    (4, 2, 'New Reservation!', 'Exciting news, Ana! A new reservation has just been made for your OceanViewEscape accommodation. Get ready to welcome your next guest!', '2023-12-10 08:30:00', 'RESERVATION_CREATED', true),
    (2, 4, 'Reservation Confirmed Instantly!', 'Hey Marko! Good news - your reservation has been automatically accepted. Your stay is confirmed and ready to go!', '2023-12-15 08:30:00', 'RESERVATION_REQUEST_RESPONSE', true),
    (5, 3, 'Reservation Update: Cancelled!', 'Unfortunately, a reservation for your accommodation has been cancelled. Feel free to review the details and reach out to our support if you need any assistance', '2023-12-11 10:00:00', 'RESERVATION_CANCELED', true),
    (2, 6, 'Reservation Accepted', 'Congratulations Nenad! Your reservation has been accepted by the host. Get ready for a fantastic stay!', '2023-12-14 14:20:00', 'RESERVATION_REQUEST_RESPONSE', true),
    (2, 5, 'Reservation Update: Rejected!', 'Hi Jovana, unfortunately, the host has declined your reservation request. Do not worry, there are plenty of other great options available! Let us know if we can help you find another place!', '2023-12-14 14:20:00', 'RESERVATION_REQUEST_RESPONSE', true),
    (4, 2, 'New Review for Your Accommodation!', 'Heads up, Ana! A new review awaits your attention. Check it out and keep the positive vibes going!', '2023-12-10 08:30:00', 'ACCOMMODATION_RATED', true),
    (5, 2, 'New Review for You!', 'Curious about the latest buzz? A new review is in-time to hear what your guests are saying about their experience!', '2023-12-10 08:30:00', 'HOST_RATED', true),
    (5, 2, 'New Reservation!', 'Exciting news, Ana! A new reservation has just been made for your OceanViewEscape accommodation. Get ready to welcome your next guest!', '2023-12-10 08:30:00', 'RESERVATION_CREATED', true),
    (3, 4, 'Reservation Confirmed Instantly!', 'Hey Marko! Good news - your reservation has been automatically accepted. Your stay is confirmed and ready to go!', '2023-12-15 08:30:00', 'RESERVATION_REQUEST_RESPONSE', true),
    (5, 3, 'Reservation Update: Cancelled!', 'Unfortunately, a reservation for your accommodation has been cancelled. Feel free to review the details and reach out to our support if you need any assistance', '2023-12-11 10:00:00', 'RESERVATION_CANCELED', true),
    (3, 4, 'Reservation Accepted', 'Congratulations Marko! Your reservation has been accepted by the host. Get ready for a fantastic stay!', '2023-12-14 14:20:00', 'RESERVATION_REQUEST_RESPONSE', true),
    (2, 4, 'Reservation Update: Rejected!', 'Hi Marko, unfortunately, the host has declined your reservation request. Do not worry, there are plenty of other great options available! Let us know if we can help you find another place!', '2023-12-14 14:20:00', 'RESERVATION_REQUEST_RESPONSE', true),
    (4, 2, 'New Review for Your Accommodation!', 'Heads up, Ana! A new review awaits your attention. Check it out and keep the positive vibes going!', '2023-12-10 08:30:00', 'ACCOMMODATION_RATED', true),
    (4, 3, 'New Review for You!', 'Curious about the latest buzz? A new review is in-time to hear what your guests are saying about their experience!', '2023-12-10 08:30:00', 'HOST_RATED', true);

