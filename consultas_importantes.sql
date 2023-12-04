-- Estações e as mesorregiões que elas pertencem
select e.codigo_estacao, m.id from estacao e, mesorregiao m
where st_contains(st_transform(m.geom, 4326), e.geom)

--Média da leitura da temperatura máxima para cada estação no mês 11/2023
select
	codigo_estacao,
	to_char(data_hora_leitura, 'MM/YYYY') as mes,
	avg(temperatura_maxima_lida) as avg_max
from leitura_estacao
where to_char(data_hora_leitura, 'MM/YYYY') = '11/2023'
group by codigo_estacao, mes

--Média da temperatura máxima em cada mesorregião para o mês de 11/2023
SELECT
	meso_sub.id,
	AVG(le_sub.avg_max) as average,
	(SELECT meso.geom FROM mesorregiao meso WHERE meso.id = meso_sub.id) as geom
FROM
	(select e.codigo_estacao, m.id from estacao e, mesorregiao m where st_contains(st_transform(m.geom, 4326), e.geom)) meso_sub JOIN
	(select codigo_estacao, to_char(data_hora_leitura, 'MM/YYYY') as mes, avg(temperatura_maxima_lida) as avg_max from leitura_estacao where to_char(data_hora_leitura, 'MM/YYYY') = '11/2023' group by codigo_estacao, mes) le_sub
	ON meso_sub.codigo_estacao = le_sub.codigo_estacao
GROUP BY meso_sub.id
ORDER BY average