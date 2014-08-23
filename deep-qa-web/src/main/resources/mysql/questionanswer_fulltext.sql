CREATE DATABASE questionanswer_fulltext;

USE questionanswer_fulltext;

CREATE  TABLE `questionanswer_fulltext`.`question` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `question` VARCHAR(200)  NOT NULL ,

  PRIMARY KEY (`id`) ,

  UNIQUE INDEX `question_UNIQUE` (`question` ASC) );
  
CREATE  TABLE `questionanswer_fulltext`.`evidence` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `title` VARCHAR(445) NOT NULL ,

  `snippet` TEXT NOT NULL ,

  `question` INT NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `question_idx` (`question` ASC) ,

  CONSTRAINT `question`

    FOREIGN KEY (`question` )

    REFERENCES `questionanswer_fulltext`.`question` (`id` )

    ON DELETE NO ACTION

    ON UPDATE NO ACTION);
    
CREATE  TABLE `questionanswer`.`rewind` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `question` VARCHAR(445) NOT NULL ,

  `text` TEXT NOT NULL ,

  PRIMARY KEY (`id`) ,

  UNIQUE INDEX `id_UNIQUE` (`id` ASC) ); 
