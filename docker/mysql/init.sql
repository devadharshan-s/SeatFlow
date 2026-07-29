-- Create schemas for all BookMyShow microservices
CREATE DATABASE IF NOT EXISTS bookmyshow_user;
CREATE DATABASE IF NOT EXISTS bookmyshow_theatre;
CREATE DATABASE IF NOT EXISTS bookmyshow_show;
CREATE DATABASE IF NOT EXISTS bookmyshow_movie;
CREATE DATABASE IF NOT EXISTS bookmyshow_payment;
CREATE DATABASE IF NOT EXISTS bookmyshow_booking;

-- Grant privileges (optional but good practice for dev database)
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
FLUSH PRIVILEGES;
