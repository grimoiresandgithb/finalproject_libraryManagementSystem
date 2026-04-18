INSERT INTO items (title, available, item_type, author, isbn, publication_year)
VALUES
('The Pragmatic Programmer', TRUE, 'book', 'Andrew Hunt', '9780201616224', 1999),
('Design Patterns', TRUE, 'book', 'Erich Gamma', '9780201633610', 1994),
('The Name of the Wind', TRUE, 'book', 'Patrick Rothfuss', '9780756404741', 2007),
('Dune', TRUE, 'book', 'Frank Herbert', '9780441172719', 1965),
('The Silent Patient', TRUE, 'book', 'Alex Michaelides', '9781250301697', 2019);

INSERT INTO items (title, available, item_type, director, runtime_minutes)
VALUES
('Interstellar', TRUE, 'dvd', 'Christopher Nolan', 169),
('Spirited Away', TRUE, 'dvd', 'Hayao Miyazaki', 125),
('The Godfather', TRUE, 'dvd', 'Francis Ford Coppola', 175),
('Jurassic Park', TRUE, 'dvd', 'Steven Spielberg', 127),
('Arrival', TRUE, 'dvd', 'Denis Villeneuve', 116);

INSERT INTO users (name, email, user_type, membership_type)
VALUES
('Charlie Nguyen', 'charlie.nguyen@example.com', 'member', 'standard'),
('Maya Patel', 'maya.patel@example.com', 'member', 'premium'),
('Ethan Brooks', 'ethan.brooks@example.com', 'member', 'standard'),
('Sofia Martinez', 'sofia.martinez@example.com', 'member', 'premium'),
('Liam Chen', 'liam.chen@example.com', 'member', 'standard');

INSERT INTO users (name, email, user_type, employee_id)
VALUES
('Jordan Lee', 'jordan.lee@example.com', 'librarian', 'EMP002'),
('Rebecca Stone', 'rebecca.stone@example.com', 'librarian', 'EMP003');

INSERT INTO items (title, available, item_type, author, isbn, publication_year)
VALUES
('The Hobbit', TRUE, 'book', 'J.R.R. Tolkien', '1234567890', 1937),
('Clean Code', TRUE, 'book', 'Robert C. Martin', '9780132350884', 2008);

INSERT INTO items (title, available, item_type, director, runtime_minutes)
VALUES
('Inception', TRUE, 'dvd', 'Christopher Nolan', 148),
('The Matrix', TRUE, 'dvd', 'The Wachowskis', 136);

INSERT INTO users (name, email, user_type, membership_type)
VALUES
('Alice Johnson', 'alice@example.com', 'member', 'standard'),
('Bob Smith', 'bob@example.com', 'member', 'premium');

INSERT INTO users (name, email, user_type, employee_id)
VALUES
('Sarah Librarian', 'sarah@example.com', 'librarian', 'EMP001');

INSERT INTO loans (item_id, member_id, loan_date, due_date)
VALUES
(1, 1, '2026-04-01', '2026-04-15');


