-- MySQL 5.5 compatible init script
-- Database: blog_db
-- Charset: utf8 (safer on MySQL 5.5)

SET NAMES utf8;

-- Create DB & select it (safe to run multiple times)
CREATE DATABASE IF NOT EXISTS blog_db DEFAULT CHARACTER SET utf8;
USE blog_db;

DROP TABLE IF EXISTS file_resource;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS site_setting;
DROP TABLE IF EXISTS admin_user;

CREATE TABLE admin_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  display_name VARCHAR(50) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE category (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tag (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_tag_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE post (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(120) NOT NULL,
  summary VARCHAR(300) DEFAULT NULL,
  content TEXT NOT NULL,
  cover_url VARCHAR(255) DEFAULT NULL,
  status VARCHAR(20) NOT NULL,
  category_id BIGINT DEFAULT NULL,
  view_count BIGINT NOT NULL DEFAULT 0,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  published_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_post_status (status),
  KEY idx_post_category_id (category_id),
  KEY idx_post_published_at (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE post_tag (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_post_tag (post_id, tag_id),
  KEY idx_post_tag_post_id (post_id),
  KEY idx_post_tag_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE comment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  nickname VARCHAR(30) NOT NULL,
  email VARCHAR(100) DEFAULT NULL,
  content VARCHAR(500) NOT NULL,
  status VARCHAR(20) NOT NULL,
  ip VARCHAR(45) DEFAULT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_comment_post_id (post_id),
  KEY idx_comment_status (status),
  KEY idx_comment_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE site_setting (
  id BIGINT NOT NULL,
  site_name VARCHAR(100) NOT NULL,
  site_notice VARCHAR(255) DEFAULT NULL,
  about_content TEXT DEFAULT NULL,
  links_json TEXT DEFAULT NULL,
  seo_title VARCHAR(255) DEFAULT NULL,
  seo_keywords VARCHAR(255) DEFAULT NULL,
  seo_description VARCHAR(255) DEFAULT NULL,
  footer_text VARCHAR(255) DEFAULT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE file_resource (
  id BIGINT NOT NULL AUTO_INCREMENT,
  url VARCHAR(255) NOT NULL,
  original_name VARCHAR(255) DEFAULT NULL,
  size BIGINT NOT NULL DEFAULT 0,
  content_type VARCHAR(100) DEFAULT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_file_resource_url (url),
  KEY idx_file_resource_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Seed data
-- password_hash is BCrypt. Default password is: admin123
INSERT INTO admin_user (username, password_hash, display_name, status, created_at, updated_at)
VALUES ('admin', '$2a$10$sjGLhXaPfA4Ykj7B7b/Jb.6LDks4bWeTFDjbN3nKsg.e4FWCSCn06', 'Administrator', 1, NOW(), NOW());

INSERT INTO site_setting (id, site_name, site_notice, about_content, links_json, seo_title, seo_keywords, seo_description, footer_text, updated_at)
VALUES (1, 'My Blog', 'Welcome', '# About\n\nAbout page.', '[]', 'My Blog', 'blog,java,vue', 'A simple blog site', '(c) My Blog', NOW());

-- Seed meta data for first run
INSERT INTO category (name, created_at, updated_at)
VALUES ('General', NOW(), NOW());

INSERT INTO tag (name, created_at, updated_at)
VALUES ('Intro', NOW(), NOW());
