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

INSERT INTO t_type (type_name) VALUES ('衣服'), ('裤子'), ('鞋'), ('配饰');
