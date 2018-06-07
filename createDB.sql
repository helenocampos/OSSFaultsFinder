CREATE SCHEMA IF NOT EXISTS `faultsFinder` DEFAULT CHARACTER SET utf8 ;
USE `faultsFinder` ;

CREATE TABLE IF NOT EXISTS `faultsFinder`.`project` (
  `idProject` INT NOT NULL AUTO_INCREMENT,
  `organization` VARCHAR(45) NULL,
  `projectName` VARCHAR(45) NULL,
  PRIMARY KEY (`idProject`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `faultsFinder`.`failedBuild` (
  `idFailedBuild` INT NOT NULL AUTO_INCREMENT,
  `buildNumber` INT NULL,
  `failedAmount` INT NULL,
  `erroredAmount` INT NULL,
  `pullRequestURL` VARCHAR(200) NULL,
  `faillingModule` VARCHAR(50) NULL,
  `sha` VARCHAR(40) NULL,
  `project_idProject` INT NOT NULL,
  PRIMARY KEY (`idFailedBuild`),
  INDEX `fk_failedBuild_project_idx` (`project_idProject` ASC),
  CONSTRAINT `fk_failedBuild_project`
    FOREIGN KEY (`project_idProject`)
    REFERENCES `faultsFinder`.`project` (`idProject`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
