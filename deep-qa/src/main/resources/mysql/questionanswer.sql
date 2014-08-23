CREATE DATABASE questionanswer;

USE questionanswer;

CREATE  TABLE `questionanswer`.`question` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `question` VARCHAR(200)  NOT NULL ,

  PRIMARY KEY (`id`) ,

  UNIQUE INDEX `question_UNIQUE` (`question` ASC) );
  
CREATE  TABLE `questionanswer`.`evidence` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `title` VARCHAR(445) NOT NULL ,

  `snippet` VARCHAR(2000) NOT NULL ,

  `question` INT NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `question_idx` (`question` ASC) ,

  CONSTRAINT `question`

    FOREIGN KEY (`question` )

    REFERENCES `questionanswer`.`question` (`id` )

    ON DELETE NO ACTION

    ON UPDATE NO ACTION);

CREATE  TABLE `questionanswer`.`rewind` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `question` VARCHAR(445) NOT NULL ,

  `text` TEXT NOT NULL ,

  PRIMARY KEY (`id`) ,

  UNIQUE INDEX `id_UNIQUE` (`id` ASC) );    
