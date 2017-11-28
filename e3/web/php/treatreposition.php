<?php

require 'connection.php';

$ean = $_POST['ean'];

$stmt = $db->prepare("SELECT * 
															FROM (reposicao NATURAL JOIN evento_reposicao) 
															WHERE ean=:ean");
$stmt->execute(['ean' => $ean]);

if($stmt->columnCount() > 0) {
?>

<table>
	<?php
	echo("<tr><th>EAN</th><th>Operador</th><th>Instante</th><th>Unidades Repostas</th></tr>");

	while($row = $stmt->fetch())
		echo "<tr><td>$row[0]</td><td>$row[1]</td><td>$row[2]</td><td>$row[3]</td></tr>";

?>
</table>
<?php
}