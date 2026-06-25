DROP TABLE IF EXISTS t_order_item;
DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_cart;
DROP TABLE IF EXISTS t_clothes;
DROP TABLE IF EXISTS t_size;
DROP TABLE IF EXISTS t_type;
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    address VARCHAR(255),
    role INT NOT NULL DEFAULT 2,
    deleted INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_size (
    id INT AUTO_INCREMENT PRIMARY KEY,
    size_name VARCHAR(255) NOT NULL,
    type_id INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES t_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_clothes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cloth_name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    type_id INT,
    style VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    operator_id INT,
    FOREIGN KEY (type_id) REFERENCES t_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cloth_id INT NOT NULL,
    cloth_size VARCHAR(255),
    amount INT NOT NULL DEFAULT 1,
    user_id INT NOT NULL,
    date VARCHAR(255),
    FOREIGN KEY (cloth_id) REFERENCES t_clothes(id),
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_order (
    id INT AUTO_INCREMENT PRIMARY KEY,
    clothes_details VARCHAR(255),
    price DECIMAL(10,2),
    status VARCHAR(255) DEFAULT '0',
    user_id INT NOT NULL,
    address VARCHAR(255),
    time VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_order_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    cloth_id INT NOT NULL,
    cloth_name VARCHAR(255),
    cloth_size VARCHAR(255),
    amount INT NOT NULL DEFAULT 1,
    price DECIMAL(10,2),
    operator_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES t_order(id),
    FOREIGN KEY (cloth_id) REFERENCES t_clothes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_type (type_name) VALUES ('衣服'), ('裤子'), ('鞋'), ('配饰');

INSERT INTO t_size (size_name, type_id) VALUES
('S',  1), ('M',  1), ('L',  1), ('XL', 1), ('XXL', 1),
('XS', 1),
('XS', 2), ('S',  2), ('M',  2), ('L',  2), ('XL', 2), ('XXL', 2),
('35', 3), ('36', 3), ('37', 3), ('38', 3), ('39', 3), ('40', 3), ('41', 3), ('42', 3), ('43', 3), ('44', 3),
('均码', 4);
