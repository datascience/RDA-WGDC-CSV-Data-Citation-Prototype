USE CITATION_DB;


-- get the most recent version of 
SELECT * FROM addresses adr
INNER JOIN (
	SELECT email, max(LAST_UPDATE) AS mostRecent 
	FROM addresses 
	WHERE (RECORD_STATUS = 'inserted' OR RECORD_STATUS = 'updated') 
	GROUP BY email
	) grouped
ON adr.email = grouped.email AND adr.LAST_UPDATE = grouped.mostRecent;



-- apply filters
SELECT * FROM addresses adr
INNER JOIN (
	SELECT email, max(LAST_UPDATE) AS mostRecent 
	FROM addresses WHERE (RECORD_STATUS = 'inserted' OR RECORD_STATUS = 'updated') GROUP BY email
	) grouped 
ON adr.email = grouped.email AND adr.LAST_UPDATE = grouped.mostRecent 
WHERE adr.ID_SYSTEM_SEQUENCE = 474;