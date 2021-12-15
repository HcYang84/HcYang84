

/*
	계약원장테이블 생성 
*/
CREATE TABLE `yhc`.`tbct0001` (
  `POLI_NO` varchar(10) NOT NULL COMMENT '계약번호',
  `PLICD` varchar(5) DEFAULT NULL COMMENT '상품코드',
  `STDT` varchar(8) DEFAULT NULL COMMENT '계약시작일자',
  `EDDT` varchar(8) DEFAULT NULL COMMENT '계약종료일자',
  `CNTR_PERIOD` decimal COMMENT '계약기간',
  `CS_STAT_CD` VARCHAR(2) DEFAULT NULL COMMENT '계약상태코드',
  `TPRM` decimal(15,2) DEFAULT NULL COMMENT '총보험료',
  `IPDTM` timestamp DEFAULT NULL COMMENT '입력일시',
  `MODIFY_DTM` timestamp DEFAULT NULL COMMENT '변경일시',
  PRIMARY KEY (`POLI_NO`)
) ;

/* 계약상세 */
CREATE TABLE `yhc`.`tbct0002` (
  `POLI_NO` varchar(10) NOT NULL COMMENT '계약번호',
  `PLICD` varchar(5) NOT NULL COMMENT '상품코드',
  `DAMBO_CD` VARCHAR(5) NOT NULL COMMENT '담보코드',
  `BOJANG_AMT` decimal(15,2) COMMENT '가입금액',
  `GIJUN_AMT` decimal(15,2) COMMENT '기준금액',
  `PRM` decimal(10,2) COMMENT '담보별보험료',
  `IPDTM` timestamp DEFAULT NULL COMMENT '입력일시',
  PRIMARY KEY (`POLI_NO`,`PLICD`,`DAMBO_CD`)
) ;

/* 상품정보 테이블 생성 */
CREATE TABLE `yhc`.`TBPD0001` (
	`PLICD` VARCHAR(5) NOT NULL COMMENT '상품코드',
    `PLINM` VARCHAR(200) DEFAULT NULL COMMENT '상품명',
    `PERIOD` numeric  DEFAULT NULL COMMENT '계약기간'
);

/* 상품_담보 테이블 생성 */
CREATE TABLE`yhc`.`TBPD0002` (
	`PLICD` VARCHAR(5) NOT NULL COMMENT '상품코드',
    `DAMBO_CD` VARCHAR(5) DEFAULT NULL COMMENT '담보코드',
    `DAMBO_NM` VARCHAR(200) DEFAULT NULL COMMENT '담보명',
    `BOJANG_AMT` decimal COMMENT '가입금액',
    `GIJUN_AMT` decimal COMMENT '기준금액'
);



