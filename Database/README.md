Standard Query

    SELECT
        outerGroup.ID_SYSTEM_SEQUENCE,
        outerGroup.id,
        outerGroup.first_name,
        outerGroup.last_name,
        outerGroup.email,
        outerGroup.country,
        outerGroup.ip_address
    FROM
        stefan_adressen AS outerGroup
            INNER JOIN
        (SELECT
            id, max(LAST_UPDATE) AS mostRecent
        FROM
            stefan_adressen AS innerSELECT
        WHERE
            (innerSELECT.RECORD_STATUS = 'inserted'
                OR innerSELECT.RECORD_STATUS = 'updated')
        GROUP BY id)
        innerGroup ON outerGroup.id = innerGroup.id
        AND outerGroup.LAST_UPDATE = innerGroup.mostRecent
    ORDER BY outerGroup.ID_SYSTEM_SEQUENCE asc
    LIMIT 10 OFFSET 0


