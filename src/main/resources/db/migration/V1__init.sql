CREATE TABLE bm_menu (
    idx INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    shop_no CHAR(10) COMMENT "식당코드",
    menu_name CHAR(32) COMMENT "메뉴명",
    price INT COMMENT "가격",
    menu_no CHAR(10) COMMENT "메뉴코드",
    max_sellable INT COMMENT "판매가능수량",
    today_remain INT COMMENT "오늘 판매 수량, 매일 매장 오픈시간에 max_sellable로 업데이트",
    UNIQUE (shop_no, menu_no)
)COMMENT='메뉴 테이블';
INSERT INTO bm_menu (shop_no, menu_name, price, menu_no, max_sellable, today_remain) VALUES("SHOP_01", "짜장면", 6000, "MENU_01", 10, 10);
INSERT INTO bm_menu (shop_no, menu_name, price, menu_no, max_sellable, today_remain) VALUES("SHOP_01", "볶음밥", 8000, "MENU_02", 10, 10);
INSERT INTO bm_menu (shop_no, menu_name, price, menu_no, max_sellable, today_remain) VALUES("SHOP_01", "햄샌드위치", 5000, "MENU_03", 10, 10);
INSERT INTO bm_menu (shop_no, menu_name, price, menu_no, max_sellable, today_remain) VALUES("SHOP_01", "족발", 20000, "MENU_04", 10, 10);
INSERT INTO bm_menu (shop_no, menu_name, price, menu_no, max_sellable, today_remain) VALUES("SHOP_01", "치킨", 12000, "MENU_05", 10, 10);

CREATE TABLE bm_order_status (
    idx int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_no CHAR(10) COMMENT "주문코드",
    shop_no CHAR(10) COMMENT "식당코드",
    accept_time BIGINT COMMENT "주문접수시간",
    total_price INT COMMENT "주문금액",
    order_status CHAR(8) COMMENT "주문상태 RECEIPT(주문성공), PICKUP(라이더픽업), COMPLETE(완료), CANCEL(실패)",
    distance INT COMMENT "배달거리",
    UNIQUE (order_no)
)COMMENT='주문 상태 관리 테이블';

CREATE TABLE bm_order (
    idx INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_no CHAR(10) COMMENT "주문코드",
    shop_no CHAR(10) COMMENT "식당코드",
    menu_no CHAR(10) COMMENT "메뉴코드",
    quantity INT COMMENT "수량",
    INDEX (order_no, shop_no, menu_no)
)COMMENT='주문 관리 테이블';

CREATE TABLE bm_rider(
    idx INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    rider_no CHAR(10) COMMENT "라이더 아이디",
    rider_name CHAR(32) COMMENT "라이더 이름",
    rider_mobility CHAR(8) COMMENT "배달수단",
    rider_speed INT UNSIGNED COMMENT "이동속도",
    rider_run CHAR(1) DEFAULT 'N' COMMENT "배달여부 Y-배달중, N-배달중아님",
    UNIQUE (rider_no)
)COMMENT='라이더 관리 테이블';
INSERT INTO bm_rider (rider_no, rider_name, rider_mobility, rider_speed) VALUES("RIDER_A01", "임꺽정", "오토바이", 100);
INSERT INTO bm_rider (rider_no, rider_name, rider_mobility, rider_speed) VALUES("RIDER_A02", "고길동", "자전거", 50);

CREATE TABLE bm_order_rider (
    idx int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_no CHAR(10) COMMENT "주문코드",
    shop_no CHAR(10) COMMENT "식당코드",
    rider_no CHAR(10) COMMENT "라이더 아이디",
    pickup_dt BIGINT DEFAULT 0 COMMENT "배달픽업시간",
    complete_dt BIGINT DEFAULT 0 COMMENT "배달완료시간",
    UNIQUE (order_no, shop_no)
)COMMENT='주문-라이더 연결 테이블';

CREATE TABLE bm_order_rider_log (
    idx INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_no CHAR(10) COMMENT "주문코드",
    shop_no CHAR(10) COMMENT "식당코드",
    rider_no CHAR(10) COMMENT "라이더 아이디",
    remain_distance INT COMMENT "배달 남은 거리",
    reg_dt BIGINT COMMENT "저장시간",
    INDEX(order_no, shop_no, rider_no)
)COMMENT='라이더 배달 로그 관리 테이블';
