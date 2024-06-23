-- use autobiography;

CREATE TABLE users (
                       user_no INT PRIMARY KEY AUTO_INCREMENT,
                       user_id VARCHAR(255) NOT NULL UNIQUE,
                       user_name VARCHAR(255) NOT NULL,
                       user_pwd VARCHAR(255) NOT NULL,
                       user_birth DATE NOT NULL,
                       user_phone VARCHAR(255),
                       gender VARCHAR(255),
                       user_role VARCHAR(255) DEFAULT 'N'
) ENGINE=INNODB;

CREATE TABLE drafts (
                        draft_no INT PRIMARY KEY AUTO_INCREMENT,
                        user_no INT,
                        request_time DATETIME NOT NULL,
                        draft_content TEXT NOT NULL,
                        response_content TEXT NOT NULL,
                        chapter_number INT NOT NULL,
                        FOREIGN KEY (user_no) REFERENCES users(user_no)
                            ON DELETE CASCADE
                            ON UPDATE CASCADE
) ENGINE=INNODB;

CREATE TABLE edit (
                      edit_no INT PRIMARY KEY AUTO_INCREMENT,
                      user_no INT NOT NULL,
                      edit_title VARCHAR(255) NOT NULL,
                      edit_text TEXT NOT NULL,
                      edit_time DATETIME NOT NULL,
                      chapter_number INT NOT NULL,
                      FOREIGN KEY (user_no) REFERENCES users(user_no)
                          ON DELETE CASCADE
                          ON UPDATE CASCADE
) ENGINE=INNODB;

CREATE TABLE comments (
                          comment_id INT PRIMARY KEY AUTO_INCREMENT,
                          chapter_number INT NOT NULL,
                          user_no INT NOT NULL,
                          comment_text TEXT NOT NULL,
                          comment_time DATETIME NOT NULL,
                          FOREIGN KEY (chapter_number) REFERENCES drafts(draft_no)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE,
                          FOREIGN KEY (user_no) REFERENCES users(user_no)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE
) ENGINE=INNODB;

CREATE TABLE likes (
                       like_id INT PRIMARY KEY AUTO_INCREMENT,
                       user_no INT NOT NULL,
                       edit_no INT NOT NULL,
                       UNIQUE KEY unique_user_edit (user_no, edit_no),
                       FOREIGN KEY (user_no) REFERENCES users(user_no)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE,
                       FOREIGN KEY (edit_no) REFERENCES edit(edit_no)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE
) ENGINE=INNODB;
