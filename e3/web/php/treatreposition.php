<?php
try {
	require 'connection.php';

	$ean = $_POST['ean'];
	$table;

	if (is_numeric($ean) and strlen($ean) == 13) {
		$stmt = $db->prepare("SELECT (:table) 
																	FROM (reposicao NATURAL JOIN evento_reposicao) 
																	WHERE ean=:ean");
		$stmt->execute(['table' => $table,'ean' => $ean]);

		echo("<table border=\"1\">\n");
		echo("<tr><td>EAN</td><td>Operador</td><td>Instante</td><td>Unidades Repostas</td></tr>");
		foreach ($table as $row) {
			echo("<tr><td>");
			echo($row['ean1']);
			echo("</td></td>");
			echo($row['operador']);
			echo("</td></td>");
			echo($row['instante']);
			echo("</td></td>");
			echo($row['unidades']);
			echo("</td></tr>\n");
		}
		echo("</table>\n");
	}
}
catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
