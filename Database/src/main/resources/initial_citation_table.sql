USE CitationDB;

CREATE TABLE `stefan_minimal_dataset` (
    `ID_SYSTEM_SEQUENCE` int(11) NOT NULL,
    `id` varchar(4) NOT NULL DEFAULT '',
    `mac_address` varchar(18) DEFAULT NULL,
    `ip_address` varchar(14) DEFAULT NULL,
    `email` varchar(27) DEFAULT NULL,
    `bitcoin` varchar(35) DEFAULT NULL,
    `INSERT_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `LAST_UPDATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    `RECORD_STATUS` enum('inserted', 'updated', 'deleted') NOT NULL DEFAULT 'inserted',
    PRIMARY KEY (`id` , `LAST_UPDATE`),
    KEY `stefan_test_INSERT_DATE` (`INSERT_DATE`),
    KEY `stefan_test_LAST_UPDATE` (`LAST_UPDATE`),
    KEY `stefan_test_RECORD_STATUS` (`RECORD_STATUS`)
)  ENGINE=InnoDB DEFAULT CHARSET=latin1;
INSERT INTO `stefan_minimal_dataset` VALUES (1,'1','15-53-60-5A-DA-06','71.137.64.159','lruiz0@soundcloud.com','1295VC8B2fwbVD11TuHQvkYseeYxuFWdLw','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(10,'10','18-3B-40-9E-28-90','203.66.64.36','rreynolds9@deviantart.com','199p1duxRvnQUmTnwwAbogdoCpTqeibjX5','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(2,'2','7B-18-BE-02-4E-EB','228.4.119.23','larnold1@zimbio.com','1NDpszMBvBgzWZox7Co3NYrWcbZbjiGMov','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(3,'3','C8-D9-A5-C8-8A-EE','92.85.53.25','aknight2@youku.com','1JuQq63LLkXZNiAUE5duzyc5rido7Py5wj','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(4,'4','18-1D-88-D9-69-BB','10.127.64.9','rmason3@examiner.com','1MeDsLJ9frTWeaD8ayy3aAqMaY4wQ4rqZh','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(5,'5','88-BE-F3-24-04-B8','124.63.150.33','aphillips4@feedburner.com','1785PQQMEVowFcdqsE2qbWoivvkNK1CkLE','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(6,'6','0B-50-6E-6B-7B-A7','212.55.144.11','kspencer5@epa.gov','1HkAVcL7wiFqAdLSMxu8XQ7aXkeZvpzv4y','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(7,'7','4E-DC-79-7E-F2-AC','42.220.41.194','jmason6@canalblog.com','1JmN47wyxzX2p2ahSfG6t1L6eVh4afi7rA','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(8,'8','08-48-5D-D3-3A-9B','210.64.78.208','sspencer7@forbes.com','15idWzeEFvkUgbJt81NFVqMcy6ErqU5F9X','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted'),(9,'9','8D-BB-25-8B-3A-80','6119234131','jryan8@photobucket.com','14pwKhE8Hhkiupf6FV6mPMzYDE414cUscs','2015-02-10 11:14:37','2015-02-10 11:14:37','inserted');