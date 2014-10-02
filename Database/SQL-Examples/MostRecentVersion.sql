USE CITATION_DB;


-- get the most recent version of 
SELECT 
    *
FROM
    versuch1 adr
        INNER JOIN
    (SELECT 
        email, max(LAST_UPDATE) AS mostRecent
    FROM
        versuch1
    WHERE
        (RECORD_STATUS = 'inserted'
            OR RECORD_STATUS = 'updated')
    GROUP BY email) grouped ON adr.email = grouped.email
        AND adr.LAST_UPDATE = grouped.mostRecent;



-- apply filters
SELECT 
    *
FROM
    addresses adr
        INNER JOIN
    (SELECT 
        email, max(LAST_UPDATE) AS mostRecent
    FROM
        addresses
    WHERE
        (RECORD_STATUS = 'inserted'
            OR RECORD_STATUS = 'updated')
    GROUP BY email) grouped ON adr.email = grouped.email
        AND adr.LAST_UPDATE = grouped.mostRecent
WHERE
    adr.ID_SYSTEM_SEQUENCE = 474;

USE CITATION_DB;


SELECT 
    *
FROM
    versuch1 outerGroup
        INNER JOIN
    (SELECT 
        LAST_UPDATE, max(LAST_UPDATE) AS mostRecent
    FROM
        versuch1
    WHERE
        (RECORD_STATUS = 'inserted'
            OR RECORD_STATUS = 'updated')
    GROUP BY LAST_UPDATE) innerGroup ON outerGroup.LAST_UPDATE = innerGroup.LAST_UPDATE
        AND outerGroup.LAST_UPDATE = innerGroup.mostRecent
ORDER BY ID_SYSTEM_SEQUENCE asc
LIMIT 10 OFFSET 0;


-- current implementation
SELECT 
    *
FROM
    versuch1 AS outerGroup
        INNER JOIN
    (SELECT 
        LAST_UPDATE, max(LAST_UPDATE) AS mostRecent
    FROM
        versuch1
    WHERE
        (RECORD_STATUS = 'inserted'
            OR RECORD_STATUS = 'updated')
    GROUP BY LAST_UPDATE) innerGroup ON outerGroup.LAST_UPDATE = innerGroup.LAST_UPDATE
        AND outerGroup.LAST_UPDATE = innerGroup.mostRecent
ORDER BY ID_SYSTEM_SEQUENCE asc
LIMIT 10 OFFSET 0

