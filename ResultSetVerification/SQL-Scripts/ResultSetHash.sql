SELECT * FROM CITATION_DB.MSD100k;

SELECT CONCAT(
    (SELECT sum(crc32(track_id)) FROM CITATION_DB.MSD100k),
    (SELECT sum(crc32(title)) FROM CITATION_DB.MSD100k)
);


SELECT sum(crc32(track_id)), sum(crc32(title)), sum(crc32(song_id)), sum(crc32(`release`)), sum(crc32(`artist_id`)), sum(crc32(`artist_mbid`))
FROM CITATION_DB.MSD100k;


UPDATE `CITATION_DB`.`MSD100k` SET title="Suddenly" WHERE ID_SYSTEM_SEQUENCE=998 AND track_id = "TRMMEDP12903CDA710";



