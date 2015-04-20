SELECT 
    *
FROM
    CITATION_DB.MSD100k;

SELECT 
    CONCAT((SELECT 
                    sum(crc32(track_id))
                FROM
                    CITATION_DB.MSD100k),
            (SELECT 
                    sum(crc32(title))
                FROM
                    CITATION_DB.MSD100k));





UPDATE `CITATION_DB`.`MSD100k` SET title="Suddenly" WHERE ID_SYSTEM_SEQUENCE=998 AND track_id = "TRMMEDP12903CDA710";

SELECT * FROM `CITATION_DB`.`MSD100k` WHERE ID_SYSTEM_SEQUENCE=998 AND track_id = "TRMMEDP12903CDA710";


SELECT 
    sum(crc32(CITATION_DB.MSD100k.ID_SYSTEM_SEQUENCE)),
    sum(crc32(CITATION_DB.MSD100k.track_id)),
    sum(crc32(CITATION_DB.MSD100k.title)),
    sum(crc32(CITATION_DB.MSD100k.song_id)),
    sum(crc32(CITATION_DB.MSD100k.release)),
    sum(crc32(CITATION_DB.MSD100k.artist_id)),
    sum(crc32(CITATION_DB.MSD100k.artist_mbid)),
    sum(crc32(CITATION_DB.MSD100k.artist_name)),
    sum(crc32(CITATION_DB.MSD100k.duration)),
    sum(crc32(CITATION_DB.MSD100k.artist_familiarity)),
    sum(crc32(CITATION_DB.MSD100k.artist_hotttnesss)),
    sum(crc32(CITATION_DB.MSD100k.year)),
    sum(crc32(CITATION_DB.MSD100k.digitalid)),
    sum(crc32(CITATION_DB.MSD100k.audiofile)),
    sum(crc32(CITATION_DB.MSD100k.lastfm)),
    sum(crc32(CITATION_DB.MSD100k.numlastfm)),
    sum(crc32(CITATION_DB.MSD100k.numlastfmmatched)),
    sum(crc32(CITATION_DB.MSD100k.rpfeatures)),
    sum(crc32(CITATION_DB.MSD100k.audiofilelength)),
    sum(crc32(CITATION_DB.MSD100k.simrplastfm)),
    sum(crc32(CITATION_DB.MSD100k.INSERT_DATE)),
    sum(crc32(CITATION_DB.MSD100k.LAST_UPDATE))
FROM
    CITATION_DB.MSD100k;



SHOW KEYS FROM CITATION_DB.MSD100k WHERE Key_name = 'PRIMARY';

SELECT sum(crc32(CITATION_DB.MSD100k.ID_SYSTEM_SEQUENCE)),sum(crc32(CITATION_DB.MSD100k.track_id)),sum(crc32(CITATION_DB.MSD100k.title)),sum(crc32(CITATION_DB.MSD100k.song_id)),sum(crc32(CITATION_DB.MSD100k.release)),sum(crc32(CITATION_DB.MSD100k.artist_id)),sum(crc32(CITATION_DB.MSD100k.artist_mbid)),sum(crc32(CITATION_DB.MSD100k.artist_name)),sum(crc32(CITATION_DB.MSD100k.duration)),sum(crc32(CITATION_DB.MSD100k.artist_familiarity)),sum(crc32(CITATION_DB.MSD100k.artist_hotttnesss)),sum(crc32(CITATION_DB.MSD100k.year)),sum(crc32(CITATION_DB.MSD100k.digitalid)),sum(crc32(CITATION_DB.MSD100k.audiofile)),sum(crc32(CITATION_DB.MSD100k.lastfm)),sum(crc32(CITATION_DB.MSD100k.numlastfm)),sum(crc32(CITATION_DB.MSD100k.numlastfmmatched)),sum(crc32(CITATION_DB.MSD100k.rpfeatures)),sum(crc32(CITATION_DB.MSD100k.audiofilelength)),sum(crc32(CITATION_DB.MSD100k.simrplastfm)),sum(crc32(CITATION_DB.MSD100k.INSERT_DATE)),sum(crc32(CITATION_DB.MSD100k.LAST_UPDATE)) FROM CITATION_DB.MSD100k ORDER BY ID_SYSTEM_SEQUENCE;



SELECT SHA1(CONCAT(CITATION_DB.MSD100k.ID_SYSTEM_SEQUENCE, CITATION_DB.MSD100k.track_id, CITATION_DB.MSD100k.title)) FROM CITATION_DB.MSD100k;


SELECT SHA1(CONCAT(MSD100k.ID_SYSTEM_SEQUENCE, MSD100k.title, MSD100k.artist_name))   FROM `CITATION_DB`.`MSD100k` WHERE ID_SYSTEM_SEQUENCE < 500